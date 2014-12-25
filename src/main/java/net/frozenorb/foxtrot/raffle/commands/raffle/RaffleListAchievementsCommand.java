package net.frozenorb.foxtrot.raffle.commands.raffle;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.raffle.RaffleHandler;
import net.frozenorb.foxtrot.raffle.data.PlayerRaffleData;
import net.frozenorb.foxtrot.raffle.enums.RaffleAchievement;
import net.frozenorb.foxtrot.raffle.enums.RaffleAchievementType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Calendar;

public class RaffleListAchievementsCommand {

    @Command(names={ "Raffle ListAchievements", "Raffle Achievements" }, permissionNode="")
    public static void raffleListAchievements(Player sender) {
        if (!FoxtrotPlugin.getInstance().getRaffleHandler().getRaffleData().containsKey(sender.getName())) {
            sender.sendMessage(RaffleHandler.PREFIX + " " + ChatColor.RED + "We don't have raffle data for you! Please contact a HCTeams staff member!");
            return;
        }

        PlayerRaffleData playerRaffleData = FoxtrotPlugin.getInstance().getRaffleHandler().getRaffleData().get(sender.getName());

        sender.sendMessage(RaffleHandler.PREFIX + " " + ChatColor.YELLOW + "Raffle achievements: " + ChatColor.AQUA + "");

        for (RaffleAchievementType achievementType : RaffleAchievementType.values()) {
            if (achievementType == RaffleAchievementType.DONATION_ACHIEVEMENTS) {
                continue;
            }

            sender.sendMessage("");
            sender.sendMessage(ChatColor.BLUE + achievementType.getName() + ": " + ChatColor.AQUA + "(+" + achievementType.getEntries() + " " + (achievementType.getEntries() == 1 ? "entry" : "entries") + ")");

            for (RaffleAchievement achievement : RaffleAchievement.values()) {
                if (achievement.getType() != achievementType) {
                    continue;
                }

                String prefix = " " + (playerRaffleData.getLastEarned().containsKey(achievement) ? ChatColor.GREEN : ChatColor.RED) + achievement.getName() + ": " + ChatColor.WHITE + achievement.getDescription();

                if (achievement.getMaxProgress() != -1) {
                    sender.sendMessage(prefix + ChatColor.GRAY + " [" + (playerRaffleData.getProgress().containsKey(achievement) ? playerRaffleData.getProgress().get(achievement) : 0) + "/" + achievement.getMaxProgress() + "]");
                } else if (achievement.getType() == RaffleAchievementType.DAILY_ACHIEVEMENTS) {
                    if (playerRaffleData.getLastEarned().containsKey(achievement)) {
                        long earned = playerRaffleData.getLastEarned().get(achievement);
                        Calendar then = Calendar.getInstance();

                        then.setTimeInMillis(earned);

                        // If they day index is the same, they can't earn it again.
                        if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == then.get(Calendar.DAY_OF_MONTH)) {
                            sender.sendMessage(" " + ChatColor.GREEN + achievement.getName() + ": " + ChatColor.WHITE + achievement.getDescription() + ChatColor.GRAY + " [Can be earned tomorrow]");
                            continue;
                        }
                    }

                    sender.sendMessage(" " + ChatColor.RED + achievement.getName() + ": " + ChatColor.WHITE + achievement.getDescription() + ChatColor.GRAY + " [Can be earned now]");
                } else {
                    sender.sendMessage(prefix);
                }
            }
        }
    }

}