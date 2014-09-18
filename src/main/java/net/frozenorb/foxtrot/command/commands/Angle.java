package net.frozenorb.foxtrot.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.command.BaseCommand;

public class Angle extends BaseCommand {

	public Angle() {
		super("angle");
	}

	@Override
	public void syncExecute() {
		sender.sendMessage(ChatColor.YELLOW + "" + ((Player) sender).getLocation().getYaw() + " yaw, " + ((Player) sender).getLocation().getPitch() + " pitch");
	}

}
