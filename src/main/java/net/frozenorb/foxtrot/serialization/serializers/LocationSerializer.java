package net.frozenorb.foxtrot.serialization.serializers;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.mongodb.BasicDBObject;

import net.frozenorb.foxtrot.serialization.JSONSerializer;

public class LocationSerializer implements JSONSerializer<Location> {

    @Override
    public Location deserialize(BasicDBObject dbobj) {
        return new Location(Bukkit.getWorld(dbobj.getString("world")), dbobj.getDouble("x"), dbobj.getDouble("y"), dbobj.getDouble("z"), dbobj.getInt("yaw"), dbobj.getInt("pitch"));
    }

    @Override
    public BasicDBObject serialize(Location loc) {
        if (loc == null)
            return new BasicDBObject("empty", true);
        return new BasicDBObject("world", loc.getWorld().getName()).append("x", loc.getX()).append("y", loc.getY()).append("z", loc.getZ()).append("yaw", loc.getYaw()).append("pitch", loc.getPitch());
    }

}
