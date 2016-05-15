package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.command.CommandSender;

public class TeamJSONCommand {

    @Command(names={ "team json", "t json", "f json", "faction json", "fac json" }, permission="op")
    public static void teamJSON(CommandSender sender, @Param(name="team", defaultValue="self") Team team) {
        sender.sendMessage(team.toJSON().toString());
    }

}