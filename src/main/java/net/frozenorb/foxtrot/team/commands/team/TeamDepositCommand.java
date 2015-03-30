package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionTracker;
import net.frozenorb.foxtrot.teamactiontracker.enums.TeamActionType;
import net.frozenorb.mBasic.Basic;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamDepositCommand {

    @Command(names={ "team deposit", "t deposit", "f deposit", "faction deposit", "fac deposit", "team d", "t d", "f d", "faction d", "fac d", "team m d", "t m d", "f m d", "faction m d", "fac m d" }, permissionNode="")
    public static void teamDeposit(Player sender, @Parameter(name="amount") float amount) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (amount <= 0 || Float.isNaN(amount)) {
            sender.sendMessage(ChatColor.RED + "You can't deposit $0.0 (or less)!");
            return;
        }

        if (Basic.get().getEconomyManager().getBalance(sender.getName()) > 100000) {
            sender.sendMessage("§cYour balance is too high to deposit money. Please contact an admin to do this.");
            Bukkit.getLogger().severe("[ECONOMY] " + sender.getName() + " tried to deposit " + amount + "to " + team);
            return;
        }

        if (Float.isNaN(amount)) {
            sender.sendMessage(ChatColor.RED + "Nope.");
            return;
        }

        if (Basic.get().getEconomyManager().getBalance(sender.getName()) < amount) {
            sender.sendMessage(ChatColor.RED + "You don't have enough money to do this!");
            return;
        }

        Basic.get().getEconomyManager().withdrawPlayer(sender.getName(), amount);

        sender.sendMessage(ChatColor.YELLOW + "You have added " + ChatColor.LIGHT_PURPLE + amount + ChatColor.YELLOW + " to the team balance!");
        TeamActionTracker.logActionAsync(team, TeamActionType.GENERAL, "Balance Change: $" + team.getBalance() + " -> $" + (team.getBalance() + amount) + " [Amount: " + amount + ", Deposited by: " + sender.getName() + "]");
        team.setBalance(team.getBalance() + amount);

        for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            if (team.isMember(player.getUniqueId())) {
                player.sendMessage(ChatColor.YELLOW + sender.getName() + " deposited " + ChatColor.LIGHT_PURPLE + amount + ChatColor.YELLOW + " into the team balance.");
            }
        }
    }

}