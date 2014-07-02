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

		if (args.length == 1) {
			if (FoxtrotPlugin.getInstance().getJoinTimerMap().hasTimer(p)) {

				FoxtrotPlugin.getInstance().getJoinTimerMap().updateValue(p.getName(), -1L);
				sender.sendMessage(ChatColor.RED + "Your PVP Timer has been removed!");
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have a PVP Timer on!");
			}
			return;
		}

		if (FoxtrotPlugin.getInstance().getJoinTimerMap().hasTimer(p)) {
			sender.sendMessage(ChatColor.RED + "You have " + TimeUtils.getDurationBreakdown(FoxtrotPlugin.getInstance().getJoinTimerMap().getValue(p.getName()) - System.currentTimeMillis()) + " left on your PVP Timer.");
		} else {
			sender.sendMessage(ChatColor.RED + "You do not have a PVP Timer on!");
		}
	}
}
