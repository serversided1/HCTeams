package net.frozenorb.foxtrot.listener;

import com.google.common.collect.ImmutableSet;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.citadel.CitadelHandler;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.foxtrot.server.RegionData;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionTracker;
import net.frozenorb.foxtrot.teamactiontracker.enums.TeamActionType;
import net.frozenorb.foxtrot.util.InventoryUtils;
import net.frozenorb.mBasic.Basic;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

@SuppressWarnings("deprecation")
public class FoxListener implements Listener {

    public static final ItemStack FIRST_SPAWN_BOOK = new ItemStack(Material.WRITTEN_BOOK);
    public static final ItemStack FIRST_SPAWN_FISHING_ROD = new ItemStack(Material.FISHING_ROD);
    public static final Set<PotionEffectType> DEBUFFS = ImmutableSet.of(PotionEffectType.POISON, PotionEffectType.SLOW, PotionEffectType.WEAKNESS, PotionEffectType.HARM, PotionEffectType.WITHER);
    public static final Set<Material> NO_INTERACT_WITH = ImmutableSet.of(Material.LAVA_BUCKET, Material.WATER_BUCKET, Material.BUCKET);
    public static final Set<Material> ATTACK_DISABLING_BLOCKS = ImmutableSet.of(Material.GLASS, Material.WOOD_DOOR, Material.IRON_DOOR, Material.FENCE_GATE);
    public static final Set<Material> NO_INTERACT = ImmutableSet.of(Material.FENCE_GATE,
            Material.FURNACE, Material.BURNING_FURNACE, Material.BREWING_STAND, Material.CHEST,
            Material.HOPPER, Material.DISPENSER, Material.WOODEN_DOOR,
            Material.STONE_BUTTON, Material.WOOD_BUTTON,
            Material.TRAPPED_CHEST, Material.TRAP_DOOR, Material.LEVER,
            Material.DROPPER, Material.ENCHANTMENT_TABLE, Material.BED_BLOCK, Material.ANVIL, Material.BEACON);

    static {
        BookMeta bookMeta = (BookMeta) FIRST_SPAWN_BOOK.getItemMeta();

        bookMeta.setTitle(ChatColor.GOLD + "Welcome to HCTeams");
        bookMeta.setPages(

                        ChatColor.BLUE + "Welcome to HCTeams!"

        );
        bookMeta.setAuthor("MineHQ");

        FIRST_SPAWN_BOOK.setItemMeta(bookMeta);
        FIRST_SPAWN_FISHING_ROD.addEnchantment(Enchantment.LURE, 2);
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        if (Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer()) || event.getBucket() != Material.LAVA_BUCKET) {
            return;
        }

        Team teamAt = LandBoard.getInstance().getTeam(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation());

        if (teamAt == null) {
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot place lava in the wilderness!");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        processTerritoryInfo(event); // this only works because I'm lucky and PlayerTeleportEvent extends PlayerMoveEvent :0
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        if (ServerHandler.getTasks().containsKey(event.getPlayer().getName())) {
            Foxtrot.getInstance().getServer().getScheduler().cancelTask(ServerHandler.getTasks().get(event.getPlayer().getName()));
            ServerHandler.getTasks().remove(event.getPlayer().getName());
            event.getPlayer().sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "LOGOUT " + ChatColor.RED.toString() + ChatColor.BOLD + "CANCELLED!");
        }

        processTerritoryInfo(event);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Foxtrot.getInstance().getPlaytimeMap().playerQuit(event.getPlayer().getUniqueId(), true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        Foxtrot.getInstance().getPlaytimeMap().playerJoined(event.getPlayer().getUniqueId());
        Foxtrot.getInstance().getLastJoinMap().setLastJoin(event.getPlayer().getUniqueId());

        if (!event.getPlayer().hasPlayedBefore()) {
            Foxtrot.getInstance().getPvPTimerMap().createTimer(event.getPlayer().getUniqueId(), 30 * 60);
            Foxtrot.getInstance().getFirstJoinMap().setFirstJoin(event.getPlayer().getUniqueId());
            Basic.get().getEconomyManager().setBalance(event.getPlayer().getName(), 100D);

            event.getPlayer().getInventory().addItem(FIRST_SPAWN_BOOK);
            event.getPlayer().getInventory().addItem(FIRST_SPAWN_FISHING_ROD);

            event.getPlayer().teleport(Foxtrot.getInstance().getServerHandler().getSpawnLocation());
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (ServerHandler.getTasks().containsKey(player.getName())) {
                Foxtrot.getInstance().getServer().getScheduler().cancelTask(ServerHandler.getTasks().get(player.getName()));
                ServerHandler.getTasks().remove(player.getName());
                player.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "LOGOUT " + ChatColor.RED.toString() + ChatColor.BOLD + "CANCELLED!");
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onProjectileInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (event.getItem().getType() == Material.POTION) {
                try { // Ensure that any errors with Potion.fromItemStack don't mess with the rest of the code.
                    ItemStack i = event.getItem();

                    // We can't run Potion.fromItemStack on a water bottle.
                    if (i.getDurability() != (short) 0) {
                        Potion pot = Potion.fromItemStack(i);

                        if (pot != null && pot.isSplash() && DEBUFFS.contains(pot.getType().getEffectType())) {
                            if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
                                player.sendMessage(ChatColor.RED + "You cannot do this while your PVP Timer is active!");
                                player.sendMessage(ChatColor.RED + "Type '" + ChatColor.YELLOW + "/pvp enable" + ChatColor.RED + "' to remove your timer.");
                                event.setCancelled(true);
                                return;
                            }

                            if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
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

        if (Foxtrot.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getClickedBlock().getLocation()) || Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        Team team = LandBoard.getInstance().getTeam(event.getClickedBlock().getLocation());

        if (team != null && !team.isMember(event.getPlayer().getUniqueId())) {
            if (NO_INTERACT.contains(event.getClickedBlock().getType()) || NO_INTERACT_WITH.contains(event.getMaterial())) {
                if (event.getClickedBlock().getType().name().contains("BUTTON") || event.getClickedBlock().getType().name().contains("CHEST") || event.getClickedBlock().getType().name().contains("DOOR")) {
                    CitadelHandler citadelHandler = Foxtrot.getInstance().getCitadelHandler();

                    if (DTRBitmask.CITADEL.appliesAt(event.getClickedBlock().getLocation()) && citadelHandler.canLootCitadel(event.getPlayer())) {
                        return;
                    }
                }

                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot do this in " + team.getName(event.getPlayer()) + ChatColor.YELLOW + "'s territory.");

                if (event.getMaterial() == Material.TRAP_DOOR || event.getMaterial() == Material.FENCE_GATE || event.getMaterial().name().contains("DOOR")) {
                    Foxtrot.getInstance().getServerHandler().disablePlayerAttacking(event.getPlayer(), 1);
                }

                return;
            }

            if (event.getAction() == Action.PHYSICAL) {
                event.setCancelled(true);
            }
        } else if (event.getMaterial() == Material.LAVA_BUCKET) {
            if (team == null || !team.isMember(event.getPlayer().getUniqueId())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "You can only do this in your own claims!");
            }
        } else {
            if (team != null && !team.isCaptain(event.getPlayer().getUniqueId()) && !team.isOwner(event.getPlayer().getUniqueId())) {
                Subclaim subclaim = team.getSubclaim(event.getClickedBlock().getLocation());

                if (subclaim != null && !subclaim.isMember(event.getPlayer().getUniqueId())) {
                    if (NO_INTERACT.contains(event.getClickedBlock().getType()) || NO_INTERACT_WITH.contains(event.getMaterial())) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ChatColor.YELLOW + "You do not have access to the subclaim " + ChatColor.GREEN + subclaim.getName() + ChatColor.YELLOW  + "!");
                    }
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onSignInteract(final PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getState() instanceof Sign) {
                Sign s = (Sign) event.getClickedBlock().getState();

                if (DTRBitmask.SAFE_ZONE.appliesAt(event.getClickedBlock().getLocation())) {
                    if (s.getLine(0).contains("Kit")) {
                        Foxtrot.getInstance().getServerHandler().handleKitSign(s, event.getPlayer());
                    } else if (s.getLine(0).contains("Buy") || s.getLine(0).contains("Sell")) {
                        Foxtrot.getInstance().getServerHandler().handleShopSign(s, event.getPlayer());
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
                        event.getClickedBlock().getRelative(event.getBlockFace()).getState().setMetadata("noSignPacket", new FixedMetadataValue(Foxtrot.getInstance(), true));

                        new BukkitRunnable() {

                            public void run() {
                                event.getClickedBlock().getRelative(event.getBlockFace()).getState().removeMetadata("noSignPacket", Foxtrot.getInstance());
                            }

                        }.runTaskLater(Foxtrot.getInstance(), 20L);
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

                    s.setMetadata("deathSign", new FixedMetadataValue(Foxtrot.getInstance(), true));
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

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onSignBreak(BlockBreakEvent e) {
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

                meta.setLore(Arrays.asList(sign.getLines()));
                deathsign.setItemMeta(meta);
                e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), deathsign);

                e.getBlock().setType(Material.AIR);
                e.getBlock().getState().removeMetadata("deathSign", Foxtrot.getInstance());
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        SpawnTagHandler.removeTag(event.getEntity());

        Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(event.getEntity());

        if (event.getEntity().getKiller() != null) {
            Team killerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(event.getEntity().getKiller());

            if (killerTeam != null) {
                TeamActionTracker.logActionAsync(killerTeam, TeamActionType.KILLS, "Member Kill: " + event.getEntity().getName() + " slain by " + event.getEntity().getKiller().getName() + " [X: " + event.getEntity().getLocation().getBlockX() + ", Y: " + event.getEntity().getLocation().getBlockY() + ", Z: " + event.getEntity().getLocation().getBlockZ() + "]");
            }
        }

        if (playerTeam != null) {
            playerTeam.playerDeath(event.getEntity().getName(), Foxtrot.getInstance().getServerHandler().getDTRLoss(event.getEntity()));
        }

        // Add deaths to armor
        String deathMsg = ChatColor.YELLOW + event.getEntity().getName() + ChatColor.RESET + " " + (event.getEntity().getKiller() != null ? "killed by " + ChatColor.YELLOW + event.getEntity().getKiller().getName() : "died") + " " + ChatColor.GOLD + InventoryUtils.DEATH_TIME_FORMAT.format(new Date());

        for (ItemStack armor : event.getEntity().getInventory().getArmorContents()) {
            if (armor != null && armor.getType() != Material.AIR) {
                InventoryUtils.addDeath(armor, deathMsg);
            }
        }

        if (event.getEntity().getKiller() != null) {
            Player killer = event.getEntity().getKiller();
            ItemStack hand = killer.getItemInHand();

            // Add kills to sword lore
            if (hand.getType().name().contains("SWORD") || hand.getType() == Material.BOW) {
                InventoryUtils.addKill(hand, killer.getDisplayName() + ChatColor.YELLOW + " " + (hand.getType() == Material.BOW ? "shot" : "killed") + " " + event.getEntity().getDisplayName());
            }

            // Add player head to item drops
            if (killer.hasPermission("foxtrot.skulldrop")) {
                ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
                SkullMeta meta = (SkullMeta) skull.getItemMeta();

                meta.setOwner(event.getEntity().getName());
                meta.setDisplayName(ChatColor.YELLOW + "Head of " + event.getEntity().getName());
                meta.setLore(Arrays.asList("", deathMsg));
                skull.setItemMeta(meta);
                event.getDrops().add(skull);
            }

//            if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
//                for (ItemStack it : event.getEntity().getKiller().getInventory().addItem(Foxtrot.getInstance().getServerHandler().generateDeathSign(event.getEntity().getName(), event.getEntity().getKiller().getName())).values()) {
//                    event.getDrops().add(it);
//                }
//            }
        }

        event.getEntity().getWorld().strikeLightningEffect(event.getEntity().getLocation());

        // Transfer money
        double bal = Basic.get().getEconomyManager().getBalance(event.getEntity().getName());
        Basic.get().getEconomyManager().withdrawPlayer(event.getEntity().getName(), bal);

        // Only tell player they earned money if they actually earned something
        if (event.getEntity().getKiller() != null && !Double.isNaN(bal) && bal > 0) {
            Basic.get().getEconomyManager().depositPlayer(event.getEntity().getKiller().getName(), bal);
            event.getEntity().getKiller().sendMessage(ChatColor.GOLD + "You earned " + ChatColor.BOLD + "$" + bal + ChatColor.GOLD + " for killing " + event.getEntity().getDisplayName() + ChatColor.GOLD + "!");
        }
    }

    private void processTerritoryInfo(PlayerMoveEvent event) {
        Team ownerTo = LandBoard.getInstance().getTeam(event.getTo());

        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getUniqueId()) && ownerTo != null && ownerTo.isMember(event.getPlayer().getUniqueId())) {
            Foxtrot.getInstance().getPvPTimerMap().removeTimer(event.getPlayer().getUniqueId());
        }

        Team ownerFrom = LandBoard.getInstance().getTeam(event.getFrom());
        ServerHandler sm = Foxtrot.getInstance().getServerHandler();
        RegionData from = sm.getRegion(ownerFrom, event.getFrom());
        RegionData to = sm.getRegion(ownerTo, event.getTo());

        if (!from.equals(to)) {
            if (!to.getRegionType().getMoveHandler().handleMove(event)) {
                return;
            }

            boolean fromReduceDeathban = from.getData() != null && (from.getData().hasDTRBitmask(DTRBitmask.FIVE_MINUTE_DEATHBAN) || from.getData().hasDTRBitmask(DTRBitmask.FIFTEEN_MINUTE_DEATHBAN) || from.getData().hasDTRBitmask(DTRBitmask.SAFE_ZONE));
            boolean toReduceDeathban = to.getData() != null && (to.getData().hasDTRBitmask(DTRBitmask.FIVE_MINUTE_DEATHBAN) || to.getData().hasDTRBitmask(DTRBitmask.FIFTEEN_MINUTE_DEATHBAN) || to.getData().hasDTRBitmask(DTRBitmask.SAFE_ZONE));

            if (fromReduceDeathban && from.getData() != null) {
                KOTH fromLinkedKOTH = Foxtrot.getInstance().getKOTHHandler().getKOTH(from.getData().getName());

                if (fromLinkedKOTH != null && !fromLinkedKOTH.isActive()) {
                    fromReduceDeathban = false;
                }
            }

            if (toReduceDeathban && to.getData() != null) {
                KOTH toLinkedKOTH = Foxtrot.getInstance().getKOTHHandler().getKOTH(to.getData().getName());

                if (toLinkedKOTH != null && !toLinkedKOTH.isActive()) {
                    toReduceDeathban = false;
                }
            }

            // create leaving message
            FancyMessage nowLeaving = new FancyMessage("Now leaving: ").color(ChatColor.YELLOW)
                    .then(from.getName(event.getPlayer())).color(ChatColor.YELLOW);

            if (ownerFrom != null) {
                nowLeaving.command("/t i " + ownerFrom.getName()).tooltip(ChatColor.GREEN + "View team info");
            }

            nowLeaving.then(" (").color(ChatColor.YELLOW).then(fromReduceDeathban ? "Non-Deathban" : "Deathban").color(fromReduceDeathban ? ChatColor.GREEN : ChatColor.RED).then(")").color(ChatColor.YELLOW);

            // create entering message
            FancyMessage nowEntering = new FancyMessage("Now entering: ").color(ChatColor.YELLOW)
                    .then(to.getName(event.getPlayer())).color(ChatColor.YELLOW);

            if (ownerTo != null) {
                nowEntering.command("/t i " + ownerTo.getName()).tooltip(ChatColor.GREEN + "View team info");
            }

            nowEntering.then(" (").color(ChatColor.YELLOW).then(toReduceDeathban ? "Non-Deathban" : "Deathban").color(toReduceDeathban ? ChatColor.GREEN : ChatColor.RED).then(")").color(ChatColor.YELLOW);

            // send both
            nowLeaving.send(event.getPlayer());
            nowEntering.send(event.getPlayer());
        }
    }

}