package net.frozenorb.foxtrot.team.commands.team.chatspy;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class TeamChatSpyClearCommand {

    @Command(names={ "team chatspy clear", "t chatspy clear", "f chatspy clear", "faction chatspy clear", "fac chatspy clear" }, permissionNode="foxtrot.chatspy")
    public static void teamChatSpyClear(Player sender) {
        FoxtrotPlugin.getInstance().getChatSpyMap().setChatSpy(sender.getName(), new ArrayList<>());
        sender.sendMessage(ChatColor.GREEN + "You are no longer spying on any teams.");
    }

}