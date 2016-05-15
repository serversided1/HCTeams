package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GoppleResetCommand {

    @Command(names={ "GoppleReset" }, permission="foxtrot.gopplereset")
    public static void goppleReset(Player sender, @Param(name="player") UUID player) {
        Foxtrot.getInstance().getOppleMap().resetCooldown(player);
        sender.sendMessage(ChatColor.RED + "Cooldown reset!");
    }

}