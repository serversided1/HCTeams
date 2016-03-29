package net.frozenorb.foxtrot.chunklimiter;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * ---------- hcteams ----------
 * Created by Fraser.Cumming on 29/03/2016.
 * © 2016 Fraser Cumming All Rights Reserved
 */
public class PlayerViewDistanceCommand {

    @Command(names={ "playerviewdistance", "playervd" }, permissionNode="op")
    public static void playerviewdistance(Player sender, @Parameter(name="player") UUID player, @Parameter(name = "value") Integer value) {
        if( value <= 15 && value >= 2 ) {
            Player p = Bukkit.getPlayer(player);
            if( p != null && p.isOnline()) {
                ((CraftPlayer)p).spigot().setViewDistance(value);
                ChunkLimiterListener.getViewDistances().put(player, value);
                sender.sendMessage(ChatColor.GREEN + "Set " + ChatColor.YELLOW + p.getName() + ChatColor.GREEN + "'s view distance to " + org.bukkit.ChatColor.YELLOW + value);
            } else {
                sender.sendMessage(ChatColor.RED + "That player is offline!");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "The value should be between 2 and 15");
        }
    }
}
