package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SetDTRCommand {

    @Command(names={ "SetDTR" }, permissionNode="foxtrot.setdtr")
    public static void setDTR(Player sender, @Param(name="Target") Team target, @Param(name="DTR") float value) {
        target.setDTR(value);
        sender.sendMessage(ChatColor.YELLOW + target.getName() + " has a new DTR of: " + value);
    }

}