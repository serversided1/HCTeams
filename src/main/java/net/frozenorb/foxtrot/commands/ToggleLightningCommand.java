package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.qlib.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ToggleLightningCommand {

    @Command(names={ "ToggleLightning" }, permissionNode="")
    public static void toggleLightning(Player sender) {
        boolean val = !FoxtrotPlugin.getInstance().getToggleLightningMap().isLightningToggled(sender.getName());

        sender.sendMessage(ChatColor.YELLOW + "You are now " + (!val ? ChatColor.RED + "unable" : ChatColor.GREEN + "able") + ChatColor.YELLOW + " to see lightning strikes on deaths!");
        FoxtrotPlugin.getInstance().getToggleLightningMap().setLightningToggled(sender.getName(), val);
    }

}