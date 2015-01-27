package net.frozenorb.foxtrot.server;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.commands.team.TeamMapCommand;
import net.frozenorb.foxtrot.team.dtr.bitmask.DTRBitmaskType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@SuppressWarnings({ "deprecation", "unchecked" })
public class PacketBorder {

    public static final int REGION_DISTANCE = 8;

    private static ConcurrentHashMap<String, Map<Location, Long>> sentBlockChanges = new ConcurrentHashMap<String, Map<Location, Long>>();
    @Getter private ConcurrentLinkedQueue<Claim> claims = new ConcurrentLinkedQueue<Claim>();

    public void sendToPlayer(Player player) {
        try {
            final Collection<Claim> syncClaims = Collections.synchronizedCollection(claims);

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
                // Chunk isn't loaded, we don't want to load it async. We'll just let it be.
                if (!FoxtrotPlugin.getInstance().getServer().getWorld(claim.getWorld()).isChunkLoaded(claim.getChunkX(), claim.getChunkZ())) {
                    continue;
                }

                Chunk chunk = claim.getChunk();

                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        if (x == 0 || x == 15 || z == 0 || z == 15) {
                            Block block = chunk.getBlock(x, player.getLocation().getBlockY() + 50, z);
                            Claim claimAtBlock = LandBoard.getInstance().getClaim(block);
                            boolean allClaimed = true;

                            for (BlockFace blockFace : TeamMapCommand.BLOCK_FACES_TO_CHECK) {
                                Claim claimAtRelative = LandBoard.getInstance().getClaim(block.getRelative(blockFace));

                                if (claimAtRelative == null || !claimAtRelative.getOwner().equals(claimAtBlock.getOwner())) {
                                    allClaimed = false;
                                    break;
                                }
                            }

                            if (allClaimed) {
                                continue;
                            }

                            Location onPlayerY = new Location(player.getWorld(), block.getX(), player.getLocation().getY(), block.getZ());

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

            if (player.getGameMode() != GameMode.CREATIVE) {
                for (Claim nearbyClaim : LandBoard.getInstance().getNearbyClaims(player.getLocation(), REGION_DISTANCE)) {
                    if (nearbyClaim.getOwner().getOwner() == null) {
                        if (nearbyClaim.getOwner().hasDTRBitmask(DTRBitmaskType.DENY_REENTRY) && !nearbyClaim.contains(player.getLocation())) {
                            // If we're denying reentry and they're not in the claim...
                            border.getClaims().add(nearbyClaim);
                        } else if (!nearbyClaim.contains(player.getLocation()) && nearbyClaim.getOwner().hasDTRBitmask(DTRBitmaskType.SAFE_ZONE) && SpawnTagHandler.isTagged(player) && !FoxtrotPlugin.getInstance().getServerHandler().isEOTW()) {
                            // If they're not in spawn, they ARE tagged, and it's not EOTW...
                            border.getClaims().add(nearbyClaim);
                        } else if ((nearbyClaim.getOwner().hasDTRBitmask(DTRBitmaskType.KOTH) || nearbyClaim.getOwner().hasDTRBitmask(DTRBitmaskType.CITADEL)) && FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(player.getName())) {
                            // If the claim is a KOTH or part of Citadel and they have PvP Protection...
                            border.getClaims().add(nearbyClaim);
                        }
                    } else if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(player.getName())) {
                        // If this is an actual team's claim and they have PvP Protection...
                        border.getClaims().add(nearbyClaim);
                    }
                }
            }

            if (border.getClaims().size() == 0) {
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