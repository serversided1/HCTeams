package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionTracker;
import net.frozenorb.foxtrot.teamactiontracker.enums.TeamActionType;
import net.frozenorb.mBasic.Basic;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamWithdrawCommand {

    @Command(names={ "team withdraw", "t withdraw", "f withdraw", "faction withdraw", "fac withdraw", "team w", "t w", "f w", "faction w", "fac w" }, permissionNode="")
    public static void teamWithdraw(Player sender, @Parameter(name = "amount") float amount) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.getBalance() > 250000) {
            sender.sendMessage("§cYour team has too much money to withdraw right now. Please contact an admin.");
            return;
        }

        if (team.isCaptain(sender.getUniqueId()) || team.isOwner(sender.getUniqueId())) {
            if (team.getBalance() < amount) {
                sender.sendMessage(ChatColor.RED + "The team doesn't have enough money to do this!");
                return;
            }

            if (Double.isNaN(team.getBalance())) {
                sender.sendMessage(ChatColor.RED + "You cannot withdraw money because your team's balance is broken!");
                return;
            }

            if (amount <= 0) {
                sender.sendMessage(ChatColor.RED + "You can't withdraw $0.0 (or less)!");
                return;
            }

            if (amount == Float.NaN) {
                sender.sendMessage(ChatColor.RED + "Nope.");
                return;
            }

            Basic.get().getEconomyManager().depositPlayer(sender.getName(), amount);
            sender.sendMessage(ChatColor.YELLOW + "You have withdrawn " + ChatColor.LIGHT_PURPLE + amount + ChatColor.YELLOW + " from the team balance!");

            TeamActionTracker.logActionAsync(team, TeamActionType.GENERAL, "Balance Change: $" + team.getBalance() + " -> $" + (team.getBalance() - amount) + " [Amount: " + amount + ", Withdrew by: " + sender.getName() + "]");
            team.setBalance(team.getBalance() - amount);
            team.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.YELLOW + " withdrew " + ChatColor.LIGHT_PURPLE + "$" + amount + ChatColor.YELLOW + " from the team balance.");
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
    }

}