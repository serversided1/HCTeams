package net.frozenorb.foxtrot.deathmessage.util;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class MobUtil {

    public static String getItemName(ItemStack itemStack) {
        if (itemStack.getItemMeta().hasDisplayName()) {
            return (ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()));
        }

        return (WordUtils.capitalizeFully(itemStack.getType().name().replace('_', ' ')));
    }

}