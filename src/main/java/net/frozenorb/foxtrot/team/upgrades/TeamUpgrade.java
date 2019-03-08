package net.frozenorb.foxtrot.team.upgrades;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.upgrades.impl.ClaimEffectsCategoryTeamUpgrade;
import net.frozenorb.foxtrot.team.upgrades.impl.ClaimEffectTeamUpgrade;
import net.frozenorb.foxtrot.team.upgrades.impl.IncreasedXPRateTeamUpgrade;
import net.frozenorb.foxtrot.team.upgrades.impl.ExtraSlotTeamUpgrade;
import net.frozenorb.foxtrot.team.upgrades.impl.ExtraSpawnerTeamUpgrade;
import net.frozenorb.foxtrot.team.upgrades.impl.IncreasedSpawnRateTeamUpgrade;
import net.frozenorb.foxtrot.team.upgrades.impl.ReducedPearlCDTeamUpgrade;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public interface TeamUpgrade {

	@Getter
	static Map<String, TeamUpgrade> upgrades = new LinkedHashMap<>();

	static void register() {
		List<TeamUpgrade> list = Arrays.asList(
				new ExtraSlotTeamUpgrade(),
				new ExtraSpawnerTeamUpgrade(),
				new IncreasedSpawnRateTeamUpgrade(),
				new IncreasedXPRateTeamUpgrade(),
				new ReducedPearlCDTeamUpgrade(),
				new ClaimEffectsCategoryTeamUpgrade()
		);

		for (TeamUpgrade upgrade : list) {
			System.out.println("Registered team upgrade: \"" + upgrade.getUpgradeName() + "\"");
			upgrades.put(upgrade.getUpgradeName(), upgrade);

			if (upgrade instanceof Listener) {
				Foxtrot.getInstance().getServer().getPluginManager().registerEvents((Listener) upgrade, Foxtrot.getInstance());
			}
		}
	}

	String getUpgradeName();

	String getDescription();

	ItemStack getIcon();

	int getTierLimit();

	int getPrice(int tier);

	default int getTier(Team team) {
		return team.getUpgradeToTier().getOrDefault(getUpgradeName(), 0);
	}

	default boolean isCategory() {
		return false;
	}

	default List<ClaimEffectTeamUpgrade> getCategoryElements() {
		return null;
	}

}
