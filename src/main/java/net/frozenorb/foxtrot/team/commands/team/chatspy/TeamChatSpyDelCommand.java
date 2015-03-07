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

public class TeamChatSpyDelCommand {

    @Command(names={ "team chatspy del", "t chatspy del", "f chatspy del", "faction chatspy del", "fac chatspy del" }, permissionNode="foxtrot.chatspy")
    public static void teamChatSpyDel(Player sender, @Parameter(name="Team") Team target) {
        if (!FoxtrotPlugin.getInstance().getChatSpyMap().getChatSpy(sender.getUniqueId()).contains(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are not spying on " + target.getName() + ".");
            return;
        }

        List<ObjectId> teams = new ArrayList<>(FoxtrotPlugin.getInstance().getChatSpyMap().getChatSpy(sender.getUniqueId()));

        teams.remove(target.getUniqueId());

        FoxtrotPlugin.getInstance().getChatSpyMap().setChatSpy(sender.getUniqueId(), teams);
        sender.sendMessage(ChatColor.GREEN + "You are no longer spying on the chat of " + ChatColor.YELLOW + target.getName() + ChatColor.GREEN + ".");
    }

}