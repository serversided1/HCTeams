package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamDemoteCommand {

    @Command(names={ "team demote", "t demote", "f demote", "faction demote", "fac demote", "team delcaptain", "t delcaptain", "f delcaptain", "faction delcaptain", "fac delcaptain" }, permissionNode="")
    public static void teamDemote(Player sender, @Parameter(name="player") UUID target) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!team.isOwner(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team owners can do this.");
            return;
        }

        if (!team.isMember(target)) {
            sender.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(target) + " is not on your team.");
            return;
        }

        if (!team.isCaptain(target)) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(target) + " isn't a captain!");
            return;
        }

        team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(target) + " has been demoted from Captain!");
        team.removeCaptain(target);
    }

}