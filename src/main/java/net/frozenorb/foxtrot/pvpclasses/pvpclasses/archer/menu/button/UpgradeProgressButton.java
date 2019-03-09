package net.frozenorb.foxtrot.pvpclasses.pvpclasses.archer.menu.button;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.map.kit.kits.KitListener;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.archer.ArcherUpgrade;
import net.frozenorb.foxtrot.util.ProgressBarBuilder;
import net.frozenorb.qlib.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

@AllArgsConstructor
public class UpgradeProgressButton extends Button {

	private ArcherUpgrade upgrade;

	@Override
	public String getName(Player player) {
		return ChatColor.YELLOW + upgrade.getUpgradeName();
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
		int progress = Foxtrot.getInstance().getArcherKillsMap().getArcherKills(player.getUniqueId());
		double percentage = ProgressBarBuilder.percentage(progress, upgrade.getKillsNeeded());

		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.DARK_GRAY.toString() + "[" + new ProgressBarBuilder().build(percentage) + ChatColor.DARK_GRAY + "] (" + ChatColor.GREEN + progress + ChatColor.DARK_GRAY + "/" + ChatColor.GRAY + upgrade.getKillsNeeded() + ChatColor.DARK_GRAY + ")");

		if (percentage >= 100.0D) {
			lore.add(ChatColor.GREEN + "You unlocked this upgrade");
			lore.add("");
			lore.add(ChatColor.YELLOW + "Click to load this kit");
		} else {
			lore.add(ChatColor.GREEN + "Kill " + upgrade.getKillsNeeded() + " people to unlock this upgrade");
		}

		return lore;
	}

	@Override
	public void clicked(Player player, int slot, ClickType clickType) {
		int progress = Foxtrot.getInstance().getArcherKillsMap().getArcherKills(player.getUniqueId());

		if (progress >= upgrade.getKillsNeeded()) {
			KitListener.attemptApplyKit(player, Foxtrot.getInstance().getMapHandler().getKitManager().get(player.getUniqueId(), "Archer" + upgrade.getUpgradeName()));
		}
	}

}
