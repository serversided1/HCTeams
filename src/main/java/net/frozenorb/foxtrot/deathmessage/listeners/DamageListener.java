package net.frozenorb.foxtrot.deathmessage.listeners;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import net.frozenorb.foxtrot.deathmessage.event.PlayerKilledEvent;
import net.frozenorb.foxtrot.util.Players;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Maps;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.deathmessage.DeathMessageHandler;
import net.frozenorb.foxtrot.deathmessage.event.CustomPlayerDamageEvent;
import net.frozenorb.foxtrot.deathmessage.objects.Damage;
import net.frozenorb.foxtrot.deathmessage.objects.PlayerDamage;
import net.frozenorb.foxtrot.deathmessage.util.UnknownDamage;
import net.frozenorb.foxtrot.map.killstreaks.Killstreak;
import net.frozenorb.foxtrot.map.killstreaks.PersistentKillstreak;
import net.frozenorb.foxtrot.map.stats.StatsEntry;
import net.frozenorb.foxtrot.team.Team;

public class DamageListener implements Listener {
    
    // kit-map only
    private Map<UUID, UUID> lastKilled = Maps.newHashMap();
    private Map<UUID, Integer> boosting = Maps.newHashMap();
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            CustomPlayerDamageEvent customEvent = new CustomPlayerDamageEvent(event, new UnknownDamage(player.getName(), event.getDamage()));
            
            Foxtrot.getInstance().getServer().getPluginManager().callEvent(customEvent);
            DeathMessageHandler.addDamage(player, customEvent.getTrackerDamage());
        }
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        DeathMessageHandler.clearDamage(event.getPlayer());
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        List<Damage> record = DeathMessageHandler.getDamage(event.getEntity());
        
        event.setDeathMessage(null);
        
        String deathMessage;
        
        if (record != null) {
            Damage deathCause = record.get(record.size() - 1);
            
            // Hacky NMS to change the player's killer
            // System.out.println("The milliseconds since death is: " +
            // deathCause.getTimeDifference() + " this should be less than " +
            // TimeUnit.MINUTES.toMillis(1) );
            if (deathCause instanceof PlayerDamage && deathCause.getTimeDifference() < TimeUnit.MINUTES.toMillis(1)) {
                // System.out.println("Its a playerdamage thing");
                String killerName = ((PlayerDamage) deathCause).getDamager();
                Player killer = Foxtrot.getInstance().getServer().getPlayerExact(killerName);
                
                if (killer != null && !Foxtrot.getInstance().getInDuelPredicate().test(event.getEntity())) {
                    ((CraftPlayer) event.getEntity()).getHandle().killer = ((CraftPlayer) killer).getHandle();
                    
                    // kit-map death handling
                    if (Foxtrot.getInstance().getMapHandler().isKitMap() || Foxtrot.getInstance().getServerHandler().isVeltKitMap()) {
                        Player victim = event.getEntity();

                        // Call event
                        PlayerKilledEvent killedEvent = new PlayerKilledEvent(killer, victim);
                        Foxtrot.getInstance().getServer().getPluginManager().callEvent(killedEvent);

                        // Prevent kill boosting
                        // Check if the victim's UUID is the same as the killer's last victim UUID
                        // Check if the victim's IP matches the killer's IP
                        if (lastKilled.containsKey(killer.getUniqueId()) && lastKilled.get(killer.getUniqueId()) == victim.getUniqueId()) {
                            boosting.putIfAbsent(killer.getUniqueId(), 0);
                            boosting.put(killer.getUniqueId(), boosting.get(killer.getUniqueId()) + 1);
                        } else {
                            boosting.put(killer.getUniqueId(), 0);
                        }

                        if (killer.equals(victim) || Players.isNaked(victim)) {
                            StatsEntry victimStats = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(victim);

                            victimStats.addDeath();
                        } else if (killer.getAddress().getAddress().getHostAddress().equalsIgnoreCase(victim.getAddress().getAddress().getHostAddress())) {
                            killer.sendMessage(ChatColor.RED + "Boost Check: You've killed a player on the same IP address as you.");
                        } else if (boosting.containsKey(killer.getUniqueId()) && boosting.get(killer.getUniqueId()) > 1) {
                            killer.sendMessage(ChatColor.RED + "Boost Check: You've killed " + victim.getName() + " " + boosting.get(killer.getUniqueId()) + " times.");
                            
                            StatsEntry victimStats = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(victim);
                            
                            victimStats.addDeath();
                        } else {
                            StatsEntry victimStats = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(victim);
                            StatsEntry killerStats = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(killer);
                            
                            victimStats.addDeath();
                            killerStats.addKill();
                            
                            lastKilled.put(killer.getUniqueId(), victim.getUniqueId());
                            
                            Killstreak killstreak = Foxtrot.getInstance().getMapHandler().getKillstreakHandler().check(killerStats.getKillstreak());
                            
                            if (killstreak != null) {
                                killstreak.apply(killer);
                                
                                Bukkit.broadcastMessage(killer.getDisplayName() + ChatColor.YELLOW + " has gotten the " + ChatColor.RED + killstreak.getName() + ChatColor.YELLOW + " killstreak!");

                                List<PersistentKillstreak> persistent = Foxtrot.getInstance().getMapHandler().getKillstreakHandler().getPersistentKillstreaks(killer, killerStats.getKillstreak());

                                for (PersistentKillstreak persistentStreak : persistent) {
                                    if (persistentStreak.matchesExactly(killerStats.getKillstreak())) {
                                        Bukkit.broadcastMessage(killer.getDisguisedName() + ChatColor.YELLOW + " has gotten the " + ChatColor.RED + killstreak.getName() + ChatColor.YELLOW + " killstreak!");
                                    }

                                    persistentStreak.apply(killer);
                                }
                            }
                            
                            Foxtrot.getInstance().getKillsMap().setKills(killer.getUniqueId(), killerStats.getKills());
                            Foxtrot.getInstance().getDeathsMap().setDeaths(victim.getUniqueId(), victimStats.getDeaths());
                        }
                    } else {
                        Foxtrot.getInstance().getKillsMap().setKills(killer.getUniqueId(), Foxtrot.getInstance().getKillsMap().getKills(killer.getUniqueId()) + 1);
                        
                        if (Foxtrot.getInstance().getServerHandler().isHardcore()) {
                            event.getDrops().add(Foxtrot.getInstance().getServerHandler().generateDeathSign(event.getEntity().getName(), killer.getName()));
                        }
                    }
                }
            }
            
            deathMessage = deathCause.getDeathMessage();
        } else {
            deathMessage = new UnknownDamage(event.getEntity().getName(), 1).getDeathMessage();
        }
        
        Player killer = event.getEntity().getKiller();
        
        Team killerTeam = killer == null ? null : Foxtrot.getInstance().getTeamHandler().getTeam(killer);
        Team deadTeam = Foxtrot.getInstance().getTeamHandler().getTeam(event.getEntity());
        
        if (killerTeam != null) {
            killerTeam.setKills(killerTeam.getKills() + 1);
        }
        
        if (deadTeam != null) {
            deadTeam.setDeaths(deadTeam.getDeaths() + 1);
        }
        
        Bukkit.getScheduler().scheduleAsyncDelayedTask(Foxtrot.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (Foxtrot.getInstance().getToggleDeathMessageMap().areDeathMessagesEnabled(player.getUniqueId())) {
                    player.sendMessage(deathMessage);
                } else {
                    if (Foxtrot.getInstance().getTeamHandler().getTeam(player) == null) {
                        continue;
                    }
                    
                    // send them the message if the player who died was on their team
                    if (Foxtrot.getInstance().getTeamHandler().getTeam(event.getEntity()) != null && Foxtrot.getInstance().getTeamHandler().getTeam(player).equals(Foxtrot.getInstance().getTeamHandler().getTeam(event.getEntity()))) {
                        player.sendMessage(deathMessage);
                    }
                    
                    // send them the message if the killer was on their team
                    if (killer != null) {
                        if (Foxtrot.getInstance().getTeamHandler().getTeam(killer) != null && Foxtrot.getInstance().getTeamHandler().getTeam(player).equals(Foxtrot.getInstance().getTeamHandler().getTeam(killer))) {
                            player.sendMessage(deathMessage);
                        }
                    }
                }
            }
        });
        
        // DeathTracker.logDeath(event.getEntity(), event.getEntity().getKiller());
        DeathMessageHandler.clearDamage(event.getEntity());
        Foxtrot.getInstance().getDeathsMap().setDeaths(event.getEntity().getUniqueId(), Foxtrot.getInstance().getDeathsMap().getDeaths(event.getEntity().getUniqueId()) + 1);
    }
    
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (Foxtrot.getInstance().getMapHandler().isKitMap() || Foxtrot.getInstance().getServerHandler().isVeltKitMap()) {
            checkKillstreaks(event.getPlayer());
        }
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (Foxtrot.getInstance().getMapHandler().isKitMap() || Foxtrot.getInstance().getServerHandler().isVeltKitMap()) {
            checkKillstreaks(event.getPlayer());
        }
    }
    
    private void checkKillstreaks(Player player) {
        Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            int killstreak = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(player).getKillstreak();
            List<PersistentKillstreak> persistent = Foxtrot.getInstance().getMapHandler().getKillstreakHandler().getPersistentKillstreaks(player, killstreak);
            
            for (PersistentKillstreak persistentStreak : persistent) {
                persistentStreak.apply(player);
            }
        }, 5L);
    }
    
    @EventHandler(ignoreCancelled = false)
    public void onRightClick(PlayerInteractEvent event) {
        if (!event.getAction().name().startsWith("RIGHT_CLICK")) {
            return;
        }
        
        ItemStack inHand = event.getPlayer().getItemInHand();
        if (inHand == null) {
            return;
        }
        
        if (inHand.getType() != Material.NETHER_STAR) {
            return;
        }
        
        if (!inHand.hasItemMeta() || !inHand.getItemMeta().hasDisplayName() || !inHand.getItemMeta().getDisplayName().startsWith(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "Potion Refill Token")) {
            return;
        }
        
        event.getPlayer().setItemInHand(null);
        
        ItemStack pot = new ItemStack(Material.POTION, 1, (short) 16421);
        while (event.getPlayer().getInventory().addItem(pot).isEmpty()) {}
    }
    
}
