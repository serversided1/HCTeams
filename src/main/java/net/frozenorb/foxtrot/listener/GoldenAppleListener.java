package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

/**
 * Created by macguy8 on 11/3/2014.
 */
public class GoldenAppleListener implements Listener {

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent e) {
        if (e.getItem() != null && e.getItem().getDurability() == (short) 1 && e.getItem().getType() == Material.GOLDEN_APPLE) {

            Long i = FoxtrotPlugin.getInstance().getOppleMap().getValue(e.getPlayer().getName());

            if (i != null && i > System.currentTimeMillis()) {
                long millisLeft = i - System.currentTimeMillis();

                String msg = TimeUtils.getDurationBreakdown(millisLeft);

                e.setCancelled(true);
                e.getPlayer().sendMessage(ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
                return;
            }

            long oppleCooldown = 8 * 60 * 60 * 1000;
            FoxtrotPlugin.getInstance().getOppleMap().updateValue(e.getPlayer().getName(), System.currentTimeMillis() + oppleCooldown);
        }
    }

}