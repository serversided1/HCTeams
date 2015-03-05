package net.frozenorb.foxtrot.commands;

import net.frozenorb.mBasic.Basic;
import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.qlib.command.annotations.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Locale;

public class BalanceCommand {

    @Command(names={ "Balance", "Econ", "Bal", "$" }, permissionNode="")
    public static void balace(Player sender, @Parameter(name="Target", defaultValue="self") String target) {
        if (target.equals("self")) {
            sender.sendMessage(ChatColor.GOLD + "Balance: " + ChatColor.WHITE + NumberFormat.getNumberInstance(Locale.US).format(Basic.get().getEconomyManager().getBalance(sender.getName())));
        } else {
            sender.sendMessage(ChatColor.GOLD + "Balance of " + target + ": " + ChatColor.WHITE + NumberFormat.getNumberInstance(Locale.US).format(Basic.get().getEconomyManager().getBalance(target)));
        }
    }

}