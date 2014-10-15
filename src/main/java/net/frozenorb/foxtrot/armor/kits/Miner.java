package net.frozenorb.foxtrot.armor.kits;

import com.google.common.collect.Lists;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.frozenorb.foxtrot.armor.Armor;
import net.frozenorb.foxtrot.armor.ArmorMaterial;
import net.frozenorb.foxtrot.armor.Kit;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Miner extends Kit implements Listener {

    private ConcurrentMap<Player, Integer> noDamage = new ConcurrentHashMap<>();

    public Miner() {
        Runnable run = new Runnable() {
            public void run() {
                for (Player key : noDamage.keySet()) {
                    int left = noDamage.remove(key);
                    if (left == 0) {
                        //Allow invisibility
                        continue;
                    }

                    noDamage.put(key, left-1);
                }

                for (String player : Kit.getEquippedKits().keySet()) {
                    Kit on = Kit.getEquippedKits().get(player);

                    if (!(on instanceof Miner))
                        continue;

                    Player p = Bukkit.getPlayer(player);
                    if (p.getLocation().getY() <= 20 && !noDamage.containsKey(p) && !p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                        p.addPotionEffect(PotionEffectType.INVISIBILITY.createEffect(Integer.MAX_VALUE, 0));
                    }

                    if (p.hasPotionEffect(PotionEffectType.INVISIBILITY) && (noDamage.containsKey(p) || p.getLocation().getY() > 20)) {
                        p.removePotionEffect(PotionEffectType.INVISIBILITY);
                    }
                }
            }
        };
        Bukkit.getScheduler().scheduleSyncRepeatingTask(FoxtrotPlugin.getInstance(), run, 20, 20);
        Bukkit.getPluginManager().registerEvents(this, FoxtrotPlugin.getInstance());
    }

	@Override
	public boolean qualifies(Armor armor) {
		return armor.isFullSet(ArmorMaterial.IRON);
	}

	@Override
	public String getName() {
		return "Miner";
	}

	@Override
	public void apply(Player p) {
		p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
		p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1));
	}

	@Override
	public void remove(Player p) {
		p.removePotionEffect(PotionEffectType.NIGHT_VISION);
		p.removePotionEffect(PotionEffectType.FAST_DIGGING);
        p.removePotionEffect(PotionEffectType.INVISIBILITY);
        noDamage.remove(p);
	}

	@Override
	public int getWarmup() {
		return 10;
	}

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        Entity damage = event.getEntity();
        if (!(damage instanceof Player))
            return;

        Player pDamage = (Player)damage;
        if (noDamage.containsKey(pDamage)) {
            noDamage.remove(pDamage);
        }

        noDamage.put(pDamage, 15);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity hurt = event.getEntity();
        Entity damager = event.getDamager();

        if (!(damager instanceof Player))
            return;

        Player pDamager = (Player)damager;

        if (noDamage.containsKey(pDamager)) {
            noDamage.remove(pDamager);
        }

        noDamage.put(pDamager, 15);
    }
}
