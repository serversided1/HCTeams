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

public class TeamChatSpyDelCommand {

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

}