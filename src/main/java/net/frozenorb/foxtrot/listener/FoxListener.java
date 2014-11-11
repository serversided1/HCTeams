package net.frozenorb.foxtrot.listener;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import net.frozenorb.Utilities.DataSystem.Regioning.RegionManager;
import net.frozenorb.Utilities.Serialization.Serializers.ItemStackSerializer;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.diamond.MountainHandler;
import net.frozenorb.foxtrot.factionactiontracker.FactionActionTracker;
import net.frozenorb.foxtrot.jedis.JedisCommand;
import net.frozenorb.foxtrot.jedis.persist.PvPTimerMap;
import net.frozenorb.foxtrot.nametag.NametagManager;
import net.frozenorb.foxtrot.server.*;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.foxtrot.util.InvUtils;
import net.frozenorb.mBasic.Basic;
import net.frozenorb.mShared.Shared;
import net.frozenorb.mShared.Utilities.Utilities;
import net.minecraft.server.v1_7_R3.EntityLightning;
import net.minecraft.server.v1_7_R3.PacketPlayOutSpawnEntityWeather;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

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

    public static Location lastDamageLocation;

    @EventHandler
    public void playerhit(EntityDamageByEntityEvent e) {
        if ((e.getEntity() instanceof Player && e.getDamager() instanceof Player)) {

            if (e.isCancelled())
                return;

            Player p = (Player) e.getEntity();
            Player pl = (Player) e.getDamager();


            if (FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(p.getName()) == null)
                return;

            if (FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(pl.getName()) == null)
                return;

            Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(p.getName());

            if (FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(pl.getName()) == team) {
                e.setCancelled(true);
            }
        }
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Projectile) {
            if (e.isCancelled()) {
                return;
            }

            if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(((Player) e.getEntity()).getName())) {
                e.setCancelled(true);
            }

            Player p = (Player) e.getEntity();

            if (!(((Projectile) e.getDamager()).getShooter() instanceof Player)) {
                return;
            }

            Player pl = ((Player) ((Projectile) e.getDamager()).getShooter());

            if (FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(p.getName()) == null)
                return;

            if (FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(pl.getName()) == null)
                return;

            Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(p.getName());

            if (FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(pl.getName()) == team) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onVerticalBlockPlaceGlitch(BlockPlaceEvent event) {
        if (FoxtrotPlugin.getInstance().getTeamHandler().isTaken(event.getBlock().getLocation())) {
            if (event.isCancelled()) {
                event.getPlayer().teleport(event.getPlayer().getLocation());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onVerticalBlockBreakGlitch(final BlockBreakEvent e) {
        if (e.isCancelled() && e.getBlock().getType().isSolid() && e.getPlayer().getName().equalsIgnoreCase("this_is_a_comment_technically")) {
            final Location tpTo = LocationTickStore.getInstance().recallOldestLocation(e.getPlayer().getName());

            Bukkit.getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), new Runnable() {
                @Override
                public void run() {
                    e.getPlayer().teleport(tpTo);
                }
            }, 1);
        }
    }

    @EventHandler
    public void onPlayerDeathWebsite(PlayerDeathEvent event) {
        BasicDBObject playerDeath = new BasicDBObject();

        if (event.getEntity().getKiller() != null) {
            playerDeath.append("soups", -1);
            playerDeath.append("healthLeft", (int) event.getEntity().getKiller().getHealth());
            playerDeath.append("killer", event.getEntity().getKiller().getName());
            playerDeath.append("killerUUID", event.getEntity().getKiller().getUniqueId().toString().replace("-", ""));
            playerDeath.append("killerHunger", event.getEntity().getKiller().getFoodLevel());

            if (event.getEntity().getKiller().getItemInHand() != null) {
                playerDeath.append("item", Shared.get().getUtilities().getDatabaseRepresentation(event.getEntity().getKiller().getItemInHand()));
            } else {
                playerDeath.append("item", "NONE");
            }
        } else {
            try{
                playerDeath.append("reason", event.getEntity().getLastDamageCause().getCause().toString());
            } catch (NullPointerException localNullPointerException) {

            }
        }

        playerDeath.append("playerHunger", event.getEntity().getFoodLevel());

        BasicDBObject playerInv = new BasicDBObject();
        BasicDBObject armor = new BasicDBObject();

        armor.put("helmet", new ItemStackSerializer().serialize(event.getEntity().getInventory().getHelmet()));
        armor.put("chestplate", new ItemStackSerializer().serialize(event.getEntity().getInventory().getChestplate()));
        armor.put("leggings", new ItemStackSerializer().serialize(event.getEntity().getInventory().getLeggings()));
        armor.put("boots", new ItemStackSerializer().serialize(event.getEntity().getInventory().getBoots()));

        BasicDBList contents = new BasicDBList();

        for (int i = 0; i < 9; i++) {
            if (event.getEntity().getInventory().getItem(i) != null) {
                contents.add(new ItemStackSerializer().serialize(event.getEntity().getInventory().getItem(i)));
            } else {
                contents.add(new ItemStackSerializer().serialize(new ItemStack(Material.AIR)));
            }
        }

        playerInv.append("armor", armor);
        playerInv.append("items", contents);

        playerDeath.append("playerInventory", playerInv);

        if (event.getEntity().getKiller() != null) {
            BasicDBObject killerInventory = new BasicDBObject();
            BasicDBObject killerArmor = new BasicDBObject();

            armor.put("helmet", new ItemStackSerializer().serialize(event.getEntity().getKiller().getInventory().getHelmet()));
            armor.put("chestplate", new ItemStackSerializer().serialize(event.getEntity().getKiller().getInventory().getChestplate()));
            armor.put("leggings", new ItemStackSerializer().serialize(event.getEntity().getKiller().getInventory().getLeggings()));
            armor.put("boots", new ItemStackSerializer().serialize(event.getEntity().getKiller().getInventory().getBoots()));

            BasicDBList killerContents = new BasicDBList();

            for (int i = 0; i < 9; i++) {
                if (event.getEntity().getKiller().getInventory().getItem(i) != null) {
                    killerContents.add(new ItemStackSerializer().serialize(event.getEntity().getKiller().getInventory().getItem(i)));
                } else {
                    killerContents.add(new ItemStackSerializer().serialize(new ItemStack(Material.AIR)));
                }
            }

            killerInventory.append("armor", killerArmor);
            killerInventory.append("items", killerContents);
            playerDeath.append("killerInventory", killerInventory);
        }

        playerDeath.append("ip", event.getEntity().getAddress().toString().split(":")[0].replace("/", ""));
        playerDeath.append("uuid", event.getEntity().getUniqueId().toString().replace("-", ""));
        playerDeath.append("player", event.getEntity().getName());
        playerDeath.append("type", "death");
        playerDeath.append("when", Utilities.getInstance().getTime(System.currentTimeMillis()));

        new BukkitRunnable() {

            public void run() {
                FoxtrotPlugin.getInstance().getMongoPool().getDB("hcteams").getCollection("deaths").insert(playerDeath);
            }

        }.runTaskAsynchronously(FoxtrotPlugin.getInstance());
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().clear();
    }

    @EventHandler
    public void onFireBurn(BlockBurnEvent e) {
        if (FoxtrotPlugin.getInstance().getServerHandler().isWarzone(e.getBlock().getLocation())) {
            e.setCancelled(true);
            return;
        }
        if (FoxtrotPlugin.getInstance().getServerHandler().isClaimedAndRaidable(e.getBlock().getLocation())) {
            return;
        }

        if (FoxtrotPlugin.getInstance().getTeamHandler().isTaken(e.getBlock().getLocation())) {
            e.setCancelled(true);
        }
    }

    // No fire spread
    @EventHandler(priority=EventPriority.HIGH)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getCause() == IgniteCause.SPREAD) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent e) {
        Location fromLoc = e.getFrom();
        Location toLoc = e.getTo();
        double toX = toLoc.getX();
        double toZ = toLoc.getZ();
        double toY = toLoc.getY();
        double fromX = fromLoc.getX();
        double fromZ = fromLoc.getZ();
        double fromY = fromLoc.getY();

        if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(e.getPlayer().getName()) && LandBoard.getInstance().getTeamAt(e.getPlayer().getLocation()) != null && LandBoard.getInstance().getTeamAt(e.getPlayer().getLocation()).isMember(e.getPlayer().getName())) {
            FoxtrotPlugin.getInstance().getPvPTimerMap().removeTimer(e.getPlayer().getName());
        }

        if (fromX != toX || fromZ != toZ || fromY != toY) {

            if (ServerHandler.getTasks().containsKey(e.getPlayer().getName())) {
                if (fromLoc.distance(toLoc) > 0.1 && (fromX != toX || fromZ != toZ || fromY != toY)) {
                    Bukkit.getScheduler().cancelTask(ServerHandler.getTasks().get(e.getPlayer().getName()));
                    ServerHandler.getTasks().remove(e.getPlayer().getName());
                    e.getPlayer().sendMessage(ChatColor.YELLOW + "§lLOGOUT §c§lCANCELLED!");
                }
            }

            ServerHandler sm = FoxtrotPlugin.getInstance().getServerHandler();

            RegionData<?> from = sm.getRegion(fromLoc, e.getPlayer());
            RegionData<?> to = sm.getRegion(toLoc, e.getPlayer());

            if (!from.equals(to)) {
                boolean cont = to.getRegion().getMoveHandler().handleMove(e);

                if (!cont) {
                    return;
                }

                if (e.getPlayer().getGameMode() != GameMode.CREATIVE && FoxtrotPlugin.getInstance().getServerHandler().isEndSpawn(toLoc)) {
                    // Using e.setTo(e.getFrom()) allows them to glitch through.
                    e.setCancelled(true);
                }

                // PVP Timer
                if (from.getRegion() == Region.SPAWN) {
                    if (FoxtrotPlugin.getInstance().getPvPTimerMap().getTimer(e.getPlayer().getName()) == PvPTimerMap.PENDING_USE) {
                        FoxtrotPlugin.getInstance().getPvPTimerMap().createTimer(e.getPlayer().getName(), 30 * 60);
                    }
                }

                String fromStr = "§eNow leaving: " + from.getName(e.getPlayer()) + (from.getRegion().isReducedDeathban() ? "§e(§aNon-Deathban§e)" : "§e(§cDeathban§e)");
                String toStr = "§eNow entering: " + to.getName(e.getPlayer()) + (to.getRegion().isReducedDeathban() ? "§e(§aNon-Deathban§e)" : "§e(§cDeathban§e)");

                e.getPlayer().sendMessage(new String[] { fromStr, toStr });
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void blockExplosion(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof Wither) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEnderchestOpen(InventoryOpenEvent e) {
        if (e.getInventory().getType() == InventoryType.ENDER_CHEST) {
            e.setCancelled(true);
        }
    }

    @SuppressWarnings("unchecked")
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        event.getPlayer().getInventory().remove(net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands.Subclaim.SELECTION_WAND);
        event.getPlayer().getInventory().remove(net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands.Claim.SELECTION_WAND);

        event.setQuitMessage(null);
        FoxtrotPlugin.getInstance().getPlaytimeMap().playerQuit(event.getPlayer().getName());

        NametagManager.getTeamMap().remove(event.getPlayer().getName());

        // Remove scoreboard
        FoxtrotPlugin.getInstance().getScoreboardHandler().remove(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerKickEvent e) {
        e.setLeaveMessage(null);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        if (!FoxtrotPlugin.getInstance().getServerHandler().isEOTW() && FoxtrotPlugin.getInstance().getServerHandler().isGlobalSpawn(e.getEntity().getLocation()) && e.getFoodLevel() < ((Player) e.getEntity()).getFoodLevel()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        ItemStack oldSlot = event.getPlayer().getInventory().getItem(event.getPreviousSlot());

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE && oldSlot != null && oldSlot.getType() == Material.REDSTONE_BLOCK) {
            for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                new BukkitRunnable() {

                    public void run() {
                        NametagManager.reloadPlayer(player, event.getPlayer());
                    }

                }.runTaskLater(FoxtrotPlugin.getInstance(), 2L);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();

        NametagManager.initPlayer(event.getPlayer());
        NametagManager.sendTeamsToPlayer(event.getPlayer());
        NametagManager.reloadPlayer(event.getPlayer());

        event.setJoinMessage(null);

        new BukkitRunnable() {

            public void run() {
                FoxtrotPlugin.getInstance().runJedisCommand(new JedisCommand<Object>() {

                    @Override
                    public Object execute(Jedis jedis) {
                        jedis.hset("ProperPlayerNames", event.getPlayer().getName().toLowerCase(), event.getPlayer().getName());
                        return (null);
                    }

                });
            }

        }.runTaskAsynchronously(FoxtrotPlugin.getInstance());

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

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        // Give back PvP protection when respawning.
        FoxtrotPlugin.getInstance().getPvPTimerMap().pendingTimer(e.getPlayer().getName());

        e.setRespawnLocation(FoxtrotPlugin.getInstance().getServerHandler().getSpawnLocation());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpawnTagMonitor(EntityDamageByEntityEvent e) {
        Player killer = null;

        if (e.getEntity() instanceof Player) {

            if (e.getDamager() instanceof Player) {
                killer = (Player) e.getDamager();
            } else if (e.getDamager() instanceof Projectile) {
                if (((Projectile) e.getDamager()).getShooter() instanceof Player) {
                    killer = (Player) ((Projectile) e.getDamager()).getShooter();
                }
            }

            if (killer != null && killer != e.getEntity()) {
                SpawnTagHandler.applyTag(killer);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSpawnProtCheck(EntityDamageByEntityEvent e) {
        Player killer = null;

        if (e.getDamager() instanceof Player) {
            killer = (Player) e.getDamager();
        } else if (e.getDamager() instanceof Projectile) {
            if (((Projectile) e.getDamager()).getShooter() instanceof Player) {
                killer = (Player) ((Projectile) e.getDamager()).getShooter();
            }
        }

        if (e.getEntity() instanceof Player && killer != null) {
            Player vic = (Player) e.getEntity();

            if (!FoxtrotPlugin.getInstance().getServerHandler().isEOTW() && FoxtrotPlugin.getInstance().getServerHandler().isGlobalSpawn(vic.getLocation())) {
                e.setCancelled(true);
                return;
            }

            if (!FoxtrotPlugin.getInstance().getServerHandler().isEOTW() && FoxtrotPlugin.getInstance().getServerHandler().isGlobalSpawn(killer.getLocation())) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (ServerHandler.getTasks().containsKey(p.getName())) {
                Bukkit.getScheduler().cancelTask(ServerHandler.getTasks().get(p.getName()));
                ServerHandler.getTasks().remove(p.getName());
                p.sendMessage(ChatColor.YELLOW + "§lLOGOUT §c§lCANCELLED!");
            }
            if (!FoxtrotPlugin.getInstance().getServerHandler().isEOTW() && FoxtrotPlugin.getInstance().getServerHandler().isGlobalSpawn(p.getLocation())) {
                e.setCancelled(true);
            }
        } else if (e.getEntity() instanceof Horse) {
            if (!FoxtrotPlugin.getInstance().getServerHandler().isEOTW() && FoxtrotPlugin.getInstance().getServerHandler().isGlobalSpawn(e.getEntity().getLocation())) {
                e.setCancelled(true);
            }
        }

        if (e instanceof EntityDamageByEntityEvent) {
            if (((EntityDamageByEntityEvent) e).getDamager() instanceof Player) {
                Player p = ((Player) ((EntityDamageByEntityEvent) e).getDamager());

                if (e.getEntity() instanceof Player) {
                    Player rec = (Player) e.getEntity();
                    if (!FoxtrotPlugin.getInstance().getServerHandler().isPreEOTW() && FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(p.getName())) {
                        p.sendMessage(ChatColor.RED + "You cannot attack others while you have your PVP Timer. Type '§e/pvptimer remove§c' to remove your timer.");
                        e.setCancelled(true);
                        return;
                    }

                    if (!FoxtrotPlugin.getInstance().getServerHandler().isPreEOTW() && FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(rec.getName())) {
                        p.sendMessage(ChatColor.RED + "That player currently has their PVP Timer!");
                        e.setCancelled(true);
                        return;
                    }
                }
                if (ServerHandler.getTasks().containsKey(p.getName())) {
                    Bukkit.getScheduler().cancelTask(ServerHandler.getTasks().get(p.getName()));
                    ServerHandler.getTasks().remove(p.getName());
                    p.sendMessage(ChatColor.YELLOW + "§lLOGOUT §c§lCANCELLED!");
                }
            }
        }
    }

    @EventHandler
    public void onBlockCombust(BlockBurnEvent e) {
        if (FoxtrotPlugin.getInstance().getServerHandler().isWarzone(e.getBlock().getLocation())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        if (FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride(e.getPlayer())) {
            return;
        }

        Team owner = FoxtrotPlugin.getInstance().getTeamHandler().getOwner(e.getBlockClicked().getRelative(e.getBlockFace()).getLocation());

        if (owner != null && !owner.isMember(e.getPlayer())) {
            e.setCancelled(true);
            e.getBlockClicked().getRelative(e.getBlockFace()).setType(Material.AIR);
            e.setItemStack(new ItemStack(e.getBucket()));
            return;
        }

        if (FoxtrotPlugin.getInstance().getServerHandler().isSpawnBufferZone(e.getBlockClicked().getLocation())) {
            e.setCancelled(true);
            e.getBlockClicked().getRelative(e.getBlockFace()).setType(Material.AIR);
            e.setItemStack(new ItemStack(e.getBucket()));
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        double mult = 1;

        if (event.getEntity().getKiller() != null) {
            Player player = event.getEntity().getKiller();

            if (player.getItemInHand() != null) {
                ItemStack itemStack = player.getItemInHand();

                if (itemStack.containsEnchantment(Enchantment.LOOT_BONUS_MOBS)) {
                    switch (itemStack.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS)) {
                        case 1:
                            mult = 1.2D;
                            break;
                        case 2:
                            mult = 1.4D;
                            break;
                        case 3:
                            mult = 2D;
                            break;
                    }
                }
            }
        }

        event.setDroppedExp((int) Math.ceil(event.getDroppedExp() * mult));
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();

            if (!FoxtrotPlugin.getInstance().getServerHandler().isPreEOTW() && FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(p.getName())) {
                p.sendMessage(ChatColor.RED + "You cannot do this while your PVP Timer is active!");
                p.sendMessage(ChatColor.RED + "Type '" + ChatColor.YELLOW + "/pvp enable" + ChatColor.RED + "' to remove your timer.");
                e.setCancelled(true);
            }
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

                    if (!FoxtrotPlugin.getInstance().getServerHandler().isPreEOTW() && FoxtrotPlugin.getInstance().getServerHandler().isGlobalSpawn(p.getLocation())) {
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

            if (FoxtrotPlugin.getInstance().getServerHandler().isClaimedAndRaidable(event.getClickedBlock().getLocation()) || FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
                return;
            }

            Team team = FoxtrotPlugin.getInstance().getTeamHandler().getOwner(event.getClickedBlock().getLocation());

            if (FoxtrotPlugin.getInstance().getServerHandler().isGlobalSpawn(event.getClickedBlock().getLocation())) {
                if (Arrays.asList(FoxListener.NO_INTERACT_WITH_SPAWN).contains(event.getMaterial()) || Arrays.asList(FoxListener.NO_INTERACT_IN_SPAWN).contains(event.getClickedBlock().getType()) || Arrays.asList(FoxListener.NO_INTERACT_WITH).contains(event.getMaterial())) {
                    event.setCancelled(true);
                    FoxtrotPlugin.getInstance().getServerHandler().disablePlayerAttacking(event.getPlayer(), 1);
                }
            }

            if (team != null && !team.isMember(event.getPlayer())) {
                if (Arrays.asList(FoxListener.NO_INTERACT).contains(event.getClickedBlock().getType()) || Arrays.asList(FoxListener.NO_INTERACT_WITH).contains(event.getMaterial())) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot do this in " + ChatColor.RED + team.getFriendlyName() + ChatColor.YELLOW + "'s territory.");
                    FoxtrotPlugin.getInstance().getServerHandler().disablePlayerAttacking(event.getPlayer(), 1);
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
                    Subclaim subClaim = team.getSubclaim(event.getClickedBlock().getLocation());

                    if (subClaim != null && !subClaim.isMember(event.getPlayer().getName())) {
                        if (Arrays.asList(FoxListener.NO_INTERACT).contains(event.getClickedBlock().getType()) || Arrays.asList(FoxListener.NO_INTERACT_WITH).contains(event.getMaterial())) {
                            event.setCancelled(true);
                            event.getPlayer().sendMessage(ChatColor.YELLOW + "You do not have access to the subclaim " + subClaim.getFriendlyColoredName() + "§e!");
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

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (!FoxtrotPlugin.getInstance().getServerHandler().isEOTW() && FoxtrotPlugin.getInstance().getServerHandler().isGlobalSpawn(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
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

                if (FoxtrotPlugin.getInstance().getServerHandler().isGlobalSpawn(event.getClickedBlock().getLocation())) {
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
                if (hand.hasItemMeta() && hand.getItemMeta().hasDisplayName()) {
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

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
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
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player && !event.isCancelled()) {
            lastDamageLocation = event.getEntity().getLocation();
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (lastDamageLocation != null && event.getItem() != null && event.getItem().getType() == Material.EMERALD && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            event.getPlayer().teleport(lastDamageLocation);
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

        for (World world : Bukkit.getWorlds()) {
            EntityLightning entity = new EntityLightning(((CraftWorld) world).getHandle(), loc.getX(), loc.getY(), loc.getZ(), true, false);
            PacketPlayOutSpawnEntityWeather packet = new PacketPlayOutSpawnEntityWeather(entity);

            for (Player online : world.getPlayers()) {
                if (online.equals(player)) {
                    continue;
                }

                if (FoxtrotPlugin.getInstance().getToggleLightningMap().isLightningToggled(online.getName())) {
                    online.playSound(online.getLocation(), Sound.AMBIENCE_THUNDER, 1F, 1F);
                    ((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
                }
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

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().toLowerCase().startsWith("/kill") || event.getMessage().toLowerCase().startsWith("/slay") || event.getMessage().toLowerCase().startsWith("/bukkit:kill") || event.getMessage().toLowerCase().startsWith("/bukkit:slay")) {
            if (!event.getPlayer().isOp()) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "No permission.");
            }
        }
    }

    @EventHandler
    public void onPlayerMount(VehicleEnterEvent event) {
        if (event.getVehicle() instanceof Horse && event.getEntered() instanceof Player && !((Player)  event.getEntered()).getName().equals(((Horse) event.getVehicle()).getOwner().getName())) {
            event.setCancelled(true);
            ((Player) event.getEntered()).sendMessage(ChatColor.RED + "This is not your horse!");
        }
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent e) {
        if (e.getBlock().getType() == Material.MOB_SPAWNER && e.getBlock().getWorld().getEnvironment() == Environment.NETHER) {
            e.getPlayer().sendMessage(ChatColor.RED + "You cannot break this here!");
            e.setCancelled(true);
            return;
        }

        if (RegionManager.get().isRegionHere(e.getBlock().getLocation(), "diamond_mountain")) {
            if (e.getBlock().getType() == Material.DIAMOND_ORE) {
                FoxtrotPlugin.getInstance().getServer().getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), new Runnable() {

                    @Override
                    public void run() {
                        MountainHandler.diamondMined(e.getBlock());
                    }

                }, 1);
            }
        }
    }

    // Attach the metadata 'Spawner' to any mob spawned by a spawner.
    // ^ NOT USED ^
    // Prevent all squid spawning.
    // Prevent all natural wither skeleton spawning.
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntity().getType() == EntityType.SQUID) {
            event.setCancelled(true);
            return;
        }

        if (event.getSpawnReason() == SpawnReason.NATURAL && event.getEntity().getType() == EntityType.SKELETON && ((Skeleton) event.getEntity()).getSkeletonType() == Skeleton.SkeletonType.WITHER) {
            event.setCancelled(true);
        }

        if (event.getSpawnReason() == SpawnReason.SPAWNER) {
            event.getEntity().setMetadata("Spawner", new FixedMetadataValue(FoxtrotPlugin.getInstance(), true));
        }
    }

    /*
	 * Lose hunger slower
	 * Apparently mEngine doesn't like this
	 */
    @EventHandler
    public void onEntityFood(FoodLevelChangeEvent e) {
        if (e.getFoodLevel() < ((Player) e.getEntity()).getFoodLevel())
            if (FoxtrotPlugin.RANDOM.nextInt(100) > 30)
                e.setCancelled(true);
    }

}