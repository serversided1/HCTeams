package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.factionactiontracker.FactionActionTracker;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by macguy8 on 11/2/2014.
 */
public class Mute {

    public static HashMap<String, String> factionMutes = new HashMap<String, String>();

    @Command(names={ "team mute", "t mute", "f mute", "faction mute", "fac mute" }, permissionNode="foxtrot.mutefaction")
    public static void teamMuteFaction(Player sender, @Param(name="team") final Team target, @Param(name="minutes") String time, @Param(name="reason", wildcard=true) String reason) {
        int timeSeconds = Integer.valueOf(time) * 60;

        for (String player : target.getMembers()) {
            factionMutes.put(player, target.getFriendlyName());

            Player bukkitPlayer = FoxtrotPlugin.getInstance().getServer().getPlayerExact(player);

            if (bukkitPlayer != null) {
                bukkitPlayer.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Your faction has been muted for " + TimeUtils.getDurationBreakdown(timeSeconds * 1000L) + " for " + reason + ".");
            }
        }

        FactionActionTracker.logAction(target, "actions", "Mute: Faction mute added. [Duration: " + time + ", Muted by: " + sender.getName() + "]");

        new BukkitRunnable() {

            public void run() {
                FactionActionTracker.logAction(target, "actions", "Mute: Faction mute expired.");

                Iterator<Map.Entry<String, String>> mutesIterator = factionMutes.entrySet().iterator();

                while (mutesIterator.hasNext()) {
                    Map.Entry<String, String> mute = mutesIterator.next();

                    if (mute.getValue().equalsIgnoreCase(target.getFriendlyName())) {
                        Player bukkitPlayer = FoxtrotPlugin.getInstance().getServer().getPlayerExact(mute.getKey());

                        if (bukkitPlayer != null) {
                            bukkitPlayer.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Your faction's mute has expired!");
                        }

                        mutesIterator.remove();
                    }
                }
            }

        }.runTaskLater(FoxtrotPlugin.getInstance(), timeSeconds * 20L);

        sender.sendMessage(ChatColor.GRAY + "Muted the faction " + target.getFriendlyName() + ChatColor.GRAY + " for " + TimeUtils.getDurationBreakdown(timeSeconds * 1000L) + " for " + reason + ".");
    }

    @Command(names={ "team unmute", "t unmute", "f unmute", "faction unmute", "fac unmute" }, permissionNode="foxtrot.mutefaction")
    public static void teamUnmuteFaction(Player sender, @Param(name="team") final Team target) {
        FactionActionTracker.logAction(target, "actions", "Mute: Faction mute removed. [Unmuted by: " + sender.getName() + "]");
        Iterator<Map.Entry<String, String>> mutesIterator = factionMutes.entrySet().iterator();

        while (mutesIterator.hasNext()) {
            Map.Entry<String, String> mute = mutesIterator.next();

            if (mute.getValue().equalsIgnoreCase(target.getFriendlyName())) {
                Player bukkitPlayer = FoxtrotPlugin.getInstance().getServer().getPlayerExact(mute.getKey());

                if (bukkitPlayer != null) {
                    bukkitPlayer.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Your faction's mute has been removed!");
                }

                mutesIterator.remove();
            }
        }

        sender.sendMessage(ChatColor.GRAY + "Unmuted the faction " + target.getFriendlyName() + ChatColor.GRAY  + ".");
    }

}