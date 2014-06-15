package net.frozenorb.foxtrot.types;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Class that will remove a player from a list, with customizable time left
 * 
 * @author Kerem Celik
 * @since 11/11/2013
 */
public abstract class CombatLogRunnable extends BukkitRunnable {

	private Player player;
	private int time;

	/**
	 * Creates a new instance of this
	 * 
	 * @param p
	 *            the player
	 * @param time
	 *            time left to remove
	 */
	public CombatLogRunnable(Player p, int time) {
		this.time = time;
		player = p;
	}

	/**
	 * The amount of time left
	 * 
	 * @return time left
	 */
	public int getTime() {
		return time;
	}

	/**
	 * Sets the time left to the given time
	 * 
	 * @param time
	 *            the time to set
	 */
	public void setTime(int time) {
		this.time = time;
	}

	@Override
	public void run() {
		time--;
		if (time == 0) {
			if (player.isOp())
				player.sendMessage(ChatColor.GREEN + "You may now log out safely.");
			cancel();
			onFinish();

		}
	}

	public abstract void onFinish();

}
