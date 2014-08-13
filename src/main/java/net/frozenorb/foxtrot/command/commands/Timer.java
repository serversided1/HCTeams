package net.frozenorb.foxtrot.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.BaseCommand;
import net.frozenorb.foxtrot.util.TimeUtils;

public class Timer extends BaseCommand {

	public Timer() {
		super("pvptimer", "timer", "pvp");
	}

	@Override
	public void syncExecute() {
		Player p = (Player) sender;

		if (args.length > 0) {

			if (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("remove")) {
				if (FoxtrotPlugin.getInstance().getJoinTimerMap().hasTimer(p)) {

					FoxtrotPlugin.getInstance().getJoinTimerMap().updateValue(p.getName(), -1L);
					sender.sendMessage(ChatColor.RED + "Your PVP Timer has been removed!");
				} else {
					sender.sendMessage(ChatColor.RED + "You do not have a PVP Timer on!");
				}
			} else if (args[0].equalsIgnoreCase("revive")) {

				int lives = FoxtrotPlugin.getInstance().getServerManager().getLives(sender.getName());

				if (lives > 0) {
					sender.sendMessage(ChatColor.RED + "You currently have no lives!");
					return;
				}
				if (!(args.length > 1)) {
					sender.sendMessage(ChatColor.RED + "/pvp revive <player>");
					return;
				}

				if (!FoxtrotPlugin.getInstance().getDeathbanMap().isDeathbanned(args[1])) {
					sender.sendMessage(ChatColor.RED + "That player is not deathbanned!");
					return;
				}

				FoxtrotPlugin.getInstance().getDeathbanMap().updateValue(args[1], 0L);
				FoxtrotPlugin.getInstance().getServerManager().setLives(sender.getName(), lives - 1);

				sender.sendMessage(ChatColor.YELLOW + "You have revived §a" + args[1] + "§e!");
			} else if (args[0].equalsIgnoreCase("time")) {
				if (FoxtrotPlugin.getInstance().getJoinTimerMap().hasTimer(p)) {
					sender.sendMessage(ChatColor.RED + "You have " + TimeUtils.getDurationBreakdown(FoxtrotPlugin.getInstance().getJoinTimerMap().getValue(p.getName()) - System.currentTimeMillis()) + " left on your PVP Timer.");
				} else {
					sender.sendMessage(ChatColor.RED + "You do not have a PVP Timer on!");
				}
				return;
			} else if (args[0].equalsIgnoreCase("lives")) {
				String name = sender.getName();

				if (args.length > 1) {
					name = args[1];
				}
				int lives = FoxtrotPlugin.getInstance().getServerManager().getLives(name);

				sender.sendMessage("§6" + name + "'s lives§f: " + lives);
			} else {
				String[] msges = {
						"§c/pvp lives [target] - Shows amount of lives that a player has",
						"§c/pvp revive <player> - Revives targeted player",
						"§c/pvp time - Shows time left on PVP Timer",
						"§c/pvp enable - Remove PVP Timer" };

				p.sendMessage(msges);
			}

			return;
		}

	}
}
