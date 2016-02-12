package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class MapXListener implements Listener {

    @EventHandler
    public void onPlayerConsumeItem(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        if (event.getItem().getType() != Material.POTION) {
            return;
        }

        Potion potion = Potion.fromItemStack(event.getItem());

        boolean invis = false;

        for (PotionEffect effect : potion.getEffects()) {
            if (effect.getType() == PotionEffectType.INVISIBILITY) {
                invis = true;
                break;
            }
        }

        if (!invis) {
            return;
        }

        event.setCancelled(true);
        player.setItemInHand(new ItemStack(Material.GLASS_BOTTLE));

        player.sendMessage(ChatColor.DARK_AQUA + "You begin to feel dizzy...");
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 10 * 20, 3));

        new BukkitRunnable() {

            @Override
            public void run() {
                player.sendMessage(ChatColor.RED + "Maybe I shouldn't drink this.");
            }

        }.runTaskLater(Foxtrot.getInstance(), 7 * 20L);
    }

}
