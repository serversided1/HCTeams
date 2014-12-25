package net.frozenorb.foxtrot.pvpclasses.pvpclasses.bard;

import lombok.Getter;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffect;

public class BardEffect {

    @Getter private PotionEffect potionEffect;
    @Getter private int energy;
    @Getter private String description;

    public BardEffect(PotionEffect potionEffect) {
        this(potionEffect, -1);
    }

    public BardEffect(PotionEffect potionEffect, int energy) {
        this(potionEffect, energy, WordUtils.capitalize(potionEffect.getType().getName().toLowerCase().replace("_", " ")) + " " + (potionEffect.getAmplifier() + 1) + ChatColor.WHITE + " (" + (potionEffect.getDuration() / 20) + "s)");
    }

    public BardEffect(PotionEffect potionEffect, int energy, String description) {
        this.potionEffect = potionEffect;
        this.energy = energy;
        this.description = ChatColor.YELLOW + description;
    }

}