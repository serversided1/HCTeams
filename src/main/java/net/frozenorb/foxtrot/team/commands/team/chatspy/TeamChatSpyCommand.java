package net.frozenorb.foxtrot.team.commands.team.chatspy;

import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/8/2014.
 */
public class TeamChatSpyCommand {

    @Command(names={ "team chatspy", "t chatspy", "f chatspy", "faction chatspy", "fac chatspy" }, permissionNode="foxtrot.chatspy")
    public static void teamChatSpy(Player sender) {
        sender.sendMessage(ChatColor.RED + "/team chatspy list - views teams who you are spying on");
        sender.sendMessage(ChatColor.RED + "/team chatspy add - starts spying on a team");
        sender.sendMessage(ChatColor.RED + "/team chatspy del - stops spying on a team");
        sender.sendMessage(ChatColor.RED + "/team chatspy clear - stops spying on all teams");
    }

}