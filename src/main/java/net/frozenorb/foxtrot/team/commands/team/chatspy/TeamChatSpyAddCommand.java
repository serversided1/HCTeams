package net.frozenorb.foxtrot.team.commands.team.chatspy;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TeamChatSpyAddCommand {

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

}