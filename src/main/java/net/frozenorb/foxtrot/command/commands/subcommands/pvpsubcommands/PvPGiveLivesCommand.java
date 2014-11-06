package net.frozenorb.foxtrot.command.commands.subcommands.pvpsubcommands;

import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/6/2014.
 */
public class PvPGiveLivesCommand {

    @Command(names={ "pvptimer givelives", "timer givelives", "pvp givelives", "pvptimer givelife", "timer givelife", "pvp givelife" }, permissionNode="")
    public static void pvpGiveLives(Player sender) {
        sender.sendMessage(ChatColor.YELLOW + "Command disable due to potential duplication glitch.");
    }

}