package net.frozenorb.foxtrot.team.claims;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

import java.util.*;
import java.util.Map.Entry;

public class LandBoard {

    private static LandBoard instance;
    private final Map<String, Multimap<CoordinateSet, Entry<Claim, Team>>> buckets = new HashMap<String, Multimap<CoordinateSet, Entry<Claim, Team>>>();

    public LandBoard() {
        for (World world : FoxtrotPlugin.getInstance().getServer().getWorlds()) {
            buckets.put(world.getName(), HashMultimap.create());
        }
    }

    public void loadFromTeams() {
        for (Team team : FoxtrotPlugin.getInstance().getTeamHandler().getTeams()) {
            for (Claim claim : team.getClaims()) {
                setTeamAt(claim, team);
            }
        }
    }

    public Set<Entry<Claim, Team>> getRegionData(Location center, int xDistance, int yDistance, int zDistance) {
        Location loc1 = new Location(center.getWorld(), center.getBlockX() - xDistance, center.getBlockY() - yDistance, center.getBlockZ() - zDistance);
        Location loc2 = new Location(center.getWorld(), center.getBlockX() + xDistance, center.getBlockY() + yDistance, center.getBlockZ() + zDistance);

        return (getRegionData(loc1, loc2));
    }

    public Set<Entry<Claim, Team>> getRegionData(Location min, Location max) {
        Set<Entry<Claim, Team>> regions = new HashSet<Entry<Claim, Team>>();
        int step = 1 << CoordinateSet.BITS;

        for (int x = min.getBlockX(); x < max.getBlockX() + step; x += step) {
            for (int z = min.getBlockZ(); z < max.getBlockZ() + step; z += step) {
                CoordinateSet coordinateSet = new CoordinateSet(x, z);

                for (Entry<Claim, Team> regionEntry : buckets.get(min.getWorld().getName()).get(coordinateSet)) {
                    if (!regions.contains(regionEntry)) {
                        if ((max.getBlockX() >= regionEntry.getKey().getX1())
                                && (min.getBlockX() <= regionEntry.getKey().getX2())
                                && (max.getBlockZ() >= regionEntry.getKey().getZ1())
                                && (min.getBlockZ() <= regionEntry.getKey().getZ2())
                                && (max.getBlockY() >= regionEntry.getKey().getY1())
                                && (min.getBlockY() <= regionEntry.getKey().getY2())) {
                            regions.add(regionEntry);
                        }
                    }
                }
            }
        }

        return (regions);
    }

    public Entry<Claim, Team> getRegionData(Location location) {
        for (Entry<Claim, Team> data : buckets.get(location.getWorld().getName()).get(new CoordinateSet(location.getBlockX(), location.getBlockZ()))) {
            if (data.getKey().contains(location)) {
                return (data);
            }
        }

        return (null);
    }

    public Claim getClaim(Location location) {
        Entry<Claim, Team> regionData = getRegionData(location);
        return (regionData == null ? null : regionData.getKey());
    }

    public Team getTeam(Location location) {
        Entry<Claim, Team> regionData = getRegionData(location);
        return (regionData == null ? null : regionData.getValue());
    }

    public void setTeamAt(Claim claim, Team team) {
        Entry<Claim, Team> regionData = new AbstractMap.SimpleEntry<Claim, Team>(claim, team);
        int step = 1 << CoordinateSet.BITS;

        for (int x = regionData.getKey().getX1(); x < regionData.getKey().getX2() + step; x += step) {
            for (int z = regionData.getKey().getZ1(); z < regionData.getKey().getZ2() + step; z += step) {
                Multimap<CoordinateSet, Entry<Claim, Team>> worldMap = buckets.get(regionData.getKey().getWorld());

                if (regionData.getValue() == null) {
                    CoordinateSet coordinateSet = new CoordinateSet(x, z);
                    Iterator<Entry<Claim, Team>> claimIterator = worldMap.get(coordinateSet).iterator();

                    while (claimIterator.hasNext()) {
                        Entry<Claim, Team> entry = claimIterator.next();

                        if (entry.getKey().equals(regionData.getKey())) {
                            worldMap.remove(coordinateSet, entry);
                        }
                    }
                } else {
                    worldMap.put(new CoordinateSet(x, z), regionData);
                }
            }
        }

        updateClaim(regionData.getKey());
    }

    public void updateClaim(Claim modified) {
        ArrayList<VisualClaim> visualClaims = new ArrayList<VisualClaim>();
        visualClaims.addAll(VisualClaim.getCurrentMaps().values());

        for (VisualClaim visualClaim : visualClaims) {
            if (modified.isWithin(visualClaim.getPlayer().getLocation().getBlockX(), visualClaim.getPlayer().getLocation().getBlockZ(), VisualClaim.MAP_RADIUS, modified.getWorld())) {
                visualClaim.draw(true);
                visualClaim.draw(true);
            }
        }
    }

    public void updateSubclaim(Subclaim modified) {
        ArrayList<VisualClaim> visualClaims = new ArrayList<VisualClaim>();
        visualClaims.addAll(VisualClaim.getCurrentSubclaimMaps().values());

        for (VisualClaim visualClaim : visualClaims) {
            if (modified.getLoc1().distanceSquared(visualClaim.getPlayer().getLocation()) < VisualClaim.MAP_RADIUS * VisualClaim.MAP_RADIUS || modified.getLoc2().distanceSquared(visualClaim.getPlayer().getLocation()) < VisualClaim.MAP_RADIUS * VisualClaim.MAP_RADIUS) {
                visualClaim.draw(true);
                visualClaim.draw(true);
            }
        }
    }

    public void clear(Team team) {
        for (Claim claim : team.getClaims()) {
            setTeamAt(claim, null);
        }
    }

    public static LandBoard getInstance() {
        if (instance == null) {
            instance = new LandBoard();
        }


        return (instance);
    }


}