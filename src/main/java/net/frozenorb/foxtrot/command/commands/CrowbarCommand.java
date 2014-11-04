package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.util.InvUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/4/2014.
 */
public class CrowbarCommand {

    @Command(names={ "Crowbar" }, permissionNode="op")
    public static void crowbar(Player sender) {
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
            return;
        }

        sender.setItemInHand(InvUtils.CROWBAR);
        sender.sendMessage(ChatColor.YELLOW + "Gave you a crowbar.");
    }

}
