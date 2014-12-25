package net.frozenorb.foxtrot.raffle.commands.raffle;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.raffle.RaffleHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RaffleDrawCommand {

    @Command(names={ "raffle draw" }, permissionNode="op")
    public static void raffleDraw(Player sender, @Param(name="scope") String scope, @Param(name="prize", wildcard=true) String prize) {
        if (!(scope.equalsIgnoreCase("week") || scope.equalsIgnoreCase("total"))) {
            sender.sendMessage(ChatColor.RED + "Invalid scope. Options: week, total");
            return;
        }

        new BukkitRunnable() {

            int time = 0;
            BasicDBObject winnerData = new BasicDBObject();

            public void run() {
                if (time == 0) {
                    FoxtrotPlugin.getInstance().getServer().broadcastMessage(RaffleHandler.PREFIX + " " + ChatColor.GOLD.toString() + ChatColor.BOLD + sender.getName() + ChatColor.YELLOW.toString() + ChatColor.BOLD + " has initiated a raffle draw.");
                    FoxtrotPlugin.getInstance().getServer().broadcastMessage(RaffleHandler.PREFIX + " " + ChatColor.YELLOW + "Prize: " + ChatColor.WHITE + prize);
                    FoxtrotPlugin.getInstance().getServer().broadcastMessage(RaffleHandler.PREFIX + " " + ChatColor.YELLOW + "Pool: " + ChatColor.WHITE + (scope.equalsIgnoreCase("week") ? "This week" : "This map") + "'s entries");
                } else if (time == 2) {
                    FoxtrotPlugin.getInstance().getServer().broadcastMessage(RaffleHandler.PREFIX + " " + ChatColor.GOLD.toString() + ChatColor.BOLD + "Beginning database communications to pick a winner...");

                    DBCursor cursor = null;

                    if (scope.equalsIgnoreCase("week")) {
                        cursor = FoxtrotPlugin.getInstance().getMongoPool().getDB("HCTeams").getCollection("RaffleEntries_Week" + Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)).find();
                    } else {
                        cursor = FoxtrotPlugin.getInstance().getMongoPool().getDB("HCTeams").getCollection("RaffleEntries").find();
                    }

                    List<BasicDBObject> allEntries = new ArrayList<BasicDBObject>();

                    while (cursor.hasNext()) {
                        BasicDBObject result = (BasicDBObject) cursor.next();

                        for (int i = 0; i < result.getInt("EntriesWithMultiplier"); i++) {
                            allEntries.add(result);
                        }
                    }

                    winnerData = allEntries.get(FoxtrotPlugin.RANDOM.nextInt(allEntries.size()));
                } else if (time == 5) {
                    FoxtrotPlugin.getInstance().getServer().broadcastMessage(RaffleHandler.PREFIX + " " + ChatColor.GOLD.toString() + ChatColor.BOLD + "A winner has been chosen. " + ChatColor.YELLOW.toString() + ChatColor.BOLD + "Announcing the raffle winner in " + ChatColor.GOLD.toString() + ChatColor.BOLD + "5s" + ChatColor.YELLOW.toString() + ChatColor.BOLD + "...");
                } else if (time == 7 || time == 8 || time == 9) {
                    FoxtrotPlugin.getInstance().getServer().broadcastMessage(RaffleHandler.PREFIX + " " + ChatColor.YELLOW.toString() + ChatColor.BOLD + "Announcing the raffle winner in " + ChatColor.GOLD.toString() + ChatColor.BOLD + (10 - time) + "s" + ChatColor.YELLOW.toString() + ChatColor.BOLD + "...");
                } else if (time == 10) {
                    FoxtrotPlugin.getInstance().getServer().broadcastMessage(RaffleHandler.PREFIX + " " + ChatColor.GOLD.toString() + ChatColor.BOLD + winnerData.getString("Player") + ChatColor.YELLOW.toString() + ChatColor.BOLD + " has won the raffle!");
                    FoxtrotPlugin.getInstance().getServer().broadcastMessage(RaffleHandler.PREFIX + " " + ChatColor.YELLOW + "Prize: " + ChatColor.WHITE + prize);
                    FoxtrotPlugin.getInstance().getServer().broadcastMessage(RaffleHandler.PREFIX + " " + ChatColor.YELLOW + "Pool: " + ChatColor.WHITE + (scope.equalsIgnoreCase("week") ? "This week" : "This map") + "'s entries");
                }

                time++;
            }

        }.runTaskTimerAsynchronously(FoxtrotPlugin.getInstance(), 0L, 20L);
    }

}