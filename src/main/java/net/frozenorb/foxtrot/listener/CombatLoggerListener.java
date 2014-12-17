package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.nms.FixedVillager;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.bitmask.DTRBitmaskType;
import net.minecraft.server.v1_7_R3.*;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R3.CraftServer;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftHumanEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by macguy8 on 11/3/2014.
 */
public class CombatLoggerListener implements Listener {

    public static final String COMBAT_LOGGER_METADATA = "CombatLogger";

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().hasMetadata(COMBAT_LOGGER_METADATA)) {
            String playerName = event.getEntity().getCustomName().substring(2);

            FoxtrotPlugin.getInstance().getLogger().info(playerName + "'s combat logger at (" + event.getEntity().getLocation().getBlockX() + ", " + event.getEntity().getLocation().getBlockY() + ", " + event.getEntity().getLocation().getBlockZ() + ") died.");

            // Deathban the player
            FoxtrotPlugin.getInstance().getDeathbanMap().deathban(playerName, FoxtrotPlugin.getInstance().getServerHandler().getDeathban(playerName, event.getEntity().getLocation()));
            Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(playerName);

            // Take away DTR.
            if (team != null) {
                team.playerDeath(playerName, FoxtrotPlugin.getInstance().getServerHandler().getDTRLoss(event.getEntity().getLocation()));
            }

            if (event.getEntity().getKiller() != null) {
                // Death message
                FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.RED + playerName + ChatColor.GRAY + " (Combat-Logger)" + ChatColor.YELLOW + " was slain by " + ChatColor.RED + event.getEntity().getKiller().getName() + ChatColor.YELLOW + ".");

                // Drop the player's items.
                for (ItemStack item : (ItemStack[]) event.getEntity().getMetadata(COMBAT_LOGGER_METADATA).get(0).value()) {
                    event.getDrops().add(item);
                }

                // Add the death sign.
                event.getDrops().add(FoxtrotPlugin.getInstance().getServerHandler().generateDeathSign(playerName, event.getEntity().getKiller().getName()));
            }

            Player target = FoxtrotPlugin.getInstance().getServer().getPlayer(playerName);

            if (target == null) {
                //Create an entity to load the player data
                MinecraftServer server = ((CraftServer) FoxtrotPlugin.getInstance().getServer()).getServer();
                EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), getGameProfile(playerName, FoxtrotPlugin.getInstance().getServer().getOfflinePlayer(playerName).getUniqueId()), new PlayerInteractManager(server.getWorldServer(0)));
                target = entity.getBukkitEntity();

                if (target != null) {
                    target.loadData();
                }
            }

            EntityHuman humanTarget = ((CraftHumanEntity) target).getHandle();

            target.getInventory().clear();
            target.getInventory().setArmorContents(null);
            humanTarget.setHealth(0);
            target.saveData();
        }
    }

    public static GameProfile getGameProfile(String name, UUID id) {
        return (new GameProfile(id, name));
    }

    // Prevent trading with the logger.
    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().hasMetadata(COMBAT_LOGGER_METADATA)) {
            event.setCancelled(true);
        }
    }

    // Don't let the chunk holding a logger unload.
    @EventHandler
    public void onEntityDespawn(ChunkUnloadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity.hasMetadata(COMBAT_LOGGER_METADATA) && !entity.isDead()) {
                event.setCancelled(true);
            }
        }
    }

    // Despawn the NPC when its owner joins.
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        for (Entity entity : event.getPlayer().getWorld().getEntitiesByClass(Villager.class)) {
            Villager villager = (Villager) entity;

            if (villager.isCustomNameVisible() && ChatColor.stripColor(villager.getCustomName()).equals(event.getPlayer().getName())) {
                villager.remove();
            }
        }
    }

    // Prevent combat logger friendly fire.
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity().hasMetadata(COMBAT_LOGGER_METADATA)) {
            Player player = (Player) event.getDamager();
            Villager villager = (Villager) event.getEntity();
            String playerName = villager.getCustomName().substring(2);

            if (!FoxtrotPlugin.getInstance().getServerHandler().isEOTW() && (DTRBitmaskType.SAFE_ZONE.appliesAt(player.getLocation()) || DTRBitmaskType.SAFE_ZONE.appliesAt(villager.getLocation()))) {
                event.setCancelled(true);
                return;
            }

            if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(player.getName())) {
                event.setCancelled(true);
                return;
            }

            Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(playerName);

            if (team != null && team.isMember(player.getName())) {
                event.setCancelled(true);
            }
        }
    }

    // Spawn the combat logger
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // If the player safe logged out
        if (event.getPlayer().hasMetadata("loggedout")) {
            event.getPlayer().removeMetadata("loggedout", FoxtrotPlugin.getInstance());
            return;
        }

        // If the player is in spawn
        if (!FoxtrotPlugin.getInstance().getServerHandler().isEOTW() && DTRBitmaskType.SAFE_ZONE.appliesAt(event.getPlayer().getLocation())) {
            return;
        }

        // If they have a PvP timer.
        if (!FoxtrotPlugin.getInstance().getServerHandler().isPreEOTW() && FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getName())) {
            return;
        }

        // If they're dead.
        if (event.getPlayer().isDead()) {
            return;
        }

        // If the player is below Y = 0
        if (event.getPlayer().getLocation().getBlockY() <= 0) {
            return;
        }

        boolean enemyWithinRange = false;

        for (Entity entity : event.getPlayer().getNearbyEntities(40, 256, 40)) {
            if (entity instanceof Player) {
                Player other = (Player) entity;

                if (other.hasMetadata("invisible")) {
                    continue;
                }

                if (FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(other.getName()) != FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(event.getPlayer().getName())) {
                    enemyWithinRange = true;
                }
            }
        }

        if (event.getPlayer().getGameMode() != GameMode.CREATIVE && !(event.getPlayer().hasMetadata("invisible"))){
            if (enemyWithinRange && !event.getPlayer().isDead()) {
                String playerName = ChatColor.RED.toString() + event.getPlayer().getName();
                FoxtrotPlugin.getInstance().getLogger().info(event.getPlayer().getName() + " combat logged at (" + event.getPlayer().getLocation().getBlockX() + ", " + event.getPlayer().getLocation().getBlockY() + ", " + event.getPlayer().getLocation().getBlockZ() + ")");

                ItemStack[] armor = event.getPlayer().getInventory().getArmorContents();
                ItemStack[] inv = event.getPlayer().getInventory().getContents();
                ItemStack[] drops = new ItemStack[armor.length + inv.length];

                System.arraycopy(armor, 0, drops, 0, armor.length);
                System.arraycopy(inv, 0, drops, armor.length, inv.length);

                FixedVillager fixedVillager = new FixedVillager(((CraftWorld) event.getPlayer().getWorld()).getHandle());
                Location location = event.getPlayer().getLocation();
                fixedVillager.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

                int i = MathHelper.floor(fixedVillager.locX / 16.0D);
                int j = MathHelper.floor(fixedVillager.locZ / 16.0D);
                net.minecraft.server.v1_7_R3.World world = ((CraftWorld) event.getPlayer().getWorld()).getHandle();

                world.getChunkAt(i, j).a(fixedVillager);
                world.entityList.add(fixedVillager);

                try {
                    Method m = world.getClass().getDeclaredMethod("a", net.minecraft.server.v1_7_R3.Entity.class);
                    m.setAccessible(true);

                    m.invoke(world, fixedVillager);
                } catch (Exception e) {

                }

                final Villager villager = (Villager) fixedVillager.getBukkitEntity();


                villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 100));
                villager.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 100));

                villager.setMetadata(COMBAT_LOGGER_METADATA, new FixedMetadataValue(FoxtrotPlugin.getInstance(), drops));
                villager.setAgeLock(true);

                int potions = 0;
                boolean gapple = false;

                for (ItemStack itemStack : event.getPlayer().getInventory().getContents()) {
                    if (itemStack == null) {
                        continue;
                    }


                    if (itemStack.getType() == Material.POTION && itemStack.getDurability() == (short) 16421) {
                        potions++;
                    } else if (!gapple && itemStack.getType() == Material.GOLDEN_APPLE && itemStack.getDurability() == (short) 1) {
                        // Only let the player have one gapple count.
                        potions += 15;
                        gapple = true;
                    }
                }

                villager.setMaxHealth((potions * 3.5D) + event.getPlayer().getHealth());
                villager.setHealth(villager.getMaxHealth());

                villager.setCustomName(playerName);
                villager.setCustomNameVisible(true);

                FoxtrotPlugin.getInstance().getServer().getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), new Runnable() {

                    public void run() {
                        if (!villager.isDead() && villager.isValid()) {
                            villager.remove();
                        }
                    }

                }, 30 * 20L);
            }
        }
    }

}