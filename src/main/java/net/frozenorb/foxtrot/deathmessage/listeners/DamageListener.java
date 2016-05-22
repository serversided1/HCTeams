package net.frozenorb.foxtrot.deathmessage.listeners;

import com.google.common.collect.Maps;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.deathmessage.DeathMessageHandler;
import net.frozenorb.foxtrot.deathmessage.event.CustomPlayerDamageEvent;
import net.frozenorb.foxtrot.deathmessage.objects.Damage;
import net.frozenorb.foxtrot.deathmessage.objects.PlayerDamage;
import net.frozenorb.foxtrot.deathmessage.util.UnknownDamage;
import net.frozenorb.foxtrot.map.kit.killstreaks.Killstreak;
import net.frozenorb.foxtrot.map.kit.stats.StatsEntry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DamageListener implements Listener {

    // kit-map only
    private Map<UUID, UUID> lastKilled = Maps.newHashMap();
    private Map<UUID, Integer> boosting = Maps.newHashMap();

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            CustomPlayerDamageEvent customEvent = new CustomPlayerDamageEvent(event, new UnknownDamage(player.getName(), event.getDamage()));

            Foxtrot.getInstance().getServer().getPluginManager().callEvent(customEvent);
            DeathMessageHandler.addDamage(player, customEvent.getTrackerDamage());
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        List<Damage> record = DeathMessageHandler.getDamage(event.getEntity());

        event.setDeathMessage(null);

        String deathMessage;

        if (record != null) {
            Damage deathCause = record.get(record.size() - 1);

            // Hacky NMS to change the player's killer
            if (deathCause instanceof PlayerDamage) {
                String killerName = ((PlayerDamage) deathCause).getDamager();
                Player killer = Foxtrot.getInstance().getServer().getPlayerExact(killerName);

                if (killer != null) {
                    ((CraftPlayer) event.getEntity()).getHandle().killer = ((CraftPlayer) killer).getHandle();

                    // kit-map death handling
                    if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
                        Player victim = event.getEntity();

                        if (lastKilled.containsKey(killer.getUniqueId()) && lastKilled.get(killer.getUniqueId()) == victim.getUniqueId()) {
                            boosting.putIfAbsent(killer.getUniqueId(), 0);
                            boosting.put(killer.getUniqueId(), boosting.get(killer.getUniqueId()) + 1);
                        } else {
                            boosting.put(killer.getUniqueId(), 0);
                        }

                        if (killer.equals(victim) || isNaked(victim)) {
                            StatsEntry victimStats = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(victim);

                            victimStats.addDeath();
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
                            }

                            Foxtrot.getInstance().getKillsMap().setKills(killer.getUniqueId(), killerStats.getKills());
                        }
                    } else {
                        Foxtrot.getInstance().getKillsMap().setKills(killer.getUniqueId(), Foxtrot.getInstance().getKillsMap().getKills(killer.getUniqueId()) + 1);
                    }
                }
            }

            deathMessage = deathCause.getDeathMessage();
        } else {
            deathMessage = new UnknownDamage(event.getEntity().getName(), 1).getDeathMessage();
        }

        if (event.getEntity().getKiller() != null) {
            event.getEntity().getKiller().sendMessage(deathMessage);
        }

        Bukkit.getScheduler().scheduleAsyncDelayedTask(Foxtrot.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (event.getEntity().getKiller() != null && player.equals(event.getEntity().getKiller())) {
                    continue;
                }

                if (Foxtrot.getInstance().getToggleDeathMessageMap().areDeathMessagesEnabled(player.getUniqueId())) {
                    player.sendMessage(deathMessage);
                } else {
                    if (Foxtrot.getInstance().getTeamHandler().getTeam(player) == null) {
                        continue;
                    }

                    // send them the message if the player who died was on their team
                    if (Foxtrot.getInstance().getTeamHandler().getTeam(event.getEntity()) != null &&
                            Foxtrot.getInstance().getTeamHandler().getTeam(player).equals(Foxtrot.getInstance().getTeamHandler().getTeam(event.getEntity()))) {
                        player.sendMessage(deathMessage);
                    }

                    // send them the message if the killer was on their team
                    if (event.getEntity().getKiller() != null) {
                        Player killer = event.getEntity().getKiller();

                        if (Foxtrot.getInstance().getTeamHandler().getTeam(killer) != null
                                && Foxtrot.getInstance().getTeamHandler().getTeam(player).equals(Foxtrot.getInstance().getTeamHandler().getTeam(killer))) {
                            player.sendMessage(deathMessage);
                        }
                    }
                }
            }
        });

        //DeathTracker.logDeath(event.getEntity(), event.getEntity().getKiller());
        DeathMessageHandler.clearDamage(event.getEntity());
        Foxtrot.getInstance().getDeathsMap().setDeaths(event.getEntity().getUniqueId(), Foxtrot.getInstance().getDeathsMap().getDeaths(event.getEntity().getUniqueId()) + 1);
    }

    private boolean isNaked(Player player) {
        return player.getInventory().getHelmet() == null &&
                player.getInventory().getChestplate() == null &&
                player.getInventory().getLeggings() == null &&
                player.getInventory().getBoots() == null;
    }


}