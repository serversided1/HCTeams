package net.frozenorb.foxtrot.listener;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.LastInvCommand;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.mBasic.CommandSystem.Commands.Freeze;
import net.frozenorb.qlib.serialization.ItemStackSerializer;
import net.minecraft.server.v1_7_R4.*;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftHumanEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CombatLoggerListener implements Listener {

    public static final String COMBAT_LOGGER_METADATA = "CombatLogger";
    private Set<Entity> combatLoggers = new HashSet<>();

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().hasMetadata(COMBAT_LOGGER_METADATA)) {
            combatLoggers.remove(event.getEntity());
            CombatLoggerMetadata metadata = (CombatLoggerMetadata) event.getEntity().getMetadata(COMBAT_LOGGER_METADATA).get(0).value();

            if (!metadata.playerName.equals(event.getEntity().getCustomName().substring(2))) {
                Foxtrot.getInstance().getLogger().warning("Combat logger name doesn't match metadata for " + metadata.playerName + " (" + event.getEntity().getCustomName().substring(2) + ")");
            }

            Foxtrot.getInstance().getLogger().info(metadata.playerName + "'s combat logger at (" + event.getEntity().getLocation().getBlockX() + ", " + event.getEntity().getLocation().getBlockY() + ", " + event.getEntity().getLocation().getBlockZ() + ") died.");

            // Deathban the player
            Foxtrot.getInstance().getDeathbanMap().deathban(metadata.playerUUID, Foxtrot.getInstance().getServerHandler().getDeathban(metadata.playerUUID, event.getEntity().getLocation()));
            Team team = Foxtrot.getInstance().getTeamHandler().getTeam(metadata.playerUUID);

            // Take away DTR.
            if (team != null) {
                team.playerDeath(metadata.playerName, Foxtrot.getInstance().getServerHandler().getDTRLoss(event.getEntity().getLocation()));
            }

            // Drop the player's items.
            for (ItemStack item : metadata.contents) {
                event.getDrops().add(item);
            }
            for (ItemStack item : metadata.armor) {
                event.getDrops().add(item);
            }

            if (event.getEntity().getKiller() != null) {
                String deathMessage = ChatColor.RED + metadata.playerName + ChatColor.GRAY + " (Combat-Logger)" + ChatColor.YELLOW + " was slain by " + ChatColor.RED + event.getEntity().getKiller().getName() + ChatColor.YELLOW + ".";

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (Foxtrot.getInstance().getToggleDeathMessageMap().areDeathMessagesEnabled(player.getUniqueId())){
                        player.sendMessage(deathMessage);
                    } else {
                        if (Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId()) == null) {
                            continue;
                        }

                        if (Foxtrot.getInstance().getTeamHandler().getTeam(metadata.playerUUID) != null
                                && Foxtrot.getInstance().getTeamHandler().getTeam(metadata.playerUUID).equals(Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId()))) {
                            player.sendMessage(deathMessage);
                        }

                        if (Foxtrot.getInstance().getTeamHandler().getTeam(event.getEntity().getKiller().getUniqueId()) != null
                                && Foxtrot.getInstance().getTeamHandler().getTeam(event.getEntity().getKiller().getUniqueId()).equals(Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId()))) {
                            player.sendMessage(deathMessage);
                        }
                    }
                }

                // Add the death sign.

//                if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
//                    event.getDrops().add(Foxtrot.getInstance().getServerHandler().generateDeathSign(metadata.playerName, event.getEntity().getKiller().getName()));
//                }

                // and give them the kill
                Foxtrot.getInstance().getKillsMap().setKills(event.getEntity().getKiller().getUniqueId(), Foxtrot.getInstance().getKillsMap().getKills(event.getEntity().getKiller().getUniqueId()) + 1);
            } else {
                String deathMessage = ChatColor.RED + metadata.playerName + ChatColor.GRAY + " (Combat-Logger)" + ChatColor.YELLOW + " died.";

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (Foxtrot.getInstance().getToggleDeathMessageMap().areDeathMessagesEnabled(player.getUniqueId())){
                        player.sendMessage(deathMessage);
                    } else {
                        if (Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId()) == null) {
                            continue;
                        }

                        if (Foxtrot.getInstance().getTeamHandler().getTeam(metadata.playerUUID) != null
                                && Foxtrot.getInstance().getTeamHandler().getTeam(metadata.playerUUID).equals(Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId()))) {
                            player.sendMessage(deathMessage);
                        }
                    }
                }
            }

            Player target = Foxtrot.getInstance().getServer().getPlayer(metadata.playerUUID);

            if (target == null) {
                // Create an entity to load the player data
                MinecraftServer server = ((CraftServer) Foxtrot.getInstance().getServer()).getServer();
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

                spoofWebsiteData(target, event.getEntity().getKiller());
                target.saveData();
            }

            LastInvCommand.recordInventory(metadata.playerUUID, metadata.contents, metadata.armor);
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
            CombatLoggerMetadata metadata = (CombatLoggerMetadata) event.getEntity().getMetadata(COMBAT_LOGGER_METADATA).get(0).value();

            if (DTRBitmask.SAFE_ZONE.appliesAt(damager.getLocation()) || DTRBitmask.SAFE_ZONE.appliesAt(event.getEntity().getLocation())) {
                event.setCancelled(true);
                return;
            }

            if (Foxtrot.getInstance().getServerHandler().isSpawnBufferZone(event.getEntity().getLocation())) {
                ((EntityLiving) ((CraftEntity) event.getEntity()).getHandle()).knockbackReduction = 1D;
            }

            if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(damager.getUniqueId())) {
                event.setCancelled(true);
                return;
            }

            Team team = Foxtrot.getInstance().getTeamHandler().getTeam(metadata.playerUUID);

            if (team != null && team.isMember(damager.getUniqueId())) {
                event.setCancelled(true);
                return;
            }

            SpawnTagHandler.addSeconds(damager, SpawnTagHandler.MAX_SPAWN_TAG);
        }
    }

    // Prevent combatloggers from activating a pressure plate
    @EventHandler
    public void onEntityPressurePlate(EntityInteractEvent event) {
        if(event.getBlock().getType() == Material.STONE_PLATE || event.getBlock().getType() == Material.GOLD_PLATE || event.getBlock().getType() == Material.IRON_PLATE) {
            if(event.getEntity() instanceof Villager && event.getEntity().hasMetadata(COMBAT_LOGGER_METADATA)) {
                event.setCancelled(true);
            }
        }
    }

    // Spawn the combat logger
    @EventHandler(priority=EventPriority.LOW) // So we run before Mod Suite code will.
    public void onPlayerQuit(PlayerQuitEvent event) {
        // If the player safe logged out
        if (event.getPlayer().hasMetadata("loggedout")) {
            event.getPlayer().removeMetadata("loggedout", Foxtrot.getInstance());
            return;
        }

        if (event.getPlayer().hasMetadata("invisible") || event.getPlayer().hasMetadata("modmode")) {
            return;
        }

        // If the player is in spawn
        if (DTRBitmask.SAFE_ZONE.appliesAt(event.getPlayer().getLocation())) {
            return;
        }

        // If they have a PvP timer.
        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getUniqueId())) {
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

        boolean spawnCombatLogger = false;

        for (Entity entity : event.getPlayer().getNearbyEntities(40, 40, 40)) {
            if (entity instanceof Player) {
                Player other = (Player) entity;

                if (other.hasMetadata("invisible")) {
                    continue;
                }

                Team otherTeam = Foxtrot.getInstance().getTeamHandler().getTeam(other);
                Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());

                if (otherTeam != playerTeam || playerTeam == null) {
                    spawnCombatLogger = true;
                    break;
                }
            }
        }

        if (!event.getPlayer().isOnGround()) {
            spawnCombatLogger = true;
        }

        if (event.getPlayer().getGameMode() != GameMode.CREATIVE && !event.getPlayer().hasMetadata("invisible") && spawnCombatLogger && !event.getPlayer().isDead()) {
            Foxtrot.getInstance().getLogger().info(event.getPlayer().getName() + " combat logged at (" + event.getPlayer().getLocation().getBlockX() + ", " + event.getPlayer().getLocation().getBlockY() + ", " + event.getPlayer().getLocation().getBlockZ() + ")");

            ItemStack[] armor = event.getPlayer().getInventory().getArmorContents();
            ItemStack[] inv = event.getPlayer().getInventory().getContents();

            final Villager villager = (Villager) event.getPlayer().getWorld().spawnEntity(event.getPlayer().getLocation(), EntityType.VILLAGER);

            villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 100));
            //villager.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 100));

            CombatLoggerMetadata metadata = new CombatLoggerMetadata();

            metadata.playerName = event.getPlayer().getName();
            metadata.playerUUID = event.getPlayer().getUniqueId();
            metadata.contents = inv;
            metadata.armor = armor;

            villager.setMetadata(COMBAT_LOGGER_METADATA, new FixedMetadataValue(Foxtrot.getInstance(), metadata));

            villager.setMaxHealth(calculateCombatLoggerHealth(event.getPlayer()));
            villager.setHealth(villager.getMaxHealth());

            villager.setCustomName(ChatColor.YELLOW.toString() + event.getPlayer().getName());
            villager.setCustomNameVisible(true);

            villager.setFallDistance(event.getPlayer().getFallDistance());
            villager.setRemoveWhenFarAway(false);
            villager.setVelocity(event.getPlayer().getVelocity());

            combatLoggers.add(villager);

            new BukkitRunnable() {

                public void run() {
                    if (!villager.isDead() && villager.isValid()) {
                        combatLoggers.remove(villager);
                        villager.remove();
                    }
                }

            }.runTaskLater(Foxtrot.getInstance(), 30 * 20L);

            if (villager.getWorld().getEnvironment() == World.Environment.THE_END) {
                // check every second if the villager fell out of the world and kill the player if that happened.
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        if (villager.getLocation().getBlockY() >= 0) {
                            return;
                        }

                        Foxtrot.getInstance().getLogger().info(metadata.playerName + "'s combat logger at (" + villager.getLocation().getBlockX() + ", " + villager.getLocation().getBlockY() + ", " + villager.getLocation().getBlockZ() + ") died.");

                        // Deathban the player
                        Foxtrot.getInstance().getDeathbanMap().deathban(metadata.playerUUID, Foxtrot.getInstance().getServerHandler().getDeathban(metadata.playerUUID, villager.getLocation()));
                        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(metadata.playerUUID);

                        // Take away DTR.
                        if (team != null) {
                            team.playerDeath(metadata.playerName, Foxtrot.getInstance().getServerHandler().getDTRLoss(villager.getLocation()));
                        }

                        String deathMessage = ChatColor.RED + metadata.playerName + ChatColor.GRAY + " (Combat-Logger)" + ChatColor.YELLOW + " died.";

                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (Foxtrot.getInstance().getToggleDeathMessageMap().areDeathMessagesEnabled(player.getUniqueId())){
                                player.sendMessage(deathMessage);
                            } else {
                                if (Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId()) == null) {
                                    continue;
                                }

                                if (Foxtrot.getInstance().getTeamHandler().getTeam(metadata.playerUUID) != null
                                        && Foxtrot.getInstance().getTeamHandler().getTeam(metadata.playerUUID).equals(Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId()))) {
                                    player.sendMessage(deathMessage);
                                }
                            }
                        }

                        Player target = Foxtrot.getInstance().getServer().getPlayer(metadata.playerUUID);

                        if (target == null) {
                            // Create an entity to load the player data
                            MinecraftServer server = ((CraftServer) Foxtrot.getInstance().getServer()).getServer();
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

                            spoofWebsiteData(target, villager.getKiller());
                            target.saveData();
                        }

                        LastInvCommand.recordInventory(metadata.playerUUID, metadata.contents, metadata.armor);

                        cancel();
                    }

                }.runTaskTimer(Foxtrot.getInstance(), 0L, 20L);
            }
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

        private ItemStack[] contents;
        private ItemStack[] armor;
        private String playerName;
        private UUID playerUUID;

    }

    private void spoofWebsiteData(Player killed, Player killer) {
        final BasicDBObject playerDeath = new BasicDBObject();

        if (killer != null) {
            playerDeath.append("soups", -1);
            playerDeath.append("healthLeft", (int) killer.getHealth());
            playerDeath.append("killer", killer.getName());
            playerDeath.append("killerUUID", killer.getUniqueId().toString().replace("-", ""));
            playerDeath.append("killerHunger", killer.getFoodLevel());

            if (killer.getItemInHand() != null) {
                playerDeath.append("item", ItemStackSerializer.serialize(killer.getItemInHand()));
            } else {
                playerDeath.append("item", "NONE");
            }
        } else {
            try{
                playerDeath.append("reason", "combat-logger");
            } catch (NullPointerException ignored) {

            }
        }

        playerDeath.append("playerHunger", killed.getFoodLevel());

        BasicDBObject playerInv = new BasicDBObject();
        BasicDBObject armor = new BasicDBObject();

        armor.put("helmet", ItemStackSerializer.serialize(killed.getInventory().getHelmet()));
        armor.put("chestplate", ItemStackSerializer.serialize(killed.getInventory().getChestplate()));
        armor.put("leggings", ItemStackSerializer.serialize(killed.getInventory().getLeggings()));
        armor.put("boots", ItemStackSerializer.serialize(killed.getInventory().getBoots()));

        BasicDBList contents = new BasicDBList();

        for (int i = 0; i < 9; i++) {
            if (killed.getInventory().getItem(i) != null) {
                contents.add(ItemStackSerializer.serialize(killed.getInventory().getItem(i)));
            } else {
                contents.add(ItemStackSerializer.serialize(new ItemStack(Material.AIR)));
            }
        }

        playerInv.append("armor", armor);
        playerInv.append("items", contents);

        playerDeath.append("playerInventory", playerInv);

        if (killer != null) {
            BasicDBObject killerInventory = new BasicDBObject();
            BasicDBObject killerArmor = new BasicDBObject();

            armor.put("helmet", ItemStackSerializer.serialize(killer.getInventory().getHelmet()));
            armor.put("chestplate", ItemStackSerializer.serialize(killer.getInventory().getChestplate()));
            armor.put("leggings", ItemStackSerializer.serialize(killer.getInventory().getLeggings()));
            armor.put("boots", ItemStackSerializer.serialize(killer.getInventory().getBoots()));

            BasicDBList killerContents = new BasicDBList();

            for (int i = 0; i < 9; i++) {
                if (killer.getInventory().getItem(i) != null) {
                    killerContents.add(ItemStackSerializer.serialize(killer.getInventory().getItem(i)));
                } else {
                    killerContents.add(ItemStackSerializer.serialize(new ItemStack(Material.AIR)));
                }
            }

            killerInventory.append("armor", killerArmor);
            killerInventory.append("items", killerContents);
            playerDeath.append("killerInventory", killerInventory);
        }

        playerDeath.append("uuid", killed.getUniqueId().toString().replace("-", ""));
        playerDeath.append("player", killed.getName());
        playerDeath.append("type", "death");
        playerDeath.append("when", new Date());

        new BukkitRunnable() {

            public void run() {
                Foxtrot.getInstance().getMongoPool().getDB("HCTeams").getCollection("Deaths").insert(playerDeath);
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

}