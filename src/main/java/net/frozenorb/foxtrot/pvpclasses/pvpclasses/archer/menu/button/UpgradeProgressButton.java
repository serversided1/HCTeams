package net.frozenorb.foxtrot.pvpclasses.pvpclasses.archer.menu.button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.archer.ArcherUpgrade;
import net.frozenorb.qlib.menu.Button;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class UpgradeProgressButton extends Button {

	private ArcherUpgrade upgrade;

	@Override
	public String getName(Player player) {
		return (Foxtrot.getInstance().getArcherKillsMap().getArcherKills(player.getUniqueId()) >= upgrade.getKillsNeeded() ? ChatColor.GREEN : ChatColor.YELLOW) + upgrade.getUpgradeName();
	}

	@Override
	public Material getMaterial(Player player) {
		return Material.INK_SACK;
	}

	@Override
	public byte getDamageValue(Player player) {
		return (byte) upgrade.getMaterialData();
	}

	@Override
	public List<String> getDescription(Player player) {
		final String[] blocks = new String[10];
		Arrays.fill(blocks, ChatColor.GRAY + StringEscapeUtils.unescapeJava("\u2588"));

		int progress = Foxtrot.getInstance().getArcherKillsMap().getArcherKills(player.getUniqueId());
		final double percentage = ((double) progress / (double) upgrade.getKillsNeeded()) * 100.0D;

		for (int i = 0; i < percentage / 10; i++) {
			blocks[i] = ChatColor.GREEN + StringEscapeUtils.unescapeJava("\u2588");
		}

		if (percentage >= 100.0D) {
			progress = upgrade.getKillsNeeded();
		}

		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.DARK_GRAY.toString() + "[" + StringUtils.join(blocks) + ChatColor.DARK_GRAY + "] (" + ChatColor.GREEN + progress + ChatColor.DARK_GRAY + "/" + ChatColor.GRAY + upgrade.getKillsNeeded() + ChatColor.DARK_GRAY + ")");

		if (percentage >= 100.0D) {
			lore.add(ChatColor.GREEN + "You unlocked this upgrade!");
		} else {
			lore.add(ChatColor.GREEN + "Kill " + upgrade.getKillsNeeded() + " people to unlock this upgrade");
		}

		return lore;
	}

}
