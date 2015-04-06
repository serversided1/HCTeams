package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

@SuppressWarnings("deprecation")
public class TeamLeaderCommand {

    @Command(names={ "team newleader", "t newleader", "f newleader", "faction newleader", "fac newleader", "team leader", "t leader", "f leader", "faction leader", "fac leader" }, permissionNode="")
    public static void teamLeader(Player sender, @Parameter(name="player") UUID target) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!team.isOwner(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only the team leader can do this.");
            return;
        }

        if (!team.isMember(target)) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(target) + " is not on your team.");
            return;
        }

        team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(target) + " has been given ownership of " + team.getName() + ".");
        team.setOwner(target);
        team.addCaptain(sender.getUniqueId());
    }

}