package net.frozenorb.foxtrot.command.commands.team;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.nametag.NametagManager;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class TeamAcceptCommand {

    @Command(names={ "team accept", "t accept", "f accept", "faction accept", "fac accept", "team a", "t a", "f a", "faction a", "fac a", "team join", "t join", "f join", "faction join", "fac join" }, permissionNode="")
    public static void teamAccept(Player sender, @Param(name="team") Team target) {
        if (target.getInvitations().contains(sender.getName())) {
            if (FoxtrotPlugin.getInstance().getTeamHandler().isOnTeam(sender.getName())) {
                sender.sendMessage(ChatColor.RED + "You are already on a team!");
                return;
            }

            target.getInvitations().remove(sender.getName());
            target.addMember(sender.getName());

            FoxtrotPlugin.getInstance().getTeamHandler().setTeam(sender.getName(), target);

            for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                if (target.isMember(player)) {
                    player.sendMessage(ChatColor.YELLOW + sender.getName() + " has joined the team!");
                }
            }

            NametagManager.reloadPlayer(sender);
            NametagManager.sendTeamsToPlayer(sender);
        } else {
            sender.sendMessage(ChatColor.RED + "This team has not invited you!");
        }
	}

}