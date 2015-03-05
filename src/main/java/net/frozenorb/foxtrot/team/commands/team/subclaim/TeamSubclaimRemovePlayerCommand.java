package net.frozenorb.foxtrot.team.commands.team.subclaim;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.qlib.command.annotations.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class TeamSubclaimRemovePlayerCommand {

    @Command(names={ "team subclaim removeplayer", "t subclaim removeplayer", "f subclaim removeplayer", "faction subclaim removeplayer", "fac subclaim removeplayer", "team sub removeplayer", "t sub removeplayer", "f sub removeplayer", "faction sub removeplayer", "fac sub removeplayer", "team subclaim revoke", "t subclaim revoke", "f subclaim revoke", "faction subclaim revoke", "fac subclaim revoke", "team sub revoke", "t sub revoke", "f sub revoke", "faction sub revoke", "fac sub revoke" }, permissionNode="")
    public static void teamSubclaimRemovePlayer(Player sender, @Parameter(name="subclaim") Subclaim subclaim, @Parameter(name="player") OfflinePlayer target) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getTeam(sender);

        if (!team.isOwner(sender.getUniqueId()) && !team.isCaptain(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only the team captains can do this.");
            return;
        }

        if (!team.isMember(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is not on your team!");
            return;
        }

        if (!subclaim.isMember(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "The player already does not have access to that subclaim!");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + target.getName() + ChatColor.YELLOW + " has been removed from the subclaim " + ChatColor.GREEN + subclaim.getName() + ChatColor.YELLOW + ".");
        subclaim.removeMember(target.getUniqueId());
        team.flagForSave();
    }

}
