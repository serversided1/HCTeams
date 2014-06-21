package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import org.bukkit.ChatColor;

import net.frozenorb.foxtrot.command.subcommand.Subcommand;

public class Rename extends Subcommand {

	public Rename(String name, String errorMessage, String[] aliases) {
		super(name, errorMessage, aliases);
	}

	@Override
	public void syncExecute() {
		if (sender.hasPermission("foxtrot.rename")) {
			if (args.length == 3) {

			} else {
				sender.sendMessage(ChatColor.RED + "/t rename <team> <newname>");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "You are not allowed to do this!");
		}
	}

}
