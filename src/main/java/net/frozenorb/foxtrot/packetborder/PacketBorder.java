package net.frozenorb.foxtrot.packetborder;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.Coordinate;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class PacketBorder {

    public static final int REGION_DISTANCE = 8;
    public static final int REGION_DISTANCE_SQUARED = REGION_DISTANCE * REGION_DISTANCE;

    @Getter private static Map<String, Map<Location, Long>> sentBlockChanges = new HashMap<>();

    @Getter private List<Claim> claims = new ArrayList<>();

    public void addClaim(Claim claim) {
        claims.add(new Claim(claim));
    }

    public void scanClaims(Player player) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        for (Map.Entry<Claim, Team> regionDataEntry : LandBoard.getInstance().getRegionData(player.getLocation(), REGION_DISTANCE, REGION_DISTANCE, REGION_DISTANCE)) {
            Claim claim = regionDataEntry.getKey();
            Team team = regionDataEntry.getValue();

            // Ignore claims if the player is in them.
            // There might become a time where we need to remove this
            // and make it a per-claim-type check, however for now
            // all checks work fine with this here.
            if (claim.contains(player)) {
                continue;
            }

            if (team.getOwner() == null) {
                if (team.hasDTRBitmask(DTRBitmask.DENY_REENTRY)) {
                    // If the team is a DENY_REENTRY claim (IE the End Spawn) and they're not inside of the claim
                    addClaim(claim);
                } else if (team.hasDTRBitmask(DTRBitmask.SAFE_ZONE) && SpawnTagHandler.isTagged(player)) {
                    // If the team is a SAFE_ZONE (IE spawn), they're not inside of it, and they're spawn tagged
                    addClaim(claim);
                } else if ((team.hasDTRBitmask(DTRBitmask.KOTH) || team.hasDTRBitmask(DTRBitmask.CITADEL)) && FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(player.getName())) {
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
            sentBlockChanges.put(player.getName(), new HashMap<>());
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
        // This gets us all the coordinates on the outside of the claim.
        // Probably could be made better
        for (Coordinate coordinate : claim) {
            Location onPlayerY = new Location(player.getWorld(), coordinate.getX(), player.getLocation().getY(), coordinate.getZ());

            // Ignore an entire pillar if the block closest to the player is further than the max distance (none of the others will be close enough, either)
            if (onPlayerY.distanceSquared(player.getLocation()) > REGION_DISTANCE_SQUARED) {
                continue;
            }

            for (int i = -4; i < 5; i++) {
                Location check = onPlayerY.clone().add(0, i, 0);

                if (check.getWorld().isChunkLoaded(check.getBlockX(), check.getBlockZ()) && check.getBlock().getType().isTransparent() && check.distanceSquared(onPlayerY) < REGION_DISTANCE_SQUARED) {
                    player.sendBlockChange(check, Material.STAINED_GLASS, (byte) 14); // Red stained glass
                    sentBlockChanges.get(player.getName()).put(check, System.currentTimeMillis() + 4000L); // The time the glass will stay for if the player walks away
                }
            }
        }
    }

    public static void clearPlayer(Player player) {
        if (sentBlockChanges.containsKey(player.getName())) {
            for (Location changedLocation : sentBlockChanges.get(player.getName()).keySet()) {
                player.sendBlockChange(changedLocation, changedLocation.getBlock().getType(), changedLocation.getBlock().getData());
            }

            sentBlockChanges.remove(player.getName());
        }
    }

}