package net.frozenorb.foxtrot.raffle;

import com.mongodb.BasicDBObject;
import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.jedis.persist.PlaytimeMap;
import net.frozenorb.foxtrot.raffle.data.PlayerRaffleData;
import net.frozenorb.foxtrot.raffle.enums.RaffleAchievement;
import net.frozenorb.foxtrot.raffle.listeners.RaffleListener;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RaffleHandler implements Listener {

    public static final String PREFIX = ChatColor.LIGHT_PURPLE + "[Raffle]";
    @Getter private Map<String, PlayerRaffleData> raffleData = new HashMap<String, PlayerRaffleData>();

    public RaffleHandler() {
        FoxtrotPlugin.getInstance().getServer().getPluginManager().registerEvents(this, FoxtrotPlugin.getInstance());
        FoxtrotPlugin.getInstance().getServer().getPluginManager().registerEvents(new RaffleListener(), FoxtrotPlugin.getInstance());

        new BukkitRunnable() {

            public void run() {
                PlaytimeMap playtime = FoxtrotPlugin.getInstance().getPlaytimeMap();

                for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                    long playtimeTime = playtime.getPlaytime(player.getName()) + (playtime.getCurrentSession(player.getName()) / 1000L);

                    if (playtimeTime > TimeUnit.HOURS.toSeconds(1)) {
                        FoxtrotPlugin.getInstance().getRaffleHandler().giveRaffleAchievement(player, RaffleAchievement.FAMILIAR_FACE);
                    }

                    if (playtimeTime > TimeUnit.DAYS.toSeconds(1)) {
                        FoxtrotPlugin.getInstance().getRaffleHandler().giveRaffleAchievement(player, RaffleAchievement.ONE_DAY);
                    }

                    if (playtimeTime > TimeUnit.DAYS.toSeconds(3)) {
                        FoxtrotPlugin.getInstance().getRaffleHandler().giveRaffleAchievement(player, RaffleAchievement.THREE_DAYS);
                    }
                }
            }

        }.runTaskTimer(FoxtrotPlugin.getInstance(), 0L, 20 * 10);
    }

    public boolean giveRaffleAchievement(Player player, RaffleAchievement achievement) {
        // We don't have their data (we're likely async loading it at the moment)
        if (!getRaffleData().containsKey(player.getName())) {
            return (false);
        }

        PlayerRaffleData playerRaffleData = getRaffleData().get(player.getName());
        int weekIndex = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
        int entriesWithMultiplier = (int) (achievement.getType().getEntries() * getRaffleEntryMultiplier(player));

        switch (achievement.getType()) {
            case LONG_TERM_ACHIEVEMENTS:
            case PLAYER_FIRSTS:
                // You can never get a player first/long term achievement again.
                if (playerRaffleData.getLastEarned().containsKey(achievement)) {
                    return (false);
                }

                break;
            case DAILY_ACHIEVEMENTS:
                // You can only get a daily achievement every day.
                if (playerRaffleData.getLastEarned().containsKey(achievement)) {
                    long earned = playerRaffleData.getLastEarned().get(achievement);
                    Calendar then = Calendar.getInstance();

                    then.setTimeInMillis(earned);

                    // If they day index is the same, they can't earn it again.
                    if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == then.get(Calendar.DAY_OF_MONTH)) {
                        return (false);
                    }
                }

                break;
        }

        playerRaffleData.getLastEarned().put(achievement, System.currentTimeMillis());
        playerRaffleData.setTotalEntries(playerRaffleData.getTotalEntries() + entriesWithMultiplier);

        if (playerRaffleData.getWeekEntries().containsKey(weekIndex)) {
            playerRaffleData.getWeekEntries().put(weekIndex, playerRaffleData.getWeekEntries().get(weekIndex) + entriesWithMultiplier);
        } else {
            playerRaffleData.getWeekEntries().put(weekIndex, entriesWithMultiplier);
        }

        new BukkitRunnable() {

            public void run() {
                BasicDBObject updateQuery = new BasicDBObject();
                BasicDBObject raffleObject = new BasicDBObject();
                BasicDBObject achievementObject = new BasicDBObject();

                updateQuery.put("$inc", new BasicDBObject("WeekEntries." + weekIndex, entriesWithMultiplier).append("TotalEntries", entriesWithMultiplier));

                achievementObject.put("Name", achievement.getName());
                achievementObject.put("Description", achievement.getDescription());
                achievementObject.put("Type", achievement.getType().getName());

                raffleObject.put("Time", new Date().toString());
                raffleObject.put("Entries", achievement.getType().getEntries());
                raffleObject.put("EntriesWithMultiplier", entriesWithMultiplier);
                raffleObject.put("Achievement", achievementObject);

                updateQuery.put("$push", new BasicDBObject("Entries", raffleObject));
                updateQuery.put("$set", new BasicDBObject("LastEarned." + achievement, System.currentTimeMillis()));

                FoxtrotPlugin.getInstance().getMongoPool().getDB("HCTeams").getCollection("RafflePlayers").update(new BasicDBObject("Name", player.getName()), updateQuery);

                raffleObject.put("Player", player.getName());

                FoxtrotPlugin.getInstance().getMongoPool().getDB("HCTeams").getCollection("RaffleEntries").insert(raffleObject);
                FoxtrotPlugin.getInstance().getMongoPool().getDB("HCTeams").getCollection("RaffleEntries_Week" + weekIndex).insert(raffleObject);

            }

        }.runTaskAsynchronously(FoxtrotPlugin.getInstance());

        player.sendMessage(PREFIX + " " + ChatColor.YELLOW + "You have earned " + ChatColor.GOLD + entriesWithMultiplier + " raffle entries" + ChatColor.YELLOW + " for earning the " + ChatColor.GOLD + achievement.getName() + ChatColor.YELLOW + " achievement!");

        return (true);
    }

    public boolean giveRaffleAchievementProgress(Player player, RaffleAchievement achievement, int amount) {
        // We don't have their data (we're likely async loading it at the moment)
        if (!getRaffleData().containsKey(player.getName())) {
            return (false);
        }

        PlayerRaffleData playerRaffleData = getRaffleData().get(player.getName());

        if (playerRaffleData.getProgress().containsKey(achievement)) {
            playerRaffleData.getProgress().put(achievement, playerRaffleData.getProgress().get(achievement) + amount);
        } else {
            playerRaffleData.getProgress().put(achievement, amount);
        }

        new BukkitRunnable() {

            public void run() {
                BasicDBObject updateQuery = new BasicDBObject();

                updateQuery.put("$set", new BasicDBObject("Progress." + achievement, playerRaffleData.getProgress().get(achievement)));

                FoxtrotPlugin.getInstance().getMongoPool().getDB("HCTeams").getCollection("RafflePlayers").update(new BasicDBObject("Name", player.getName()), updateQuery);
            }

        }.runTaskAsynchronously(FoxtrotPlugin.getInstance());

        if (playerRaffleData.getProgress().get(achievement) >= achievement.getMaxProgress()) {
            giveRaffleAchievement(player, achievement);
        } else {
            player.sendMessage(PREFIX + " " + ChatColor.YELLOW + "Progress for " + ChatColor.GOLD + achievement.getName() + ChatColor.AQUA + " (+" + achievement.getType().getEntries() + " " + (achievement.getType().getEntries() == 1 ? "entry" : "entries") + ")" +  ChatColor.YELLOW + ": " + ChatColor.GREEN + playerRaffleData.getProgress().get(achievement) + ChatColor.GOLD + "/" + ChatColor.GREEN + achievement.getMaxProgress());
        }

        return (true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {

            public void run() {
                BasicDBObject result = (BasicDBObject) FoxtrotPlugin.getInstance().getMongoPool().getDB("HCTeams").getCollection("RafflePlayers").findOne(new BasicDBObject("Name", event.getPlayer().getName()));
                PlayerRaffleData playerRaffleData = new PlayerRaffleData();

                if (result != null) {
                    for (Map.Entry<String, Object> entry : ((BasicDBObject) result.get("LastEarned")).entrySet()) {
                        playerRaffleData.getLastEarned().put(RaffleAchievement.valueOf(entry.getKey()), (long) entry.getValue());
                    }

                    for (Map.Entry<String, Object> entry : ((BasicDBObject) result.get("WeekEntries")).entrySet()) {
                        playerRaffleData.getWeekEntries().put(Integer.valueOf(entry.getKey()), (int) entry.getValue());
                    }

                    for (Map.Entry<String, Object> entry : ((BasicDBObject) result.get("Progress")).entrySet()) {
                        playerRaffleData.getProgress().put(RaffleAchievement.valueOf(entry.getKey()), (int) entry.getValue());
                    }

                    playerRaffleData.setTotalEntries(result.getInt("TotalEntries"));
                } else {
                    result = new BasicDBObject();

                    result.put("Name", event.getPlayer().getName());
                    result.put("LastEarned", playerRaffleData.getLastEarned());
                    result.put("WeekEntries", playerRaffleData.getWeekEntries());
                    result.put("TotalEntries", playerRaffleData.getTotalEntries());
                    result.put("Progress", playerRaffleData.getProgress());

                    FoxtrotPlugin.getInstance().getMongoPool().getDB("HCTeams").getCollection("RafflePlayers").insert(result);
                }

                raffleData.put(event.getPlayer().getName(), playerRaffleData);
            }

        }.runTaskAsynchronously(FoxtrotPlugin.getInstance());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        raffleData.remove(event.getPlayer().getName());
    }

    public float getRaffleEntryMultiplier(Player player) {
        if (player.hasPermission("foxtrot.raffle.doubleentries")) {
            return (2F);
        }

        return (1F);
    }

}