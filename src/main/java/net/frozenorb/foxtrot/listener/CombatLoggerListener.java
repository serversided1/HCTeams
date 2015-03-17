package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.mBasic.CommandSystem.Commands.Freeze;
import net.minecraft.server.v1_7_R4.EntityHuman;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.PlayerInteractManager;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftHumanEntity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class CombatLoggerListener implements Listener {

    public static final String COMBAT_LOGGER_METADATA = "CombatLogger";
    private Set<Entity> combatLoggers = new HashSet<>();

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().hasMetadata(COMBAT_LOGGER_METADATA)) {
            combatLoggers.remove(event.getEntity());
            CombatLoggerMetadata metadata = (CombatLoggerMetadata) event.getEntity().getMetadata(COMBAT_LOGGER_METADATA).get(0).value();

            if (!metadata.playerName.equals(event.getEntity().getCustomName().substring(2))) {
                FoxtrotPlugin.getInstance().getLogger().warning("Combat logger name doesn't match metadata for " + metadata.playerName + " (" + event.getEntity().getCustomName().substring(2) + ")");
            }

            FoxtrotPlugin.getInstance().getLogger().info(metadata.playerName + "'s combat logger at (" + event.getEntity().getLocation().getBlockX() + ", " + event.getEntity().getLocation().getBlockY() + ", " + event.getEntity().getLocation().getBlockZ() + ") died.");

            // Deathban the player
            FoxtrotPlugin.getInstance().getDeathbanMap().deathban(metadata.playerUUID, FoxtrotPlugin.getInstance().getServerHandler().getDeathban(metadata.playerUUID, event.getEntity().getLocation()));
            Team team = FoxtrotPlugin.getInstance().getTeamHandler().getTeam(metadata.playerUUID);

            // Take away DTR.
            if (team != null) {
                team.playerDeath(metadata.playerName, FoxtrotPlugin.getInstance().getServerHandler().getDTRLoss(event.getEntity().getLocation()));
            }

            // Drop the player's items.
            for (ItemStack item : metadata.drops) {
                event.getDrops().add(item);
            }

            if (event.getEntity().getKiller() != null) {
                FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.RED + metadata.playerName + ChatColor.GRAY + " (Combat-Logger)" + ChatColor.YELLOW + " was slain by " + ChatColor.RED + event.getEntity().getKiller().getName() + ChatColor.YELLOW + ".");

                // Add the death sign.
                event.getDrops().add(FoxtrotPlugin.getInstance().getServerHandler().generateDeathSign(metadata.playerName, event.getEntity().getKiller().getName()));

                // and give them the kill
                FoxtrotPlugin.getInstance().getKillsMap().setKills(event.getEntity().getKiller().getUniqueId(), FoxtrotPlugin.getInstance().getKillsMap().getKills(event.getEntity().getKiller().getUniqueId()) + 1);
            } else {
                FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.RED + metadata.playerName + ChatColor.GRAY + " (Combat-Logger)" + ChatColor.YELLOW + " died.");
            }

            Player target = FoxtrotPlugin.getInstance().getServer().getPlayer(metadata.playerUUID);

            if (target == null) {
                // Create an entity to load the player data
                MinecraftServer server = ((CraftServer) FoxtrotPlugin.getInstance().getServer()).getServer();
                EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), new GameProfile(metadata.playerUUID, metadata.playerName), new PlayerInteractManager(server.getWorldServer(0)));
                target = entity.getBukkitEntity();

                if (target != null) {
                    target.loadData();
                }
            }

            if (target != null) {
                EntityHuman humanTarget = ((CraftHumanEntity) target).getHandle();

                target.getInventory().clear();
                target.getInventory().setArmorContents(null);
                humanTarget.setHealth(0);
                target.saveData();
            }
        }
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
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity.hasMetadata(COMBAT_LOGGER_METADATA) && !entity.isDead()) {
                event.setCancelled(true);
            }
        }
    }

    // Don't let the NPC go through portals
    @EventHandler
    public void onEntityPortal(EntityPortalEvent event) {
        if (event.getEntity().hasMetadata(COMBAT_LOGGER_METADATA)) {
            event.setCancelled(true);
        }
    }

    // Despawn the NPC when its owner joins.
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Iterator<Entity> combatLoggerIterator = combatLoggers.iterator();

        while (combatLoggerIterator.hasNext()) {
            Villager villager = (Villager) combatLoggerIterator.next();

            if (villager.isCustomNameVisible() && ChatColor.stripColor(villager.getCustomName()).equals(event.getPlayer().getName())) {
                villager.remove();
                combatLoggerIterator.remove();
            }
        }
    }

    // Prevent combat logger friendly fire.
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!event.getEntity().hasMetadata(COMBAT_LOGGER_METADATA)) {
            return;
        }

        Player damager = null;

        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();

            if (projectile.getShooter() instanceof Player) {
                damager = (Player) projectile.getShooter();
            }
        }

        if (damager != null) {
            Villager villager = (Villager) event.getEntity();
            CombatLoggerMetadata metadata = (CombatLoggerMetadata) event.getEntity().getMetadata(COMBAT_LOGGER_METADATA).get(0).value();

            if (DTRBitmask.SAFE_ZONE.appliesAt(damager.getLocation()) || DTRBitmask.SAFE_ZONE.appliesAt(villager.getLocation())) {
                event.setCancelled(true);
                return;
            }

            if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(damager.getUniqueId())) {
                event.setCancelled(true);
                return;
            }

            Team team = FoxtrotPlugin.getInstance().getTeamHandler().getTeam(metadata.playerUUID);

            if (team != null && team.isMember(damager.getUniqueId())) {
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
        if (DTRBitmask.SAFE_ZONE.appliesAt(event.getPlayer().getLocation())) {
            return;
        }

        // If they have a PvP timer.
        if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getUniqueId())) {
            return;
        }

        // If they're dead.
        if (event.getPlayer().isDead()) {
            return;
        }

        // If they're frozen
        // TODO: Make this check if the server is frozen
        if (Freeze.isFrozen(event.getPlayer())) {
            return;
        }

        // If the player is below Y = 0 (aka in the void)
        if (event.getPlayer().getLocation().getBlockY() <= 0) {
            return;
        }

        boolean enemyWithinRange = false;

        for (Entity entity : event.getPlayer().getNearbyEntities(40, 40, 40)) {
            if (entity instanceof Player) {
                Player other = (Player) entity;

                if (other.hasMetadata("invisible")) {
                    continue;
                }

                if (FoxtrotPlugin.getInstance().getTeamHandler().getTeam(other) != FoxtrotPlugin.getInstance().getTeamHandler().getTeam(event.getPlayer())) {
                    enemyWithinRange = true;
                    break;
                }
            }
        }

        if (event.getPlayer().getGameMode() != GameMode.CREATIVE && !event.getPlayer().hasMetadata("invisible") && enemyWithinRange && !event.getPlayer().isDead()) {
            FoxtrotPlugin.getInstance().getLogger().info(event.getPlayer().getName() + " combat logged at (" + event.getPlayer().getLocation().getBlockX() + ", " + event.getPlayer().getLocation().getBlockY() + ", " + event.getPlayer().getLocation().getBlockZ() + ")");

            ItemStack[] armor = event.getPlayer().getInventory().getArmorContents();
            ItemStack[] inv = event.getPlayer().getInventory().getContents();
            ItemStack[] drops = new ItemStack[armor.length + inv.length];

            System.arraycopy(armor, 0, drops, 0, armor.length);
            System.arraycopy(inv, 0, drops, armor.length, inv.length);

            final Villager villager = (Villager) event.getPlayer().getWorld().spawnEntity(event.getPlayer().getLocation(), EntityType.VILLAGER);

            villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 100));
            //villager.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 100));

            CombatLoggerMetadata metadata = new CombatLoggerMetadata();

            metadata.playerName = event.getPlayer().getName();
            metadata.playerUUID = event.getPlayer().getUniqueId();
            metadata.drops = drops;

            villager.setMetadata(COMBAT_LOGGER_METADATA, new FixedMetadataValue(FoxtrotPlugin.getInstance(), metadata));

            villager.setMaxHealth(calculateCombatLoggerHealth(event.getPlayer()));
            villager.setHealth(villager.getMaxHealth());

            villager.setCustomName(ChatColor.YELLOW.toString() + event.getPlayer().getName());
            villager.setCustomNameVisible(true);

            villager.setFallDistance(event.getPlayer().getFallDistance());
            villager.setRemoveWhenFarAway(false);
            villager.setVelocity(event.getPlayer().getVelocity());

            combatLoggers.add(villager);

            FoxtrotPlugin.getInstance().getServer().getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), () -> {

                if (!villager.isDead() && villager.isValid()) {
                    combatLoggers.remove(villager);
                    villager.remove();
                }

            }, 30 * 20L);
        }
    }

    public double calculateCombatLoggerHealth(Player player) {
        int potions = 0;
        boolean gapple = false;

        for (ItemStack itemStack : player.getInventory().getContents()) {
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

        return ((potions * 3.5D) + player.getHealth());
    }

    public static class CombatLoggerMetadata {

        private ItemStack[] drops;
        private String playerName;
        private UUID playerUUID;

    }

}