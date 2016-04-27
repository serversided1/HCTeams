package net.frozenorb.foxtrot.map.kit.stats.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;

public class LeaderboardAddCommand {

    @Command(names = {"leaderboard add"}, permissionNode = "op")
    public static void leaderboardAdd(Player sender, @Parameter(name = "place") int place) {
        Block block = sender.getTargetBlock(null, 10);

        if (block == null || !(block.getState() instanceof Skull || block.getState() instanceof Sign)) {
            sender.sendMessage(ChatColor.RED + "You must be looking at a head or a sign.");
            return;
        }

        if (block.getState() instanceof Skull) {
            Skull skull = (Skull) block.getState();

            if (skull.getSkullType() != SkullType.PLAYER) {
                sender.sendMessage(ChatColor.RED + "That's not a player skull.");
                return;
            }

            Foxtrot.getInstance().getMapHandler().getStatsHandler().getLeaderboardHeads().put(skull.getLocation(), place);
            Foxtrot.getInstance().getMapHandler().getStatsHandler().updatePhysicalLeaderboards();
            sender.sendMessage(ChatColor.GREEN + "This skull will now display the number " + ChatColor.WHITE + place + ChatColor.GREEN + " player's head.");
        } else {
            Sign sign = (Sign) block.getState();

            Foxtrot.getInstance().getMapHandler().getStatsHandler().getLeaderboardSigns().put(sign.getLocation(), place);
            Foxtrot.getInstance().getMapHandler().getStatsHandler().updatePhysicalLeaderboards();
            sender.sendMessage(ChatColor.GREEN + "This sign will now display the number " + ChatColor.WHITE + place + ChatColor.GREEN + " player's stats.");
        }
    }

}
