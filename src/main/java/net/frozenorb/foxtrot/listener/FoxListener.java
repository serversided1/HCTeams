package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.commands.ToggleDonorOnlyCommand;
import net.frozenorb.foxtrot.command.commands.team.TeamClaimCommand;
import net.frozenorb.foxtrot.command.commands.team.TeamSubclaimCommand;
import net.frozenorb.foxtrot.factionactiontracker.FactionActionTracker;
import net.frozenorb.foxtrot.jedis.persist.PvPTimerMap;
import net.frozenorb.foxtrot.nametag.NametagManager;
import net.frozenorb.foxtrot.server.RegionData;
import net.frozenorb.foxtrot.server.RegionType;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.bitmask.DTRBitmaskType;
import net.frozenorb.foxtrot.team.claims.Subclaim;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SuppressWarnings("deprecation")
public class FoxListener implements Listener {

    public static final PotionEffectType[] DEBUFFS = { PotionEffectType.POISON,
            PotionEffectType.SLOW, PotionEffectType.WEAKNESS,
            PotionEffectType.HARM, PotionEffectType.WITHER };

    public static final Material[] NO_INTERACT_WITH = { Material.LAVA_BUCKET,
            Material.WATER_BUCKET, Material.BUCKET };

    public static final Material[] NO_INTERACT_WITH_SPAWN = {
            Material.SNOW_BALL, Material.ENDER_PEARL, Material.EGG,
            Material.FISHING_ROD };

    public static final Material[] NO_INTERACT = { Material.FENCE_GATE,
            Material.FURNACE, Material.BURNING_FURNACE, Material.BREWING_STAND, Material.CHEST,
            Material.HOPPER, Material.DISPENSER, Material.WOODEN_DOOR,
            Material.STONE_BUTTON, Material.WOOD_BUTTON,
            Material.TRAPPED_CHEST, Material.TRAP_DOOR, Material.LEVER,
            Material.DROPPER, Material.ENCHANTMENT_TABLE, Material.WORKBENCH, Material.BED_BLOCK, Material.ANVIL };

    public static final Material[] NO_INTERACT_IN_SPAWN = { Material.FENCE_GATE,
            Material.FURNACE, Material.BURNING_FURNACE, Material.BREWING_STAND, Material.CHEST,
            Material.HOPPER, Material.DISPENSER, Material.WOODEN_DOOR,
            Material.STONE_BUTTON, Material.WOOD_BUTTON,
            Material.TRAPPED_CHEST, Material.TRAP_DOOR, Material.LEVER,
            Material.DROPPER, Material.ENCHANTMENT_TABLE, Material.BED_BLOCK, Material.ANVIL };

    public static final Material[] NON_TRANSPARENT_ATTACK_DISABLING_BLOCKS = {
            Material.GLASS, Material.WOOD_DOOR, Material.IRON_DOOR,
            Material.FENCE_GATE };

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Location fromLoc = event.getFrom();
        Location toLoc = event.getTo();
        double toX = toLoc.getX();
        double toZ = toLoc.getZ();
        double toY = toLoc.getY();
        double fromX = fromLoc.getX();
        double fromZ = fromLoc.getZ();
        double fromY = fromLoc.getY();

        if (fromX != toX || fromZ != toZ || fromY != toY) {
            if (ServerHandler.getTasks().containsKey(event.getPlayer().getName())) {
                if (fromLoc.distance(toLoc) > 0.1 && (fromX != toX || fromZ != toZ || fromY != toY)) {
                    Bukkit.getScheduler().cancelTask(ServerHandler.getTasks().get(event.getPlayer().getName()));
                    ServerHandler.getTasks().remove(event.getPlayer().getName());
                    event.getPlayer().sendMessage(ChatColor.YELLOW + "§lLOGOUT §c§lCANCELLED!");
                }
            }

            Team ownerTo = FoxtrotPlugin.getInstance().getTeamHandler().getOwner(event.getTo());

            if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getName()) && ownerTo != null && ownerTo.isMember(event.getPlayer().getName())) {
                FoxtrotPlugin.getInstance().getPvPTimerMap().removeTimer(event.getPlayer().getName());
            }

            Team ownerFrom = FoxtrotPlugin.getInstance().getTeamHandler().getOwner(event.getFrom());
            ServerHandler sm = FoxtrotPlugin.getInstance().getServerHandler();
            RegionData from = sm.getRegion(ownerFrom, fromLoc);
            RegionData to = sm.getRegion(ownerTo, toLoc);

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

                String fromStr = "§eNow leaving: " + from.getName(event.getPlayer()) + (fromReduceDeathban ? "§e(§aNon-Deathban§e)" : "§e(§cDeathban§e)");
                String toStr = "§eNow entering: " + to.getName(event.getPlayer()) + (toReduceDeathban ? "§e(§aNon-Deathban§e)" : "§e(§cDeathban§e)");

                event.getPlayer().sendMessage(new String[] { fromStr, toStr });
            }
        }
    }

    @SuppressWarnings("unchecked")
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        event.getPlayer().getInventory().remove(TeamSubclaimCommand.SELECTION_WAND);
        event.getPlayer().getInventory().remove(TeamClaimCommand.SELECTION_WAND);

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

    @EventHandler(priority=EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();

            if (ServerHandler.getTasks().containsKey(p.getName())) {
                Bukkit.getScheduler().cancelTask(ServerHandler.getTasks().get(p.getName()));
                ServerHandler.getTasks().remove(p.getName());
                p.sendMessage(ChatColor.YELLOW + "§lLOGOUT §c§lCANCELLED!");
            }
        }
    }

    @EventHandler(priority=EventPriority.NORMAL)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (ToggleDonorOnlyCommand.donorOnly && !event.getPlayer().hasPermission("foxtrot.donator")) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "The server is full.");
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerLogin2(PlayerLoginEvent event) {
        if (event.getResult() == PlayerLoginEvent.Result.KICK_FULL && event.getPlayer().hasPermission("foxtrot.joinfull")) {
            event.setResult(PlayerLoginEvent.Result.ALLOWED);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onProjetileInteract(PlayerInteractEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player p = event.getPlayer();

        if (event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (event.getItem().getType() == Material.POTION) {
                ItemStack i = event.getItem();

                // We can't run Potion.fromItemStack on a water bottle.
                if (i.getDurability() == (short) 0) {
                    return;
                }

                Potion pot = Potion.fromItemStack(i);

                if (pot.isSplash() && Arrays.asList(DEBUFFS).contains(pot.getType().getEffectType())) {
                    if (!FoxtrotPlugin.getInstance().getServerHandler().isPreEOTW() && FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(p.getName())) {
                        p.sendMessage(ChatColor.RED + "You cannot do this while your PVP Timer is active!");
                        p.sendMessage(ChatColor.RED + "Type '" + ChatColor.YELLOW + "/pvp enable" + ChatColor.RED + "' to remove your timer.");
                        event.setCancelled(true);
                        return;
                    }

                    if (!FoxtrotPlugin.getInstance().getServerHandler().isPreEOTW() && DTRBitmaskType.SAFE_ZONE.appliesAt(p.getLocation())) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ChatColor.RED + "You cannot launch debuffs from inside spawn!");
                        event.getPlayer().updateInventory();
                    }
                }
            }
        }

        if (event.getClickedBlock() != null) {
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

            Team team = FoxtrotPlugin.getInstance().getTeamHandler().getOwner(event.getClickedBlock().getLocation());

            if (DTRBitmaskType.SAFE_ZONE.appliesAt(event.getClickedBlock().getLocation())) {
                if (Arrays.asList(FoxListener.NO_INTERACT_WITH_SPAWN).contains(event.getMaterial()) || Arrays.asList(FoxListener.NO_INTERACT_IN_SPAWN).contains(event.getClickedBlock().getType())) {
                    event.setCancelled(true);
                }
            }

            if (team != null && !team.isMember(event.getPlayer())) {
                if (Arrays.asList(FoxListener.NO_INTERACT).contains(event.getClickedBlock().getType()) || Arrays.asList(FoxListener.NO_INTERACT_WITH).contains(event.getMaterial())) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot do this in " + ChatColor.RED + team.getFriendlyName() + ChatColor.YELLOW + "'s territory.");

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
                    return;
                }
            } else {
                if (team != null && !team.isCaptain(event.getPlayer().getName()) && !team.isOwner(event.getPlayer().getName())) {
                    Subclaim subclaim = team.getSubclaim(event.getClickedBlock().getLocation());

                    if (subclaim != null && !subclaim.isMember(event.getPlayer().getName())) {
                        if (Arrays.asList(FoxListener.NO_INTERACT).contains(event.getClickedBlock().getType()) || Arrays.asList(FoxListener.NO_INTERACT_WITH).contains(event.getMaterial())) {
                            event.setCancelled(true);
                            event.getPlayer().sendMessage(ChatColor.YELLOW + "You do not have access to the subclaim " + ChatColor.GREEN + subclaim.getName() + ChatColor.YELLOW  + "!");
                            return;
                        }
                    }
                }
            }
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().getItemInHand().getTypeId() == 333) {
            Block target = event.getClickedBlock();
            if (target.getTypeId() != 8 && target.getTypeId() != 9) {
                event.getPlayer().sendMessage(ChatColor.RED + "You can only place a boat on water!");
                event.setCancelled(true);
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

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerDeath(final PlayerDeathEvent e) {
        Player player = e.getEntity();
        Date now = new Date();

        SpawnTagHandler.removeTag(e.getEntity());

        Team t = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(e.getEntity().getName());

        if (e.getEntity().getKiller() != null) {
            Team killerTeam = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(e.getEntity().getKiller().getName());

            if (killerTeam != null) {
                FactionActionTracker.logAction(killerTeam, "actions", "Member Kill: " + e.getEntity().getName() + " slain by " + e.getEntity().getKiller().getName());
            }
        }

        if (t != null) {
            t.playerDeath(e.getEntity().getName(), FoxtrotPlugin.getInstance().getServerHandler().getDTRLossAt(e.getEntity().getLocation()));
        }

        // Add deaths to armor
        String deathMsg = ChatColor.YELLOW + player.getName() + ChatColor.RESET + " " + (player.getKiller() != null ? "killed by " + ChatColor.YELLOW + player.getKiller().getName() : "died") + " " + InvUtils.DEATH_TIME_FORMAT.format(now);

        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor != null && armor.getType() != Material.AIR) {
                InvUtils.addDeath(armor, deathMsg);
            }
        }

        if (e.getEntity().getKiller() != null) {
            Player killer = e.getEntity().getKiller();
            ItemStack sword = killer.getItemInHand();

            // Add kills to sword lore
            if (sword.getType().name().contains("SWORD")) {
                int killsIndex = 1;
                int[] lastKills = { 3, 4, 5 };

                int currentKills = 1;

                ItemMeta meta = sword.getItemMeta();
                List<String> lore = new ArrayList<String>();

                if (meta.hasLore()) {
                    lore = meta.getLore();

                    boolean hasForgedMeta = false;
                    for (String s : meta.getLore()) {
                        if (s.toLowerCase().contains("forged"))
                            hasForgedMeta = true;
                    }

                    if (hasForgedMeta) {
                        killsIndex++;

                        for (int i = 0; i < lastKills.length; i++) {
                            lastKills[i] = lastKills[i] + 1;
                        }
                    }

                    if (meta.getLore().size() > killsIndex) {
                        String killStr = lore.get(killsIndex);

                        currentKills += Integer.parseInt(ChatColor.stripColor(killStr.split(":")[1]).trim());
                    }

                    for (int j : lastKills) {
                        if (j == lastKills[lastKills.length - 1]) {
                            continue;
                        }
                        if (lore.size() > j) {
                            String atJ = meta.getLore().get(j);

                            if (lore.size() <= j + 1) {
                                lore.add(null);
                            }

                            lore.set(j + 1, atJ);
                        }

                    }
                }

                if (lore.size() <= killsIndex) {
                    for (int i = lore.size(); i <= killsIndex + 1; i++) {
                        lore.add("");
                    }
                }
                lore.set(killsIndex, "§6§lKills:§f " + currentKills);

                int firsKill = lastKills[0];

                if (lore.size() <= firsKill) {
                    for (int i = lore.size(); i <= firsKill + 1; i++) {
                        lore.add("");
                    }
                }
                lore.set(firsKill, killer.getDisplayName() + "§e slayed " + e.getEntity().getDisplayName());
                meta.setLore(lore);
                sword.setItemMeta(meta);
            }

            FoxtrotPlugin.getInstance().getKillsMap().setKills(e.getEntity().getKiller().getName(), 1 + FoxtrotPlugin.getInstance().getKillsMap().getKills(e.getEntity().getKiller().getName()));

            // Add player head to item drops
            if (killer.hasPermission("foxtrot.skulldrop")) {
                ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
                SkullMeta meta = (SkullMeta) skull.getItemMeta();

                meta.setOwner(player.getName());
                meta.setDisplayName(ChatColor.YELLOW + "Head of " + player.getName());
                meta.setLore(Arrays.asList("", deathMsg));
                skull.setItemMeta(meta);
                e.getDrops().add(skull);
            }

            for (ItemStack it : e.getEntity().getKiller().getInventory().addItem(FoxtrotPlugin.getInstance().getServerHandler().generateDeathSign(e.getEntity().getName(), e.getEntity().getKiller().getName())).values()) {
                e.getDrops().add(it);
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
                online.playSound(online.getLocation(), Sound.AMBIENCE_THUNDER, 1F, 1F);
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