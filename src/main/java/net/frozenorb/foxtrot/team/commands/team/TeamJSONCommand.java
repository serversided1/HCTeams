package net.frozenorb.foxtrot.team.commands.team;

import com.mongodb.BasicDBObject;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.command.CommandSender;

public class TeamJSONCommand {

    @Command(names={ "team json", "t json", "f json", "faction json", "fac json" }, permissionNode="op")
    public static void teamSaveString(CommandSender sender, @Param(name="team", defaultValue="self") Team target) {
        BasicDBObject json = target.json();

        sender.sendMessage(json.toString());
    }

}
