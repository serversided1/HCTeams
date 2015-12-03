package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamPromoteCommand {

    @Command(names={ "team promote", "t promote", "f promote", "faction promote", "fac promote", "team captain", "t captain", "f captain", "faction captain", "fac captain", "team mod", "t mod", "f mod", "faction mod", "fac mod" }, permissionNode="")
    public static void teamPromote(Player sender, @Parameter(name="player") UUID player) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!team.isOwner(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team owners can do this.");
            return;
        }

        if (!team.isMember(player)) {
            sender.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player) + " is not on your team.");
            return;
        }

        if (team.isCaptain(player)) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " is already a captain!");
            return;
        }

        team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player) + " has been promoted to Captain!");
        team.addCaptain(player);
    }

}