package net.frozenorb.foxtrot.diamond;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import net.frozenorb.foxtrot.util.ParticleEffects;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import net.frozenorb.Utilities.DataSystem.Regioning.CuboidRegion;
import net.frozenorb.Utilities.DataSystem.Regioning.RegionManager;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.minecraft.util.org.apache.commons.io.FileUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

public class MountainHandler {
	private static int totalDiamonds;
	private static int minedDiamonds;

	private static HashSet<Location> minedBlocks = new HashSet<Location>();

	public static void load() {
		try {
			File f = new File("diamondmountain.json");
			if (!f.exists()) {
				f.createNewFile();

				FileUtils.write(f, "{}");
			}

			BasicDBObject dbo = (BasicDBObject) JSON.parse(FileUtils.readFileToString(f));
			if (dbo.containsField("totalDiamonds")) {
				totalDiamonds = dbo.getInt("totalDiamonds");
				System.out.println(totalDiamonds + " diamonds have been loaded");
			} else {
				scan();
			}

		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void scan() {
		totalDiamonds = 0;
		CuboidRegion cr = RegionManager.get().getByName("diamond_mountain");

		if (cr == null) {

			System.out.println("\nDiamond Mountain couldn't load!\nThere's no region with the name 'diamond_mountain'!\n");
			return;
		}
		for (Location l : cr) {
			if (l.getBlock().getType() == Material.DIAMOND_ORE) {
				totalDiamonds++;
			}
		}
		System.out.println(totalDiamonds + " diamonds have been scanned");

	}

	public static void diamondMined(Block b) {
		minedDiamonds++;
		b.getWorld().playEffect(b.getLocation(), Effect.MOBSPAWNER_FLAMES, 20);
        ParticleEffects.sendToLocation(ParticleEffects.RED_DUST, b.getLocation(), 0, 0, 0, 1, 2);
        ParticleEffects.sendToLocation(ParticleEffects.INSTANT_SPELL, b.getLocation(), 0, 0, 0, 1, 15);

		b.setType(Material.COBBLESTONE);
		minedBlocks.add(b.getLocation());

		if (minedDiamonds == totalDiamonds) {
			Bukkit.broadcastMessage("§b§lDiamond Mountain§e will regenerate in §d6§e hours!");

			Bukkit.getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), new Runnable() {

				@Override
				public void run() {
					reset();
					Bukkit.broadcastMessage("§b§lDiamond Mountain§e has just regenerated!");

				}
			}, 6 * 3600 * 20);

			/* Broadcasts */
			Bukkit.getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), new Runnable() {

				@Override
				public void run() {
					Bukkit.broadcastMessage("§b§lDiamond Mountain§e will regenerate in §d1§e hour!");

				}
			}, (6 * 3600 * 20) - 3600);

			Bukkit.getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), new Runnable() {

				@Override
				public void run() {
					Bukkit.broadcastMessage("§b§lDiamond Mountain§e will regenerate in §d30§e minutes!");

				}
			}, (6 * 3600 * 20) - 1800);

			Bukkit.getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), new Runnable() {

				@Override
				public void run() {
					Bukkit.broadcastMessage("§b§lDiamond Mountain§e will regenerate in §d15§e minutes!");

				}
			}, (6 * 3600 * 20) - 900);

			Bukkit.getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), new Runnable() {

				@Override
				public void run() {
					Bukkit.broadcastMessage("§b§lDiamond Mountain§e will regenerate in §d5§e minutes!");

				}
			}, (6 * 3600 * 20) - 300);

		}
	}

	public static void reset() {

		for (Location b : minedBlocks) {
			b.getBlock().setType(Material.DIAMOND_ORE);

		}

	}

}
