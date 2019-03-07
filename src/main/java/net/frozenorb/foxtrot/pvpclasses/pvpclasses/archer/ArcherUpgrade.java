package net.frozenorb.foxtrot.pvpclasses.pvpclasses.archer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.entity.Player;

public interface ArcherUpgrade {

	@Getter
	static Map<UUID, Long> cooldown = new HashMap<>();

	static boolean canUseAbility(Player player) {
		return System.currentTimeMillis() >= cooldown.getOrDefault(player.getUniqueId(), 0L);
	}

	static void setCooldown(Player player, int seconds) {
		cooldown.put(player.getUniqueId(), System.currentTimeMillis() + (seconds * 1000L));
	}

	static long getRemainingTime(Player player) {
		return cooldown.get(player.getUniqueId()) - System.currentTimeMillis();
	}

	String getUpgradeName();

	int getKillsNeeded();

	short getMaterialData();

	void onHit(Player shooter, Player victim);

	boolean applies(Player shooter);

}
