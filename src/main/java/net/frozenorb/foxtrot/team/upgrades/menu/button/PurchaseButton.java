package net.frozenorb.foxtrot.team.upgrades.menu.button;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.upgrades.TeamUpgrade;
import net.frozenorb.qlib.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

@AllArgsConstructor
public class PurchaseButton extends Button {

	private Team team;
	private TeamUpgrade upgrade;

	@Override
	public String getName(Player player) {
		return ChatColor.YELLOW + upgrade.getUpgradeName();
	}

	@Override
	public List<String> getDescription(Player player) {
		int nextTier = upgrade.getTier(team) + 1;

		List<String> lore = new ArrayList<>();

		if (nextTier > upgrade.getTierLimit()) {
			lore.add("");
			lore.add(ChatColor.GRAY + "This upgrade is at maximum tier");
		} else {
			if (upgrade.getTierLimit() > 1) {
				lore.add(ChatColor.GRAY + "(" + ChatColor.BLUE + "Tier " + nextTier + ChatColor.GRAY + ")");
			}

			lore.add(ChatColor.GRAY + "(" + ChatColor.BLUE + "Price: " + ChatColor.GREEN + upgrade.getPrice(nextTier) + " points" + ChatColor.GRAY + ")");
			lore.add("");
			lore.add(ChatColor.GRAY + ChatColor.ITALIC.toString() + upgrade.getDescription());
			lore.add("");
			lore.add(ChatColor.YELLOW + "Click to purchase this upgrade");
		}

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
		if (!team.isOwner(player.getUniqueId()) && !team.isCoLeader(player.getUniqueId())) {
			player.sendMessage(ChatColor.RED + "You must be the owner or co-leader of your team to purchase upgrades.");
			return;
		}

		int nextTier = upgrade.getTier(team) + 1;

		if (nextTier > upgrade.getTierLimit()) {
			player.sendMessage(ChatColor.RED + "You cannot purchase a higher tier for this upgrade.");
			return;
		}

		int price = upgrade.getPrice(nextTier);

		if (price > team.getPoints()) {
			player.sendMessage(ChatColor.RED + "Your team does not have enough points to purchase that upgrade.");
			return;
		}

		team.spendPoints(price);
		team.getUpgradeToTier().put(upgrade.getUpgradeName(), nextTier);
		team.flagForSave();

		upgrade.onPurchase(player, team, nextTier, price);

		player.sendMessage(ChatColor.GREEN + "You purchased the " + ChatColor.AQUA + ChatColor.BOLD.toString() + upgrade.getUpgradeName() + (upgrade.getTierLimit() > 1 ? " Tier " + nextTier : "") + ChatColor.GREEN + " upgrade for " + ChatColor.LIGHT_PURPLE + price + " points" + ChatColor.GREEN + ".");
		player.closeInventory();
	}

}
