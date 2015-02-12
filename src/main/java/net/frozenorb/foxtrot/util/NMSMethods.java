package net.frozenorb.foxtrot.util;

import net.minecraft.server.v1_7_R4.ItemStack;
import net.minecraft.server.v1_7_R4.PotionBrewer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;

public class NMSMethods {

    public static int getPotionResult(int origdata, org.bukkit.inventory.ItemStack ingredient) {
        return getPotionResult(origdata, CraftItemStack.asNMSCopy(ingredient));
    }

    private static int getPotionResult(int origdata, ItemStack ingredient) {
        int newdata = getBrewResult(origdata, ingredient);

        if ((origdata <= 0 || origdata != newdata)) {
            return origdata != newdata ? newdata : origdata;
        } else {
            return origdata;
        }
    }

    private static int getBrewResult(int i, ItemStack itemstack) {
        return itemstack == null ? i : (itemstack.getItem().m(itemstack) ? PotionBrewer.a(i, itemstack.getItem().i(itemstack)) : i);
    }

}