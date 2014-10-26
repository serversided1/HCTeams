package net.frozenorb.foxtrot.server;

import java.util.HashMap;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.visual.scrollers.ImportantScrollable;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class SpawnTag {

	private static final int SPAWN_TAG_LENGTH_SECONDS = 60;
	@Getter private static HashMap<String, SpawnTag> spawnTags = new HashMap<String, SpawnTag>();

	@NonNull private Player player;

    @Getter private long expires;
	@Getter private int secondsLeft;
	private boolean inProgress;

	public static void removeTag(Player player) {

		if (spawnTags.containsKey(player.getName())) {
			SpawnTag spt = spawnTags.get(player.getName());

			spt.inProgress = false;
		}
	}

	public static void applyTag(Player player) {

		if (FoxtrotPlugin.getInstance().getServerManager().isSpawn(player.getLocation())) {
			return;
		}

		if (!isTagged(player)) {
			player.sendMessage(ChatColor.YELLOW + "You have been spawn-tagged for §c" + SPAWN_TAG_LENGTH_SECONDS + " §eseconds!");

		}

		if (spawnTags.containsKey(player.getName())) {
			SpawnTag spt = spawnTags.get(player.getName());

			spt.secondsLeft = SPAWN_TAG_LENGTH_SECONDS;
            spt.expires = System.currentTimeMillis() + (spt.secondsLeft * 1000L);

			if (!spt.inProgress) {
				spt.start();
			}
		} else {
			SpawnTag spt = new SpawnTag(player);

			spt.secondsLeft = SPAWN_TAG_LENGTH_SECONDS;
            spt.expires = System.currentTimeMillis() + (spt.secondsLeft * 1000L);
			spt.start();

			spawnTags.put(player.getName(), spt);

		}

	}

	public static void addSeconds(Player player, int seconds) {
		if (isTagged(player)) {
			SpawnTag spt = spawnTags.get(player.getName());
            int secondsAdd = Math.min(spt.secondsLeft + 16, SPAWN_TAG_LENGTH_SECONDS);

			spt.secondsLeft = secondsAdd;
            spt.expires = System.currentTimeMillis() + (spt.secondsLeft * 1000L);
		} else {
			player.sendMessage(ChatColor.YELLOW + "You have been spawn-tagged for §c" + seconds + " §eseconds!");

			SpawnTag spt = new SpawnTag(player);

			spt.secondsLeft = seconds;
            spt.expires = System.currentTimeMillis() + (spt.secondsLeft * 1000L);
			spt.start();

			spawnTags.put(player.getName(), spt);

		}
	}

	public static boolean isTagged(Player player) {
		return spawnTags.containsKey(player.getName()) && spawnTags.get(player.getName()).inProgress;
	}

	public void start() {
		inProgress = true;
		new BukkitRunnable() {

			@Override
			public void run() {
				secondsLeft--;

				if (secondsLeft == 0 || !inProgress) {
					inProgress = false;
					cancel();
				}
			}
		}.runTaskTimer(FoxtrotPlugin.getInstance(), 20L, 20L);
	}

	public ImportantScrollable createScrollable() {
		ImportantScrollable scrol = new ImportantScrollable() {

			@Override
			public String next() {
				return "§a§lSpawn-Tag:§d " + secondsLeft + " §eseconds left";
			}

			@Override
			public boolean canFinish() {
				return secondsLeft <= 0;
			}
		};

		return scrol;
	}

}
