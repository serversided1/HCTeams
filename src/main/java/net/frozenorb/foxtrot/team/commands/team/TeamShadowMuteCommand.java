package net.frozenorb.foxtrot.team.commands.team;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionTracker;
import net.frozenorb.foxtrot.teamactiontracker.enums.TeamActionType;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class TeamShadowMuteCommand {

    @Getter public static Map<UUID, String> teamShadowMutes = new HashMap<>();

    @Command(names={ "team shadowmute", "t shadowmute", "f shadowmute", "faction shadowmute", "fac shadowmute" }, permissionNode="foxtrot.mutefaction")
    public static void teamShadowMuteFaction(Player sender, @Parameter(name="team") final Team target, @Parameter(name="minutes") int time) {
        int timeSeconds = time * 60;

        for (UUID player : target.getMembers()) {
            teamShadowMutes.put(player, target.getName());
        }

        TeamActionTracker.logActionAsync(target, TeamActionType.GENERAL, "Mute: Team shadowmute added. [Duration: " + time + ", Muted by: " + sender.getName() + "]");

        new BukkitRunnable() {

            public void run() {
                TeamActionTracker.logActionAsync(target, TeamActionType.GENERAL, "Mute: Team shadowmute expired.");

                Iterator<java.util.Map.Entry<UUID, String>> mutesIterator = teamShadowMutes.entrySet().iterator();

                while (mutesIterator.hasNext()) {
                    java.util.Map.Entry<UUID, String> mute = mutesIterator.next();

                    if (mute.getValue().equalsIgnoreCase(target.getName())) {
                        mutesIterator.remove();
                    }
                }
            }

        }.runTaskLater(Foxtrot.getInstance(), timeSeconds * 20L);

        sender.sendMessage(ChatColor.YELLOW + "Shadow muted the team " + target.getName() + ChatColor.GRAY + " for " + TimeUtils.formatIntoMMSS(timeSeconds) + ".");
    }

}