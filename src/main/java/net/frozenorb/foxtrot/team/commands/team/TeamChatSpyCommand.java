package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macguy8 on 11/8/2014.
 */
public class TeamChatSpyCommand {

    @Command(names={ "team chatspy", "t chatspy", "f chatspy", "faction chatspy", "fac chatspy" }, permissionNode="foxtrot.chatspy")
    public static void teamChatSpy(Player sender) {
        sender.sendMessage(ChatColor.RED + "/f chatspy list - views teams who you are spying on");
        sender.sendMessage(ChatColor.RED + "/f chatspy add - starts spying on a team");
        sender.sendMessage(ChatColor.RED + "/f chatspy del - stops spying on a team");
        sender.sendMessage(ChatColor.RED + "/f chatspy clear - stops spying on all teams");
    }

    @Command(names={ "team chatspy list", "t chatspy list", "f chatspy list", "faction chatspy list", "fac chatspy list" }, permissionNode="foxtrot.chatspy")
    public static void teamChatSpyList(Player sender) {
        StringBuilder stringBuilder = new StringBuilder();

        for (ObjectId team : FoxtrotPlugin.getInstance().getChatSpyMap().getChatSpy(sender.getName())) {
            Team teamObj = FoxtrotPlugin.getInstance().getTeamHandler().getTeam(team);

            if (teamObj != null) {
                stringBuilder.append(ChatColor.YELLOW).append(teamObj.getName()).append(ChatColor.GOLD).append(", ");
            }
        }

        if (stringBuilder.length() > 2) {
            stringBuilder.setLength(stringBuilder.length() - 2);
        }

        sender.sendMessage(ChatColor.GOLD + "You are currently spying on the team chat of: " + stringBuilder.toString());
    }

    @Command(names={ "team chatspy add", "t chatspy add", "f chatspy add", "faction chatspy add", "fac chatspy add" }, permissionNode="foxtrot.chatspy")
    public static void teamChatSpyAdd(Player sender, @Param(name="Team") Team target) {
        if (FoxtrotPlugin.getInstance().getChatSpyMap().getChatSpy(sender.getName()).contains(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are already spying on " + target.getName() + ".");
            return;
        }

        List<ObjectId> teams = new ArrayList<ObjectId>(FoxtrotPlugin.getInstance().getChatSpyMap().getChatSpy(sender.getName()));

        teams.add(target.getUniqueId());

        FoxtrotPlugin.getInstance().getChatSpyMap().setChatSpy(sender.getName(), teams);
        sender.sendMessage(ChatColor.GREEN + "You are now spying on the chat of " + ChatColor.YELLOW + target.getName() + ChatColor.GREEN + ".");
    }

    @Command(names={ "team chatspy del", "t chatspy del", "f chatspy del", "faction chatspy del", "fac chatspy del" }, permissionNode="foxtrot.chatspy")
    public static void teamChatSpyDel(Player sender, @Param(name="Team") Team target) {
        if (!FoxtrotPlugin.getInstance().getChatSpyMap().getChatSpy(sender.getName()).contains(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are not spying on " + target.getName() + ".");
            return;
        }

        List<ObjectId> teams = new ArrayList<ObjectId>(FoxtrotPlugin.getInstance().getChatSpyMap().getChatSpy(sender.getName()));

        teams.remove(target.getUniqueId());

        FoxtrotPlugin.getInstance().getChatSpyMap().setChatSpy(sender.getName(), teams);
        sender.sendMessage(ChatColor.GREEN + "You are no longer spying on the chat of " + ChatColor.YELLOW + target.getName() + ChatColor.GREEN + ".");
    }

    @Command(names={ "team chatspy clear", "t chatspy clear", "f chatspy clear", "faction chatspy clear", "fac chatspy clear" }, permissionNode="foxtrot.chatspy")
    public static void teamChatSpyClear(Player sender) {
        FoxtrotPlugin.getInstance().getChatSpyMap().setChatSpy(sender.getName(), new ArrayList<ObjectId>());
        sender.sendMessage(ChatColor.GREEN + "You are no longer spying on any teams.");
    }

}