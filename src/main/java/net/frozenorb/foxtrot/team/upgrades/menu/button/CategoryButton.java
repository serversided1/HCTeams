package net.frozenorb.foxtrot.team.upgrades.menu.button;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.team.upgrades.TeamUpgrade;
import net.frozenorb.foxtrot.team.upgrades.menu.CategoryUpgradesMenu;
import net.frozenorb.qlib.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

@AllArgsConstructor
public class CategoryButton extends Button {

	private TeamUpgrade upgrade;

	@Override
	public String getName(Player player) {
		return ChatColor.YELLOW + upgrade.getUpgradeName();
	}

	@Override
	public List<String> getDescription(Player player) {
		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.GRAY + ChatColor.ITALIC.toString() + upgrade.getDescription());
		lore.add("");
		lore.add(ChatColor.YELLOW + "Click to view all purchasable upgrades");

		return lore;
	}

	@Override
	public Material getMaterial(Player player) {
		return upgrade.getIcon().getType();
	}

	@Override
	public byte getDamageValue(Player player) {
		return (byte) upgrade.getIcon().getDurability();
	}

	@Override
	public void clicked(Player player, int slot, ClickType clickType) {
		new CategoryUpgradesMenu(ChatColor.GOLD + upgrade.getUpgradeName(), upgrade.getCategoryElements()).openMenu(player);
	}

}
