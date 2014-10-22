package net.frozenorb.foxtrot.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import net.frozenorb.Utilities.DataSystem.Regioning.CuboidRegion;
import net.frozenorb.Utilities.DataSystem.Regioning.RegionManager;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.Coordinate;
import net.frozenorb.foxtrot.team.claims.LandBoard;

@SuppressWarnings({ "deprecation", "unchecked" })
public class PacketBorder {
	private static ConcurrentHashMap<String, ArrayList<Location>> borderBlocksSent = new ConcurrentHashMap<String, ArrayList<Location>>();
	private ConcurrentLinkedQueue<Claim> regions;

	public PacketBorder(Claim... regions) {
		this.regions = new ConcurrentLinkedQueue<Claim>(Arrays.asList(regions));
	}

	public void addRegion(Claim rg) {
		regions.add(rg);
	}

	public void sendToPlayer(Player p) {

		final Collection<Claim> syncregions = Collections.synchronizedCollection(regions);

		synchronized (syncregions) {

			for (Claim cr : syncregions) {

				final ArrayList<Location> locs = new ArrayList<Location>();
				for (Coordinate loc : cr) {

					int x = loc.getX(), z = loc.getZ();

					Location ll = new Location(Bukkit.getWorld("world"), x, p.getLocation().getY(), z);

					for (int i = -4; i < 5; i++) {
						Location check = ll.clone().add(0, i, -0);

						if (check.distanceSquared(p.getLocation()) <= (8 * 8)) {

							Block b = check.getBlock();
							if (!b.getType().isSolid()) {

								p.sendBlockChange(check, Material.STAINED_GLASS, (byte) 14);
								locs.add(check);
							}
						}

					}

				}

				if (borderBlocksSent.containsKey(p.getName())) {
					locs.addAll(borderBlocksSent.get(p.getName()));
				}

				borderBlocksSent.put(p.getName(), locs);
			}

		}

	}

	public static void clearPlayer(Player p) {

		if (borderBlocksSent.containsKey(p.getName())) {
			borderBlocksSent.get(p.getName()).forEach(l -> p.sendBlockChange(l, l.getBlock().getType(), l.getBlock().getData()));
		}
		borderBlocksSent.remove(p.getName());
	}

	public static void checkPlayer(Player p) {

		PacketBorder border = new PacketBorder();
		Set<CuboidRegion> regionManagerRegions = Collections.synchronizedSet((Set<CuboidRegion>) RegionManager.get().getRegions().clone());

		int x = p.getLocation().getBlockX(), z = p.getLocation().getBlockZ();

		if (FoxtrotPlugin.getInstance().getJoinTimerMap().hasTimer(p)) {

			for (Claim cBack : LandBoard.getInstance().getClaims()) {
				Claim c = cBack.clone();
				c.setY1(0);
				c.setY2(256);
				if (c.isWithin(x, z, 8)) {
					border.addRegion(c);
				}
			}

			for (CuboidRegion cr : regionManagerRegions) {

				if (cr.getName().startsWith("koth_") && new Claim(cr.getMinimumPoint(), cr.getMaximumPoint()).isWithin(x, z, 8)) {

					CuboidRegion crAdd = new CuboidRegion("", cr.getMinimumPoint(), cr.getMaximumPoint());

					Location min = crAdd.getMinimumPoint();
					Location max = crAdd.getMaximumPoint();

					min.setY(0D);
					max.setY(256D);

					crAdd.setLocation(min, max);

					Claim c = new Claim(crAdd.getMinimumPoint(), crAdd.getMaximumPoint());

					border.addRegion(c);
				}
			}

		} else if (SpawnTag.isTagged(p)) {
			for (final CuboidRegion cr : regionManagerRegions) {

				if (cr.hasTag("spawn") && new Claim(cr.getMinimumPoint(), cr.getMaximumPoint()).isWithin(x, z, 8)) {

					CuboidRegion crAdd = new CuboidRegion("", cr.getMinimumPoint(), cr.getMaximumPoint());

					Location min = crAdd.getMinimumPoint();
					Location max = crAdd.getMaximumPoint();

					min.setY(0D);
					max.setY(256D);

					crAdd.setLocation(min, max);
					Claim c = new Claim(crAdd.getMinimumPoint(), crAdd.getMaximumPoint());

					border.addRegion(c);
				}
			}
		} else {
			clearPlayer(p);
			return;
		}

		border.sendToPlayer(p);
	}

	public static class BorderThread extends Thread {
		@Override
		public void run() {
			while (true) {

				for (Player p : Bukkit.getOnlinePlayers()) {

					checkPlayer(p);

				}
				try {
					Thread.sleep(100L);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
