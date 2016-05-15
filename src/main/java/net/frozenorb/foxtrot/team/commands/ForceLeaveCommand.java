package net.frozenorb.foxtrot.team.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ForceLeaveCommand {

    @Command(names={ "forceleave" }, permission="foxtrot.forceleave")
    public static void forceLeave(Player player) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (team == null) {
            player.sendMessage(ChatColor.RED + "You are not on a team!");
            return;
        }

        team.removeMember(player.getUniqueId());
        Foxtrot.getInstance().getTeamHandler().setTeam(player.getUniqueId(), null);
        player.sendMessage(ChatColor.YELLOW + "Force left your team.");
    }

}