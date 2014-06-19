package net.frozenorb.foxtrot.game;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.serialization.ReflectionSerializer;
import net.frozenorb.foxtrot.serialization.SerializableClass;
import net.frozenorb.foxtrot.util.TimeUtils;
import net.frozenorb.foxtrot.visual.scrollers.MinigameCountdownScroller;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
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

	private transient Set<Player> players = new HashSet<Player>();
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
	 * Adds a player to the list of participating players.
	 * 
	 * @param p
	 *            the player to add
	 */
	public void addPlayer(Player p) {
		p.setMetadata("joinLoc", new FixedMetadataValue(FoxtrotPlugin.getInstance(), p.getLocation()));

		players.add(p);
		onJoin(p);
		p.sendMessage(MESSAGE_HEADER + "§eYou have joined the §6" + getName() + "§a event!");
		FoxtrotPlugin.getInstance().getBossBarManager().registerMessage(p, new MinigameCountdownScroller(this));

	}

	/**
	 * Removes a player from the list of participating players.
	 * 
	 * @param p
	 *            the player to remove
	 */
	public void removePlayer(Player p) {
		players.remove(p);
		p.sendMessage(MESSAGE_HEADER + "§cYou have left the §6" + getName() + "§c event.");

		p.teleport((Location) p.getMetadata("joinLoc").get(0).value());
		FoxtrotPlugin.getInstance().getBossBarManager().unregisterPlayer(p);
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
	 * Gets the list of players partaking in the game.
	 * 
	 * @return players
	 */
	public Set<Player> getPlayers() {
		return players;
	}

	/**
	 * Announces that the game is starting soon, and creates/runs the task of
	 * displaying the time left to join the minigame.
	 */
	public void startAndAnnounce() {
		gameState = State.JOINABLE;

		final String format = MESSAGE_HEADER + "§6%s§e will start in §d%s§e.";
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

				if (secondsLeft % 15 == 0 || secondsLeft <= 5 || secondsLeft == 10) {

					String bc = String.format(format, getName(), TimeUtils.getConvertedTime(secondsLeft));
					Bukkit.broadcastMessage(bc);
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.sendMessage(bc + (!players.contains(p) ? " Type '§6/" + getPrimaryCommandName() + " join§e' to join!" : ""));
					}
				}

				currentTime++;
			}
		};
		onAnnounce();
		startTimer.runTaskTimer(FoxtrotPlugin.getInstance(), 0L, 20L);

	}

	/**
	 * Cancels the ongoing gamemode.
	 */
	public void cancelGame() {
		if (startTimer != null) {
			startTimer.cancel();
		}

		for (Player p : players) {
			p.teleport((Location) p.getMetadata("joinLoc").get(0).value());
		}
		Bukkit.broadcastMessage(MESSAGE_HEADER + "§cThe event has been cancelled.");
		players.clear();
		HandlerList.unregisterAll(this);
		cleanup();

	}

	/**
	 * Tries to start the game, but if there are not enough players, does not.
	 */
	public boolean tryToStart() {
		if (players.size() > 0) {

			String bc = MESSAGE_HEADER + "§eThe §6%s§e event has started with §b%s§e players!";
			Bukkit.broadcastMessage(String.format(bc, getName(), players.size()));
			gameState = State.IN_PROGRESS;

			for (Player p : players) {
				FoxtrotPlugin.getInstance().getBossBarManager().unregisterPlayer(p);

			}

			beginGame();

			Bukkit.getPluginManager().registerEvents(this, FoxtrotPlugin.getInstance());
			return true;

		} else {
			Bukkit.broadcastMessage(MESSAGE_HEADER + "§cThere are not enough players to start the event!");
			return false;
		}
	}

	/**
	 * Finishes the gamemode and announces the winner.
	 * 
	 * @param winner
	 *            the player who won the game
	 */
	public void finish(Player winner) {
		String msg = MESSAGE_HEADER + "§3%s§e has won the §6%s§e event!";
		Bukkit.broadcastMessage(String.format(msg, winner.getDisplayName(), getName()));

		for (Player p : players) {
			FoxtrotPlugin.getInstance().getBossBarManager().unregisterPlayer(p);
			p.teleport((Location) p.getMetadata("joinLoc").get(0).value());

		}

		players.clear();
		FoxtrotPlugin.getInstance().getMinigameManager().currentMinigame = null;
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

	/*
	 * --------------BUKKIT LISTENER METHODS-----------
	 */

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		players.remove(e.getPlayer());
	}
}
