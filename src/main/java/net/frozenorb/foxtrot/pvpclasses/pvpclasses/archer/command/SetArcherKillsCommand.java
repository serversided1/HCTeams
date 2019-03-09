package net.frozenorb.foxtrot.pvpclasses.pvpclasses.archer.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SetArcherKillsCommand {

	@Command(names = "setarcherkills", permission = "op")
	public static void setArcherKills(Player player, @Param(name = "player") Player target, @Param(name = "kills") int kills) {
		Foxtrot.getInstance().getArcherKillsMap().setArcherKills(player.getUniqueId(), kills);
		player.sendMessage(ChatColor.GREEN + "You set " + target.getName() + "'s archer kills to: " + kills);
	}

}
