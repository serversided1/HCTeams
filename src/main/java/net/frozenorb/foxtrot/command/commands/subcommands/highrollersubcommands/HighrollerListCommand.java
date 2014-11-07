package net.frozenorb.foxtrot.command.commands.subcommands.highrollersubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/6/2014.
 */
public class HighrollerListCommand {

    @Command(names={ "highroller list", "highrollers list" }, permissionNode="op")
    public static void highrollerList(Player sender) {
        StringBuilder highRollers = new StringBuilder();

        for (String highRoller : FoxtrotPlugin.getInstance().getServerHandler().getHighRollers()) {
            highRollers.append(ChatColor.DARK_PURPLE).append(highRoller).append(ChatColor.YELLOW).append(", ");
        }

        if (highRollers.length() > 2) {
            highRollers.setLength(highRollers.length() - 2);
        } else {
            highRollers.append(ChatColor.GRAY).append("No results.");
        }

        sender.sendMessage(ChatColor.YELLOW + "HighRollers: " + highRollers.toString());
    }

}