package net.frozenorb.foxtrot.events.nightmare.command;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NightmareSetupCommands {

	@Command(names = { "nm gp", "nightmare gp" }, permission = "op")
	public static void givePotion(CommandSender sender, @Param(name = "player", defaultValue = "self") Player player) {
		player.getInventory().addItem(Foxtrot.getInstance().getNightmareHandler().getPotionItemStack());
		player.sendMessage(ChatColor.GREEN + "You were given a Potion of Dreams by a mysterious force...");
		sender.sendMessage(ChatColor.GOLD + "Gave potion to " + player.getName() + "!");
	}

	@Command(names = { "nm setwall", "nightmare setwall" }, permission = "op")
	public static void setWall(Player player, @Param(name = "stage") int stage) {
		if (stage <= 0 || stage > 5) {
			player.sendMessage(ChatColor.RED + "Stage must be 1-5!");
			return;
		}

		WorldEditPlugin worldEdit = (WorldEditPlugin) Foxtrot.getInstance().getServer().getPluginManager().getPlugin("WorldEdit");
		Selection selection = worldEdit.getSelection(player);

		if (selection == null) {
			player.sendMessage(ChatColor.RED + "Selection not finished!");
			return;
		}

		if (!selection.getWorld().equals(Foxtrot.getInstance().getNightmareHandler().getWorld())) {
			player.sendMessage(ChatColor.RED + "Selection world does not equal Nightmare event world!");
			return;
		}

		Location minPoint = selection.getMinimumPoint();
		Location maxPoint = selection.getMaximumPoint();

		List<Location> locations = new ArrayList<>();

		for (int x = minPoint.getBlockX(); x < maxPoint.getBlockX() + 1; x++) {
			for (int y = minPoint.getBlockY(); y < maxPoint.getBlockY() + 1; y++) {
				for (int z = minPoint.getBlockZ(); z < maxPoint.getBlockZ() + 1; z++) {
					locations.add(new Location(Foxtrot.getInstance().getNightmareHandler().getWorld(), x, y, z));
				}
			}
		}

		Foxtrot.getInstance().getNightmareHandler().getWallLocations().put(stage, locations);
		Foxtrot.getInstance().getNightmareHandler().saveConfig();

		player.sendMessage(ChatColor.GREEN + "Set wall locations (" + locations.size() + ") for stage: " + stage);
	}

	@Command(names = { "nm setcore", "nightmare setcore" }, permission = "op")
	public static void setCore(Player player, @Param(name = "stage") int stage) {
		if (stage <= 0 || stage > 5) {
			player.sendMessage(ChatColor.RED + "Stage must be 1-5!");
			return;
		}

		WorldEditPlugin worldEdit = (WorldEditPlugin) Foxtrot.getInstance().getServer().getPluginManager().getPlugin("WorldEdit");
		Selection selection = worldEdit.getSelection(player);

		if (selection == null) {
			player.sendMessage(ChatColor.RED + "Selection not finished!");
			return;
		}

		if (!selection.getWorld().equals(Foxtrot.getInstance().getNightmareHandler().getWorld())) {
			player.sendMessage(ChatColor.RED + "Selection world does not equal Nightmare event world!");
			return;
		}

		if (selection.getArea() > 1) {
			player.sendMessage(ChatColor.RED + "Selection area must be equal to 1 block!");
		}

		Foxtrot.getInstance().getNightmareHandler().getCoreLocations().put(stage, selection.getMinimumPoint());
		Foxtrot.getInstance().getNightmareHandler().saveConfig();

		player.sendMessage(ChatColor.GREEN + "Set core location for stage: " + stage);
	}

}
