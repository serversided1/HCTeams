package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.mBasic.Basic;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.NumberFormat;

public class PayCommand {

    @Command(names={ "Pay", "P2P" }, permissionNode="")
    public static void pay(Player sender, @Param(name="Target") String target, @Param(name="Amount") float value) {
        double balance = Basic.get().getEconomyManager().getBalance(sender.getName());

        if (!FoxtrotPlugin.getInstance().getPlaytimeMap().contains(target)) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return;
        }

        // Format online players name
        Player pTarget = FoxtrotPlugin.getInstance().getServer().getPlayer(target);

        if (pTarget != null) {
            target = pTarget.getName();
        }

        if (target.equalsIgnoreCase(sender.getName())) {
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

        Basic.get().getEconomyManager().depositPlayer(target, value);
        Basic.get().getEconomyManager().withdrawPlayer(sender.getName(), value);

        sender.sendMessage(ChatColor.YELLOW + "You sent " + ChatColor.LIGHT_PURPLE + NumberFormat.getCurrencyInstance().format(value) + ChatColor.YELLOW + " to " + ChatColor.LIGHT_PURPLE + target + ChatColor.YELLOW + ".");

        if (pTarget != null) {
            pTarget.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.YELLOW + " sent you " + ChatColor.LIGHT_PURPLE + NumberFormat.getCurrencyInstance().format(value) + ChatColor.YELLOW + ".");
        }
    }

}