package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

/**
 * Created by macguy8 on 11/2/2014.
 */
public class Mute {

    public static HashMap<String, String> factionMutes = new HashMap<String, String>();

    @Command(names={ "team mutefaction", "t mutefaction", "f mutefaction", "faction mutefaction", "fac mutefaction" }, permissionNode="foxtrot.mutefaction")
    public static void teamMuteFaction(Player sender, @Param(name="Target") final Team target, @Param(name="Time") String time, @Param(name="Reason") String reason) {
        int timeSeconds = Integer.valueOf(time) * 60;

        for (Player player : target.getOnlineMembers()) {
            factionMutes.put(player.getName(), target.getFriendlyName());
            player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Your faction has been muted for " + TimeUtils.getDurationBreakdown(timeSeconds * 1000L) + " for " + reason + ".");
        }

        new BukkitRunnable() {

            public void run() {
                for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                    if (factionMutes.containsKey(player.getName()) && factionMutes.get(player.getName()).equals(target.getFriendlyName())) {
                        player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Your faction's mute has expired!");
                        factionMutes.remove(player.getName());
                    }
                }
            }

        }.runTaskLater(FoxtrotPlugin.getInstance(), timeSeconds * 20L);

        sender.sendMessage(ChatColor.GRAY + "Muted the faction " + target.getFriendlyName() + ChatColor.GRAY + " for " + TimeUtils.getDurationBreakdown(timeSeconds * 1000L) + " for " + reason + ".");
    }

    @Command(names={ "team unmutefaction", "t unmutefaction", "f unmutefaction", "faction unmutefaction", "fac unmutefaction" }, permissionNode="foxtrot.mutefaction")
    public static void teamUnmuteFaction(Player sender, @Param(name="Target") final Team target) {
        for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
            if (factionMutes.containsKey(player.getName()) && factionMutes.get(player.getName()).equals(target.getFriendlyName())) {
                player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Your faction's mute has been removed!");
                factionMutes.remove(player.getName());
            }
        }

        sender.sendMessage(ChatColor.GRAY + "Unmuted the faction " + target.getFriendlyName() + ChatColor.GRAY  + ".");
    }

}