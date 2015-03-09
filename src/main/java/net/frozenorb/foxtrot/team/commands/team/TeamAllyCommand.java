package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.qlib.nametag.FrozenNametagHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamAllyCommand {

    @Command(names={ "team ally", "t ally", "f ally", "faction ally", "fac ally" }, permissionNode="")
    public static void teamAlly(Player sender, @Parameter(name="team") Team targetTeam) {
        Team senderTeam = FoxtrotPlugin.getInstance().getTeamHandler().getTeam(sender);

        if (senderTeam == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!(senderTeam.isOwner(sender.getUniqueId()) || senderTeam.isCaptain(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }

        if (senderTeam.equals(targetTeam)) {
            sender.sendMessage(ChatColor.YELLOW + "You cannot ally your own team!");
            return;
        }

        if (senderTeam.getAllies().size() >= Team.MAX_ALLIES) {
            sender.sendMessage(ChatColor.YELLOW + "Your team already has the max number of allies, which is " + Team.MAX_ALLIES + ".");
            return;
        }

        if (targetTeam.getAllies().size() >= Team.MAX_ALLIES) {
            sender.sendMessage(ChatColor.YELLOW + "The team you're trying to ally already has the max number of allies, which is " + Team.MAX_ALLIES + ".");
            return;
        }

        if (senderTeam.isAlly(targetTeam)) {
            sender.sendMessage(ChatColor.YELLOW + "You're already allied to " + targetTeam.getName(sender) + ChatColor.YELLOW + ".");
            return;
        }

        if (senderTeam.getRequestedAllies().contains(targetTeam.getUniqueId())) {
            senderTeam.getRequestedAllies().remove(targetTeam.getUniqueId());

            targetTeam.getAllies().add(senderTeam.getUniqueId());
            senderTeam.getAllies().add(targetTeam.getUniqueId());

            targetTeam.flagForSave();
            senderTeam.flagForSave();

            for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                if (targetTeam.isMember(player.getUniqueId())) {
                    player.sendMessage(senderTeam.getName(player) + ChatColor.YELLOW + " has accepted your request to ally. You now have " + Team.ALLY_COLOR + targetTeam.getAllies().size() + "/" + Team.MAX_ALLIES + " allies" + ChatColor.YELLOW + ".");
                } else if (senderTeam.isMember(player.getUniqueId())) {
                    player.sendMessage(ChatColor.YELLOW + "Your team has allied " + targetTeam.getName(sender) + ChatColor.YELLOW + ". You now have " + Team.ALLY_COLOR + senderTeam.getAllies().size() + "/" + Team.MAX_ALLIES + " allies" + ChatColor.YELLOW + ".");
                }

                if (targetTeam.isMember(player.getUniqueId()) || senderTeam.isMember(player.getUniqueId())) {
                    FrozenNametagHandler.reloadPlayer(sender);
                    FrozenNametagHandler.reloadOthersFor(sender);
                }
            }
        } else {
            if (targetTeam.getRequestedAllies().contains(senderTeam.getUniqueId())) {
                sender.sendMessage(ChatColor.YELLOW + "You have already requested to ally " + targetTeam.getName(sender) + ChatColor.YELLOW + ".");
                return;
            }

            targetTeam.getRequestedAllies().add(senderTeam.getUniqueId());
            targetTeam.flagForSave();

            for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                if (targetTeam.isMember(player.getUniqueId())) {
                    player.sendMessage(senderTeam.getName(player.getPlayer()) + ChatColor.YELLOW + " has requested to be your ally. Type " + Team.ALLY_COLOR + "/team ally " + senderTeam.getName() + ChatColor.YELLOW + " to accept.");
                } else if (senderTeam.isMember(player.getUniqueId())) {
                    player.sendMessage(ChatColor.YELLOW + "Your team has requested to ally " + targetTeam.getName(player) + ChatColor.YELLOW + ".");
                }
            }
        }
    }

}