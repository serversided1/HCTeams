package net.frozenorb.foxtrot.util;

import com.mongodb.BasicDBList;

import java.util.Collection;
import java.util.UUID;

public class UUIDUtils {

    public static String name(UUID uuid) {
        return (UUIDCache.name(uuid));
    }

    public static UUID uuid(String name) {
        return (UUIDCache.uuid(name));
    }

    public static String formatPretty(UUID uuid) {
        return (uuid == null ? "null" : uuid.toString());
    }

    public static BasicDBList uuidsToStrings(Collection<UUID> toConvert) {
        if (toConvert == null) {
            return (new BasicDBList());
        }

        BasicDBList dbList = new BasicDBList();

        for (UUID uuid : toConvert) {
            dbList.add(uuid.toString());
        }

        return (dbList);
    }

}