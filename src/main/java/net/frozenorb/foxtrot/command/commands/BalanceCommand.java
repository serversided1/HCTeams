package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.mBasic.Basic;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Locale;

public class BalanceCommand {

    @Command(names={ "Balance", "Econ", "Bal", "$" }, permissionNode="")
    public static void balace(Player sender, @Param(name="Target", defaultValue="self") String target) {
        if (target.equals("self")) {
            target = sender.getName();
        }

        sender.sendMessage(ChatColor.GOLD + "Balance: " + ChatColor.WHITE + NumberFormat.getNumberInstance(Locale.US).format(Basic.get().getEconomyManager().getBalance(target)));
    }

}