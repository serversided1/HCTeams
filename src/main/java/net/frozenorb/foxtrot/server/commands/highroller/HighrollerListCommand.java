package net.frozenorb.foxtrot.server.commands.highroller;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class HighrollerListCommand {

    @Command(names={ "highroller list", "highrollers list" }, permission="op")
    public static void highrollerList(Player sender) {
        StringBuilder highRollers = new StringBuilder();

        for (UUID highRoller : Foxtrot.getInstance().getServerHandler().getHighRollers()) {
            highRollers.append(ChatColor.DARK_PURPLE).append(UUIDUtils.name(highRoller)).append(ChatColor.GOLD).append(", ");
        }

        if (highRollers.length() > 2) {
            highRollers.setLength(highRollers.length() - 2);
        }

        sender.sendMessage(ChatColor.GOLD + "HCTeams HighRollers: " + highRollers.toString());
    }

}