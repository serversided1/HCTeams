package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.command.commands.Team;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;

public class Help extends Subcommand {

	public Help(String name, String errorMessage, String... aliases) {
		super(name, errorMessage, aliases);
	}

	@Override
	public void syncExecute() {
		Team.sendHelp((Player) sender);
	}
}
