package net.frozenorb.foxtrot.team.commands.team.subclaim;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamSubclaimListCommand {

    @Command(names={ "team subclaim list", "t subclaim list", "f subclaim list", "faction subclaim list", "fac subclaim list", "team sub list", "t sub list", "f sub list", "faction sub list", "fac sub list" }, permissionNode="")
    public static void teamSubclaimList(Player sender) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

        if (team == null) {
            sender.sendMessage(ChatColor.RED + "You must be on a team to execute this command!");
            return;
        }

        StringBuilder access = new StringBuilder();
        StringBuilder other = new StringBuilder();

        for (Subclaim scs : team.getSubclaims()) {
            if (scs.isMember(sender.getName()) || team.getOwner().equalsIgnoreCase(sender.getName()) || team.isCaptain(sender.getName())) {
                access.append(scs.getName()).append(", ");
                continue;
            }

            other.append(scs).append(", ");
        }

        if (access.length() > 2) {
            access.setLength(access.length() - 2);
        }

        if (other.length() > 2) {
            other.setLength(other.length() - 2);
        }

        sender.sendMessage(ChatColor.BLUE + team.getName() + ChatColor.YELLOW + " Subclaim List");
        sender.sendMessage(ChatColor.YELLOW + "Subclaims you can access: " + ChatColor.WHITE + access.toString());
        sender.sendMessage(ChatColor.YELLOW + "Other Subclaims: " + ChatColor.WHITE + other.toString());
    }

}