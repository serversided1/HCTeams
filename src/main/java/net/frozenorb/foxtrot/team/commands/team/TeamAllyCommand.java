package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.nametag.NametagManager;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamAllyCommand {

    @Command(names={ "team ally", "t ally", "f ally", "faction ally", "fac ally" }, permissionNode="")
    public static void teamAlly(Player sender, @Param(name="team") Team targetTeam) {
        Team senderTeam = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

        if (senderTeam == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!(senderTeam.isOwner(sender.getName()) || senderTeam.isCaptain(sender.getName()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }

        if (senderTeam.equals(targetTeam)) {
            sender.sendMessage(ChatColor.RED + "You cannot ally your own team!");
            return;
        }

        if (senderTeam.getAllies().size() >= Team.MAX_ALLIES) {
            sender.sendMessage(ChatColor.RED + "Your team already has the max number of allies, which is " + Team.MAX_ALLIES + ".");
            return;
        }

        if (targetTeam.getAllies().size() >= Team.MAX_ALLIES) {
            sender.sendMessage(ChatColor.RED + "The team you're trying to ally already has the max number of allies, which is " + Team.MAX_ALLIES + ".");
            return;
        }

        if (senderTeam.isAlly(targetTeam)) {
            sender.sendMessage(ChatColor.RED + "You're already allied to " + targetTeam.getName() + ".");
        }

        if (senderTeam.getRequestedAllies().contains(targetTeam.getUniqueId())) {
            senderTeam.getRequestedAllies().remove(targetTeam.getUniqueId());

            targetTeam.getAllies().add(senderTeam.getUniqueId());
            senderTeam.getAllies().add(targetTeam.getUniqueId());

            targetTeam.flagForSave();
            senderTeam.flagForSave();

            for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                if (targetTeam.isMember(player)) {
                    player.sendMessage(ChatColor.BLUE + senderTeam.getName() + ChatColor.YELLOW + " has accepted your request to ally. You have " + ChatColor.LIGHT_PURPLE + targetTeam.getAllies().size() + "/" + Team.MAX_ALLIES + " allies" + ChatColor.YELLOW + ".");
                } else if (senderTeam.isMember(player)) {
                    player.sendMessage(ChatColor.YELLOW + "Your team has allied " + targetTeam.getName(sender) + ChatColor.YELLOW + ". You have " + ChatColor.LIGHT_PURPLE + senderTeam.getAllies().size() + "/" + Team.MAX_ALLIES + " allies" + ChatColor.YELLOW + ".");
                }

                if (targetTeam.isMember(player) || senderTeam.isMember(player)) {
                    NametagManager.reloadPlayer(player);
                }
            }
        } else {
            if (targetTeam.getRequestedAllies().contains(senderTeam.getUniqueId())) {
                sender.sendMessage(ChatColor.YELLOW + "You have already requested to ally " + ChatColor.BLUE + targetTeam.getName() + ChatColor.YELLOW + ".");
                return;
            }

            targetTeam.getRequestedAllies().add(senderTeam.getUniqueId());
            targetTeam.flagForSave();

            for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                if (targetTeam.isMember(player)) {
                    player.sendMessage(ChatColor.BLUE + senderTeam.getName() + ChatColor.YELLOW + " has requested to be your ally. Type " + ChatColor.LIGHT_PURPLE + "/team ally " + senderTeam.getName() + ChatColor.YELLOW + " to accept.");
                } else if (senderTeam.isMember(player)) {
                    player.sendMessage(ChatColor.YELLOW + "Your team has requested to ally " + ChatColor.BLUE + targetTeam.getName() + ChatColor.YELLOW + ".");
                }
            }
        }

    }

}