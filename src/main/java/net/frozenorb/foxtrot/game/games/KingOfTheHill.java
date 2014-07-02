package net.frozenorb.foxtrot.game.games;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import net.frozenorb.Utilities.DataSystem.Regioning.CuboidRegion;
import net.frozenorb.Utilities.DataSystem.Regioning.RegionManager;
import net.frozenorb.Utilities.Types.Scrollable;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.commands.HostKOTH;
import net.frozenorb.foxtrot.game.Minigame;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.TimeUtils;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.mongodb.BasicDBObject;

/**
 * An online game classic, the person to capture the hill wins the event.
 * 
 * @author Kerem Celik
 * 
 */
public class KingOfTheHill extends Minigame {

	public static final int CAPTURE_SECODNS = 1200;

	private transient CuboidRegion hillRegion;
	private transient HashMap<String, Integer> progress = new HashMap<String, Integer>();
	private transient HashMap<String, Integer> broadcasted = new HashMap<String, Integer>();
	private transient String name;

	public KingOfTheHill(String name) {

		this.name = name;

		if (FoxtrotPlugin.getInstance().getMinigameManager().getData().containsField(getName())) {
			deserialize((BasicDBObject) FoxtrotPlugin.getInstance().getMinigameManager().getData().get(getName()));
		}

		hillRegion = RegionManager.get().getByName("kothHill_" + name.toLowerCase());

	}

	@Override
	public String getName() {
		return "King of the Hill";
	}

	@Override
	public String getPrimaryCommandName() {
		return "koth";
	}

	@Override
	public void beginGame() {

		new TaskRunnable().runTaskTimer(FoxtrotPlugin.getInstance(), 20L, 20L);

		for (final Player p : Bukkit.getOnlinePlayers()) {

			progress.put(getStorageValue(p), 0);

		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {

		if (hillRegion.contains(e.getFrom()) && !hillRegion.contains(e.getTo())) {
			if (progress.get(getStorageValue(e.getPlayer())) >= 1080) {

				Team t = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(e.getPlayer().getName());

				if (t != null) {
					for (Player p : t.getOnlineMembers()) {
						if (p != e.getPlayer() && !p.isDead()) {
							if (isOnHill(p)) {
								return;
							}
						}
					}
				}

				progress.put(getStorageValue(e.getPlayer()), 1080);

			}
		}

		if (hillRegion.contains(e.getTo())) {

			if (FoxtrotPlugin.getInstance().getBossBarManager().getMessage(e.getPlayer()) == null) {
				FoxtrotPlugin.getInstance().getBossBarManager().registerMessage(e.getPlayer(), new KOTHScroller(e.getPlayer()));

				Team t = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(e.getPlayer().getName());

				if (t != null) {
					for (Player p : t.getOnlineMembers()) {
						FoxtrotPlugin.getInstance().getBossBarManager().registerMessage(p, new KOTHScroller(p));

					}
				}
			}

		}
	}

	@Override
	public void onAnnounce() {
		try {
			Field f = HostKOTH.class.getDeclaredField("current" + name);
			f.set(null, this);
		}
		catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private String getStorageValue(Player p) {
		Team t = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName());

		if (t == null) {
			return p.getName();
		} else {
			return t.getFriendlyName();
		}
	}

	/**
	 * Gets if the player is currently in the hill region.
	 * 
	 * @param p
	 *            the player to check
	 * @return player on hill
	 */
	private boolean isOnHill(Player p) {

		Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName());

		if (team != null) {
			for (Player ps : team.getOnlineMembers()) {
				if (!ps.isDead() && hillRegion.contains(ps)) {
					return true;
				}
			}
		}

		return (hillRegion.contains(p)) && !p.isDead();
	}

	public boolean isContesting(Player p) {
		HashSet<String> con = new HashSet<String>();
		if (isContested(con)) {
			return isOnHill(p);
		}

		return false;
	}

	private List<Player> getPlayersOnHill() {
		List<Player> list = new ArrayList<Player>();

		for (Player p : Bukkit.getOnlinePlayers()) {
			if (hillRegion.contains(p)) {
				list.add(p);
			}
		}

		return list;

	}

	private boolean isContested(Set<String> handledNames) {

		boolean first = true;

		Team onHill = null;
		Player ponHill = null;

		for (Player p : getPlayersOnHill()) {

			if (p.hasMetadata("invisible") || p.getGameMode() == GameMode.CREATIVE || p.isDead()) {
				continue;
			}

			if (first) {
				ponHill = p;
				onHill = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName());
			} else {
				Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName());

				if (team == null || team != onHill) {

					if (handledNames != null) {
						handledNames.add(getStorageValue(p));
						handledNames.add(getStorageValue(ponHill));
					}

					return true;
				}
			}

			first = false;
		}
		return false;
	}

	@AllArgsConstructor
	private final class KOTHScroller implements Scrollable {
		private Player p;

		@Override
		public String next() {
			boolean onHill = isOnHill(p);

			double i = Math.round(((progress.containsKey(getStorageValue(p)) ? progress.get(getStorageValue(p)) : 0) / (double) CAPTURE_SECODNS) * 1000.0D) / 1000.0D;
			double percent = (i * 100);

			DecimalFormat df = new DecimalFormat("0.0");

			int secondsLeft = CAPTURE_SECODNS - progress.get(getStorageValue(p));
			String hm = TimeUtils.getMMSS(secondsLeft);

			String msg = "§eYou are at §d" + df.format(percent) + "% §7(" + hm + ")§e  -  " + ((p.getGameMode() == GameMode.CREATIVE || p.hasMetadata("invisible")) ? "§7Not capturing" : isContesting(p) ? "§6Contested" : !onHill ? "§cNot capturing" : "§aCapturing");

			return msg;
		}
	}

	@AllArgsConstructor
	private final class TaskRunnable extends BukkitRunnable {

		@Override
		public void run() {
			Set<String> handledNames = new HashSet<String>();

			boolean shouldIncrement = !isContested(handledNames);

			if (shouldIncrement) {
				for (Player p : getPlayersOnHill()) {

					if (p.hasMetadata("invisible") || p.getGameMode() == GameMode.CREATIVE || p.isDead()) {
						continue;
					}

					String key = getStorageValue(p);

					if (handledNames.contains(key)) {
						continue;
					}

					int current = progress.containsKey(key) ? progress.get(key) : 0;

					progress.put(key, current + 1);
					current = progress.get(key);
					handledNames.add(key);

					double i = Math.round((current / (double) CAPTURE_SECODNS) * 100.0D) / 100.0D;
					int percent = (int) (i * 100);

					if (percent != 100) {
						if ((percent % 20 == 0 && percent != 0) || percent >= 95) {

							if (FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName()) != null) {
								Team t = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName());

								if (!broadcasted.containsKey(t.getFriendlyName()) || broadcasted.get(t.getFriendlyName()) != percent) {

									Bukkit.broadcastMessage(MESSAGE_HEADER + "§6Team §c" + t.getFriendlyName() + "§6 is at §c" + (percent >= 95 ? "§l" : "") + (int) percent + "§6% captured!");
									broadcasted.put(t.getFriendlyName(), percent);
								}

							} else {

								if (!broadcasted.containsKey(p.getName()) || broadcasted.get(p.getName()) != percent) {

									Bukkit.broadcastMessage(MESSAGE_HEADER + "§6Player " + p.getDisplayName() + "§6 is at §c" + (percent >= 95 ? "§l" : "") + (int) percent + "§6% captured!");
									broadcasted.put(p.getName(), percent);
								}
							}
						}

					}

					if ((int) current >= CAPTURE_SECODNS) {
						finish(p);
						cancel();
					}

				}
			}

			for (String key : progress.keySet()) {

				if (handledNames.contains(key)) {
					continue;
				}

				int current = progress.get(key);

				progress.put(key, Math.max(0, current - 2));
				current = progress.get(key);

			}

		}
	}

	@Override
	public void cleanup() {
		progress.clear();
		broadcasted.clear();

		for (Player p : Bukkit.getOnlinePlayers()) {
			FoxtrotPlugin.getInstance().getBossBarManager().unregisterPlayer(p);
		}

		try {
			Field f = HostKOTH.class.getDeclaredField("current" + name);
			f.set(null, null);
		}
		catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void broadcastWinMessage(Player winner) {

		Team t = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(winner.getName());

		if (t == null) {

			String msg = MESSAGE_HEADER + "§ePlayer §3%s§e has won the §6%s§e event!";
			Bukkit.broadcastMessage(String.format(msg, winner.getDisplayName(), "§b" + name + " §6KOTH"));
		} else {

			String msg = MESSAGE_HEADER + "§eTeam §3%s§e has won the §6%s§e event!";
			Bukkit.broadcastMessage(String.format(msg, t.getFriendlyName(), "§b" + name + " §6KOTH"));
		}

	}

	@Override
	public void broadcastBegin() {
		Bukkit.broadcastMessage(MESSAGE_HEADER + "§6The §b" + name + "§d " + getName() + "§6 event has started!");

	}

	@Override
	public String getBCName() {
		return "§b" + name + " §6" + getName();
	}

}
