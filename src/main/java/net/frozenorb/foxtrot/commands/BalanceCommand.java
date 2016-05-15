package net.frozenorb.foxtrot.commands;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.qlib.economy.FrozenEconomyHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

public class BalanceCommand {

    @Command(names={ "Balance", "Econ", "Bal", "$" }, permissionNode="")
    public static void balance(Player sender, @Parameter(name="player", defaultValue="self") UUID player) {
        if (sender.getUniqueId().equals(player)) {
            sender.sendMessage(ChatColor.GOLD + "Balance: " + ChatColor.WHITE + NumberFormat.getNumberInstance(Locale.US).format(FrozenEconomyHandler.getBalance(sender.getUniqueId())));
        } else {
            sender.sendMessage(ChatColor.GOLD + "Balance of " + player + ": " + ChatColor.WHITE + NumberFormat.getNumberInstance(Locale.US).format(FrozenEconomyHandler.getBalance(player)));
        }
    }

}