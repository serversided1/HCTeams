package net.frozenorb.foxtrot.map.kit.killstreaks;

import org.bukkit.entity.Player;

public abstract class Killstreak {

    public abstract String getName();

    public abstract int[] getKills();

    public abstract void apply(Player player);

    public boolean check(Player player, int kills) {
        if (shouldApply(kills)) {
            apply(player);
            return true;
        } else {
            return false;
        }
    }

    private boolean shouldApply(int kills) {
        for (int k : getKills()) {
            if (k == kills) {
                return true;
            }
        }

        return false;
    }

}
