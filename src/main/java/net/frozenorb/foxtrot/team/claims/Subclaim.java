package net.frozenorb.foxtrot.team.claims;

import com.google.common.base.Joiner;
import com.mongodb.BasicDBObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.frozenorb.foxtrot.serialization.ReflectionSerializer;
import net.frozenorb.foxtrot.serialization.SerializableClass;
import net.frozenorb.foxtrot.serialization.serializers.LocationSerializer;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@EqualsAndHashCode(callSuper=false)
@SerializableClass
@Data
public class Subclaim extends ReflectionSerializer {

    @NonNull private Location loc1, loc2;
    @NonNull private String name;
    private List<String> members = new ArrayList<String>();

    public void addMember(String name) {
        members.add(name);
    }

    public boolean isMember(String name) {
        for (String str : members) {
            if (str.equalsIgnoreCase(name)) {
                return (true);
            }
        }

        return (false);
    }

    public void removeMember(String name) {
        Iterator<String> iterator = members.iterator();

        while (iterator.hasNext()) {
            String member = iterator.next();

            if (member.equalsIgnoreCase(name)) {
                iterator.remove();
            }
        }
    }

    public BasicDBObject json() {
        BasicDBObject dbObject = new BasicDBObject();
        LocationSerializer locationSerializer = new LocationSerializer();

        dbObject.put("Name", name);
        dbObject.put("Members", members);
        dbObject.put("Location1", locationSerializer.serialize(loc1));
        dbObject.put("Location2", locationSerializer.serialize(loc2));

        return (dbObject);
    }

    @Override
    public String toString() {
        return (loc1.getBlockX() + ":" + loc1.getBlockY() + ":" + loc1.getBlockZ() + ":" + loc2.getBlockX() + ":" + loc2.getBlockY() + ":" + loc2.getBlockZ() + ":" + name + ":" + Joiner.on(",").join(members));
    }

}