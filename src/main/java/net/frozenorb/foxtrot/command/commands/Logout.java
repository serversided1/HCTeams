package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.BaseCommand;

import org.bukkit.entity.Player;

public class Logout extends BaseCommand {

	public Logout() {
		super("logout");
	}

	public void syncExecute() {
		FoxtrotPlugin.getInstance().getServerManager().startLogoutSequence((Player) sender);
	}

}
