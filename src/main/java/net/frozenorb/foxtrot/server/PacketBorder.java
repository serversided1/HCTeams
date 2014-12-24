package net.frozenorb.foxtrot.server;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.Coordinate;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.bitmask.DTRBitmaskType;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@SuppressWarnings({ "deprecation", "unchecked" })
public class PacketBorder {

    public static final int REGION_DISTANCE = 8;

    private static ConcurrentHashMap<String, Map<Location, Long>> sentBlockChanges = new ConcurrentHashMap<String, Map<Location, Long>>();
    @Getter private ConcurrentLinkedQueue<Claim> regions = new ConcurrentLinkedQueue<Claim>();

    public void addClaim(Claim claim) {
        regions.add(claim);
    }

    public void sendToPlayer(Player player) {
        try {
            final Collection<Claim> syncClaims = Collections.synchronizedCollection(regions);

            if (!sentBlockChanges.containsKey(player.getName())) {
                sentBlockChanges.put(player.getName(), new HashMap<Location, Long>());
            }

            Iterator<Map.Entry<Location, Long>> bordersIterator = sentBlockChanges.get(player.getName()).entrySet().iterator();

            // Remove borders after they 'expire' -- This is used to get rid of block changes the player has walked away from,
            // whose value in the map hasn't been updated recently.
            while (bordersIterator.hasNext()) {
                Map.Entry<Location, Long> border = bordersIterator.next();

                if (System.currentTimeMillis() >= border.getValue()) {
                    player.sendBlockChange(border.getKey(), border.getKey().getBlock().getType(), border.getKey().getBlock().getData());
                    bordersIterator.remove();
                }
            }

            for (Claim claim : syncClaims) {
                for (Coordinate coordinate : claim) {
                    Location onPlayerY = new Location(player.getWorld(), coordinate.getX(), player.getLocation().getY(), coordinate.getZ());

                    // Ignore an entire pillar if the block closest to the player is further than the max distance (none of the others will be close enough, either)
                    if (onPlayerY.distanceSquared(player.getLocation()) > REGION_DISTANCE * REGION_DISTANCE) {
                        continue;
                    }

                    for (int i = -4; i < 5; i++) {
                        Location check = onPlayerY.clone().add(0, i, 0);

                        if (!check.getBlock().getType().isSolid() && check.distanceSquared(onPlayerY) < REGION_DISTANCE * REGION_DISTANCE) {
                            player.sendBlockChange(check, Material.STAINED_GLASS, (byte) 14);
                            sentBlockChanges.get(player.getName()).put(check, System.currentTimeMillis() + 3000L);
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (player.isOp()) {
                player.sendMessage(ChatColor.RED + "An exception was thrown while trying to send border packets to you.");
            }

            e.printStackTrace();
        }
    }

    public static void clearPlayer(Player player) {
        try {
            if (!sentBlockChanges.containsKey(player.getName())) {
                return;
            }

            Iterator<Map.Entry<Location, Long>> bordersIterator = sentBlockChanges.get(player.getName()).entrySet().iterator();

            while (bordersIterator.hasNext()) {
                Location changedBlock = bordersIterator.next().getKey();

                player.sendBlockChange(changedBlock, changedBlock.getBlock().getType(), changedBlock.getBlock().getData());
                bordersIterator.remove();
            }
        } catch (Exception e) {
            if (player.isOp()) {
                player.sendMessage(ChatColor.RED + "An exception was thrown while trying to clear border packets for you.");
            }

            e.printStackTrace();
        }
    }

    public static void checkPlayer(Player player) {
        try {
            PacketBorder border = new PacketBorder();

            for (Map.Entry<Claim, Team> claimTeamEntry : LandBoard.getInstance().getRegionData(player.getLocation(), REGION_DISTANCE, REGION_DISTANCE, REGION_DISTANCE)) {
                if (player.getGameMode() != GameMode.CREATIVE) {
                    if (claimTeamEntry.getValue().getOwner() == null) {
                        if (claimTeamEntry.getValue().hasDTRBitmask(DTRBitmaskType.DENY_REENTRY) && !claimTeamEntry.getKey().contains(player)) {
                            border.addClaim(claimTeamEntry.getKey().clone());
                        } else if (!claimTeamEntry.getKey().contains(player) && claimTeamEntry.getValue().hasDTRBitmask(DTRBitmaskType.SAFE_ZONE) && SpawnTagHandler.isTagged(player) && !FoxtrotPlugin.getInstance().getServerHandler().isEOTW()) {
                            border.addClaim(claimTeamEntry.getKey().clone());
                        } else if ((claimTeamEntry.getValue().hasDTRBitmask(DTRBitmaskType.KOTH) || claimTeamEntry.getValue().hasDTRBitmask(DTRBitmaskType.CITADEL_TOWN) || claimTeamEntry.getValue().hasDTRBitmask(DTRBitmaskType.CITADEL_KEEP) || claimTeamEntry.getValue().hasDTRBitmask(DTRBitmaskType.CITADEL_COURTYARD)) && FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(player.getName())) {
                            border.addClaim(claimTeamEntry.getKey().clone());
                        }
                    } else if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(player.getName())) {
                        border.addClaim(claimTeamEntry.getKey().clone());
                    }
                }
            }

            if (border.getRegions().size() == 0) {
                clearPlayer(player);
            } else {
                border.sendToPlayer(player);
            }
        } catch (Exception e) {
            if (player.isOp()) {
                player.sendMessage(ChatColor.RED + "An exception was thrown while trying to calculate border packets for you.");
            }

            e.printStackTrace();
        }
    }

    public static class BorderThread extends Thread {

        public void run() {
            while (true) {
                for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
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