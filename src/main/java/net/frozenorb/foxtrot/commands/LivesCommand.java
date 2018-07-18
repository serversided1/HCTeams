package net.frozenorb.foxtrot.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;

/**
 * ---------- hcteams ----------
 * Created by Fraser.Cumming on 29/03/2016.
 * © 2016 Fraser Cumming All Rights Reserved
 */
public class LivesCommand {

    @Command(names={ "lives" }, permission="")
    public static void lives(CommandSender commandSender) {
        
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "Bad console.");
            return;
        }

        Player sender = (Player) commandSender;
        
        int shared = Foxtrot.getInstance().getFriendLivesMap().getLives(sender.getUniqueId());
        int soulbound = Foxtrot.getInstance().getSoulboundLivesMap().getLives(sender.getUniqueId());
        sender.sendMessage(ChatColor.YELLOW + "Lives are used to revive you instantly upon death. You can purchase more lives at: " + ChatColor.RED + "http://" + Foxtrot.getInstance().getServerHandler().getNetworkWebsite() + "/store");
        sender.sendMessage(ChatColor.YELLOW + "Friend Lives: " + ChatColor.RED + shared);
        sender.sendMessage(ChatColor.YELLOW + "Soulbound Lives: " + ChatColor.RED + soulbound);
        sender.sendMessage(ChatColor.RED + "You cannot revive other players with soulbound lives.");
    }
}
