package net.frozenorb.foxtrot.visual;

import lombok.Getter;
import net.frozenorb.foxtrot.visual.TabPlayer.TabOperation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class TabHandler extends BukkitRunnable {
	private static final boolean SEND_FAKE_PLAYER_LIST = false;

	@Getter private static TabHandler instance;
	private static HashMap<String, UUID> cachedUUIDs = new HashMap<String, UUID>();

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
        /*
		EntityPlayer pl = ((CraftPlayer) p).getHandle();

		RegionData<?> rd = FoxtrotPlugin.getInstance().getServerHandler().getRegion(p.getLocation(), p);

		if (pl.playerConnection.networkManager.getVersion() >= 47) {
			pl.playerConnection.sendPacket(new PacketTabHeader((IChatBaseComponent) ChatSerializer.a("{\"text\":\"§6HCTeams\"}"), (IChatBaseComponent) ChatSerializer.a("{\"text\":\"§eCurrently at " + rd.getRegion().getDisplayName() + "\"}")));

			if (!p.hasMetadata("subTitle")) {
				pl.playerConnection.sendPacket(new PacketTitle(Action.TIMES, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE));
				p.setMetadata("subTitle", new FixedMetadataValue(FoxtrotPlugin.getInstance(), true));
			}

		}

		if (SEND_FAKE_PLAYER_LIST) {

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
				String name = tp.getName().substring(1) + " ";

				PacketPlayOutPlayerInfo pi = null;

				if (tp.getOperation() == TabOperation.ADD) {
					pi = PacketPlayOutPlayerInfo.addPlayer(new WrappedEntityPlayer(new GameProfile(cachedUUIDs.get(tp.getName()), name), name));
				} else if (tp.getOperation() == TabOperation.REMOVE) {
					pi = PacketPlayOutPlayerInfo.removePlayer(new WrappedEntityPlayer(new GameProfile(cachedUUIDs.get(tp.getName()), name), name));
				}

				if (pi != null) {

					String code = "";

					if (Shared.get().getProfileManager().getProfile(tp.getName()) != null) {

						String color = Shared.get().getProfileManager().getProfile(tp.getName()).getColor();

						for (char c : color.toCharArray()) {
							code += "§" + c;
						}
					}

					NameChanger.setNametagHard(name, code + tp.getName().substring(0, 1), "");
					pl.playerConnection.sendPacket(pi);
				}
			}

		}
		*/
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
			cachedUUIDs.put(p.getName(), p.getUniqueId());

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
