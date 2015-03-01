package net.frozenorb.foxtrot.server.commands.highroller;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.qlib.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HighrollerListCommand {

    @Command(names={ "highroller list", "highrollers list" }, permissionNode="op")
    public static void highrollerList(Player sender) {
        StringBuilder highRollers = new StringBuilder();

        for (String highRoller : FoxtrotPlugin.getInstance().getServerHandler().getHighRollers()) {
            highRollers.append(ChatColor.DARK_PURPLE).append(highRoller).append(ChatColor.GOLD).append(", ");
        }

        if (highRollers.length() > 2) {
            highRollers.setLength(highRollers.length() - 2);
        }

        sender.sendMessage(ChatColor.GOLD + "HCTeams HighRollers: " + highRollers.toString());
    }

}