package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.util.InventoryUtils;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class KOTHRewardKeyCommand {

    @Command(names={ "kothrewardkey" }, permission="op")
    public static void kothRewardKey(Player sender, @Param(name="koth") String koth) {
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
            return;
        }

        sender.setItemInHand(InventoryUtils.generateKOTHRewardKey(koth));
        sender.sendMessage(ChatColor.YELLOW + "Gave you a KOTH reward key.");
    }

}