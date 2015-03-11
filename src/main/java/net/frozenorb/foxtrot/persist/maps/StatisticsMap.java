package net.frozenorb.foxtrot.persist.maps;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import net.frozenorb.foxtrot.persist.PersistMap;

import java.util.UUID;

public class StatisticsMap extends PersistMap<BasicDBObject> {

    public StatisticsMap() {
        super("Statistics", "Statistics");
    }

    @Override
    public String getRedisValue(BasicDBObject dbObject) {
        return (dbObject.toString());
    }

    @Override
    public BasicDBObject getJavaObject(String str) {
        return ((BasicDBObject) JSON.parse(str));
    }

    @Override
    public Object getMongoValue(BasicDBObject dbObject) {
        return (dbObject);
    }

    public void updateBasic(UUID update) {
        updateValueAsync(update, new BasicDBObject());
    }

}