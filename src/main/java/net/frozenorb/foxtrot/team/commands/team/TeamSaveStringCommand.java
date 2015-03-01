package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.qlib.command.annotations.Parameter;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class TeamSaveStringCommand {

    @Command(names={ "team savestring", "t savestring", "f savestring", "faction savestring", "fac savestring" }, permissionNode="op")
    public static void teamSaveString(CommandSender sender, @Parameter(name="team", defaultValue="self") Team target) {
        String saveString = target.saveString(false);

        sender.sendMessage(ChatColor.BLUE.toString() + ChatColor.UNDERLINE + "Save String (" + target.getName() + ")");
        sender.sendMessage("");

        for (String line : saveString.split("\n")) {
            sender.sendMessage(ChatColor.BLUE + line.substring(0, line.indexOf(":")) + ": " + ChatColor.YELLOW + line.substring(line.indexOf(":") + 1).replace(",", ChatColor.BLUE + "," + ChatColor.YELLOW).replace(":", ChatColor.BLUE + ":" + ChatColor.YELLOW));
        }
    }

}