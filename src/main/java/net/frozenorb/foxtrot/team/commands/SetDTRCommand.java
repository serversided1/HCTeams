package net.frozenorb.foxtrot.team.commands;

import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.qlib.command.annotations.Parameter;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SetDTRCommand {

    @Command(names={ "SetDTR" }, permissionNode="foxtrot.setdtr")
    public static void setDTR(Player sender, @Parameter(name="Target") Team target, @Parameter(name="DTR") float value) {
        target.setDTR(value);
        sender.sendMessage(ChatColor.LIGHT_PURPLE + target.getName() + ChatColor.YELLOW + " has a new DTR of " + ChatColor.LIGHT_PURPLE + value + ChatColor.YELLOW + ".");
    }

}