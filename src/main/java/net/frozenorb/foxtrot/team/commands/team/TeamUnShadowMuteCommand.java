package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionTracker;
import net.frozenorb.foxtrot.teamactiontracker.enums.TeamActionType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Map;

public class TeamUnShadowMuteCommand {

    @Command(names={ "team unshadowmute", "t unshadowmute", "f unshadowmute", "faction unshadowmute", "fac unshadowmute" }, permissionNode="foxtrot.mutefaction")
    public static void teamUnShadowmuteFaction(Player sender, @Param(name="team") Team target) {
        TeamActionTracker.logActionAsync(target, TeamActionType.GENERAL, "Mute: Team shadowmute removed. [Unmuted by: " + sender.getName() + "]");
        Iterator<Map.Entry<String, String>> mutesIterator = TeamShadowMuteCommand.getTeamShadowMutes().entrySet().iterator();

        while (mutesIterator.hasNext()) {
            java.util.Map.Entry<String, String> mute = mutesIterator.next();

            if (mute.getValue().equalsIgnoreCase(target.getName())) {
                mutesIterator.remove();
            }
        }

        sender.sendMessage(ChatColor.GRAY + "Un-shadowmuted the team " + target.getName() + ChatColor.GRAY  + ".");
    }

}