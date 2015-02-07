package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by macguy8 on 11/5/2014.
 */
public class DeathbanListener implements Listener {

    private Map<String, Long> lastJoinedRevive = new HashMap<String, Long>();

    @EventHandler(priority=EventPriority.MONITOR)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }

        FoxtrotPlugin.getInstance().getDeathbanMap().reloadValue(event.getName());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (FoxtrotPlugin.getInstance().getDeathbanMap().isDeathbanned(event.getPlayer().getName())) {
            long unbannedOn = FoxtrotPlugin.getInstance().getDeathbanMap().getDeathban(event.getPlayer().getName());
            long left = unbannedOn - System.currentTimeMillis();

            if (event.getPlayer().isOp() && !event.getPlayer().getName().equals("Stimpay")) {
                return;
            }

            if (FoxtrotPlugin.getInstance().getServerHandler().isPreEOTW()) {
                event.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.YELLOW + "You have died, and are deathbanned for the remainder of the map.");
                return;
            }

            int soulboundLives = FoxtrotPlugin.getInstance().getSoulboundLivesMap().getLives(event.getPlayer().getName());
            int friendLives = FoxtrotPlugin.getInstance().getFriendLivesMap().getLives(event.getPlayer().getName());
            int transferableLives = FoxtrotPlugin.getInstance().getTransferableLivesMap().getLives(event.getPlayer().getName());
            int totalLives = soulboundLives + friendLives + transferableLives;

            if (FoxtrotPlugin.getInstance().getMapHandler().isKitMap()) {
                event.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.YELLOW + "You have died, and are deathbanned. Your deathban will expire in " + TimeUtils.getDurationBreakdown(left) + ". Lives cannot be used on kit maps.");
                return;
            }

            if (lastJoinedRevive.containsKey(event.getPlayer().getName()) && (System.currentTimeMillis() - lastJoinedRevive.get(event.getPlayer().getName())) < 1000 * 20) {
                if (totalLives > 0) {
                    FoxtrotPlugin.getInstance().getDeathbanMap().revive(event.getPlayer().getName());

                    if (soulboundLives == 0) {
                        if (friendLives == 0) {
                            // Use a transferable life.
                            FoxtrotPlugin.getInstance().getTransferableLivesMap().setLives(event.getPlayer().getName(), transferableLives - 1);
                        } else {
                            // Use a friend life.
                            FoxtrotPlugin.getInstance().getFriendLivesMap().setLives(event.getPlayer().getName(), friendLives - 1);
                        }
                    } else {
                        // Use a soulbound life.
                        FoxtrotPlugin.getInstance().getSoulboundLivesMap().setLives(event.getPlayer().getName(), soulboundLives - 1);
                    }

                    totalLives--;

                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.YELLOW + "You now have " + totalLives + " " + (totalLives == 1 ? "life" : "lives") + " left. You have been revived.");
                } else {
                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.YELLOW + "You do not have any lives. To buy a life, go to MineHQ.com/shop.");
                }
            } else {
                if (totalLives > 0) {
                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.YELLOW + "You have died, and are deathbanned. Your deathban will expire in " + TimeUtils.getDurationBreakdown(left) + ". You have " + totalLives + " total " + (totalLives == 1 ? "life" : "lives") + ". To use a life, reconnect within 20 seconds.");
                    lastJoinedRevive.put(event.getPlayer().getName(), System.currentTimeMillis());
                } else {
                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.YELLOW + "You have died, and are deathbanned. Your deathban will expire in " + TimeUtils.getDurationBreakdown(left) + ". You have no lives. To buy a life, go to MineHQ.com/store.");
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        int seconds = (int) FoxtrotPlugin.getInstance().getServerHandler().getDeathban(event.getEntity());
        FoxtrotPlugin.getInstance().getDeathbanMap().deathban(event.getEntity().getName(), seconds);

        final String time = TimeUtils.getDurationBreakdown(seconds * 1000);

        FoxtrotPlugin.getInstance().getServer().getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), new Runnable() {

            @Override
            public void run() {
                event.getEntity().teleport(event.getEntity().getLocation().add(0, 100, 0));

                if (FoxtrotPlugin.getInstance().getServerHandler().isPreEOTW()) {
                    event.getEntity().kickPlayer(ChatColor.YELLOW + "Come back tomorrow for SOTW!");
                } else {
                    event.getEntity().kickPlayer(ChatColor.YELLOW + "Come back in " + time + "!");
                }
            }
        }, 5L);
    }

}