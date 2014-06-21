package net.frozenorb.foxtrot.game;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.serialization.ReflectionSerializer;
import net.frozenorb.foxtrot.serialization.SerializableClass;
import net.frozenorb.foxtrot.util.TimeUtils;
import net.frozenorb.foxtrot.visual.scrollers.MinigameCountdownScroller;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Represents a game that takes place in a seperate arena, under different
 * rules.
 * 
 * @author Kerem Celik
 * 
 */
@SerializableClass(appendClassSignature = true)
public abstract class Minigame extends ReflectionSerializer implements Listener {
	/**
	 * The message that is sent before most, if not all, messages regarding
	 * minigames.
	 */
	public static final String MESSAGE_HEADER = "§7§l[§3§lE§7§l]§r ";

	private transient State gameState;

	@Getter private transient int currentTime = -60;

	private transient BukkitRunnable startTimer;

	/**
	 * The states that the game can be in.
	 * 
	 * @author Kerem Celik
	 */
	public enum State {

		/**
		 * The game has been initialized but not started yet. Players are able
		 * to join.
		 */
		JOINABLE,

		/**
		 * The game is currently in progress, and new players cannot join.
		 */
		IN_PROGRESS,
	}

	/**
	 * Gets the state of the game.
	 * 
	 * @return game state
	 */
	public State getGameState() {
		return gameState;
	}

	/**
	 * Announces that the game is starting soon, and creates/runs the task of
	 * displaying the time left to join the minigame.
	 */
	public void startAndAnnounce() {
		gameState = State.JOINABLE;

		final String format = MESSAGE_HEADER + "§6%s§e will begin in §d%s§e.";

		String bc = String.format(format, getBCName(), TimeUtils.getConvertedTime(Math.abs(currentTime)));
		Bukkit.broadcastMessage(bc);

		startTimer = new BukkitRunnable() {

			@Override
			public void run() {
				int secondsLeft = Math.abs(currentTime);
				if (secondsLeft == 0) {
					if (tryToStart()) {
						cancel();
						startTimer = null;
						return;
					} else {
						currentTime = -60;
						secondsLeft = Math.abs(currentTime);
					}
				}

				currentTime++;
			}
		};
		onAnnounce();

		for (Player p : Bukkit.getOnlinePlayers()) {
			FoxtrotPlugin.getInstance().getBossBarManager().registerMessage(p, new MinigameCountdownScroller(this));
		}

		startTimer.runTaskTimer(FoxtrotPlugin.getInstance(), 0L, 20L);

	}

	/**
	 * Cancels the ongoing gamemode.
	 */
	public void cancelGame() {
		if (startTimer != null) {
			startTimer.cancel();
		}

		Bukkit.broadcastMessage(MESSAGE_HEADER + "§cThe event has been cancelled.");
		HandlerList.unregisterAll(this);
		cleanup();

	}

	/**
	 * Tries to start the game, but if there are not enough players, does not.
	 */
	public boolean tryToStart() {
		for (Player p : Bukkit.getOnlinePlayers()) {

			FoxtrotPlugin.getInstance().getBossBarManager().unregisterPlayer(p);

		}

		broadcastBegin();

		beginGame();
		gameState = State.IN_PROGRESS;

		Bukkit.getPluginManager().registerEvents(this, FoxtrotPlugin.getInstance());
		return true;
	}

	/**
	 * Finishes the gamemode and announces the winner.
	 * 
	 * @param winner
	 *            the player who won the game
	 */
	public void finish(Player winner) {
		broadcastWinMessage(winner);

		for (Player p : Bukkit.getOnlinePlayers()) {

			FoxtrotPlugin.getInstance().getBossBarManager().unregisterPlayer(p);

		}

		HandlerList.unregisterAll(this);
		currentTime = -60;
		cleanup();
	}

	/**
	 * Gets if the current minigame is under progress.
	 * 
	 * @return in progress
	 */
	public final boolean isInProgress() {
		return currentTime >= 0;
	}

	/*
	 * ---------------ABSTRACT METHODS-------------
	 */

	/**
	 * Gets the name of the Minigame.
	 * 
	 * @return name
	 */
	public abstract String getName();

	/**
	 * Gets the primary command name.
	 * 
	 * @return command
	 */
	public abstract String getPrimaryCommandName();

	/**
	 * Starts the game, and sets the state to {@link State#IN_PROGRESS};
	 */
	public abstract void beginGame();

	public abstract void broadcastWinMessage(Player winner);

	public abstract void broadcastBegin();

	public abstract String getBCName();

	/*
	 * --------------OVERRIDABLE METHODS-----------
	 */

	/**
	 * Gets an array of taliases for the Minigame.
	 * 
	 * @return aliases
	 */
	public String[] getAliases() {
		return new String[] {};
	}

	/**
	 * Called when a player joins the game.
	 * 
	 * @param p
	 *            the player joining
	 */
	public void onJoin(Player p) {}

	/**
	 * Called when the game is announced.
	 */
	public void onAnnounce() {}

	/**
	 * Method used to clean up the subclass objects.
	 */
	public void cleanup() {}

}
