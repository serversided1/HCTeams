package net.frozenorb.foxtrot.command.commands.team;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by macguy8 on 11/8/2014.
 */
public class TeamChatSpyCommand {

    @Command(names={ "team chatspy list", "t chatspy list", "f chatspy list", "faction chatspy list", "fac chatspy list" }, permissionNode="foxtrot.chatspy")
    public static void teamChatSpyList(Player sender) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String team : FoxtrotPlugin.getInstance().getChatSpyMap().getChatSpy(sender.getName())) {
            stringBuilder.append(ChatColor.YELLOW).append(team).append(ChatColor.GOLD).append(", ");
        }

        if (stringBuilder.length() > 2) {
            stringBuilder.setLength(stringBuilder.length() - 2);
        }

        sender.sendMessage(ChatColor.GOLD + "You are currently spying on the faction chat of: " + stringBuilder.toString());
    }

    @Command(names={ "team chatspy add", "t chatspy add", "f chatspy add", "faction chatspy add", "fac chatspy add" }, permissionNode="foxtrot.chatspy")
    public static void teamChatSpyAdd(Player sender, @Param(name="Team") Team target) {
        if (FoxtrotPlugin.getInstance().getChatSpyMap().getChatSpy(sender.getName()).contains(target.getFriendlyName().toLowerCase())) {
            sender.sendMessage(ChatColor.RED + "You are already spying on " + target.getFriendlyName() + ".");
            return;
        }

        ArrayList<String> teams = new ArrayList<String>(FoxtrotPlugin.getInstance().getChatSpyMap().getChatSpy(sender.getName()));

        teams.add(target.getFriendlyName().toLowerCase());

        FoxtrotPlugin.getInstance().getChatSpyMap().setChatSpy(sender.getName(), teams);
        sender.sendMessage(ChatColor.GREEN + "You are now spying on the chat of " + ChatColor.YELLOW + target.getFriendlyName() + ChatColor.GREEN + ".");
    }

    @Command(names={ "team chatspy del", "t chatspy del", "f chatspy del", "faction chatspy del", "fac chatspy del" }, permissionNode="foxtrot.chatspy")
    public static void teamChatSpyDel(Player sender, @Param(name="Team") Team target) {
        if (!FoxtrotPlugin.getInstance().getChatSpyMap().getChatSpy(sender.getName()).contains(target.getFriendlyName().toLowerCase())) {
            sender.sendMessage(ChatColor.RED + "You are not spying on " + target.getFriendlyName() + ".");
            return;
        }

        ArrayList<String> teams = new ArrayList<String>(FoxtrotPlugin.getInstance().getChatSpyMap().getChatSpy(sender.getName()));

        teams.remove(target.getFriendlyName().toLowerCase());

        FoxtrotPlugin.getInstance().getChatSpyMap().setChatSpy(sender.getName(), teams);
        sender.sendMessage(ChatColor.GREEN + "You are no longer spying on the chat of " + ChatColor.YELLOW + target.getFriendlyName() + ChatColor.GREEN + ".");
    }

    @Command(names={ "team chatspy clear", "t chatspy clear", "f chatspy clear", "faction chatspy clear", "fac chatspy clear" }, permissionNode="foxtrot.chatspy")
    public static void teamChatSpyClear(Player sender) {
        FoxtrotPlugin.getInstance().getChatSpyMap().setChatSpy(sender.getName(), new ArrayList<String>());
        sender.sendMessage(ChatColor.GREEN + "You are no longer spying on any factions.");
    }

}