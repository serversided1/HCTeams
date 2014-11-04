package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.mBasic.Basic;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Withdraw {

    @Command(names={ "team withdraw", "t withdraw", "f withdraw", "faction withdraw", "fac withdraw", "team w", "t w", "f w", "faction w", "fac w" }, permissionNode="")
    public static void teamInvite(Player sender, @Param(name="amount") int amount) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isCaptain(sender.getName()) || team.isOwner(sender.getName())) {
            if (team.getBalance() < amount) {
                sender.sendMessage("§cThe team doesn't have enough money to do this!");
                return;
            }

            Basic.get().getEconomyManager().depositPlayer(sender.getName(), amount);
            sender.sendMessage(ChatColor.YELLOW + "You have withdrawn §d" + amount + "§e from the team balance!");

            team.setBalance(team.getBalance() - amount);
            team.getOnlineMembers().forEach(pe -> pe.sendMessage("§e" + sender.getName() + " withdrew §d" + amount + " §efrom the team balance."));
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
	}

}