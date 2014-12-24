package net.frozenorb.foxtrot.pvpclasses.pvpclasses.bard;

import lombok.Getter;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffect;

public class BardEffect {

    @Getter private PotionEffect potionEffect;
    @Getter private int mana;
    @Getter private String description;

    public BardEffect(PotionEffect potionEffect) {
        this(potionEffect, -1);
    }

    public BardEffect(PotionEffect potionEffect, int mana) {
        this(potionEffect, mana, WordUtils.capitalize(potionEffect.getType().getName().toLowerCase().replace("_", " ")) + " " + (potionEffect.getAmplifier() + 1) + ChatColor.WHITE + " (" + (potionEffect.getDuration() / 20) + "s)");
    }

    public BardEffect(PotionEffect potionEffect, int mana, String description) {
        this.potionEffect = potionEffect;
        this.mana = mana;
        this.description = ChatColor.YELLOW + description;
    }

}