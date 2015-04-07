package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRHandler;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.qlib.nametag.FrozenNametagHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class TeamAcceptCommand {

    @Command(names={ "team accept", "t accept", "f accept", "faction accept", "fac accept", "team a", "t a", "f a", "faction a", "fac a", "team join", "t join", "f join", "faction join", "fac join" }, permissionNode="")
    public static void teamAccept(Player sender, @Parameter(name="team") Team team) {
        if (team.getInvitations().contains(sender.getUniqueId())) {
            if (Foxtrot.getInstance().getTeamHandler().getTeam(sender) != null) {
                sender.sendMessage(ChatColor.RED + "You are already on a team!");
                return;
            }

            if (team.getMembers().size() >= Team.MAX_TEAM_SIZE) {
                sender.sendMessage(ChatColor.RED + team.getName() + " cannot be joined: Team is full!");
                return;
            }

            if (DTRHandler.isOnCooldown(team) && !Foxtrot.getInstance().getMapHandler().isKitMap()) {
                sender.sendMessage(ChatColor.RED + team.getName() + " cannot be joined: Team not regenerating DTR!");
                return;
            }

            team.getInvitations().remove(sender.getUniqueId());
            team.addMember(sender.getUniqueId());
            Foxtrot.getInstance().getTeamHandler().setTeam(sender.getUniqueId(), team);

            team.sendMessage(ChatColor.YELLOW + sender.getName() + " has joined the team!");

            FrozenNametagHandler.reloadPlayer(sender);
            FrozenNametagHandler.reloadOthersFor(sender);
        } else {
            sender.sendMessage(ChatColor.RED + "This team has not invited you!");
        }
    }

}