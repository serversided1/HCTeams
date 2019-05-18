package net.frozenorb.foxtrot.events.nightmare.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.events.nightmare.progress.ProgressData;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class NightmareDebugCommand {

	@Command(names = { "nm progress", "nightmare progress" }, permission = "op")
	public static void progress(Player sender, @Param(name = "player", defaultValue = "self") Player target) {
		if (Foxtrot.getInstance().getNightmareHandler().hasProgression(target.getUniqueId())) {
			ProgressData progressData = Foxtrot.getInstance().getNightmareHandler().getOrCreateProgression(target.getUniqueId());

			sender.sendMessage(ChatColor.GOLD + "Displaying Nightmare progression data for " + target.getName());
			sender.sendMessage(ChatColor.YELLOW + "Participants: " + ChatColor.RED + progressData.getParticipants().size());
			sender.sendMessage(ChatColor.YELLOW + "Dropped Items: " + ChatColor.RED + progressData.getDroppedItems().size());
			sender.sendMessage(ChatColor.YELLOW + "Tracked Entities: " + ChatColor.RED + progressData.getTrackedEntities().size());
			sender.sendMessage(ChatColor.YELLOW + "Stages Complete: " + ChatColor.RED + progressData.getStagesComplete());
		} else {
			sender.sendMessage(ChatColor.RED + "There is no progression data for that player.");
		}
	}

	@Command(names = { "nm setstage", "nightmare setstage" }, permission = "op")
	public static void setStage(Player sender, @Param(name = "player", defaultValue = "self") Player target, @Param(name = "stage") int stage) {
		if (stage < 0 || stage > 5) {
			sender.sendMessage(ChatColor.RED + "Stage must be 0-5!");
			return;
		}

		if (Foxtrot.getInstance().getNightmareHandler().hasProgression(target.getUniqueId())) {
			ProgressData progressData = Foxtrot.getInstance().getNightmareHandler().getOrCreateProgression(target.getUniqueId());
			progressData.setStagesComplete(progressData.getStagesComplete() + 1);

			sender.sendMessage(ChatColor.GREEN + "Updated stage for " + target.getName() + ": " + stage);
		} else {
			sender.sendMessage(ChatColor.RED + "There is no progression data for that player.");
		}
	}

}
