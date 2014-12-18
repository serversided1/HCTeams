package net.frozenorb.foxtrot.relic.tasks;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.relic.enums.Relic;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class MinerRelicTask extends BukkitRunnable {

    public static final int MINER_INVISIBLITY_Y_HEIGHT = 20;

    @Getter private static Map<String, Integer> minerLastDamage = new HashMap<String, Integer>();
    @Getter private static Map<String, Integer> minerInvisWarmup = new HashMap<String, Integer>();

    public void run() {
        for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
            int tier = FoxtrotPlugin.getInstance().getRelicHandler().getTier(player, Relic.MINER);

            if (!minerInvisWarmup.containsKey(player.getName()) && tier == -1) {
                for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                    if (potionEffect.getDuration() > 1_000_000 && (potionEffect.getType().equals(PotionEffectType.NIGHT_VISION) || potionEffect.getType().equals(PotionEffectType.FAST_DIGGING) || potionEffect.getType().equals(PotionEffectType.WEAKNESS))) {
                        player.removePotionEffect(potionEffect.getType());
                    }
                }
            }

            if (minerLastDamage.containsKey(player.getName())) {
                if (tier == -1) {
                    minerLastDamage.remove(player.getName());
                } else {
                    int left = minerLastDamage.remove(player.getName());

                    if (left == 0) {
                        if (player.getLocation().getY() <= MINER_INVISIBLITY_Y_HEIGHT) {
                            if (!minerInvisWarmup.containsKey(player.getName())) {
                                player.sendMessage(ChatColor.BLUE + "Miner Invisibility" + ChatColor.YELLOW + " will be activated in 10 seconds!");
                            }

                            minerInvisWarmup.put(player.getName(), 10);
                        }
                    } else {
                        minerLastDamage.put(player.getName(), left - 1);
                    }
                }
            } else if (minerInvisWarmup.containsKey(player.getName())) {
                int secs = minerInvisWarmup.get(player.getName());

                if (tier == -1) {
                    if (secs == -1) {
                        player.sendMessage(ChatColor.BLUE + "Miner Invisibility" + ChatColor.YELLOW + " has been disabled!");
                        player.removePotionEffect(PotionEffectType.INVISIBILITY);
                    }

                    minerInvisWarmup.remove(player.getName());
                } else {
                    if (secs == 0) {
                        if (player.getLocation().getBlockY() <= MINER_INVISIBLITY_Y_HEIGHT) {
                            if (!player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                                player.sendMessage(ChatColor.BLUE + "Miner Invisibility" + ChatColor.YELLOW + " has been enabled!");
                                player.addPotionEffect(PotionEffectType.INVISIBILITY.createEffect(Integer.MAX_VALUE, 0));
                            }
                        }

                        minerInvisWarmup.put(player.getName(), -1);
                    } else if (secs != -1) {
                        minerInvisWarmup.put(player.getName(), secs - 1);
                    } else {
                        if (player.getLocation().getY() > MINER_INVISIBLITY_Y_HEIGHT) {
                            player.sendMessage(ChatColor.BLUE + "Miner Invisibility" + ChatColor.YELLOW + " has been disabled!");
                            player.removePotionEffect(PotionEffectType.INVISIBILITY);
                            minerInvisWarmup.remove(player.getName());
                        }
                    }
                }
            } else if (tier != -1) {
                if (!player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0), true);
                }

                if (!player.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1), true);
                }

                if (!player.hasPotionEffect(PotionEffectType.WEAKNESS)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 1), true);
                }

                if (player.getLocation().getY() <= MINER_INVISIBLITY_Y_HEIGHT) {
                    minerInvisWarmup.put(player.getName(), 10);
                    player.sendMessage(ChatColor.BLUE + "Miner Invisibility" + ChatColor.YELLOW + " will be activated in 10 seconds!");
                }
            }
        }
    }

}