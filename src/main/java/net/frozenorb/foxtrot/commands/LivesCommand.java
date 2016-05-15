package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * ---------- hcteams ----------
 * Created by Fraser.Cumming on 29/03/2016.
 * © 2016 Fraser Cumming All Rights Reserved
 */
public class LivesCommand {

    @Command(names={ "lives" }, permission="")
    public static void lives(Player sender) {
        int shared = Foxtrot.getInstance().getFriendLivesMap().getLives(sender.getUniqueId());
        int soulbound = Foxtrot.getInstance().getSoulboundLivesMap().getLives(sender.getUniqueId());
        sender.sendMessage(ChatColor.YELLOW + "Lives are used to revive you instantly upon death. You can purchase more lives at: " + ChatColor.RED + "http://minehq.com/store");
        sender.sendMessage(ChatColor.YELLOW + "Friend Lives: " + ChatColor.RED + shared);
        sender.sendMessage(ChatColor.YELLOW + "Soulbound Lives: " + ChatColor.RED + soulbound);
        sender.sendMessage(ChatColor.RED + "You cannot revive other players with soulbound lives.");
    }
}
