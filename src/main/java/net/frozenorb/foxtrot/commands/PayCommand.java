package net.frozenorb.foxtrot.commands;

import java.text.NumberFormat;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.economy.FrozenEconomyHandler;
import net.frozenorb.qlib.util.UUIDUtils;

public class PayCommand {

    @Command(names={ "Pay", "P2P" }, permission="")
    public static void pay(Player sender, @Param(name="player") UUID player, @Param(name="amount") float amount) {
        double balance = FrozenEconomyHandler.getBalance(sender.getUniqueId());
        Player bukkitPlayer = Foxtrot.getInstance().getServer().getPlayer(player);

        if (bukkitPlayer == null || !bukkitPlayer.isOnline()) {
            sender.sendMessage(ChatColor.RED + "That player is not online.");
            return;
        }

        if (sender.equals(bukkitPlayer)) {
            sender.sendMessage(ChatColor.RED + "You cannot send money to yourself!");
            return;
        }

        if (amount < 5) {
            sender.sendMessage(ChatColor.RED + "You must send at least $5!");
            return;
        }

        if (balance > 100000) {
            sender.sendMessage("§cYour balance is too high to send money. Please contact an admin to transfer money.");
            Bukkit.getLogger().severe("[ECONOMY] " + sender.getName() + " tried to send " + amount);
            return;
        }

        if (Double.isNaN(balance)) {
            sender.sendMessage("§cYou can't send money because your balance is fucked.");
            return;
        }

        if (Float.isNaN(amount)) {
            sender.sendMessage(ChatColor.RED + "Nope.");
            return;
        }

        if (balance < amount) {
            sender.sendMessage(ChatColor.RED + "You do not have $" + amount + "!");
            return;
        }

        FrozenEconomyHandler.deposit(player, amount);
        FrozenEconomyHandler.withdraw(sender.getUniqueId(), amount);
 
        Foxtrot.getInstance().getWrappedBalanceMap().setBalance(player, FrozenEconomyHandler.getBalance(player));
        Foxtrot.getInstance().getWrappedBalanceMap().setBalance(sender.getUniqueId(), FrozenEconomyHandler.getBalance(sender.getUniqueId()));

        sender.sendMessage(ChatColor.YELLOW + "You sent " + ChatColor.LIGHT_PURPLE + NumberFormat.getCurrencyInstance().format(amount) + ChatColor.YELLOW + " to " + ChatColor.LIGHT_PURPLE + UUIDUtils.name(player) + ChatColor.YELLOW + ".");

        bukkitPlayer.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.YELLOW + " sent you " + ChatColor.LIGHT_PURPLE + NumberFormat.getCurrencyInstance().format(amount) + ChatColor.YELLOW + ".");
    }

}