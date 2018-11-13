package net.frozenorb.foxtrot.challenges;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.PlayerInventory;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.pvpclasses.PvPClass;
import net.frozenorb.foxtrot.pvpclasses.PvPClassHandler;

public abstract class Challenge implements Listener {
    
    @Getter private String name;
    @Getter private int countToQualify;
    
    public Challenge(String name, int countToQualify) {
        this.name = name;
        this.countToQualify = countToQualify;
        
        Bukkit.getPluginManager().registerEvents(this, Foxtrot.getInstance());
    }

    public boolean qualifies(PlayerInventory armor, String armorContains) {
        return (armor.getHelmet() != null && armor.getChestplate() != null && armor.getLeggings() != null && armor.getBoots() != null &&
                       armor.getHelmet().getType().name().startsWith(armorContains) && armor.getChestplate().getType().name().startsWith(armorContains) && armor.getLeggings().getType().name().startsWith(armorContains) && armor.getBoots().getType().name().startsWith(armorContains));
    }
    
    public String getMongoName() {
        return name.replaceAll(" ", "_");
    }
}
