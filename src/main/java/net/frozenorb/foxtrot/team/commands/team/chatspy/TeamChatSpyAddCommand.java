package net.frozenorb.foxtrot.team.commands.team.chatspy;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TeamChatSpyAddCommand {

    @Command(names={ "team chatspy add", "t chatspy add", "f chatspy add", "faction chatspy add", "fac chatspy add" }, permissionNode="foxtrot.chatspy")
    public static void teamChatSpyAdd(Player sender, @Parameter(name="Team") Team target) {
        if (FoxtrotPlugin.getInstance().getChatSpyMap().getChatSpy(sender.getUniqueId()).contains(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are already spying on " + target.getName() + ".");
            return;
        }

        List<ObjectId> teams = new ArrayList<>(FoxtrotPlugin.getInstance().getChatSpyMap().getChatSpy(sender.getUniqueId()));

        teams.add(target.getUniqueId());

        FoxtrotPlugin.getInstance().getChatSpyMap().setChatSpy(sender.getUniqueId(), teams);
        sender.sendMessage(ChatColor.GREEN + "You are now spying on the chat of " + ChatColor.YELLOW + target.getName() + ChatColor.GREEN + ".");
    }

}