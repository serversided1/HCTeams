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
import java.util.concurrent.TimeUnit;

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
        if (event.getPlayer().isOp() || event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            return;
        }

        if (FoxtrotPlugin.getInstance().getDeathbanMap().isDeathbanned(event.getPlayer().getName())) {
            long unbannedOn = FoxtrotPlugin.getInstance().getDeathbanMap().getDeathban(event.getPlayer().getName());
            long left = unbannedOn - System.currentTimeMillis();

            if (FoxtrotPlugin.getInstance().getServerHandler().isPreEOTW()) {
                event.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.RED + "You have died, and are death-banned for the remainder of the map.");
                return;
            }

            if (event.getPlayer().isOp()) {
                System.out.println(event.getPlayer().getName() + "'s hostname: " + event.getHostname());
            }

            if (event.getHostname().toLowerCase().contains("revive")) {
                if (!FoxtrotPlugin.getInstance().getDeathbanMap().isDeathbanned(event.getPlayer().getName())) {
                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.RED + "You are not deathbanned.");
                    return;
                }

                if (FoxtrotPlugin.getInstance().getServerHandler().isPreEOTW()) {
                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.RED + "The server is in EOTW Mode: Lives cannot be used.");
                    return;
                }

                if (lastJoinedRevive.containsKey(event.getPlayer().getName()) && (System.currentTimeMillis() - lastJoinedRevive.get(event.getPlayer().getName())) < 1000 * 20) {
                    int soulboundLives = FoxtrotPlugin.getInstance().getSoulboundLivesMap().getLives(event.getPlayer().getName());
                    int friendLives = FoxtrotPlugin.getInstance().getFriendLivesMap().getLives(event.getPlayer().getName());
                    int transferableLives = FoxtrotPlugin.getInstance().getTransferableLivesMap().getLives(event.getPlayer().getName());
                    int totalLives = soulboundLives + friendLives + transferableLives;

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

                        event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                                ChatColor.RED + "You now have " + ChatColor.GOLD + totalLives + " total lives" + ChatColor.RED + "." + ChatColor.RESET + "\n" +
                                        ChatColor.RED + "To use a life, reconnect within 20 seconds."
                        );
                    } else {
                        event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                                ChatColor.RED + "You have " + ChatColor.GOLD + totalLives + " total lives" + ChatColor.RED + "." + ChatColor.RESET + "\n" +
                                        ChatColor.RED + "To buy a life, go to " + ChatColor.GOLD + "MineHQ.com/shop" + ChatColor.RED + "."
                        );
                    }
                } else {
                    int soulboundLives = FoxtrotPlugin.getInstance().getSoulboundLivesMap().getLives(event.getPlayer().getName());
                    int friendLives = FoxtrotPlugin.getInstance().getFriendLivesMap().getLives(event.getPlayer().getName());
                    int transferableLives = FoxtrotPlugin.getInstance().getTransferableLivesMap().getLives(event.getPlayer().getName());
                    int totalLives = soulboundLives + friendLives + transferableLives;

                    if (FoxtrotPlugin.getInstance().getLastDeathMap().recentlyDied(event.getPlayer().getName())) {
                        long millisLeft = FoxtrotPlugin.getInstance().getLastDeathMap().getLastDeath(event.getPlayer().getName()) - System.currentTimeMillis();
                        millisLeft -= TimeUnit.MINUTES.toMillis(15);

                        double value = (millisLeft / 1000D);
                        double sec = Math.round(10.0 * value) / 10.0;

                        event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                                ChatColor.RED + "You have recently died." + ChatColor.RESET + "\n" +
                                        ChatColor.RED + "You will be able to use a life in " + ChatColor.GOLD + sec + ChatColor.RED + "."
                        );
                        return;
                    }

                    if (totalLives > 0) {
                        event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                                ChatColor.RED + "You have " + ChatColor.GOLD + totalLives + " total lives" + ChatColor.RED + "." + ChatColor.RESET + "\n" +
                                        ChatColor.RED + "To use a life, reconnect within 20 seconds."
                        );
                    } else {
                        event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                                ChatColor.RED + "You have " + ChatColor.GOLD + totalLives + " total lives" + ChatColor.RED + "." + ChatColor.RESET + "\n" +
                                        ChatColor.RED + "To buy a life, go to " + ChatColor.GOLD + "MineHQ.com/shop" + ChatColor.RED + "."
                        );
                    }

                    lastJoinedRevive.put(event.getPlayer().getName(), System.currentTimeMillis());
                }
            } else {
                event.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.RED + "You are death-banned for another " + TimeUtils.getDurationBreakdown(left) + ".");
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        int seconds = FoxtrotPlugin.getInstance().getServerHandler().getDeathBanAt(event.getEntity().getName(), event.getEntity().getLocation());
        FoxtrotPlugin.getInstance().getDeathbanMap().deathban(event.getEntity().getName(), seconds);

        final String time = TimeUtils.getDurationBreakdown(seconds * 1000);

        FoxtrotPlugin.getInstance().getServer().getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), new Runnable() {

            @Override
            public void run() {
                event.getEntity().teleport(event.getEntity().getLocation().add(0, 100, 0));

                if (FoxtrotPlugin.getInstance().getServerHandler().isPreEOTW()) {
                    event.getEntity().kickPlayer(ChatColor.RED + "§cCome back tomorrow for SOTW!");
                } else {
                    event.getEntity().kickPlayer(ChatColor.RED + "Come back in " + time + "!");
                }
            }
        }, 5L);
    }

}