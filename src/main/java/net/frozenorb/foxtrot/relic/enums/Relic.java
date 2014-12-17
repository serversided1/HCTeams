package net.frozenorb.foxtrot.relic.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

@AllArgsConstructor
@RequiredArgsConstructor
public enum Relic {

    LIFESTEAL("Lifesteal", "Gives a chance of healing 0.5 hearts when hitting another player", Material.SLIME_BALL, 3),
    ANTI_ARCHER("Anti-Archer", "Randomly reduces incoming archer damage", Material.SLIME_BALL, 3),
    MINER("Miner", "Provides Haste II always and Invisiblity I when below Y 20", Material.SLIME_BALL, 1),
    FOOD_LOCK("Food Lock", "Locks your food at max", Material.SLIME_BALL, 1),
    PEARL_CDR("Pearl CDR", "Reduces enderpearl cooldown (-1s per tier)", Material.SLIME_BALL, 3);

    @Getter @NonNull private String name;
    @Getter @NonNull private String description;
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