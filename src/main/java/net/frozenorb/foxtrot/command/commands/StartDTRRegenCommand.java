package net.frozenorb.foxtrot.command.commands;

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
        target.setDeathCooldown(System.currentTimeMillis());
        target.setRaidableCooldown(System.currentTimeMillis());
        sender.sendMessage(ChatColor.GRAY + target.getFriendlyName() + ChatColor.GRAY + " is now regenerating DTR.");
    }

}