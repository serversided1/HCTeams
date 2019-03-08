package net.frozenorb.foxtrot.listener;

import java.util.Optional;

import net.frozenorb.foxtrot.server.EnderpearlCooldownHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.LastInvCommand;
import net.frozenorb.foxtrot.util.CheatBreakerKey;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.profile.Profile;
import net.frozenorb.hydrogen.rank.Rank;
import net.frozenorb.qlib.util.TimeUtils;

public class DeathbanListener implements Listener {

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        LastInvCommand.recordInventory(event.getEntity());

        EnderpearlCooldownHandler.getEnderpearlCooldown().remove(event.getEntity().getName()); // cancel enderpearls
        CheatBreakerKey.ENDER_PEARL.clear(event.getEntity());

        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return;
        }

        if (Foxtrot.getInstance().getInDuelPredicate().test(event.getEntity())) {
            return;
        }

        if (Foxtrot.getInstance().getServerHandler().isVeltKitMap()) {
        		return;
        }

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

        }.runTaskLater(Foxtrot.getInstance(), 10 * 20L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        boolean shouldBypass = event.getPlayer().isOp();
        
        if (!shouldBypass) {
            Optional<Profile> highestRank = Hydrogen.getInstance().getProfileHandler().getProfile(player.getUniqueId());
            Rank playersRank = highestRank.isPresent() ? highestRank.get().getBestGeneralRank() : null;
            shouldBypass = playersRank != null && playersRank.isStaffRank();
        }
        
        if (shouldBypass) {
            Foxtrot.getInstance().getDeathbanMap().revive(event.getPlayer().getUniqueId());
        }
    }

}