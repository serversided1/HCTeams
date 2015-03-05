package net.frozenorb.foxtrot.util;

import net.frozenorb.foxtrot.FoxtrotPlugin;

import java.util.UUID;

public class UUIDUtils {

    public static String name(UUID uuid) {
        return (FoxtrotPlugin.getInstance().getServer().getOfflinePlayer(uuid).getName());
    }

    public static UUID uuid(String name) {
        return (FoxtrotPlugin.getInstance().getServer().getOfflinePlayer(name).getUniqueId());
    }

    public static String formatPretty(UUID uuid) {
        return (name(uuid) + " [" + uuid + "]");
    }

}