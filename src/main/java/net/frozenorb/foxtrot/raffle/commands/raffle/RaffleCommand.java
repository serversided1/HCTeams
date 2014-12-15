package net.frozenorb.foxtrot.raffle.commands.raffle;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.raffle.RaffleHandler;
import net.frozenorb.foxtrot.raffle.data.PlayerRaffleData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Calendar;

public class RaffleCommand {

    @Command(names={ "Raffle" }, permissionNode="")
    public static void raffle(Player sender) {
        if (!FoxtrotPlugin.getInstance().getRaffleHandler().getRaffleData().containsKey(sender.getName())) {
            sender.sendMessage(RaffleHandler.PREFIX + " " + ChatColor.RED + "We don't have raffle data for you! Please contact a HCTeams staff member!");
            return;
        }

        PlayerRaffleData playerRaffleData = FoxtrotPlugin.getInstance().getRaffleHandler().getRaffleData().get(sender.getName());
        int weekIndex = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);

        sender.sendMessage(RaffleHandler.PREFIX + " " + ChatColor.YELLOW + "Raffle data for " + sender.getDisplayName() + ChatColor.YELLOW + ":");
        sender.sendMessage(ChatColor.GOLD + "Total Entries: " + ChatColor.WHITE + playerRaffleData.getTotalEntries());
        sender.sendMessage(ChatColor.GOLD + "Entries this Week: " + ChatColor.WHITE + (playerRaffleData.getWeekEntries().containsKey(weekIndex) ? playerRaffleData.getWeekEntries().get(weekIndex) : 0));
        sender.sendMessage(ChatColor.GOLD + "Achievements: " + ChatColor.WHITE + "Use /raffle listachievements to view.");
    }

}