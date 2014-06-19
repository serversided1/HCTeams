package net.frozenorb.foxtrot.visual;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.frozenorb.Utilities.Types.Scrollable;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.armor.Kit;
import net.frozenorb.foxtrot.listener.FoxListener;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.TimeUtils;
import net.frozenorb.foxtrot.util.WrappedPlayer;
import net.frozenorb.foxtrot.visual.scrollers.ConstantScroller;
import net.frozenorb.foxtrot.visual.scrollers.HeaderScrollable;
import net.frozenorb.foxtrot.visual.scrollers.ToggleableScrollable;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Class that handles the scoreboard of a player.
 * 
 * @author Kerem Celik
 * 
 */
@SuppressWarnings("deprecation")
public class ScoreboardManager {

	public static final String OBJECTIVE_NAME = "smoke_weed";
	public static final int MAX_LINES = 16;
	public static final int LINE_WIDTH = 16;

	private HashMap<String, SBData> storedValues = new HashMap<String, ScoreboardManager.SBData>();

	private Set<String> hasSB = new HashSet<String>();

	private Set<String> toCancel = new HashSet<String>();

	/**
	 * Gets an offline player with the given name.
	 * 
	 * @param str
	 *            the name of the offline player
	 * @return the created offline player
	 */
	public OfflinePlayer generateName(String str) {

		return new WrappedPlayer(str);
	}

	public void startTask(final Player p) {
		final Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName());
		SBMap[] ar = {

		new SBMap(new Scrollable() {

			@Override
			public String next() {
				if (team == null || team.getRallyExpires() < System.currentTimeMillis()) {
					return "~1a";
				}
				return "§c§lRally Timer";
			}
		}, new Scrollable() {

			@Override
			public String next() {
				if (team == null || team.getRallyExpires() < System.currentTimeMillis()) {
					return "~1i";
				}

				long diff = team.getRallyExpires() - System.currentTimeMillis();
				return "   " + TimeUtils.getConvertedTime(diff / 1000);
			}
		}),

		new SBMap(new Scrollable() {

			@Override
			public String next() {
				if (!FoxListener.getEnderpearlCooldown().containsKey(p.getName()) || FoxListener.getEnderpearlCooldown().get(p.getName()) < System.currentTimeMillis()) {
					return "~2a";
				}
				return "§e§lPearl Timer";
			}
		}, new Scrollable() {

			@Override
			public String next() {
				if (!FoxListener.getEnderpearlCooldown().containsKey(p.getName()) || FoxListener.getEnderpearlCooldown().get(p.getName()) < System.currentTimeMillis()) {
					return "~2i";
				}

				long diff = FoxListener.getEnderpearlCooldown().get(p.getName()) - System.currentTimeMillis();

				if ((int) diff / 1000 == 0) {
					return "~2i";
				}

				return "   " + TimeUtils.getConvertedTime(diff / 1000);
			}
		}),

		new SBMap(new Scrollable() {

			@Override
			public String next() {
				if (!FoxtrotPlugin.getInstance().getJoinTimerMap().hasTimer(p)) {
					return "~3a";
				}
				return "§a§lPVP Timer";
			}
		}, new Scrollable() {

			@Override
			public String next() {
				if (!FoxtrotPlugin.getInstance().getJoinTimerMap().hasTimer(p)) {
					return "~3i";
				}

				long diff = FoxtrotPlugin.getInstance().getJoinTimerMap().getValue(p.getName()) - System.currentTimeMillis();
				return "   " + TimeUtils.getConvertedTime(diff / 1000);
			}
		}),

		new SBMap(new Scrollable() {

			@Override
			public String next() {
				if (FoxtrotPlugin.getInstance().getOppleMap().getValue(p.getName()) == null || FoxtrotPlugin.getInstance().getOppleMap().getValue(p.getName()) < System.currentTimeMillis()) {
					return "~4a";
				}
				return "§b§lGApple Timer";
			}
		}, new Scrollable() {

			@Override
			public String next() {
				if (FoxtrotPlugin.getInstance().getOppleMap().getValue(p.getName()) == null || FoxtrotPlugin.getInstance().getOppleMap().getValue(p.getName()) < System.currentTimeMillis()) {
					return "~4i";
				}

				long diff = FoxtrotPlugin.getInstance().getOppleMap().getValue(p.getName()) - System.currentTimeMillis();
				return "   " + TimeUtils.getConvertedTime(diff / 1000);
			}
		}),

		new SBMap(new Scrollable() {

			@Override
			public String next() {
				if (!Kit.getWarmupTasks().containsKey(p.getName())) {
					return "~5a";
				}
				return "§d§lClass Warmup";
			}
		}, new Scrollable() {

			@Override
			public String next() {
				if (!Kit.getWarmupTasks().containsKey(p.getName())) {
					return "~5i";
				}

				return "   " + Kit.getWarmupTasks().get(p.getName()).getSeconds() + " seconds";

			}
		}),

		};

		set(p, "§6§lFoxtrot Alpha", ar);
	}

	/**
	 * Cancels the scoreboard task for a player, and sets the scoreboard to
	 * null.
	 * 
	 * @param p
	 *            the player to cancel
	 */
	public void cancel(Player p) {
		toCancel.add(p.getName());
	}

	/**
	 * Sets the player's scoreboard and begins the update task.
	 * 
	 * @param p
	 *            the player whose scoreboard to set
	 * @param headers
	 *            array of scrollable headers
	 * @param datas
	 *            array of scrollable data
	 */
	public void set(final Player p, final String title, final SBMap[] sbmap) {
		Scoreboard sb = p.getScoreboard();

		hasSB.add(p.getName().toLowerCase());
		final ConstantScroller scrollingTitle = new ConstantScroller(title);

		SBData sbdata = new SBData(scrollingTitle);

		if (sb != null) {
			p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			sb = null;
		}

		if (sb == null || (sb != null && sb.getObjective(OBJECTIVE_NAME) == null)) {

			int currentlyAt = MAX_LINES;
			sb = Bukkit.getScoreboardManager().getNewScoreboard();
			Objective o = sb.registerNewObjective(OBJECTIVE_NAME, "everyday");
			o.setDisplaySlot(DisplaySlot.SIDEBAR);
			o.setDisplayName(scrollingTitle.next());

			for (int i = 0; i < sbmap.length; i += 1) {

				final Scrollable header = sbmap[i].getHeader();
				Scrollable data = sbmap[i].getData();

				final String spc = (currentlyAt == 16 ? "               ࡃ" : StringUtils.repeat(" ", currentlyAt));

				Scrollable spacer = new Scrollable() {

					@Override
					public String next() {
						if (header.next().startsWith("~")) {
							return "~noSpacer" + spc;
						} else
							return spc;
					}
				};
				sbdata.map(spc, new SBValue(spacer, currentlyAt));

				/* Handles the first spacer in the sb */
				if (!header.next().startsWith("~")) {

					Score gs = o.getScore(generateName(spc));
					gs.setScore(currentlyAt);

				}
				currentlyAt--;

				/* Handles the header */
				String head = header.next();

				sbdata.map(head, new SBValue(header, currentlyAt));

				if (!header.next().startsWith("~")) {
					Score s = o.getScore(generateName(head));
					s.setScore(currentlyAt);

				}
				currentlyAt--;

				/* Handles the line below the header */
				String dcur = data.next();

				sbdata.map(dcur, new SBValue(data, currentlyAt));

				if (!header.next().startsWith("~")) {
					Score sd = o.getScore(generateName(dcur));
					sd.setScore(currentlyAt);

				}
				currentlyAt--;

			}

			storedValues.put(p.getName(), sbdata);
			p.setScoreboard(sb);
		}
		/* Create the update task */
		new BukkitRunnable() {

			@Override
			public void run() {
				if (!p.isOnline() || !update(p)) {
					cancel();

					hasSB.remove(p.getName().toLowerCase());
					toCancel.remove(p.getName());
				}

			}
		}.runTaskTimer(FoxtrotPlugin.getInstance(), 5L, 20L);
	}

	/**
	 * Updates a player's scoreboard, given that
	 * {@link ScoreboardManager#set(Player, String, Scrollable[], Scrollable[])}
	 * has already been called in their online session.
	 * 
	 * @param player
	 *            the player to update
	 * @return if the update was successful
	 */
	public boolean update(Player player) {

		if (toCancel.contains(player.getName())) {
			toCancel.remove(player.getName());

			player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			return false;
		}

		Scoreboard sb = player.getScoreboard();

		Objective o = sb.getObjective(OBJECTIVE_NAME);

		if (o == null) {
			o = sb.registerNewObjective(OBJECTIVE_NAME, "everyday");
		}

		if (storedValues.containsKey(player.getName())) {

			SBData scbData = storedValues.get(player.getName());

			o.setDisplayName(scbData.getTitle().next());

			SBData newSbData = new SBData(scbData.getTitle());

			for (String nm : scbData.getTitles()) {

				SBValue val = scbData.recall(nm);

				sb.resetScores(generateName(nm));

				Scrollable data = val.getScrollable();

				String dTitle = data.next();

				/* Handles the enabling of old disabled scrollables */
				if (data instanceof ToggleableScrollable) {
					if (!((ToggleableScrollable) data).wasEnabled()) {
						if (((ToggleableScrollable) data).shouldDisplay()) {
							if (data instanceof HeaderScrollable) {
								Score gs = o.getScore(generateName(StringUtils.repeat(" ", val.getScore() + 1)));
								gs.setScore(val.getScore() + 1);
								((ToggleableScrollable) data).setEnabled(true);
							}
						}
					}
				}

				newSbData.map(dTitle, new SBValue(data, val.getScore()));

				Score s = o.getScore(generateName(dTitle));
				if (!dTitle.startsWith("~")) {

					s.setScore(val.getScore());

					if (data instanceof ToggleableScrollable) {
						((ToggleableScrollable) data).setEnabled(true);
					}
				} else {
					if (data instanceof ToggleableScrollable) {
						((ToggleableScrollable) data).setEnabled(false);
					}
				}

			}
			storedValues.put(player.getName(), newSbData);

		}
		return true;

	}

	/**
	 * Gets if the player currently has the scoreboard or not.
	 * 
	 * @param name
	 *            the name of the player to check
	 * @return has sb
	 */
	public boolean hasScoreboard(String name) {
		return hasSB.contains(name.toLowerCase());
	}

	@AllArgsConstructor
	@Data
	public static class SBMap {

		private Scrollable header;
		private Scrollable data;
	}

	@RequiredArgsConstructor
	public static class SBData {
		HashMap<String, SBValue> keyMap = new HashMap<String, ScoreboardManager.SBValue>();

		@Getter @NonNull private Scrollable title;

		/**
		 * Gets a list of current titles in the map.
		 * 
		 * @return titles
		 */
		public Collection<String> getTitles() {
			return keyMap.keySet();
		}

		/**
		 * Wipes the map of values.
		 */
		public void wipe() {
			keyMap.clear();
		}

		/**
		 * Delets a title from the map.
		 * 
		 * @param title
		 *            the title to delete
		 */
		public void deleteTitle(String title) {
			keyMap.remove(title);
		}

		/**
		 * Maps an {@link OfflinePlayer}'s name to the given {@link SBValue}
		 * instance to store values for a player's {@link Scoreboard} instance.
		 * 
		 * @param name
		 *            the name to map
		 * @param key
		 *            the key to map to the name
		 */
		public void map(String name, SBValue key) {
			keyMap.put(name, key);
		}

		/**
		 * Returns the appropriate {@link SBValue} instance if
		 * {@link SBData#map(String, SBValue)} was called.
		 * 
		 * @param name
		 *            the name that the {@link SBValue} is mapped to
		 * @return key
		 */
		public SBValue recall(String name) {
			return keyMap.get(name);
		}

	}

	@Data
	@AllArgsConstructor
	public static class SBValue {
		private Scrollable scrollable;
		private int score;
	}
}
