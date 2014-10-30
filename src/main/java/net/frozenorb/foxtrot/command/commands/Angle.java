package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Angle extends BaseCommand {

    public Angle() {
        super("angle");
    }

    @Override
    public void syncExecute() {
        sender.sendMessage(ChatColor.YELLOW + "" + ((Player) sender).getLocation().getYaw() + " yaw, " + ((Player) sender).getLocation().getPitch() + " pitch");
    }

}
