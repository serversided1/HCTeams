package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class TeamSaveStringCommand {

    @Command(names={ "team savestring", "t savestring", "f savestring", "faction savestring", "fac savestring" }, permission="op")
    public static void teamSaveString(CommandSender sender, @Param(name="team", defaultValue="self") Team team) {
        String saveString = team.saveString(false);

        sender.sendMessage(ChatColor.BLUE.toString() + ChatColor.UNDERLINE + "Save String (" + team.getName() + ")");
        sender.sendMessage("");

        for (String line : saveString.split("\n")) {
            sender.sendMessage(ChatColor.BLUE + line.substring(0, line.indexOf(":")) + ": " + ChatColor.YELLOW + line.substring(line.indexOf(":") + 1).replace(",", ChatColor.BLUE + "," + ChatColor.YELLOW).replace(":", ChatColor.BLUE + ":" + ChatColor.YELLOW));
        }
    }

}