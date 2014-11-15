package net.frozenorb.foxtrot.server;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.bitmask.DTRBitmaskType;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.Coordinate;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@SuppressWarnings({ "deprecation", "unchecked" })
public class PacketBorder {

    private static ConcurrentHashMap<String, HashMap<Location, Long>> borderBlocksSent = new ConcurrentHashMap<String, HashMap<Location, Long>>();
    @Getter private ConcurrentLinkedQueue<Claim> regions = new ConcurrentLinkedQueue<Claim>();

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

                        if (!check.getBlock().getType().isSolid() && check.distanceSquared(player.getLocation()) <= 64D) {
                            player.sendBlockChange(check, Material.STAINED_GLASS, (byte) 14);
                            borderBlocksSent.get(player.getName()).put(check, System.currentTimeMillis());
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
            int x = player.getLocation().getBlockX();
            int z = player.getLocation().getBlockZ();

            for (Claim claim : LandBoard.getInstance().getClaims()) {
                if (claim.isWithin(x, z, 8, player.getWorld().getName()) && player.getGameMode() != GameMode.CREATIVE) {
                    Team owner = LandBoard.getInstance().getTeamAt(claim);

                    if (owner.getOwner() == null) {
                        if (owner.hasDTRBitmask(DTRBitmaskType.DENY_REENTRY) && !claim.contains(player)) {
                            border.addRegion(claim.clone());
                        } else if (owner.hasDTRBitmask(DTRBitmaskType.SAFE_ZONE) && SpawnTagHandler.isTagged(player) && !FoxtrotPlugin.getInstance().getServerHandler().isEOTW()) {
                            Claim claimClone = claim.clone();

                            claimClone.setY1(0);
                            claimClone.setY2(256);

                            border.addRegion(claimClone);
                        } else if ((owner.hasDTRBitmask(DTRBitmaskType.KOTH) || owner.hasDTRBitmask(DTRBitmaskType.CITADEL_TOWN) || owner.hasDTRBitmask(DTRBitmaskType.CITADEL_KEEP) || owner.hasDTRBitmask(DTRBitmaskType.CITADEL_COURTYARD)) && FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(player.getName())) {
                            Claim claimClone = claim.clone();

                            claimClone.setY1(0);
                            claimClone.setY2(256);

                            border.addRegion(claimClone);
                        }
                    } else if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(player.getName())) {
                        Claim claimClone = claim.clone();

                        claimClone.setY1(0);
                        claimClone.setY2(256);

                        border.addRegion(claimClone);
                    }
                }
            }

            if (border.getRegions().size() == 0) {
                clearPlayer(player);
            } else {
                //border.sendToPlayer(player);
            }
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