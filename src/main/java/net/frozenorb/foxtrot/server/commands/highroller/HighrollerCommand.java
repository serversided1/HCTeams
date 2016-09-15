package net.frozenorb.foxtrot.server.commands.highroller;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class HighrollerCommand {

    @Command(names={ "highroller", "highrollers", "highroller list", "highrollers list" }, permission="")
    public static void highroller(Player sender) {
        StringBuilder highRollers = new StringBuilder();

        for (Player onlineHighRoller : Foxtrot.getInstance().getServerHandler().getOnlineHighRollers()) {
            // shouldn't happen often but occasionally
            // staff members will have highroller "left over"
            // so we have to respect invisibility
            if (onlineHighRoller.hasMetadata("ModMode")) {
                continue;
            }

            highRollers.append(ChatColor.DARK_PURPLE).append(onlineHighRoller.getName()).append(ChatColor.GOLD).append(", ");
        }

        if (highRollers.length() > 2) {
            highRollers.setLength(highRollers.length() - 2);
        }

        sender.sendMessage(ChatColor.GOLD + "HighRollers: " + highRollers.toString());
    }

}