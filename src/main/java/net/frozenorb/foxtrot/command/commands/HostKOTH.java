package net.frozenorb.foxtrot.command.commands;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.BaseCommand;
import net.frozenorb.foxtrot.game.games.KingOfTheHill;

public class HostKOTH extends BaseCommand {

	public static KingOfTheHill currentTower;

	String[] locs = { "Tower" };

	public HostKOTH() {
		super("koth", "hostkoth");
	}

	@Override
	public void syncExecute() {
		if (sender.isOp()) {
			if (args.length > 0) {

				String n = args[0];

				if (!Arrays.asList(locs).contains(n)) {
					sender.sendMessage(ChatColor.RED + "Unrecognized name! Please tab-complete '§e/koth§c' for a list of valid names!");
					return;
				}

				try {
					Field f = HostKOTH.class.getDeclaredField("current" + n);

					if (f.get(null) != null) {
						sender.sendMessage(ChatColor.RED + "A " + n + " KOTH is currently running!");
						return;
					}

				}
				catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}

				FoxtrotPlugin.getInstance().getMinigameManager().startMinigame(new KingOfTheHill(n));
			} else {
				sender.sendMessage(ChatColor.RED + "/koth <name>");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "You are not allowed to do this!");
		}
	}

	@Override
	public List<String> getTabCompletions() {
		return Arrays.asList(locs);
	}

}
