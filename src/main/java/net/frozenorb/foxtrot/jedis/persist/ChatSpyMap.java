package net.frozenorb.foxtrot.jedis.persist;

import net.frozenorb.foxtrot.jedis.RedisPersistMap;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by macguy8 on 11/8/2014.
 */
public class ChatSpyMap extends RedisPersistMap<List<ObjectId>> {

    public ChatSpyMap() {
        super("ChatSpy");
    }

    @Override
    public String getRedisValue(List<ObjectId> teams) {
        StringBuilder stringBuilder = new StringBuilder();

        for (ObjectId team : teams) {
            stringBuilder.append(team).append(",");
        }

        if (stringBuilder.length() > 2) {
            stringBuilder.setLength(stringBuilder.length() - 2);
        }

        return (stringBuilder.toString());
    }

    @Override
    public List<ObjectId> getJavaObject(String str) {
        List<ObjectId> results = new ArrayList<ObjectId>();

        for (String split : str.split(",")) {
            results.add(new ObjectId(split));
        }

        return (results);
    }

    public List<ObjectId> getChatSpy(String player) {
        return (contains(player) ? getValue(player) : new ArrayList<ObjectId>());
    }

    public void setChatSpy(String player, List<ObjectId> teams) {
        updateValueAsync(player, teams);
    }

}