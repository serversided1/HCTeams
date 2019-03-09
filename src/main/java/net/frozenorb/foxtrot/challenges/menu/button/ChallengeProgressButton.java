package net.frozenorb.foxtrot.challenges.menu.button;

import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.challenges.Challenge;
import net.frozenorb.foxtrot.util.ProgressBarBuilder;
import net.frozenorb.qlib.menu.Button;
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
		return ChatColor.YELLOW + challenge.getName();
	}

	@Override
	public List<String> getDescription(Player player) {
		int progress = Foxtrot.getInstance().getChallengeHandler().getProgress(player, challenge);
		double percentage = ProgressBarBuilder.percentage(progress, challenge.getCountToQualify());

		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.DARK_GRAY.toString() + "[" + new ProgressBarBuilder().build(percentage) + ChatColor.DARK_GRAY + "] (" + ChatColor.GREEN + progress + ChatColor.DARK_GRAY + "/" + ChatColor.GRAY + challenge.getCountToQualify() + ChatColor.DARK_GRAY + ")");
		lore.add(ChatColor.GREEN + challenge.getDescription());

		return lore;
	}

	@Override
	public Material getMaterial(Player player) {
		return challenge.getIcon().getType();
	}

}
