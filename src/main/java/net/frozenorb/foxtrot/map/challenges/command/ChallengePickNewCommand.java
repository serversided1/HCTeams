package net.frozenorb.foxtrot.map.challenges.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

public class ChallengePickNewCommand {

	@Command(names = {"challenge picknew"}, permission = "op")
	public static void newchallenges(CommandSender sender) {
		Foxtrot.getInstance().getChallengeHandler().pickNewChallenges();
		Foxtrot.getInstance().getChallengeHandler().saveDailyChallenges();

		sender.sendMessage(ChatColor.GREEN + "The challenges have been refreshed!");
	}

}
