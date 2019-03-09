package net.frozenorb.foxtrot.challenges.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.util.TimeUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class TokensCommand {

	@Command(names = {"tokens"}, permission = "")
	public static void tokens(Player sender) {
		int nextTokenSeconds = (int) ((Foxtrot.getInstance().getChallengeHandler().getPendingTokens().get(sender.getUniqueId()) - System.currentTimeMillis()) / 1_000L);

		sender.sendMessage(ChatColor.YELLOW + "You have " + ChatColor.GOLD + Foxtrot.getInstance().getTokensMap().getTokens(sender.getUniqueId()) + ChatColor.YELLOW + " tokens.");
		sender.sendMessage(ChatColor.YELLOW + "You will receive another token in " + ChatColor.LIGHT_PURPLE + TimeUtils.formatIntoDetailedString(nextTokenSeconds) + ChatColor.YELLOW + ".");
	}

}
