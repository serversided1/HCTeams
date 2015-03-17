package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.qlib.nametag.FrozenNametagHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamUnallyCommand {

    @Command(names={ "team unally", "t unally", "f unally", "faction unally", "fac unally" }, permissionNode="")
    public static void teamUnally(Player sender, @Parameter(name="team") Team targetTeam) {
        Team senderTeam = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (senderTeam == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!(senderTeam.isOwner(sender.getUniqueId()) || senderTeam.isCaptain(sender.getUniqueId()))) {
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

        for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            if (targetTeam.isMember(player.getUniqueId())) {
                player.sendMessage(senderTeam.getName(player) + ChatColor.YELLOW + " has dropped their alliance with your team.");
            } else if (senderTeam.isMember(player.getUniqueId())) {
                player.sendMessage(ChatColor.YELLOW + "Your team has dropped its alliance with " + targetTeam.getName(sender) + ChatColor.YELLOW + ".");
            }

            if (targetTeam.isMember(player.getUniqueId()) || senderTeam.isMember(player.getUniqueId())) {
                FrozenNametagHandler.reloadPlayer(sender);
                FrozenNametagHandler.reloadOthersFor(sender);
            }
        }
    }

}