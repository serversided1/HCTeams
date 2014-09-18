package net.frozenorb.foxtrot.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.command.BaseCommand;
import net.frozenorb.foxtrot.nametag.NametagManager;

@SuppressWarnings("deprecation")
public class Test extends BaseCommand {

	public Test() {
		super("test");
	}

	@Override
	public void syncExecute() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			NametagManager.clear(p);
			// NametagManager.cleanupTeams(p);
		}
	}

}
