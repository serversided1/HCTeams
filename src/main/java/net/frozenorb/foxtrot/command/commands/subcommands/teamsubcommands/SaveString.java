package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/9/2014.
 */
public class SaveString {

    @Command(names={ "team savestring", "t savestring", "f savestring", "faction savestring", "fac savestring" }, permissionNode="op")
    public static void teamSaveString(Player sender, @Param(name="team", defaultValue="self") Team target) {
        String saveString = target.saveString();

        System.out.println(saveString);

        sender.sendMessage(ChatColor.YELLOW + "Save String (" + target.getName() + ")");

        for (String line : saveString.split("\n")) {
            sender.sendMessage(ChatColor.GOLD + line.substring(0, line.indexOf(":")) + ": " + ChatColor.YELLOW + line.substring(line.indexOf(":") + 1));
        }
    }

}