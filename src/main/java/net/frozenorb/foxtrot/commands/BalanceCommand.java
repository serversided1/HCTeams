package net.frozenorb.foxtrot.commands;

import net.frozenorb.mBasic.Basic;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Locale;

public class BalanceCommand {

    @Command(names={ "Balance", "Econ", "Bal", "$" }, permissionNode="")
    public static void balance(Player sender, @Parameter(name="player", defaultValue="self") String player) {
        if (player.equals("self")) {
            sender.sendMessage(ChatColor.GOLD + "Balance: " + ChatColor.WHITE + NumberFormat.getNumberInstance(Locale.US).format(Basic.get().getEconomyManager().getBalance(sender.getName())));
        } else {
            sender.sendMessage(ChatColor.GOLD + "Balance of " + player + ": " + ChatColor.WHITE + NumberFormat.getNumberInstance(Locale.US).format(Basic.get().getEconomyManager().getBalance(player)));
        }
    }

}