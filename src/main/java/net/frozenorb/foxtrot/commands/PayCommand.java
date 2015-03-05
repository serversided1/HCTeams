package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.mBasic.Basic;
import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.qlib.command.annotations.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.text.NumberFormat;

public class PayCommand {

    @Command(names={ "Pay", "P2P" }, permissionNode="")
    public static void pay(Player sender, @Parameter(name="Target") OfflinePlayer target, @Parameter(name="Amount") float value) {
        double balance = Basic.get().getEconomyManager().getBalance(sender.getName());

        if (!FoxtrotPlugin.getInstance().getPlaytimeMap().hasPlayed(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return;
        }

        Player bukkitPlayer = target.getPlayer();

        if (sender.equals(bukkitPlayer)) {
            sender.sendMessage(ChatColor.RED + "You cannot send money to yourself!");
            return;
        }

        if (value < 5) {
            sender.sendMessage(ChatColor.RED + "You must send at least $5!");
            return;
        }

        if (balance < value) {
            sender.sendMessage(ChatColor.RED + "You do not have $" + value + "!");
            return;
        }

        Basic.get().getEconomyManager().depositPlayer(target.getName(), value);
        Basic.get().getEconomyManager().withdrawPlayer(sender.getName(), value);

        sender.sendMessage(ChatColor.YELLOW + "You sent " + ChatColor.LIGHT_PURPLE + NumberFormat.getCurrencyInstance().format(value) + ChatColor.YELLOW + " to " + ChatColor.LIGHT_PURPLE + target.getName() + ChatColor.YELLOW + ".");

        if (bukkitPlayer != null) {
            bukkitPlayer.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.YELLOW + " sent you " + ChatColor.LIGHT_PURPLE + NumberFormat.getCurrencyInstance().format(value) + ChatColor.YELLOW + ".");
        }
    }

}