package net.frozenorb.foxtrot.command.commands;

import java.util.ArrayList;
import java.util.List;

import net.frozenorb.foxtrot.command.BaseCommand;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PlaySound extends BaseCommand {

	public PlaySound() {
		super("playsound");
	}

	@Override
	public void syncExecute() {
		if (args.length > 0 && sender.isOp()) {
			try {
				Sound sound = Sound.valueOf(args[0]);
				double pitch = Double.parseDouble(args[1]);
				((Player) sender).playSound(((Player) sender).getLocation(), sound, 20F, (float) pitch);
			}
			catch (Exception ex) {
				sender.sendMessage(ex.getMessage());
			}
		}
	}

	@Override
	public List<String> getTabCompletions() {
		ArrayList<String> names = new ArrayList<String>();
		for (Sound s : Sound.values()) {
			names.add(s.toString());
		}
		return names;
	}

}
