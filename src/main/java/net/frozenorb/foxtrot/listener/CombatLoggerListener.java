package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.bitmask.DTRBitmaskType;
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

                // and give them the kill
                FoxtrotPlugin.getInstance().getKillsMap().setKills(event.getEntity().getKiller().getName(), FoxtrotPlugin.getInstance().getKillsMap().getKills(event.getEntity().getKiller().getName()) + 1);
            }

            Player target = FoxtrotPlugin.getInstance().getServer().getPlayerExact(playerName);

            if (target == null) {
                // Create an entity to load the player data
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
            String playerName = villager.getCustomName().substring(2);

            if (DTRBitmaskType.SAFE_ZONE.appliesAt(damager.getLocation()) || DTRBitmaskType.SAFE_ZONE.appliesAt(villager.getLocation())) {
                event.setCancelled(true);
                return;
            }

            if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(damager.getName())) {
                event.setCancelled(true);
                return;
            }

            Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(playerName);

            if (team != null && team.isMember(damager.getName())) {
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
        if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getName())) {
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

        for (Entity entity : event.getPlayer().getNearbyEntities(40, 40, 40)) {
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

        if (event.getPlayer().getGameMode() != GameMode.CREATIVE && !event.getPlayer().hasMetadata("invisible") && enemyWithinRange && !event.getPlayer().isDead()) {
            FoxtrotPlugin.getInstance().getLogger().info(event.getPlayer().getName() + " combat logged at (" + event.getPlayer().getLocation().getBlockX() + ", " + event.getPlayer().getLocation().getBlockY() + ", " + event.getPlayer().getLocation().getBlockZ() + ")");

            ItemStack[] armor = event.getPlayer().getInventory().getArmorContents();
            ItemStack[] inv = event.getPlayer().getInventory().getContents();
            ItemStack[] drops = new ItemStack[armor.length + inv.length];

            System.arraycopy(armor, 0, drops, 0, armor.length);
            System.arraycopy(inv, 0, drops, armor.length, inv.length);

            final Villager villager = (Villager) event.getPlayer().getWorld().spawnEntity(event.getPlayer().getLocation(), EntityType.VILLAGER);

            villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 100));
            villager.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 100));

            villager.setMetadata(COMBAT_LOGGER_METADATA, new FixedMetadataValue(FoxtrotPlugin.getInstance(), drops));

            villager.setMaxHealth(calculateCombatLoggerHealth(event.getPlayer()));
            villager.setHealth(villager.getMaxHealth());

            villager.setCustomName(ChatColor.RED.toString() + event.getPlayer().getName());
            villager.setCustomNameVisible(true);

            combatLoggers.add(villager);

            FoxtrotPlugin.getInstance().getServer().getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), new Runnable() {

                public void run() {
                    if (!villager.isDead() && villager.isValid()) {
                        combatLoggers.remove(villager);
                        villager.remove();
                    }
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

}