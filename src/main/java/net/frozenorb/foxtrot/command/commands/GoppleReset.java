package net.frozenorb.foxtrot.command.commands;

import org.bukkit.ChatColor;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.BaseCommand;

public class GoppleReset extends BaseCommand {

	public GoppleReset() {
		super("gopplereset");
		setPermissionLevel("foxtrot.resetapple", "§cYou are not allowed to do this!");
	}

	@Override
	public void syncExecute() {
		if (args.length > 0) {
			String plName = args[0];

			FoxtrotPlugin.getInstance().getOppleMap().updateValue(plName.toLowerCase(), 0L);
			sender.sendMessage(ChatColor.RED + "Cooldown reset!");
		} else {
			sender.sendMessage(ChatColor.RED + "/gopplereset <player>");
		}
	}
}
