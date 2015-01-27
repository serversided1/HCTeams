package net.frozenorb.foxtrot.team.claims;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.commands.team.TeamMapCommand;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

public class LandBoard {

    private static LandBoard instance;
    private final Map<String, Claim> claims = new HashMap<String, Claim>();

    public void loadFromTeams() {
        for (Team team : FoxtrotPlugin.getInstance().getTeamHandler().getTeams()) {
            for (Claim claim : team.getClaims()) {
                addClaim(claim);
            }
        }
    }

    public Collection<Claim> getNearbyClaims(Location location, double radius) {
        int radiusChunks = (int) Math.ceil(radius / 16D);
        Set<Claim> results = new HashSet<Claim>();

        int xOffset = location.getBlockX() >> 4;
        int zOffset = location.getBlockZ() >> 4;

        for (int x = -radiusChunks; x <= radiusChunks; x++) {
            for (int z = -radiusChunks; z <= radiusChunks; z++) {
                Claim claimAt = claims.get(location.getWorld().getName() + ";" + (x + xOffset) + ";" + (z + zOffset));

                if (claimAt != null) {
                    results.add(claimAt);
                }
            }
        }

        return (results);
    }

    public Claim getClaim(Chunk chunk) {
        return (claims.get(chunk.getWorld().getName() + ";" + chunk.getX() + ";" + chunk.getZ()));
    }

    public Claim getClaim(Location location) {
        return (claims.get(location.getWorld().getName() + ";" + location.getChunk().getX() + ";" + location.getChunk().getZ()));
    }

    public Claim getClaim(Block block) {
        return (claims.get(block.getWorld().getName() + ";" + block.getChunk().getX() + ";" + block.getChunk().getZ()));
    }

    public Team getTeam(Location location) {
        Claim claim = getClaim(location);
        return (claim == null ? null : claim.getOwner());
    }

    public void addClaim(Claim claim) {
        claims.put(claim.toString(), claim);
        notifyClaimChange(claim);
    }

    public void removeClaim(Claim claim) {
        claims.remove(claim.toString());
        notifyClaimChange(claim);
    }

    public void removeClaims(Collection<Claim> toRemove) {
        for (Claim claim : toRemove) {
            claims.remove(claim.toString());
        }

        Iterator<Claim> peek = toRemove.iterator();

        if (peek.hasNext()) {
            notifyClaimChange(peek.next());
        }
    }

    public void notifyClaimChange(Claim modified) {
        for (Map.Entry<String, Set<Location>> claimMapEntry : new HashSet<Map.Entry<String, Set<Location>>>(TeamMapCommand.getSentLocations().entrySet())) {
            Player bukkitPlayer = FoxtrotPlugin.getInstance().getServer().getPlayerExact(claimMapEntry.getKey());

            if (bukkitPlayer != null) {
                TeamMapCommand.toggleMap(bukkitPlayer, true);
                TeamMapCommand.toggleMap(bukkitPlayer, true);
            }
        }
    }

    public void notifySubclaimChange(Subclaim modified) {
        for (VisualSubclaimMap visualSubclaimMap : VisualSubclaimMap.getCurrentMaps().values()) {
            if (modified.getLoc1().distanceSquared(visualSubclaimMap.getPlayer().getLocation()) < VisualSubclaimMap.MAP_RADIUS * VisualSubclaimMap.MAP_RADIUS || modified.getLoc2().distanceSquared(visualSubclaimMap.getPlayer().getLocation()) < VisualSubclaimMap.MAP_RADIUS * VisualSubclaimMap.MAP_RADIUS) {
                visualSubclaimMap.draw(true);
                visualSubclaimMap.draw(true);
            }
        }
    }

    public static LandBoard getInstance() {
        if (instance == null) {
            instance = new LandBoard();
        }

        return (instance);
    }

}