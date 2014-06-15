package net.frozenorb.foxtrot.command.commands;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.frozenorb.foxtrot.command.BaseCommand;
import net.frozenorb.mBasic.Basic;
import net.frozenorb.mBasic.EconomySystem.EconomyAccess;

public class Withdraw extends BaseCommand {

	public Withdraw() {
		super("withdraw", new String[] { "withd", "wd" });
	}

	@Override
	public void syncExecute() {
		EconomyAccess economyManager = Basic.get().getEconomyManager();
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "/" + cmd.getName() + " <amount>");
			return;
		}
		String number = args[0];
		int amount = 0;
		try {
			amount = Integer.parseInt(number);
		} catch (NumberFormatException ex) {
			sender.sendMessage(ChatColor.RED + "'" + args[0] + "' is not a number.");
			return;
		}
		if (amount < 1) {
			sender.sendMessage(ChatColor.RED + "You may not withdraw less than 1 gold.");
			return;
		}
		Player p = ((Player) sender);
		if (economyManager.getBalance(sender.getName()) < amount) {
			sender.sendMessage(ChatColor.RED + "You tried to withdraw " + amount + " gold, but your bank only has " + NumberFormat.getNumberInstance(Locale.US).format(economyManager.getBalance(sender.getName())) + ".");
			return;
		}
		if (p.getInventory().firstEmpty() == -1) {
			sender.sendMessage(ChatColor.RED + "Your inventory is full!");
			return;
		}
		economyManager.withdrawPlayer(p.getName(), amount);
		HashMap<Integer, ItemStack> leftOver = p.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, amount));
		for (ItemStack item : leftOver.values()) {
			if (item.getType() == Material.GOLD_INGOT) {
				p.sendMessage(ChatColor.RED + "" + item.getAmount() + " gold was dropped on the ground.");
				p.getWorld().dropItem(p.getLocation(), item);
			}
		}

		p.sendMessage("§7Successfully withdrew " + amount + " gold. Your new balance is " + (economyManager.getBalance(p.getName()) + "."));

	}

}
