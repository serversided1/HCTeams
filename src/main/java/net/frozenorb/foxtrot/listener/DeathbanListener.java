package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class DeathbanListener implements Listener {

    private Map<String, Long> lastJoinedRevive = new HashMap<>();

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (Foxtrot.getInstance().getDeathbanMap().isDeathbanned(event.getPlayer().getUniqueId())) {
            long unbannedOn = Foxtrot.getInstance().getDeathbanMap().getDeathban(event.getPlayer().getUniqueId());
            long left = unbannedOn - System.currentTimeMillis();

            if (event.getPlayer().isOp()) {
                Foxtrot.getInstance().getDeathbanMap().revive(event.getPlayer().getUniqueId());
                return;
            }

            if (Foxtrot.getInstance().getServerHandler().isPreEOTW()) {
                event.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.YELLOW + "You have died, and are deathbanned for the remainder of the map.");
                return;
            }

            int soulboundLives = Foxtrot.getInstance().getSoulboundLivesMap().getLives(event.getPlayer().getUniqueId());
            int friendLives = Foxtrot.getInstance().getFriendLivesMap().getLives(event.getPlayer().getUniqueId());
            int transferableLives = Foxtrot.getInstance().getTransferableLivesMap().getLives(event.getPlayer().getUniqueId());
            int totalLives = soulboundLives + friendLives + transferableLives;

            if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
                event.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.YELLOW + "You have died, and are deathbanned. Your deathban will expire in " + TimeUtils.formatIntoDetailedString((int) left / 1000) + ". Lives cannot be used on kit maps.");
                return;
            }

            if (lastJoinedRevive.containsKey(event.getPlayer().getName()) && (System.currentTimeMillis() - lastJoinedRevive.get(event.getPlayer().getName())) < 1000 * 20) {
                if (totalLives > 0) {
                    Foxtrot.getInstance().getDeathbanMap().revive(event.getPlayer().getUniqueId());

                    if (soulboundLives == 0) {
                        if (friendLives == 0) {
                            // Use a transferable life.
                            Foxtrot.getInstance().getTransferableLivesMap().setLives(event.getPlayer().getUniqueId(), transferableLives - 1);
                        } else {
                            // Use a friend life.
                            Foxtrot.getInstance().getFriendLivesMap().setLives(event.getPlayer().getUniqueId(), friendLives - 1);
                        }
                    } else {
                        // Use a soulbound life.
                        Foxtrot.getInstance().getSoulboundLivesMap().setLives(event.getPlayer().getUniqueId(), soulboundLives - 1);
                    }

                    totalLives--;

                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.YELLOW + "You now have " + totalLives + " " + (totalLives == 1 ? "life" : "lives") + " left. You have been revived.");
                } else {
                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.YELLOW + "You do not have any lives. To buy a life, go to MineHQ.com/shop.");
                }
            } else {
                if (totalLives > 0) {
                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.YELLOW + "You have died, and are deathbanned. Your deathban will expire in " + TimeUtils.formatIntoDetailedString((int) left / 1000) + ". You have " + totalLives + " total " + (totalLives == 1 ? "life" : "lives") + ". To use a life, reconnect within 20 seconds.");
                    lastJoinedRevive.put(event.getPlayer().getName(), System.currentTimeMillis());
                } else {
                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.YELLOW + "You have died, and are deathbanned. Your deathban will expire in " + TimeUtils.formatIntoDetailedString((int) left / 1000) + ". You have no lives. To buy a life, go to MineHQ.com/store.");
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        int seconds = (int) Foxtrot.getInstance().getServerHandler().getDeathban(event.getEntity());
        Foxtrot.getInstance().getDeathbanMap().deathban(event.getEntity().getUniqueId(), seconds);

        final String time = TimeUtils.formatIntoDetailedString(seconds);

        new BukkitRunnable() {

            public void run() {
                if (!event.getEntity().isOnline()) {
                    return;
                }

                if (Foxtrot.getInstance().getServerHandler().isPreEOTW()) {
                    event.getEntity().kickPlayer(ChatColor.YELLOW + "Come back tomorrow for SOTW!");
                } else {
                    event.getEntity().kickPlayer(ChatColor.YELLOW + "Come back in " + time + "!");
                }
            }

        }.runTaskLater(Foxtrot.getInstance(), 5 * 20L);
    }

}