package net.frozenorb.foxtrot.armor.kits;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.armor.Armor;
import net.frozenorb.foxtrot.armor.ArmorMaterial;
import net.frozenorb.foxtrot.armor.Kit;
import net.frozenorb.foxtrot.deathmessage.DeathMessageHandler;
import net.frozenorb.foxtrot.deathmessage.objects.PlayerDamage;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Rogue extends Kit {

    HashMap<String, Long> lastSpeedUsage = new HashMap<String, Long>();
    HashMap<String, Long> lastJumpUsage = new HashMap<String, Long>();

	@Override
	public boolean qualifies(Armor armor) {
		return armor.isFullSet(ArmorMaterial.CHAINMAIL);
	}

	@Override
	public String getName() {
		return "Rogue";
	}

	@Override
	public void apply(Player p) {
		smartAddPotion(p, new PotionEffect(PotionEffectType.SPEED, 200, 2));
        smartAddPotion(p, new PotionEffect(PotionEffectType.JUMP, 200, 1));
	}

    @Override
    public boolean itemConsumed(Player p, Material m) {
        if (m == Material.SUGAR) {
            if (lastSpeedUsage.containsKey(p.getName()) && lastSpeedUsage.get(p.getName()) > System.currentTimeMillis()) {
                Long millisLeft = ((lastSpeedUsage.get(p.getName()) - System.currentTimeMillis()) / 1000L) * 1000L;
                String msg = TimeUtils.getDurationBreakdown(millisLeft);

                p.sendMessage(ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
                return (false);
            }

            lastSpeedUsage.put(p.getName(), System.currentTimeMillis() + (1000L * 60 * 5));

            p.setMetadata("speedBoost", new FixedMetadataValue(FoxtrotPlugin.getInstance(), true));

            p.removePotionEffect(PotionEffectType.SPEED);
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 4));

            Bukkit.getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), new Runnable() {
                public void run() {
                    if (hasKitOn(p)) {
                        apply(p);
                    }

                    p.removeMetadata("speedBoost", FoxtrotPlugin.getInstance());
                }
            }, 200);
        } else {
            if (lastJumpUsage.containsKey(p.getName()) && lastJumpUsage.get(p.getName()) > System.currentTimeMillis()) {
                Long millisLeft = ((lastJumpUsage.get(p.getName()) - System.currentTimeMillis()) / 1000L) * 1000L;
                String msg = TimeUtils.getDurationBreakdown(millisLeft);

                p.sendMessage(ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
                return (false);
            }

            lastJumpUsage.put(p.getName(), System.currentTimeMillis() + (1000L * 60 * 5));

            p.setMetadata("jumpBoost", new FixedMetadataValue(FoxtrotPlugin.getInstance(), true));

            p.removePotionEffect(PotionEffectType.JUMP);
            p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 200, 6));

            Bukkit.getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), new Runnable() {
                public void run() {
                    if (hasKitOn(p)) {
                        apply(p);
                    }

                    p.removeMetadata("jumpBoost", FoxtrotPlugin.getInstance());
                }
            }, 200);
        }

        return (true);
    }

    @Override
    public List<Material> getConsumables() {
        return Arrays.asList(Material.SUGAR, Material.FEATHER);
    }

	@Override
	public double getCooldownSeconds() {
		return 0.6;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityArrowHit(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

		if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
			Player damager = (Player) event.getDamager();
			Player victim = (Player) event.getEntity();

			if (damager.getItemInHand() != null && damager.getItemInHand().getType() == Material.GOLD_SWORD && hasKitOn(damager) && !hasCooldown(damager, true)) {
                Vector playerVector = damager.getLocation().getDirection();
                Vector entityVector = victim.getLocation().getDirection();

                playerVector.setY(0F);
                entityVector.setY(0F);

                double degrees = playerVector.angle(entityVector);

				if (Math.abs(degrees) < 1.4) {
					damager.setItemInHand(new ItemStack(Material.AIR));

					damager.playSound(damager.getLocation(), Sound.ITEM_BREAK, 1F, 1F);
					damager.getWorld().playEffect(victim.getEyeLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);

                    if (victim.getHealth() - 6D <= 0) {
                        event.setCancelled(true);
                    } else {
                        event.setDamage(0D);
                    }

                    DeathMessageHandler.addDamage(victim, new BackstabDamage(victim.getName(), 6D, damager.getName()));
					victim.setHealth(Math.max(0D, victim.getHealth() - 6D));
					
					addCooldown(damager, getCooldownSeconds());
				} else {
					damager.sendMessage(ChatColor.RED + "Backstab failed!");
				}
			}
		}
	}

    public class BackstabDamage extends PlayerDamage {

        //***************************//

        public BackstabDamage(String damaged, double damage, String damager) {
            super(damaged, damage, damager);
        }

        //***************************//

        public String getDescription() {
            return ("Killed by " + getDamager());
        }

        public String getDeathMessage() {
            return (ChatColor.RED + getDamaged() + ChatColor.DARK_RED + "[" + FoxtrotPlugin.getInstance().getKillsMap().getKills(getDamaged()) + "]" + ChatColor.YELLOW + " was backstabbed by " + ChatColor.RED + getDamager() + ChatColor.DARK_RED + "[" + FoxtrotPlugin.getInstance().getKillsMap().getKills(getDamager()) + "]" + ChatColor.YELLOW + ".");
        }

        //***************************//

    }

	@Override
	public int getWarmup() {
		return 5;
	}

}