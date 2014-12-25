package net.frozenorb.foxtrot.raffle.commands.raffle;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.raffle.RaffleHandler;
import net.frozenorb.foxtrot.raffle.enums.RaffleAchievement;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RaffleGiveAchievementCommand {

    @Command(names={ "Raffle GiveAchievement" }, permissionNode="op")
    public static void raffleGiveAchievement(CommandSender sender, @Param(name="player") Player target, @Param(name="achievement") String achievement) {
        try {
            RaffleAchievement raffleAchievement = RaffleAchievement.valueOf(achievement.toUpperCase());

            FoxtrotPlugin.getInstance().getRaffleHandler().giveRaffleAchievement(target, raffleAchievement);
            sender.sendMessage(RaffleHandler.PREFIX + " " + ChatColor.YELLOW + "Gave " + ChatColor.GOLD + target.getName() + ChatColor.YELLOW + " the " + ChatColor.GOLD + raffleAchievement.getName() + ChatColor.YELLOW + " raffle achievement.");
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Failed to parse a raffle achievement type from " + achievement + ".");
        }
    }

}