package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.server.ServerHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;

public class CreateBorderCommand {

    @Command(names={ "CreateBorder" }, permissionNode="op")
    public static void createBorder(Player sender) {
        ArrayList<Location> toChange = new ArrayList<Location>();
        int radius = ServerHandler.WARZONE_RADIUS;

        int lastz = 0;
        for (int x = -radius; x <= radius; ++x) {
            int z = (int) (Math.sqrt(radius * radius - x * x) + 0.5);
            int a = z;
            int bOld = lastz;
            if (lastz < z) {
                int swap = bOld;
                bOld = a;
                a = swap;
            }

            for (int zp = a; zp <= bOld; ++zp) {
                for (int y = 5; y < 256; ++y) {

                    Location l = new Location(Bukkit.getWorld("world"), x, y, zp);
                    Location l2 = new Location(Bukkit.getWorld("world"), x, y, -zp);

                    Block b = l.getBlock();
                    Block b1 = l2.getBlock();

                    if (!b.getType().name().startsWith("LOG") && b.getType() != Material.AIR && !b.isEmpty() && b.getType().isSolid() && !b.getType().isTransparent() && b.getType() != Material.LEAVES && b.getType() != Material.LEAVES_2) {
                        toChange.add(l);
                    }

                    if (!b1.getType().name().startsWith("LOG") && b1.getType() != Material.AIR && !b1.isEmpty() && b1.getType().isSolid() && !b1.getType().isTransparent() && b1.getType() != Material.LEAVES && b1.getType() != Material.LEAVES_2) {

                        toChange.add(l2);
                    }
                }
            }

            lastz = z;
        }

        sender.sendMessage(ChatColor.YELLOW + "Total of " + toChange.size() + " blocks to be changed.");

        final Iterator<Location> iter = toChange.iterator();

        new BukkitRunnable() {

            @Override
            public void run() {
                int done = 0;

                while (iter.hasNext() && done <= 200) {
                    iter.next().getBlock().setTypeIdAndData(Material.WOOL.getId(), (byte) 14, false);
                    done++;
                    iter.remove();

                }

                if (!iter.hasNext()) {
                    Bukkit.broadcastMessage("finished drawing line");
                    cancel();
                }
            }
        }.runTaskTimer(FoxtrotPlugin.getInstance(), 0L, 1L);
    }

}