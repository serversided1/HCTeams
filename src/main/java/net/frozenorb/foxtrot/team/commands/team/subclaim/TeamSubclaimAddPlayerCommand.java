package net.frozenorb.foxtrot.team.commands.team.subclaim;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.qlib.command.annotations.Parameter;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class TeamSubclaimAddPlayerCommand {

    @Command(names={ "team subclaim addplayer", "t subclaim addplayer", "f subclaim addplayer", "faction subclaim addplayer", "fac subclaim addplayer", "team sub addplayer", "t sub addplayer", "f sub addplayer", "faction sub addplayer", "fac sub addplayer", "team subclaim grant", "t subclaim grant", "f subclaim grant", "faction subclaim grant", "fac subclaim grant", "team sub grant", "t sub grant", "f sub grant", "faction sub grant", "fac sub grant" }, permissionNode="")
    public static void teamSubclaimAddPlayer(Player sender, @Parameter(name="subclaim") Subclaim subclaim, @Parameter(name="player") OfflinePlayer player) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

        if (!team.getOwner().equals(sender.getName()) && !team.isCaptain(sender.getName())) {
            sender.sendMessage(ChatColor.RED + "Only the team captains can do this.");
            return;
        }

        if (!team.isMember(player.getName())) {
            sender.sendMessage(ChatColor.RED + player.getName() + " is not on your team!");
            return;
        }

        if (subclaim.isMember(player.getName())) {
            sender.sendMessage(ChatColor.RED + "The player already has access to that subclaim!");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + team.getActualPlayerName(player.getName()) + ChatColor.YELLOW + " has been added to the subclaim " + ChatColor.GREEN + subclaim.getName() + ChatColor.YELLOW + ".");
        subclaim.addMember(player.getName());
        team.flagForSave();
    }

}