package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.factionactiontracker.FactionActionTracker;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.mBasic.Basic;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Deposit {

    @Command(names={ "team deposit", "t deposit", "f deposit", "faction deposit", "fac deposit", "team d", "t d", "f d", "faction d", "fac d" }, permissionNode="")
    public static void teamDeposit(Player sender, @Param(name="amount") int amount) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "You can't deposit $0.0 (or less)!");
            return;
        }

        if (Basic.get().getEconomyManager().getBalance(sender.getName()) < amount) {
            sender.sendMessage(ChatColor.RED + "You don't have enough money to do this!");
            return;
        }

        Basic.get().getEconomyManager().withdrawPlayer(sender.getName(), amount);

        sender.sendMessage(ChatColor.YELLOW + "You have added " + ChatColor.LIGHT_PURPLE + amount + ChatColor.YELLOW + " to the team balance!");
        FactionActionTracker.logAction(team, "actions", "Balance Change: $" + team.getBalance() + " -> $" + (team.getBalance() + amount) + " [Amount: " + amount + ", Deposited by: " + sender.getName() + "]");
        team.setBalance(team.getBalance() + amount);
        team.getOnlineMembers().forEach(pe -> pe.sendMessage("§e" + sender.getName() + " deposited §d" + amount + " §einto the team balance."));
	}

}