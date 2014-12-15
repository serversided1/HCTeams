package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.jedis.persist.PlaytimeMap;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 12/3/2014.
 */
public class PlaytimeCommand {

    @Command(names={ "Playtime", "PTime" }, permissionNode="")
    public static void playtime(Player sender, @Param(name="Target", defaultValue="self") OfflinePlayer target) {
        PlaytimeMap playtime = FoxtrotPlugin.getInstance().getPlaytimeMap();
        long playtimeTime = playtime.getPlaytime(target.getName());

        if (target.isOnline() && sender.canSee(target.getPlayer())) {
            playtimeTime += playtime.getCurrentSession(target.getName()) / 1000L;
        }

        sender.sendMessage(ChatColor.GREEN + target.getName() + ChatColor.YELLOW + "'s total playtime is " + ChatColor.GOLD + TimeUtils.getDurationBreakdown(playtimeTime * 1000L) + ChatColor.YELLOW + ".");
    }

}