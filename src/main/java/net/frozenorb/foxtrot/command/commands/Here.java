package net.frozenorb.foxtrot.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.BaseCommand;
import net.frozenorb.foxtrot.team.claims.PhysicalChunk;

public class Here extends BaseCommand {

	public Here() {
		super("location", "here", "wherethefuck", "whereami", "loc");
	}

	@Override
	public void syncExecute() {
		Location loc = ((Player) sender).getLocation();

		net.frozenorb.foxtrot.team.Team owner = FoxtrotPlugin.getInstance().getTeamManager().getOwner(new PhysicalChunk(loc.getChunk().getX(), loc.getChunk().getZ()));
		if (owner != null) {
			sender.sendMessage("§eYou are in §c" + owner.getFriendlyName() + "§e's territory.");
			return;
		}

		if (!FoxtrotPlugin.getInstance().getServerManager().isWarzone(loc)) {
			sender.sendMessage(ChatColor.YELLOW + "You are in §7Unclaimed Territory§e!");
		} else {
			sender.sendMessage(ChatColor.YELLOW + "You are in the §cWarzone§e!");
		}
	}

}
