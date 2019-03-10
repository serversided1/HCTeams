package net.frozenorb.foxtrot.map.killstreaks.arcanetypes;

import net.frozenorb.foxtrot.map.killstreaks.Killstreak;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AttackDogs extends Killstreak {

    @Override
    public String getName() {
        return "Attack Dogs";
    }

    @Override
    public int[] getKills() {
        return new int[] {
                25
        };
    }

    @Override
    public void apply(Player player) {
        for (int i = 0; i < 3; i++) {
            Wolf wolf = player.getWorld().spawn(player.getLocation(), Wolf.class);

            wolf.setOwner(player);
            wolf.setTamed(true);
            wolf.setAgeLock(true);
            wolf.setAdult();

            wolf.setMaxHealth(100);
            wolf.setHealth(wolf.getMaxHealth());

            wolf.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));
            wolf.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
        }
    }

}
