package net.frozenorb.foxtrot.serialization.serializers;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import net.frozenorb.Utilities.DataSystem.Regioning.CuboidRegion;
import net.frozenorb.foxtrot.serialization.JSONSerializer;

public class CuboidSerializer implements JSONSerializer<CuboidRegion> {

    @Override
    public BasicDBObject serialize(CuboidRegion o) {
        BasicDBObject db = new BasicDBObject();
        db.append("name", o.getName());
        db.append("max", new LocationSerializer().serialize(o.getMaximumPoint())).append("min", new LocationSerializer().serialize(o.getMinimumPoint()));
        BasicDBList list = new BasicDBList();
        for (String str : o.getTags())
            list.add(str);
        db.append("tags", list);
        return db;
    }

    @Override
    public CuboidRegion deserialize(BasicDBObject dbobj) {
        if (dbobj.containsField("name") && dbobj.containsField("max") && dbobj.containsField("min")) {
            CuboidRegion rg = new CuboidRegion(dbobj.getString("name"), new LocationSerializer().deserialize((BasicDBObject) dbobj.get("max")), new LocationSerializer().deserialize((BasicDBObject) dbobj.get("min")));
            if (dbobj.containsField("tags")) {
                BasicDBList dbl = (BasicDBList) dbobj.get("tags");
                for (Object o : dbl)
                    rg.addTag((String) o);
            }
            return rg;
        }
        return null;
    }
}
