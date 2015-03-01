package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.qlib.command.annotations.Parameter;
import net.frozenorb.foxtrot.jedis.persist.PlaytimeMap;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlaytimeCommand {

    @Command(names={ "Playtime", "PTime" }, permissionNode="")
    public static void playtime(Player sender, @Parameter(name="Target", defaultValue="self") String target) {
        PlaytimeMap playtime = FoxtrotPlugin.getInstance().getPlaytimeMap();
        long playtimeTime = playtime.getPlaytime(target);
        Player targetPlayer = FoxtrotPlugin.getInstance().getServer().getPlayerExact(target);

        if (targetPlayer != null && sender.canSee(targetPlayer)) {
            playtimeTime += playtime.getCurrentSession(targetPlayer.getName()) / 1000L;
        }

        sender.sendMessage(ChatColor.LIGHT_PURPLE + target + ChatColor.YELLOW + "'s total playtime is " + ChatColor.LIGHT_PURPLE + TimeUtils.getDurationBreakdown(playtimeTime * 1000L) + ChatColor.YELLOW + ".");
    }

}