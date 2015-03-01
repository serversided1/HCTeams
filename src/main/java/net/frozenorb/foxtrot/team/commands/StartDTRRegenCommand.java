package net.frozenorb.foxtrot.team.commands;

import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.qlib.command.annotations.Parameter;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class StartDTRRegenCommand {

    @Command(names={ "startdtrregen" }, permissionNode="foxtrot.startdtrregen")
    public static void startDTRRegen(Player sender, @Parameter(name="Target") Team target) {
        target.setDTRCooldown(System.currentTimeMillis());
        sender.sendMessage(ChatColor.LIGHT_PURPLE + target.getName() + ChatColor.YELLOW + " is now regenerating DTR.");
    }

}