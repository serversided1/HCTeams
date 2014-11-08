package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class MCCommand {

    @Command(names={ "MC" }, permissionNode="")
    public static void mc(Player sender, @Param(name="Chat Mode") String mode) {
        switch (mode.toLowerCase()) {
            case "fc":
                if (!sender.hasMetadata("teamchat")) {
                    sender.setMetadata("teamChat", new FixedMetadataValue(FoxtrotPlugin.getInstance(), true));
                }

                sender.sendMessage(ChatColor.DARK_AQUA + "You are now in team chat only mode.");
                break;
            case "gc":
                if (sender.hasMetadata("teamChat")) {
                    sender.removeMetadata("teamChat", FoxtrotPlugin.getInstance());
                }

                sender.sendMessage(ChatColor.DARK_AQUA + "You are now in public chat.");
                break;
            default:
                sender.sendMessage(ChatColor.RED + "/mc <fc|gc>");
                break;
        }
    }

}