package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionTracker;
import net.frozenorb.foxtrot.teamactiontracker.enums.TeamActionType;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class TeamUnMuteCommand {

    @Command(names={ "team unmute", "t unmute", "f unmute", "faction unmute", "fac unmute" }, permissionNode="foxtrot.mutefaction")
    public static void teamUnmute(Player sender, @Parameter(name="team") Team target) {
        TeamActionTracker.logActionAsync(target, TeamActionType.GENERAL, "Mute: Team mute removed. [Unmuted by: " + sender.getName() + "]");
        Iterator<Map.Entry<UUID, String>> mutesIterator = TeamMuteCommand.getTeamMutes().entrySet().iterator();

        while (mutesIterator.hasNext()) {
            Map.Entry<UUID, String> mute = mutesIterator.next();

            if (mute.getValue().equalsIgnoreCase(target.getName())) {
                mutesIterator.remove();
            }
        }

        sender.sendMessage(ChatColor.GRAY + "Unmuted the team " + target.getName() + ChatColor.GRAY  + ".");
    }

}