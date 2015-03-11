package net.frozenorb.foxtrot.util;

import com.mongodb.BasicDBList;
import net.frozenorb.foxtrot.FoxtrotPlugin;

import java.util.Collection;
import java.util.UUID;

public class UUIDUtils {

    public static String name(UUID uuid) {
        return (FoxtrotPlugin.getInstance().getServer().getOfflinePlayer(uuid).getName());
    }

    public static UUID uuid(String name) {
        return (FoxtrotPlugin.getInstance().getServer().getOfflinePlayer(name).getUniqueId());
    }

    public static String formatPretty(UUID uuid) {
        return (uuid.toString());
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