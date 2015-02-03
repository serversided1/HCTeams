package net.frozenorb.foxtrot.listener;

import com.google.common.collect.ImmutableSet;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.citadel.CitadelHandler;
import net.frozenorb.foxtrot.jedis.persist.PvPTimerMap;
import net.frozenorb.foxtrot.nametag.NametagManager;
import net.frozenorb.foxtrot.server.RegionData;
import net.frozenorb.foxtrot.server.RegionType;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.foxtrot.team.dtr.bitmask.DTRBitmaskType;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionTracker;
import net.frozenorb.foxtrot.teamactiontracker.enums.TeamActionType;
import net.frozenorb.foxtrot.util.InvUtils;
import net.frozenorb.mBasic.Basic;
import net.minecraft.server.v1_7_R3.EntityLightning;
import net.minecraft.server.v1_7_R3.PacketPlayOutSpawnEntityWeather;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

@SuppressWarnings("deprecation")
public class FoxListener implements Listener {

    public static final Set<PotionEffectType> DEBUFFS = ImmutableSet.of(PotionEffectType.POISON, PotionEffectType.SLOW, PotionEffectType.WEAKNESS, PotionEffectType.HARM, PotionEffectType.WITHER);
    public static final Set<Material> NO_INTERACT_WITH = ImmutableSet.of(Material.LAVA_BUCKET, Material.WATER_BUCKET, Material.BUCKET);
    public static final Set<Material> ATTACK_DISABLING_BLOCKS = ImmutableSet.of(Material.GLASS, Material.WOOD_DOOR, Material.IRON_DOOR, Material.FENCE_GATE);
    public static final Set<Material> NO_INTERACT = ImmutableSet.of(Material.FENCE_GATE,
            Material.FURNACE, Material.BURNING_FURNACE, Material.BREWING_STAND, Material.CHEST,
            Material.HOPPER, Material.DISPENSER, Material.WOODEN_DOOR,
            Material.STONE_BUTTON, Material.WOOD_BUTTON,
            Material.TRAPPED_CHEST, Material.TRAP_DOOR, Material.LEVER,
            Material.DROPPER, Material.ENCHANTMENT_TABLE, Material.BED_BLOCK, Material.ANVIL);

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof IronGolem) {
            ((IronGolem) event.getEntity()).setPlayerCreated(false);
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        if (ServerHandler.getTasks().containsKey(event.getPlayer().getName())) {
            FoxtrotPlugin.getInstance().getServer().getScheduler().cancelTask(ServerHandler.getTasks().get(event.getPlayer().getName()));
            ServerHandler.getTasks().remove(event.getPlayer().getName());
            event.getPlayer().sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "LOGOUT " + ChatColor.RED.toString() + ChatColor.BOLD + "CANCELLED!");
        }

        Team ownerTo = LandBoard.getInstance().getTeam(event.getTo());

        if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getName()) && ownerTo != null && ownerTo.isMember(event.getPlayer().getName())) {
            FoxtrotPlugin.getInstance().getPvPTimerMap().removeTimer(event.getPlayer().getName());
        }

        Team ownerFrom = LandBoard.getInstance().getTeam(event.getFrom());
        ServerHandler sm = FoxtrotPlugin.getInstance().getServerHandler();
        RegionData from = sm.getRegion(ownerFrom, event.getFrom());
        RegionData to = sm.getRegion(ownerTo, event.getTo());

        if (!from.equals(to)) {
            if (!to.getRegionType().getMoveHandler().handleMove(event)) {
                return;
            }

            // PVP Timer
            if (from.getRegionType() == RegionType.SPAWN) {
                if (FoxtrotPlugin.getInstance().getPvPTimerMap().getTimer(event.getPlayer().getName()) == PvPTimerMap.PENDING_USE) {
                    FoxtrotPlugin.getInstance().getPvPTimerMap().createTimer(event.getPlayer().getName(), 30 * 60);
                }
            }

            boolean fromReduceDeathban = from.getData() != null && (from.getData().hasDTRBitmask(DTRBitmaskType.FIVE_MINUTE_DEATHBAN) || from.getData().hasDTRBitmask(DTRBitmaskType.FIFTEEN_MINUTE_DEATHBAN) || from.getData().hasDTRBitmask(DTRBitmaskType.SAFE_ZONE));
            boolean toReduceDeathban = to.getData() != null && (to.getData().hasDTRBitmask(DTRBitmaskType.FIVE_MINUTE_DEATHBAN) || to.getData().hasDTRBitmask(DTRBitmaskType.FIFTEEN_MINUTE_DEATHBAN) || to.getData().hasDTRBitmask(DTRBitmaskType.SAFE_ZONE));

            // Don't display non-deathbans during EOTW.
            if (FoxtrotPlugin.getInstance().getServerHandler().isEOTW()) {
                fromReduceDeathban = false;
                toReduceDeathban = false;
            }

            event.getPlayer().sendMessage(ChatColor.YELLOW + "Now leaving: " + from.getName(event.getPlayer()) + ChatColor.YELLOW + "(" + (fromReduceDeathban ? ChatColor.GREEN + "Non-Deathban" : ChatColor.RED + "Deathban") + ChatColor.YELLOW + ")");
            event.getPlayer().sendMessage(ChatColor.YELLOW + "Now entering: " + to.getName(event.getPlayer()) + ChatColor.YELLOW + "(" + (toReduceDeathban ? ChatColor.GREEN + "Non-Deathban" : ChatColor.RED + "Deathban") + ChatColor.YELLOW + ")");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        FoxtrotPlugin.getInstance().getPlaytimeMap().playerQuit(event.getPlayer().getName(), true);

        NametagManager.getTeamMap().remove(event.getPlayer().getName());
        FoxtrotPlugin.getInstance().getScoreboardHandler().remove(event.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();

        NametagManager.initPlayer(event.getPlayer());
        NametagManager.sendTeamsToPlayer(event.getPlayer());
        NametagManager.reloadPlayer(event.getPlayer());

        event.setJoinMessage(null);

        FoxtrotPlugin.getInstance().getPlaytimeMap().playerJoined(event.getPlayer().getName());
        FoxtrotPlugin.getInstance().getLastJoinMap().setLastJoin(event.getPlayer().getName());

        if (!event.getPlayer().hasPlayedBefore()) {
            FoxtrotPlugin.getInstance().getFirstJoinMap().setFirstJoin(event.getPlayer().getName());
            Basic.get().getEconomyManager().setBalance(event.getPlayer().getName(), 100D);
            event.getPlayer().teleport(FoxtrotPlugin.getInstance().getServerHandler().getSpawnLocation());
        }

        // PVP timer
        if (!(FoxtrotPlugin.getInstance().getPvPTimerMap().contains(name))) {
            FoxtrotPlugin.getInstance().getPvPTimerMap().pendingTimer(player.getName());
        }

        if (FoxtrotPlugin.getInstance().getPvPTimerMap().getTimer(name) == PvPTimerMap.PENDING_USE) {
            player.sendMessage(ChatColor.YELLOW + "You have still not activated your 30 minute PVP timer! Walk out of spawn to activate it!");
        }

        FoxtrotPlugin.getInstance().getScoreboardHandler().update(event.getPlayer());
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (ServerHandler.getTasks().containsKey(player.getName())) {
                FoxtrotPlugin.getInstance().getServer().getScheduler().cancelTask(ServerHandler.getTasks().get(player.getName()));
                ServerHandler.getTasks().remove(player.getName());
                player.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "LOGOUT " + ChatColor.RED.toString() + ChatColor.BOLD + "CANCELLED!");
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onProjetileInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (event.getItem().getType() == Material.POTION) {
                try { // Ensure that any errors with Potion.fromItemStack don't mess with the rest of the code.
                    ItemStack i = event.getItem();

                    // We can't run Potion.fromItemStack on a water bottle.
                    if (i.getDurability() != (short) 0) {
                        Potion pot = Potion.fromItemStack(i);

                        if (pot.isSplash() && DEBUFFS.contains(pot.getType().getEffectType())) {
                            if (!FoxtrotPlugin.getInstance().getServerHandler().isPreEOTW() && FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(player.getName())) {
                                player.sendMessage(ChatColor.RED + "You cannot do this while your PVP Timer is active!");
                                player.sendMessage(ChatColor.RED + "Type '" + ChatColor.YELLOW + "/pvp enable" + ChatColor.RED + "' to remove your timer.");
                                event.setCancelled(true);
                                return;
                            }

                            if (!FoxtrotPlugin.getInstance().getServerHandler().isPreEOTW() && DTRBitmaskType.SAFE_ZONE.appliesAt(player.getLocation())) {
                                event.setCancelled(true);
                                event.getPlayer().sendMessage(ChatColor.RED + "You cannot launch debuffs from inside spawn!");
                                event.getPlayer().updateInventory();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (event.getClickedBlock() == null) {
            return;
        }

        if (event.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE && event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (event.getItem() != null) {
                if (event.getItem().getType() == Material.ENCHANTED_BOOK) {
                    event.getItem().setType(Material.BOOK);

                    event.getPlayer().sendMessage(ChatColor.GREEN + "You reverted this book to its original form!");
                    event.setCancelled(true);
                }
            }

            return;
        }

        if (FoxtrotPlugin.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getClickedBlock().getLocation()) || FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        Team team = LandBoard.getInstance().getTeam(event.getClickedBlock().getLocation());

        if (team != null && !team.isMember(event.getPlayer())) {
            if (NO_INTERACT.contains(event.getClickedBlock().getType()) || NO_INTERACT_WITH.contains(event.getMaterial())) {
                if (event.getClickedBlock().getType() == Material.CHEST || event.getClickedBlock().getType().name().contains("DOOR")) {
                    CitadelHandler citadelHandler = FoxtrotPlugin.getInstance().getCitadelHandler();

                    if (DTRBitmaskType.CITADEL.appliesAt(event.getClickedBlock().getLocation()) && citadelHandler.canLootCitadel(event.getPlayer())) {
                        return;
                    }
                }

                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot do this in " + team.getName(event.getPlayer()) + ChatColor.YELLOW + "'s territory.");

                if (event.getMaterial() == Material.TRAP_DOOR || event.getMaterial() == Material.FENCE_GATE || event.getMaterial().name().contains("DOOR")) {
                    FoxtrotPlugin.getInstance().getServerHandler().disablePlayerAttacking(event.getPlayer(), 1);
                }

                return;
            }

            if (event.getAction() == Action.PHYSICAL) {
                event.setCancelled(true);
            }
        } else if (event.getMaterial() == Material.LAVA_BUCKET) {
            if (team == null || !team.isMember(event.getPlayer())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "You can only do this in your own claims!");
            }
        } else {
            if (team != null && !team.isCaptain(event.getPlayer().getName()) && !team.isOwner(event.getPlayer().getName())) {
                Subclaim subclaim = team.getSubclaim(event.getClickedBlock().getLocation());

                if (subclaim != null && !subclaim.isMember(event.getPlayer().getName())) {
                    if (NO_INTERACT.contains(event.getClickedBlock().getType()) || NO_INTERACT_WITH.contains(event.getMaterial())) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ChatColor.YELLOW + "You do not have access to the subclaim " + ChatColor.GREEN + subclaim.getName() + ChatColor.YELLOW  + "!");
                    }
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onSignInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.SKULL) {
            Skull sk = (Skull) event.getClickedBlock().getState();

            if (sk.getSkullType() == SkullType.PLAYER) {
                event.getPlayer().sendMessage(ChatColor.YELLOW + "Head of " + ChatColor.WHITE + sk.getOwner() + ChatColor.YELLOW + ".");
            }
        }

        if (event.getClickedBlock() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getState() instanceof Sign) {
                Sign s = (Sign) event.getClickedBlock().getState();

                if (DTRBitmaskType.SAFE_ZONE.appliesAt(event.getClickedBlock().getLocation())) {
                    if (s.getLine(0).contains("Kit")) {
                        FoxtrotPlugin.getInstance().getServerHandler().handleKitSign(s, event.getPlayer());
                    } else if (s.getLine(0).contains("Buy") || s.getLine(0).contains("Sell")) {
                        FoxtrotPlugin.getInstance().getServerHandler().handleShopSign(s, event.getPlayer());
                    } else if (s.getLine(0).contains("DTR Regen")) {
                        FoxtrotPlugin.getInstance().getServerHandler().handleDTRRegenSign(s, event.getPlayer());
                    }

                    event.setCancelled(true);
                }
            }
        }

        if (event.getItem() != null && event.getMaterial() == Material.SIGN) {
            if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().getLore() != null) {
                ArrayList<String> lore = (ArrayList<String>) event.getItem().getItemMeta().getLore();

                if (lore.size() > 1 && lore.get(1).contains("§e")) {
                    if (event.getClickedBlock() != null) {
                        event.getClickedBlock().getRelative(event.getBlockFace()).getState().setMetadata("noSignPacket", new FixedMetadataValue(FoxtrotPlugin.getInstance(), true));

                        Bukkit.getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), new Runnable() {

                            @Override
                            public void run() {
                                event.getClickedBlock().getRelative(event.getBlockFace()).getState().removeMetadata("noSignPacket", FoxtrotPlugin.getInstance());
                            }
                        }, 20);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSignPlace(BlockPlaceEvent e) {
        Block block = e.getBlock();
        ItemStack hand = e.getItemInHand();

        if (hand.getType() == Material.SIGN) {
            if (hand.hasItemMeta() && hand.getItemMeta().getLore() != null) {
                ArrayList<String> lore = (ArrayList<String>) hand.getItemMeta().getLore();

                if (e.getBlock().getType() == Material.WALL_SIGN || e.getBlock().getType() == Material.SIGN_POST) {
                    Sign s = (Sign) e.getBlock().getState();

                    for (int i = 0; i < 4; i++) {
                        s.setLine(i, lore.get(i));
                    }

                    s.setMetadata("deathSign", new FixedMetadataValue(FoxtrotPlugin.getInstance(), true));
                    s.update();
                }
            }
        } else if (hand.getType() == Material.MOB_SPAWNER) {
            if (!(e.isCancelled())) {
                if (hand.hasItemMeta() && hand.getItemMeta().hasDisplayName() && hand.getItemMeta().getDisplayName().startsWith(ChatColor.RESET.toString())) {
                    String name = ChatColor.stripColor(hand.getItemMeta().getDisplayName());
                    String entName = name.replace(" Spawner", "");
                    EntityType type = EntityType.valueOf(entName.toUpperCase().replaceAll(" ", "_"));
                    CreatureSpawner spawner = (CreatureSpawner) block.getState();

                    spawner.setSpawnedType(type);
                    spawner.update();
                    e.getPlayer().sendMessage(ChatColor.AQUA + "You placed a " + entName + " spawner!");
                }
            }
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        if (e.getBlock().getState().hasMetadata("deathSign") || ((Sign) e.getBlock().getState()).getLine(1).contains("§e")) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onSignBreak(BlockBreakEvent e) {
        if (e.isCancelled()) {
            return;
        }

        if (e.getBlock().getType() == Material.WALL_SIGN || e.getBlock().getType() == Material.SIGN_POST) {
            if (e.getBlock().getState().hasMetadata("deathSign") || ((e.getBlock().getState() instanceof Sign && ((Sign) e.getBlock().getState()).getLine(1).contains("§e")))) {
                e.setCancelled(true);

                Sign sign = (Sign) e.getBlock().getState();

                ItemStack deathsign = new ItemStack(Material.SIGN);
                ItemMeta meta = deathsign.getItemMeta();

                if (sign.getLine(1).contains("Captured")) {
                    meta.setDisplayName("§dKOTH Capture Sign");
                } else {
                    meta.setDisplayName("§dDeath Sign");
                }

                ArrayList<String> lore = new ArrayList<String>();

                for (String str : sign.getLines()) {
                    lore.add(str);
                }

                meta.setLore(lore);
                deathsign.setItemMeta(meta);
                e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), deathsign);

                e.getBlock().setType(Material.AIR);
                e.getBlock().getState().removeMetadata("deathSign", FoxtrotPlugin.getInstance());
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        Player player = event.getEntity();
        Date now = new Date();

        SpawnTagHandler.removeTag(event.getEntity());

        Team t = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(event.getEntity().getName());

        if (event.getEntity().getKiller() != null) {
            Team killerTeam = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(event.getEntity().getKiller().getName());

            if (killerTeam != null) {
                TeamActionTracker.logAction(killerTeam, TeamActionType.KILLS, "Member Kill: " + event.getEntity().getName() + " slain by " + event.getEntity().getKiller().getName() + " [X: " + event.getEntity().getLocation().getBlockX() + ", Y: " + event.getEntity().getLocation().getBlockY() + ", Z: " + event.getEntity().getLocation().getBlockZ() + "]");
            }
        }

        if (t != null) {
            t.playerDeath(event.getEntity().getName(), FoxtrotPlugin.getInstance().getServerHandler().getDTRLoss(event.getEntity()));
        }

        // Add deaths to armor
        String deathMsg = ChatColor.YELLOW + player.getName() + ChatColor.RESET + " " + (player.getKiller() != null ? "killed by " + ChatColor.YELLOW + player.getKiller().getName() : "died") + " " + ChatColor.GOLD + InvUtils.DEATH_TIME_FORMAT.format(now);

        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor != null && armor.getType() != Material.AIR) {
                InvUtils.addDeath(armor, deathMsg);
            }
        }

        if (event.getEntity().getKiller() != null) {
            Player killer = event.getEntity().getKiller();
            ItemStack hand = killer.getItemInHand();

            // Add kills to sword lore
            if (hand.getType().name().contains("SWORD") || hand.getType() == Material.BOW) {
                InvUtils.addKill(hand, killer.getDisplayName() + ChatColor.YELLOW + " " + (hand.getType() == Material.BOW ? "shot" : "killed") + " " + event.getEntity().getDisplayName());
            }

            // Add player head to item drops
            if (killer.hasPermission("foxtrot.skulldrop")) {
                ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
                SkullMeta meta = (SkullMeta) skull.getItemMeta();

                meta.setOwner(player.getName());
                meta.setDisplayName(ChatColor.YELLOW + "Head of " + player.getName());
                meta.setLore(Arrays.asList("", deathMsg));
                skull.setItemMeta(meta);
                event.getDrops().add(skull);
            }

            for (ItemStack it : event.getEntity().getKiller().getInventory().addItem(FoxtrotPlugin.getInstance().getServerHandler().generateDeathSign(event.getEntity().getName(), event.getEntity().getKiller().getName())).values()) {
                event.getDrops().add(it);
            }
        }

        // Lightning
        Location loc = player.getLocation();

        EntityLightning entity = new EntityLightning(((CraftWorld) loc.getWorld()).getHandle(), loc.getX(), loc.getY(), loc.getZ(), true, false);
        PacketPlayOutSpawnEntityWeather packet = new PacketPlayOutSpawnEntityWeather(entity);

        for (Player online : player.getWorld().getPlayers()) {
            if (online.equals(player)) {
                continue;
            }

            if (FoxtrotPlugin.getInstance().getToggleLightningMap().isLightningToggled(online.getName())) {
                online.playSound(online.getLocation(), Sound.AMBIENCE_THUNDER, 10000F, 0.8F + FoxtrotPlugin.RANDOM.nextFloat() * 0.2F);
                ((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
            }
        }

        // Transfer money
        double bal = Basic.get().getEconomyManager().getBalance(player.getName());
        Basic.get().getEconomyManager().withdrawPlayer(player.getName(), bal);

        if (player.getKiller() != null) {
            Basic.get().getEconomyManager().depositPlayer(player.getKiller().getName(), bal);
            player.getKiller().sendMessage(ChatColor.GOLD + "You earned " + ChatColor.BOLD + "$" + bal + ChatColor.GOLD + " for killing " + player.getDisplayName() + ChatColor.GOLD + "!");
        }
    }

}