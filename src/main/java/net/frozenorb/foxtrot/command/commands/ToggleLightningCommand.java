package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.jedis.persist.ToggleLightningMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by chasechocolate.
 */
public class ToggleLightningCommand {

    @Command(names={ "ToggleLightning" }, permissionNode="")
    public static void toggleLightning(Player sender) {
        boolean val = !sender.hasMetadata(ToggleLightningMap.META);

        sender.sendMessage(ChatColor.YELLOW + "You are now " + (val ? ChatColor.RED + "unable" : ChatColor.GREEN + "able") + ChatColor.YELLOW + " to see lightning strikes on deaths!");

        if (val) {
            sender.setMetadata(ToggleLightningMap.META, ToggleLightningMap.META_OBJ);
        } else {
            sender.removeMetadata(ToggleLightningMap.META, ToggleLightningMap.META_OBJ.getOwningPlugin());
        }
    }

}