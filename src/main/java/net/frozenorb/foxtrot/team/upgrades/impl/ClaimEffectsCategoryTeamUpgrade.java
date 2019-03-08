package net.frozenorb.foxtrot.team.upgrades.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.upgrades.TeamUpgrade;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ClaimEffectsCategoryTeamUpgrade implements TeamUpgrade, Listener {

	private static final List<ClaimEffectTeamUpgrade> complexUpgrades = Arrays.asList(
			new ClaimEffectTeamUpgrade("Saturation", 50, 0, 1, new ItemStack(Material.MELON), PotionEffectType.SATURATION),
			new ClaimEffectTeamUpgrade("Speed", 25, 25, 3, new ItemStack(Material.SUGAR), PotionEffectType.SPEED),
			new ClaimEffectTeamUpgrade("Haste", 15, 10, 5, new ItemStack(Material.DIAMOND_PICKAXE), PotionEffectType.FAST_DIGGING),
			new ClaimEffectTeamUpgrade("Fire Resistance", 20, 0, 1, new ItemStack(Material.BLAZE_POWDER), PotionEffectType.FIRE_RESISTANCE),
			new ClaimEffectTeamUpgrade("Water Breathing", 20, 0, 1, new ItemStack(Material.WATER_BUCKET), PotionEffectType.WATER_BREATHING),
			new ClaimEffectTeamUpgrade("Jump Boost", 20, 20, 3, new ItemStack(Material.FEATHER), PotionEffectType.JUMP)
	);

	private static final Map<UUID, Boolean> receivingEffects = new HashMap<>();

	public ClaimEffectsCategoryTeamUpgrade() {
		// Start task to give effects to players in teams that have team upgrades
		Bukkit.getScheduler().runTaskTimerAsynchronously(Foxtrot.getInstance(), () -> {
			playerLoop:
			for (Player player : Bukkit.getOnlinePlayers()) {
				Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

				if (team != null) {
					for (Claim claim : team.getClaims()) {
						if (claim.contains(player)) {
							receivingEffects.put(player.getUniqueId(), true);

							for (ClaimEffectTeamUpgrade upgrade : complexUpgrades) {
								if (upgrade.getTier(team) > 0) {
									player.addPotionEffect(new PotionEffect(upgrade.getPotionEffectType(), Integer.MAX_VALUE, upgrade.getTier(team) - 1));
								}
							}

							continue playerLoop;
						}
					}

					if (receivingEffects.getOrDefault(player.getUniqueId(), false)) {
						receivingEffects.put(player.getUniqueId(), false);

						for (ClaimEffectTeamUpgrade upgrade : complexUpgrades) {
							if (upgrade.getTier(team) > 0) {
								player.removePotionEffect(upgrade.getPotionEffectType());
							}
						}
					}
				}
			}
		}, 0L, 20L);
	}

	@Override
	public String getUpgradeName() {
		return "Claim Effects";
	}

	@Override
	public String getDescription() {
		return "Receive passive effects while in your claim";
	}

	@Override
	public ItemStack getIcon() {
		return new ItemStack(Material.BEACON);
	}

	@Override
	public int getPrice(int tier) {
		return -1;
	}

	@Override
	public int getTier(Team team) {
		return -1;
	}

	@Override
	public int getTierLimit() {
		return -1;
	}

	@Override
	public boolean isCategory() {
		return true;
	}

	@Override
	public List<ClaimEffectTeamUpgrade> getCategoryElements() {
		return complexUpgrades;
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		receivingEffects.remove(event.getPlayer().getUniqueId());
	}

}
