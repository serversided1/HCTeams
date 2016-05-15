package net.frozenorb.foxtrot.team.commands;

import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SetDTRCommand {

    @Command(names={ "SetDTR" }, permission="foxtrot.setdtr")
    public static void setDTR(Player sender, @Param(name="team") Team team, @Param(name="dtr") float dtr) {
        team.setDTR(dtr);
        sender.sendMessage(ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + " has a new DTR of " + ChatColor.LIGHT_PURPLE + dtr + ChatColor.YELLOW + ".");
    }

}