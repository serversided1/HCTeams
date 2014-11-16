package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.commands.TeamCommand;
import org.bukkit.entity.Player;

public class TeamHelpCommand {

    @Command(names={ "team help", "t help", "f help", "faction help", "fac help" }, permissionNode="")
    public static void teamHelp(Player player) {
		TeamCommand.team(player);
	}

}