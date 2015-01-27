package net.frozenorb.foxtrot.team.commands.team.subclaim;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamSubclaimUnclaimCommand {

    @Command(names={ "team subclaim unclaim", "t subclaim unclaim", "f subclaim unclaim", "faction subclaim unclaim", "fac subclaim unclaim", "team subclaim unsubclaim", "t subclaim unsubclaim", "f subclaim unsubclaim", "faction subclaim unsubclaim", "fac subclaim unsubclaim", "team unsubclaim", "t unsubclaim", "f unsubclaim", "faction unsubclaim", "fac unsubclaim"}, permissionNode="")
    public static void teamSubclaimUnclaim(Player sender, @Param(name="subclaim", defaultValue="location") Subclaim subclaim) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

        if (team.isOwner(sender.getName()) || team.isCaptain(sender.getName())) {
            team.getSubclaims().remove(subclaim);
            LandBoard.getInstance().notifySubclaimChange(subclaim);
            team.flagForSave();
            sender.sendMessage(ChatColor.RED + "You have unclaimed the subclaim " + ChatColor.YELLOW + subclaim.getName() + ChatColor.RED + ".");
        } else {
            sender.sendMessage(ChatColor.RED + "Only team captains can unclaim subclaims!");
        }
    }

}