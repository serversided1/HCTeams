package net.frozenorb.foxtrot.team.commands;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 10/31/2014.
 */
public class StartDTRRegenCommand {

    @Command(names={ "startdtrregen" }, permissionNode="foxtrot.startdtrregen")
    public static void startDTRRegen(Player sender, @Param(name="Target") Team target) {
        target.setDTRCooldown(System.currentTimeMillis());
        sender.sendMessage(ChatColor.LIGHT_PURPLE + target.getName() + ChatColor.YELLOW + " is now regenerating DTR.");
    }

}