package net.frozenorb.foxtrot.team.claims;

import com.mongodb.BasicDBObject;
import lombok.Data;
import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;

@Data
public class Claim {

    @Getter private String world;
    @Getter private int chunkX;
    @Getter private int chunkZ;
    @Getter private Team owner;

    public Claim(Chunk chunk, Team owner) {
        this.world = chunk.getWorld().getName();
        this.chunkX = chunk.getX();
        this.chunkZ = chunk.getZ();
        this.owner = owner;
    }

    public Claim(String saveString, Team owner) {
        String[] split = saveString.split(";");

        this.world = split[0];
        this.chunkX = Integer.valueOf(split[1]);
        this.chunkZ = Integer.valueOf(split[2]);
        this.owner = owner;
    }

    public Claim(String world, int chunkX, int chunkZ, Team owner) {
        this.world = world;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.owner = owner;
    }

    public BasicDBObject json() {
        BasicDBObject dbObject = new BasicDBObject();

        dbObject.put("World", world);
        dbObject.put("ChunkX", chunkX);
        dbObject.put("ChunkZ", chunkZ);
        dbObject.put("Owner", owner.getUniqueId());

        return (dbObject);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Claim)) {
            return (false);
        }

        Claim claim = (Claim) object;
        return (claim.chunkX == chunkX && claim.chunkZ == chunkZ && claim.owner.getUniqueId().equals(owner.getUniqueId()));
    }

    public boolean contains(Block block) {
        return (block.getX() >> 4 == chunkX && block.getZ() >> 4 == chunkZ);
    }

    public boolean contains(Location location) {
        return (location.getBlockX() >> 4 == chunkX && location.getBlockZ() >> 4 == chunkZ);
    }

    public boolean contains(Chunk chunk) {
        return (chunk.getX() == chunkX && chunk.getZ() == chunkZ);
    }

    public Chunk getChunk() {
        return (FoxtrotPlugin.getInstance().getServer().getWorld(world).getChunkAt(chunkX, chunkZ));
    }

    @Override
    public int hashCode() {
        return (chunkX * 31 + chunkZ);
    }

    @Override
    public String toString() {
        return (world + ";" + chunkX + ";" + chunkZ);
    }

}