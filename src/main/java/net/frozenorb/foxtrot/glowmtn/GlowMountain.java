package net.frozenorb.foxtrot.glowmtn;

import lombok.Getter;
import net.frozenorb.foxtrot.team.claims.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GlowMountain {

    @Getter private Claim claim;
    @Getter private final Set<BlockVector> glowstone = new HashSet<>();
    @Getter private final Map<UUID, Set<BlockVector>> minedMap = new ConcurrentHashMap<>(); //TODO: Use this data more :)

    public GlowMountain(Claim claim) {
        this.claim = claim;
    }

    public void scan() {
        reset(); // any mined glowstone returned to correct state

        glowstone.clear(); // clean storage

        World world = Bukkit.getWorld(claim.getWorld());
        for(int x = claim.getX1(); x < claim.getX2(); x++) {
            for(int y = claim.getY1(); y < claim.getY2(); y++) {
                for(int z = claim.getZ1(); z < claim.getZ2(); z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if(block.getType() == Material.GLOWSTONE) {
                        glowstone.add(block.getLocation().toVector().toBlockVector());
                    }
                }
            }
        }
    }

    public void reset() {
        minedMap.clear(); // erase mining history
        World world = Bukkit.getWorld(claim.getWorld());

        for(BlockVector vector : glowstone) {
            world.getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()).setType(Material.GLOWSTONE);
        }

    }
}
