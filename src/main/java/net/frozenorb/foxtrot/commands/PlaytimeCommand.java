package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.persist.maps.PlaytimeMap;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlaytimeCommand {

    @Command(names={ "Playtime", "PTime" }, permissionNode="")
    public static void playtime(Player sender, @Parameter(name="Target", defaultValue="self") OfflinePlayer target) {
        PlaytimeMap playtime = FoxtrotPlugin.getInstance().getPlaytimeMap();
        int playtimeTime = (int) playtime.getPlaytime(target.getUniqueId());
        Player targetPlayer = target.getPlayer();

        if (targetPlayer != null && sender.canSee(targetPlayer)) {
            playtimeTime += playtime.getCurrentSession(targetPlayer.getUniqueId()) / 1000;
        }

        sender.sendMessage(ChatColor.LIGHT_PURPLE + target.getName() + ChatColor.YELLOW + "'s total playtime is " + ChatColor.LIGHT_PURPLE + TimeUtils.formatIntoDetailedString(playtimeTime) + ChatColor.YELLOW + ".");
    }

}