package net.frozenorb.foxtrot.visual;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import lombok.Getter;
import net.frozenorb.NametagSystem.NameChanger;
import net.frozenorb.foxtrot.visual.TabPlayer.TabOperation;
import net.frozenorb.mShared.Shared;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;

public class TabHandler extends BukkitRunnable {

	@Getter private static TabHandler instance;

	private ArrayList<TabPlayer> viewablePlayers = new ArrayList<TabPlayer>();

	public TabHandler() {
		instance = this;
	}

	/**
	 * Sends the tab packets to a player, populating their tab screen with
	 * accurate data.
	 * 
	 * @param p
	 *            the player to send the tab packets to
	 */
	public void sendTabPackets(Player p) {
		LinkedList<TabPlayer> send = new LinkedList<TabPlayer>();

		send.addAll(getViewablePlayers());

		Iterator<TabPlayer> iter = send.iterator();

		while (iter.hasNext()) {
			if (iter.next().getName().equals(p.getName())) {
				iter.remove();
			}
		}

		send.push(new TabPlayer(p.getName(), TabOperation.ADD));

		for (TabPlayer tp : send) {

			boolean online = tp.getOperation() == TabOperation.ADD;

			WrapperPlayServerPlayerInfo pac = new WrapperPlayServerPlayerInfo();
			pac.setOnline(online);
			pac.setPing((short) 0);

			String name = tp.getName().substring(1) + " ";
			pac.setPlayerName("$" + name);
			pac.sendPacket(p);

			String code = "";

			if (Shared.get().getProfileManager().getProfile(tp.getName()) != null) {

				String color = Shared.get().getProfileManager().getProfile(tp.getName()).getColor();

				for (char c : color.toCharArray()) {
					code += "§" + c;
				}
			}

			NameChanger.setNametagHard(name, code + tp.getName().substring(0, 1), "");

		}

	}

	/**
	 * Gets the list of names that should populate the tab screen.
	 * 
	 * @return tab screen
	 */
	public ArrayList<TabPlayer> getViewablePlayers() {
		return viewablePlayers;
	}

	/**
	 * Refreshes the list of viewable players to see in tab.
	 */
	@SuppressWarnings("unchecked")
	public void refreshViewablePlayers() {

		ArrayList<TabPlayer> oldnames = (ArrayList<TabPlayer>) viewablePlayers.clone();

		viewablePlayers.clear();

		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!p.hasMetadata("invisible")) {
				viewablePlayers.add(new TabPlayer(p.getName(), TabOperation.ADD));
			}
		}

		for (TabPlayer str : oldnames) {
			if (!viewablePlayers.contains(str) && str.getOperation() == TabOperation.ADD) {
				viewablePlayers.add(new TabPlayer(str.getName(), TabOperation.REMOVE));
			}
		}

	}

	@Override
	public void run() {
		refreshViewablePlayers();

		for (Player p : Bukkit.getOnlinePlayers()) {
			sendTabPackets(p);
		}
	}
}
