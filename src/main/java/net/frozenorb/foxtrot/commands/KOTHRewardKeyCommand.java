package net.frozenorb.foxtrot.commands;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.frozenorb.foxtrot.util.InventoryUtils;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

public class KOTHRewardKeyCommand {

    @Command(names={ "kothrewardkey" }, permission="op")
    public static void kothRewardKey(Player sender, @Param(name = "player", defaultValue = "self") Player player, @Param(name="koth") String koth, @Param(name="amount", defaultValue = "1") int amount) {
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
            return;
        }

        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Unable to locate player.");
            return;
        }

        if (amount == 0 || 32 < amount) {
            sender.sendMessage(ChatColor.RED + "Illegal amount! Must be between 1 and 32.");
            return;
        }

        ItemStack stack = InventoryUtils.generateKOTHRewardKey(koth);
        stack.setAmount(amount);
        Map<Integer, ItemStack> failed = player.getInventory().addItem(stack);

        if (amount == 1) {
            String msg = ChatColor.YELLOW + "Gave " + player.getName() + " a KOTH reward key." + failed == null || failed.isEmpty() ? "" : " " + failed.size() + " didn't fit.";
            org.bukkit.command.Command.broadcastCommandMessage(sender, msg);
            sender.sendMessage(msg);
        } else {
            String msg = ChatColor.YELLOW + "Gave " + player.getName() + " " + amount + " KOTH reward keys." + failed == null || failed.isEmpty() ? "" : " " + failed.size() + " didn't fit.";
            org.bukkit.command.Command.broadcastCommandMessage(sender, msg);
            sender.sendMessage(msg);
        }
    }

}