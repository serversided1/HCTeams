package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.BaseCommand;
import net.frozenorb.mBasic.Basic;
import org.bukkit.ChatColor;

import java.text.NumberFormat;
import java.util.Locale;

public class Balance extends BaseCommand {

    public Balance() {
        super("balance", "econ", "bal", "$");
    }

    @Override
    public void syncExecute() {
        if (args.length == 1) {
            String m = args[0];
            sender.sendMessage(ChatColor.GOLD + "Balance: " + ChatColor.WHITE + NumberFormat.getNumberInstance(Locale.US).format(Basic.get().getEconomyManager().getBalance(m)));
            return;
        }
        sender.sendMessage(ChatColor.GOLD + "Balance: " + ChatColor.WHITE + NumberFormat.getNumberInstance(Locale.US).format(Basic.get().getEconomyManager().getBalance(sender.getName())));
    }
}
