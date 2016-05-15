package net.frozenorb.foxtrot.team.commands.team;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionTracker;
import net.frozenorb.foxtrot.teamactiontracker.enums.TeamActionType;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class TeamMuteCommand {

    @Getter private static Map<UUID, String> teamMutes = new HashMap<>();

    @Command(names={ "team mute", "t mute", "f mute", "faction mute", "fac mute" }, permission="foxtrot.mutefaction")
    public static void teamMute(Player sender, @Param(name="team") final Team team, @Param(name="time") int time, @Param(name="reason", wildcard=true) String reason) {
        int timeSeconds = time * 60;

        for (UUID player : team.getMembers()) {
            teamMutes.put(player, team.getName());

            Player bukkitPlayer = Foxtrot.getInstance().getServer().getPlayer(player);

            if (bukkitPlayer != null) {
                bukkitPlayer.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Your team has been muted for " + TimeUtils.formatIntoMMSS(timeSeconds) + " for " + reason + ".");
            }
        }

        TeamActionTracker.logActionAsync(team, TeamActionType.GENERAL, "Mute: Team mute added. [Duration: " + time + ", Muted by: " + sender.getName() + "]");

        new BukkitRunnable() {

            public void run() {
                TeamActionTracker.logActionAsync(team, TeamActionType.GENERAL, "Mute: Team mute expired.");

                Iterator<Map.Entry<UUID, String>> mutesIterator = teamMutes.entrySet().iterator();

                while (mutesIterator.hasNext()) {
                    Map.Entry<UUID, String> mute = mutesIterator.next();

                    if (mute.getValue().equalsIgnoreCase(team.getName())) {
                        mutesIterator.remove();
                    }
                }
            }

        }.runTaskLater(Foxtrot.getInstance(), timeSeconds * 20L);

        sender.sendMessage(ChatColor.YELLOW + "Muted the team " + team.getName() + ChatColor.GRAY + " for " + TimeUtils.formatIntoMMSS(timeSeconds) + " for " + reason + ".");
    }

}