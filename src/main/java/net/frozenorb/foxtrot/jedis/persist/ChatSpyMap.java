package net.frozenorb.foxtrot.jedis.persist;

import net.frozenorb.foxtrot.jedis.RedisPersistMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by macguy8 on 11/8/2014.
 */
public class ChatSpyMap extends RedisPersistMap<List<String>> {

    public ChatSpyMap() {
        super("ChatSpy");
    }

    @Override
    public String getRedisValue(List<String> teams) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String team : teams) {
            stringBuilder.append(team).append(",");
        }

        if (stringBuilder.length() > 2) {
            stringBuilder.setLength(stringBuilder.length() - 2);
        }

        return (stringBuilder.toString());
    }

    @Override
    public List<String> getJavaObject(String str) {
        return (Arrays.asList(str.split(",")));
    }

    public List<String> getChatSpy(String player) {
        return (contains(player) ? getValue(player) : new ArrayList<String>());
    }

    public void setChatSpy(String player, List<String> teams) {
        updateValueAsync(player, teams);
    }

}