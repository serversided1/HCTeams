package net.frozenorb.foxtrot.team.commands;

import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SetDTRCommand {

    @Command(names={ "SetDTR" }, permissionNode="foxtrot.setdtr")
    public static void setDTR(Player sender, @Parameter(name="team") Team team, @Parameter(name="dtr") float dtr) {
        team.setDTR(dtr);
        sender.sendMessage(ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + " has a new DTR of " + ChatColor.LIGHT_PURPLE + dtr + ChatColor.YELLOW + ".");
    }

}