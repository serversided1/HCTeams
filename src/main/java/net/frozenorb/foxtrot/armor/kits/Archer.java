package net.frozenorb.foxtrot.armor.kits;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.armor.Armor;
import net.frozenorb.foxtrot.armor.ArmorMaterial;
import net.frozenorb.foxtrot.armor.Kit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("deprecation")
public class Archer extends Kit {

    public static final double MAX_FINAL_DAMAGE = 85D;

    @Override
    public boolean qualifies(Armor armor) {
        return armor.isFullSet(ArmorMaterial.LEATHER);
    }

    @Override
    public String getName() {
        return "Archer";
    }

    @Override
    public void apply(Player p) {
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2), true);
    }

    @Override
    public void remove(Player p) {
        p.removePotionEffect(PotionEffectType.SPEED);
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 2), true);

    }

    @Override
    public double getCooldownSeconds() {
        return 600;
    }

    @Override
    public List<Material> getConsumables() {
        return (Arrays.asList(Material.SUGAR));
    }

    private double getMultiplier(double range) {
        return range > 10 ? range > 105 ? 1D : ((range) * .07D) + (range > 25 ? .5 : 0) : 1D;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        if (e.getEntity().getShooter() instanceof Player && e.getEntity() instanceof Arrow) {
            Player p = (Player) e.getEntity().getShooter();
            Arrow a = (Arrow) e.getEntity();

            if (hasKitOn(p)) {
                a.setMetadata("firedLoc", new FixedMetadataValue(FoxtrotPlugin.getInstance(), p.getLocation()));
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onEntityArrowHit(EntityDamageByEntityEvent e) {
        if (e.isCancelled()) {
            return;
        }

        if (e.getEntity() instanceof Player && e.getDamager() instanceof Arrow) {
            Arrow a = (Arrow) e.getDamager();

            if (a.hasMetadata("firedLoc")) {
                Location firedFrom = (Location) a.getMetadata("firedLoc").get(0).value();
                double range = firedFrom.distance(e.getEntity().getLocation());
                double mod = getMultiplier(range);

                mod = Math.max(1D, mod);

                if (e.getEntity() instanceof Player && hasKitOn(((Player) e.getEntity()))) {
                    mod = 0.5D;

                    ((Player) e.getEntity()).sendMessage("§eReduced §9Incoming Arrow Damage");
                }

                // No mod when shooting into a KOTH.
                if (FoxtrotPlugin.getInstance().getServerHandler().isKOTHArena(e.getEntity().getLocation())) {
                    mod = 1F;
                }

                Player p = (Player) a.getShooter();
                double perc = mod * 100D;
                perc = Math.round(10.0 * perc) / 10.0;

                e.setDamage(Math.min(MAX_FINAL_DAMAGE, e.getDamage() * mod));

                /*if (((Player) e.getEntity()).getHealth() > (((Player) e.getEntity()).getMaxHealth() - 4)) {
                    if (FoxtrotPlugin.getInstance().getServerHandler().isGlobalSpawn(e.getEntity().getLocation())) {
                        return;
                    }

                    if (FoxtrotPlugin.getInstance().getJoinTimerMap().hasTimer((Player) e.getEntity())) {
                        return;
                    }

                    e.setDamage(0F);
                    ((Player) e.getEntity()).setHealth(Math.min(((Player) e.getEntity()).getMaxHealth() - (perc / 100), 4));
                }*/

                if (!FoxtrotPlugin.getInstance().getServerHandler().isKOTHArena(e.getEntity().getLocation())) {
                    p.sendMessage("§e[§9Arrow Range§e (§c" + (int) range + "§e)] Damage Output => §9§l" + perc + "%");
                } else {
                    p.sendMessage("§e[§9Arrow Range§e (§c" + (int) range + "§e)] Damage Output §cNormalized (KOTH Zone)");
                }
            }
        }
    }

    @Override
    public boolean itemConsumed(Player p, Material m) {
        p.setMetadata("speedBoost", new FixedMetadataValue(FoxtrotPlugin.getInstance(), true));

        p.removePotionEffect(PotionEffectType.SPEED);
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 3));

        Bukkit.getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), new Runnable() {
            public void run() {
                if (hasKitOn(p)) {
                    apply(p);
                }

                p.removeMetadata("speedBoost", FoxtrotPlugin.getInstance());
            }
        }, 200);

        return (true);
    }

    @Override
    public int getWarmup() {
        return 5;
    }

}
