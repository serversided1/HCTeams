package net.frozenorb.foxtrot.commands;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.economy.FrozenEconomyHandler;
import net.frozenorb.qlib.uuid.FrozenUUIDCache;

public class BalanceCommand {

    @Command(names={ "Balance", "Econ", "Bal", "$" }, permission="")
    public static void balance(Player sender, @Param(name="player", defaultValue="self") UUID player) {
        if (sender.getUniqueId().equals(player)) {
            sender.sendMessage(ChatColor.GOLD + "Balance: " + ChatColor.WHITE + NumberFormat.getNumberInstance(Locale.US).format(FrozenEconomyHandler.getBalance(sender.getUniqueId())));
        } else {
            sender.sendMessage(ChatColor.GOLD + "Balance of " + FrozenUUIDCache.name(player) + ": " + ChatColor.WHITE + NumberFormat.getNumberInstance(Locale.US).format(FrozenEconomyHandler.getBalance(player)));
        }
    }

}