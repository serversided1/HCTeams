package net.frozenorb.foxtrot.challenges.menu.button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.challenges.Challenge;
import net.frozenorb.qlib.menu.Button;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ChallengeProgressButton extends Button {

	private final Challenge challenge;

	public ChallengeProgressButton(Challenge challenge) {
		this.challenge = challenge;
	}

	@Override
	public String getName(Player player) {
		return (challenge.getCountToQualify() == Foxtrot.getInstance().getChallengeHandler().getProgress(player, challenge) ? ChatColor.GREEN : ChatColor.YELLOW) + challenge.getName();
	}

	@Override
	public List<String> getDescription(Player player) {
		final String[] blocks = new String[10];
		Arrays.fill(blocks, ChatColor.GRAY + StringEscapeUtils.unescapeJava("\u2588"));

		final int progress = Foxtrot.getInstance().getChallengeHandler().getProgress(player, challenge);
		final double percentage = ((double) progress / (double) challenge.getCountToQualify()) * 100.0D;

		for (int i = 0; i < percentage / 10; i++) {
			if (i > blocks.length) {
				break;
			}

			blocks[i] = ChatColor.GREEN + StringEscapeUtils.unescapeJava("\u2588");
		}

		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.DARK_GRAY.toString() + "[" + StringUtils.join(blocks) + ChatColor.DARK_GRAY + "] (" + ChatColor.GREEN + progress + ChatColor.DARK_GRAY + "/" + ChatColor.GRAY + challenge.getCountToQualify() + ChatColor.DARK_GRAY + ")");
		lore.add(ChatColor.GREEN + challenge.getDescription());

		return lore;
	}

	@Override
	public Material getMaterial(Player player) {
		return challenge.getIcon().getType();
	}

}
