package net.frozenorb.foxtrot.events.hell.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HellGivePotionCommand {

	@Command(names = "hell givepotion", permission = "op")
	public static void givePotion(CommandSender sender, @Param(name = "player") Player player) {
		player.getInventory().addItem(Foxtrot.getInstance().getHellHandler().getPotionItemStack());
		player.sendMessage(ChatColor.GREEN + "You were given a Potion of Dreams... Drink on your own accord.");
		sender.sendMessage(ChatColor.GOLD + "Gave potion to " + player.getName() + "!");
	}

}
