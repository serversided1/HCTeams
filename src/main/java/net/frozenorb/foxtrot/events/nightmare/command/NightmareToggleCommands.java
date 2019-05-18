package net.frozenorb.foxtrot.events.nightmare.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class NightmareToggleCommands {

	@Command(names = { "nm enable", "nightmare enable" }, permission = "op")
	public static void enable(CommandSender sender) {
		Foxtrot.getInstance().getNightmareHandler().setAdminDisabled(false);
		sender.sendMessage(ChatColor.GREEN + "The Hell event has been enabled.");
	}

	@Command(names = { "nm disable", "nightmare disable" }, permission = "op")
	public static void disable(CommandSender sender) {
		Foxtrot.getInstance().getNightmareHandler().setAdminDisabled(true);
		sender.sendMessage(ChatColor.RED + "The Hell event has been enabled.");
	}

}
