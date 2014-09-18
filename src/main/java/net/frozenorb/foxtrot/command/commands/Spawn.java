package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.BaseCommand;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Spawn extends BaseCommand {

	public Spawn() {
		super("spawn");
	}

	public void syncExecute() {
		if (sender.isOp()) {
			((Player) sender).teleport(FoxtrotPlugin.getInstance().getServerManager().getSpawnLocation());
		} else {
			sender.sendMessage(ChatColor.RED + "HCTeams does not have a spawn command! You must walk there!");
		}
	}
}
