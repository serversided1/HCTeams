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

    @Command(names={ "team promote", "t promote", "f promote", "faction promote", "fac promote", "team captain", "t captain", "f captain", "faction captain", "fac captain" }, permissionNode="")
    public static void teamPromote(Player sender, @Parameter(name="Player") UUID target) {
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

        if (team.isCaptain(target)) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(target) + " is already a captain!");
            return;
        }

        team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(target) + " has been promoted to Captain!");
        team.addCaptain(target);
    }

}