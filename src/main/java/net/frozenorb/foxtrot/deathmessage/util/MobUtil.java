package net.frozenorb.foxtrot.deathmessage.util;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class MobUtil {

    //***************************//

    public static String getItemName(ItemStack i) {
        if (i.getItemMeta().hasDisplayName()) {
            return (ChatColor.stripColor(i.getItemMeta().getDisplayName()));
        }

        return (WordUtils.capitalizeFully(i.getType().name().replace('_', ' ')));
    }

    //***************************//

}