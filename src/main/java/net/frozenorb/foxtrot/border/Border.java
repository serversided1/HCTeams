package net.frozenorb.foxtrot.border;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.Coordinate;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.bitmask.DTRBitmaskType;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class Border {

    public static final int REGION_DISTANCE = 8;
    public static final int REGION_DISTANCE_SQUARED = REGION_DISTANCE * REGION_DISTANCE;

    @Getter private static Map<String, Map<Location, Long>> sentBlockChanges = new HashMap<String, Map<Location, Long>>();

    @Getter private List<Claim> claims = new ArrayList<Claim>();

    public void addClaim(Claim claim) {
        claims.add(claim.clone());
    }

    public void scanClaims(Player player) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        for (Map.Entry<Claim, Team> regionDataEntry : LandBoard.getInstance().getRegionData(player.getLocation(), REGION_DISTANCE, REGION_DISTANCE, REGION_DISTANCE)) {
            Claim claim = regionDataEntry.getKey();
            Team team = regionDataEntry.getValue();

            if (claim.contains(player)) {
                continue;
            }

            if (team.getOwner() == null) {
                if (team.hasDTRBitmask(DTRBitmaskType.DENY_REENTRY)) {
                    // If the team is a DENY_REENTRY claim (IE the End Spawn) and they're not inside of the claim
                    addClaim(claim);
                } else if (team.hasDTRBitmask(DTRBitmaskType.SAFE_ZONE) && SpawnTagHandler.isTagged(player)) {
                    // If the team is a SAFE_ZONE (IE spawn), they're not inside of it, and they're spawn tagged
                    addClaim(claim);
                } else if ((team.hasDTRBitmask(DTRBitmaskType.KOTH) || team.hasDTRBitmask(DTRBitmaskType.CITADEL)) && FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(player.getName())) {
                    // If it's an event zone (KOTH or Citadel) and they have a PvP Timer
                    addClaim(claim);
                }
            } else {
                if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(player.getName())) {
                    // If it's an actual claim and the player has a PvP Timer
                    addClaim(claim);
                }
            }
        }
    }

    public void sendToPlayer(Player player) {
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

        for (Claim claim : claims) {
            sendClaimToPlayer(player, claim);
        }
    }

    public void sendClaimToPlayer(Player player, Claim claim) {
        for (Coordinate coordinate : claim) {
            Location onPlayerY = new Location(player.getWorld(), coordinate.getX(), player.getLocation().getY(), coordinate.getZ());

            // Ignore an entire pillar if the block closest to the player is further than the max distance (none of the others will be close enough, either)
            if (onPlayerY.distanceSquared(player.getLocation()) > REGION_DISTANCE_SQUARED) {
                continue;
            }

            for (int i = -4; i < 5; i++) {
                Location check = onPlayerY.clone().add(0, i, 0);

                if (check.getBlock().getType().isTransparent() && check.distanceSquared(onPlayerY) < REGION_DISTANCE_SQUARED) {
                    player.sendBlockChange(check, Material.STAINED_GLASS, (byte) 14);
                    sentBlockChanges.get(player.getName()).put(check, System.currentTimeMillis() + 4000L);
                }
            }
        }
    }

    public static void clearPlayer(Player player) {
        if (!sentBlockChanges.containsKey(player.getName())) {
            return;
        }

        for (Location changedLocation : sentBlockChanges.get(player.getName()).keySet()) {
            player.sendBlockChange(changedLocation, changedLocation.getBlock().getType(), changedLocation.getBlock().getData());
        }

        sentBlockChanges.remove(player.getName());
    }

}