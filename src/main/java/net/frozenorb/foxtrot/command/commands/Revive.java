package net.frozenorb.foxtrot.command.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.BaseCommand;

public class Revive extends BaseCommand {

	public Revive() {
		super("revive", "removedeathban");
	}

	@Override
	public void syncExecute() {
		if (canRevive(sender)) {
			if (args.length > 0) {
				String name = args[0];

				if (FoxtrotPlugin.getInstance().getDeathbanMap().isDeathbanned(name)) {
					FoxtrotPlugin.getInstance().getDeathbanMap().updateValue(name, 0L);
				} else {
					sender.sendMessage(ChatColor.RED + "That player is not deathbanned!");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "/revive <player>");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "You are not allowed to do this! Did you mean §e/pvp revive§c?");
		}
	}

	@Override
	public List<String> getTabCompletions() {
		return FoxtrotPlugin.getInstance().getDeathbanMap().keyList();
	}

	private boolean canRevive(CommandSender s) {
		return s.hasPermission("foxtrot.revive");
	}
}
