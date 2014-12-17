package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.nametag.NametagManager;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamUnallyCommand {

    @Command(names={ "team unally", "t unally", "f unally", "faction unally", "fac unally" }, permissionNode="")
    public static void teamUnally(Player sender, @Param(name="team") Team targetTeam) {
        Team senderTeam = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

        if (senderTeam == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!(senderTeam.isOwner(sender.getName()) || senderTeam.isCaptain(sender.getName()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }

        if (!senderTeam.isAlly(targetTeam)) {
            sender.sendMessage(ChatColor.RED + "You are not allied to " + targetTeam.getName() + "!");
            return;
        }

        senderTeam.getAllies().remove(targetTeam.getUniqueId());
        targetTeam.getAllies().remove(senderTeam.getUniqueId());

        senderTeam.flagForSave();
        targetTeam.flagForSave();

        for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
            if (targetTeam.isMember(player)) {
                player.sendMessage(senderTeam.getName(player) + ChatColor.YELLOW + " has dropped their alliance with your team.");
            } else if (senderTeam.isMember(player)) {
                player.sendMessage(ChatColor.YELLOW + "Your team has dropped its alliance with " + targetTeam.getName(sender) + ChatColor.YELLOW + ".");
            }

            if (targetTeam.isMember(player) || senderTeam.isMember(player)) {
                NametagManager.reloadPlayer(player);
            }
        }
    }

}