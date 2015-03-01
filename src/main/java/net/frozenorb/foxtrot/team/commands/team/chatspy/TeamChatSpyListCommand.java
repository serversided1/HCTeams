package net.frozenorb.foxtrot.team.commands.team.chatspy;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.foxtrot.team.Team;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamChatSpyListCommand {

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

}