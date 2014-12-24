package net.frozenorb.foxtrot.relic.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@RequiredArgsConstructor
public enum Relic {

    LIFESTEAL("Lifesteal", Arrays.asList("Gives a chance of regenerating health when hitting another player", "", ChatColor.GREEN + "Tier 1: ", "5% chance, 1/2 heart restored", "", ChatColor.GREEN + "Tier 2: ", "10% chance, 1/2 heart restored", "", ChatColor.GREEN + "Tier 3:", "15% chance, 1 heart restored"), Material.WATCH, 3),
    ANTI_ARCHER("Anti-Archer", Arrays.asList("Randomly reduces incoming archer damage"), Material.WATCH, 3),
    MINER("Miner", Arrays.asList("Provides Haste II, Night Vision, and Invisibility!", "", ChatColor.GREEN + "Invisibility:", "When below Y 20", "", ChatColor.GREEN + "Night Vision:", "Always", "", ChatColor.GREEN + "Haste II:", "Always", "", ChatColor.DARK_PURPLE + "Debuffs:", "Weakness II"), Material.WATCH, 1),
    FOOD_LOCK("Food Lock", Arrays.asList("Prevents your food level from decreasing"), Material.WATCH, 1),
    PEARL_CDR("Pearl CDR", Arrays.asList("Reduces enderpearl cooldown", "", ChatColor.GREEN + "Tier 1: " + ChatColor.WHITE + "0.5s", ChatColor.GREEN + "Tier 2: " + ChatColor.WHITE + "1s", ChatColor.GREEN + "Tier 3: " + ChatColor.WHITE + "2s"), Material.WATCH, 3);

    @Getter @NonNull private String name;
    @Getter @NonNull private List<String> description;
    @Getter private Material material = Material.SLIME_BALL;
    @Getter private int maxTier = 3;

    public static Relic parse(String input) {
        for (Relic relic : values()) {
            if (relic.name.equalsIgnoreCase(input) || relic.name().equalsIgnoreCase(input)) {
                return (relic);
            }
        }

        return (null);
    }

}