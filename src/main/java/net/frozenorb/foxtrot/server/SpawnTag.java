package net.frozenorb.foxtrot.server;

import java.util.HashMap;

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
	private static HashMap<String, SpawnTag> spawnTags = new HashMap<String, SpawnTag>();

	@NonNull private Player player;

	private int secondsLeft;
	private boolean inProgress;

	public static void removeTag(Player player) {

		if (spawnTags.containsKey(player.getName())) {
			SpawnTag spt = spawnTags.get(player.getName());

			spt.inProgress = false;
		}
	}

	public static void applyTag(Player player) {

		if (!isTagged(player)) {
			player.sendMessage(ChatColor.YELLOW + "You have been spawn-tagged for §c" + SPAWN_TAG_LENGTH_SECONDS + " §eseconds!");

		}

		if (spawnTags.containsKey(player.getName())) {
			SpawnTag spt = spawnTags.get(player.getName());

			spt.secondsLeft = SPAWN_TAG_LENGTH_SECONDS;

			if (!spt.inProgress) {
				spt.start();
			}
		} else {
			SpawnTag spt = new SpawnTag(player);

			spt.secondsLeft = SPAWN_TAG_LENGTH_SECONDS;
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
