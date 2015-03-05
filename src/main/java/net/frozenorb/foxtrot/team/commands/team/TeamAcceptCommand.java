package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.nametag.NametagManager;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRHandler;
import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.qlib.command.annotations.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class TeamAcceptCommand {

    @Command(names={ "team accept", "t accept", "f accept", "faction accept", "fac accept", "team a", "t a", "f a", "faction a", "fac a", "team join", "t join", "f join", "faction join", "fac join" }, permissionNode="")
    public static void teamAccept(Player sender, @Parameter(name="team") Team target) {
        if (target.getInvitations().contains(sender.getUniqueId())) {
            if (FoxtrotPlugin.getInstance().getTeamHandler().getTeam(sender) != null) {
                sender.sendMessage(ChatColor.RED + "You are already on a team!");
                return;
            }

            if (target.getMembers().size() >= Team.MAX_TEAM_SIZE) {
                sender.sendMessage(ChatColor.RED + target.getName() + " cannot be joined: Team is full!");
                return;
            }

            if (DTRHandler.isOnCooldown(target)) {
                sender.sendMessage(ChatColor.RED + target.getName() + " cannot be joined: Team not regenerating DTR!");
                return;
            }

            target.getInvitations().remove(sender.getUniqueId());
            target.addMember(sender.getUniqueId());
            FoxtrotPlugin.getInstance().getTeamHandler().setTeam(sender.getUniqueId(), target);

            for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                if (target.isMember(player.getUniqueId())) {
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