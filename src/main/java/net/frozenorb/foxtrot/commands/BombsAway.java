package net.frozenorb.foxtrot.commands;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;

public class BombsAway {

	@Command(names = "bombsaway", permission = "op")
	public static void bombsAway(Player player) {
		TNTPrimed entity = player.getWorld().spawn(player.getLocation(), TNTPrimed.class);
		entity.setFuseTicks(0);

		player.sendMessage(ChatColor.GOLD + "Bombs away!");
	}

}
