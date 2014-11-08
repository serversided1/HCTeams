package net.frozenorb.foxtrot.server;

import net.frozenorb.Utilities.DataSystem.Regioning.CuboidRegion;
import net.frozenorb.Utilities.DataSystem.Regioning.RegionManager;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.Coordinate;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@SuppressWarnings({ "deprecation", "unchecked" })
public class PacketBorder {
    private static ConcurrentHashMap<String, HashMap<Location, Long>> borderBlocksSent = new ConcurrentHashMap<String, HashMap<Location, Long>>();

    private ConcurrentLinkedQueue<Claim> regions = new ConcurrentLinkedQueue<Claim>();

    public void addRegion(Claim rg) {
        regions.add(rg);
    }

    public void sendToPlayer(Player player) {
        try {
            final Collection<Claim> syncRegions = Collections.synchronizedCollection(regions);

            if (!borderBlocksSent.containsKey(player.getName())) {
                borderBlocksSent.put(player.getName(), new HashMap<Location, Long>());
            }

            Iterator<Map.Entry<Location, Long>> bordersIterator = borderBlocksSent.get(player.getName()).entrySet().iterator();

            while (bordersIterator.hasNext()) {
                Map.Entry<Location, Long> border = bordersIterator.next();

                if (System.currentTimeMillis() >= border.getValue() + 3000L) {
                    player.sendBlockChange(border.getKey(), border.getKey().getBlock().getType(), border.getKey().getBlock().getData());
                    bordersIterator.remove();
                }
            }

            for (Claim cr : syncRegions) {
                for (Coordinate loc : cr) {
                    int x = loc.getX();
                    int z = loc.getZ();

                    Location playerYLocation = new Location(player.getWorld(), x, player.getLocation().getY(), z);

                    for (int i = -4; i < 5; i++) {
                        Location check = playerYLocation.clone().add(0, i, 0);

                        if (cr.contains(check.getBlockX(), check.getBlockY(), check.getBlockZ())) {
                            if (check.distanceSquared(player.getLocation()) <= 64D) {
                                if (!check.getBlock().getType().isSolid()) {
                                    player.sendBlockChange(check, Material.STAINED_GLASS, (byte) 14);
                                    borderBlocksSent.get(player.getName()).put(check, System.currentTimeMillis());
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearPlayer(Player player) {
        if (!borderBlocksSent.containsKey(player.getName())) {
            return;
        }

        Iterator<Map.Entry<Location, Long>> bordersIterator = borderBlocksSent.get(player.getName()).entrySet().iterator();

        while (bordersIterator.hasNext()) {
            Map.Entry<Location, Long> border = bordersIterator.next();

            player.sendBlockChange(border.getKey(), border.getKey().getBlock().getType(), border.getKey().getBlock().getData());
            bordersIterator.remove();
        }
    }

    public static void checkPlayer(Player player) {
        try {
            PacketBorder border = new PacketBorder();
            Set<CuboidRegion> regionManagerRegions = Collections.synchronizedSet((Set<CuboidRegion>) RegionManager.get().getRegions().clone());

            int x = player.getLocation().getBlockX();
            int z = player.getLocation().getBlockZ();

            if (player.getWorld().getEnvironment() == World.Environment.THE_END) {
                for (CuboidRegion cr : regionManagerRegions) {
                    if (cr.getMaximumPoint().getWorld().equals(player.getWorld())) {
                        if (cr.hasTag("endspawn") && new Claim(cr.getMinimumPoint(), cr.getMaximumPoint()).isWithin(x, z, 8) && !cr.contains(player.getLocation()) && player.getGameMode() != GameMode.CREATIVE) {
                            CuboidRegion crAdd = new CuboidRegion("", cr.getMinimumPoint(), cr.getMaximumPoint());
                            border.addRegion(new Claim(crAdd.getMinimumPoint(), crAdd.getMaximumPoint()));
                        }

                        if (SpawnTag.isTagged(player) && cr.hasTag("endexit") && new Claim(cr.getMinimumPoint(), cr.getMaximumPoint()).isWithin(x, z, 8) && !FoxtrotPlugin.getInstance().getServerHandler().isEOTW()) {
                            CuboidRegion crAdd = new CuboidRegion("", cr.getMinimumPoint(), cr.getMaximumPoint());

                            Location min = crAdd.getMinimumPoint();
                            Location max = crAdd.getMaximumPoint();

                            min.setY(0D);
                            max.setY(256D);

                            crAdd.setLocation(min, max);
                            border.addRegion(new Claim(crAdd.getMinimumPoint(), crAdd.getMaximumPoint()));
                        }
                    }
                }
            } else if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(player.getName())) {
                for (Claim cBack : LandBoard.getInstance().getClaims()) {
                    if (cBack.isWithin(x, z, 8) && player.getGameMode() != GameMode.CREATIVE && !FoxtrotPlugin.getInstance().getServerHandler().isPreEOTW()) {
                        Claim c = cBack.clone();

                        c.setY1(0);
                        c.setY2(256);

                        border.addRegion(c);
                    }
                }
            } else if (SpawnTag.isTagged(player)) {
                for (CuboidRegion cr : regionManagerRegions) {
                    if (cr.getMaximumPoint().getWorld().equals(player.getWorld())) {
                        if ((cr.hasTag("overworldspawn") || cr.hasTag("netherspawn") || cr.hasTag("endspawn")) && new Claim(cr.getMinimumPoint(), cr.getMaximumPoint()).isWithin(x, z, 8) && player.getGameMode() != GameMode.CREATIVE && !FoxtrotPlugin.getInstance().getServerHandler().isEOTW()) {
                            CuboidRegion crAdd = new CuboidRegion("", cr.getMinimumPoint(), cr.getMaximumPoint());

                            Location min = crAdd.getMinimumPoint();
                            Location max = crAdd.getMaximumPoint();

                            min.setY(0D);
                            max.setY(256D);

                            crAdd.setLocation(min, max);
                            border.addRegion(new Claim(crAdd.getMinimumPoint(), crAdd.getMaximumPoint()));
                        }
                    }
                }
            } else {
                clearPlayer(player);
                return;
            }

            border.sendToPlayer(player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class BorderThread extends Thread {

        @Override
        public void run() {
            while (true) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    checkPlayer(player);
                }

                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}