package net.frozenorb.foxtrot.team.commands.team;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionTracker;
import net.frozenorb.foxtrot.teamactiontracker.enums.TeamActionType;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TeamShadowMuteCommand {

    @Getter public static Map<String, String> teamShadowMutes = new HashMap<>();

    @Command(names={ "team shadowmute", "t shadowmute", "f shadowmute", "faction shadowmute", "fac shadowmute" }, permissionNode="foxtrot.mutefaction")
    public static void teamShadowMuteFaction(Player sender, @Param(name="team") final Team target, @Param(name="minutes") String time) {
        int timeSeconds = Integer.parseInt(time) * 60;

        for (String player : target.getMembers()) {
            teamShadowMutes.put(player, target.getName());
        }

        TeamActionTracker.logActionAsync(target, TeamActionType.GENERAL, "Mute: Team shadowmute added. [Duration: " + time + ", Muted by: " + sender.getName() + "]");

        new BukkitRunnable() {

            public void run() {
                TeamActionTracker.logActionAsync(target, TeamActionType.GENERAL, "Mute: Team shadowmute expired.");

                Iterator<java.util.Map.Entry<String, String>> mutesIterator = teamShadowMutes.entrySet().iterator();

                while (mutesIterator.hasNext()) {
                    java.util.Map.Entry<String, String> mute = mutesIterator.next();

                    if (mute.getValue().equalsIgnoreCase(target.getName())) {
                        mutesIterator.remove();
                    }
                }
            }

        }.runTaskLater(FoxtrotPlugin.getInstance(), timeSeconds * 20L);

        sender.sendMessage(ChatColor.GRAY + "Shadow muted the team " + target.getName() + ChatColor.GRAY + " for " + TimeUtils.getDurationBreakdown(timeSeconds * 1000L) + ".");
    }

}