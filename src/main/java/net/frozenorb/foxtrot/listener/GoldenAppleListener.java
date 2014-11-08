package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.concurrent.TimeUnit;

/**
 * Created by macguy8 on 11/3/2014.
 */
public class GoldenAppleListener implements Listener {

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (event.getItem() == null || event.getItem().getDurability() != (short) 1 || event.getItem().getType() != Material.GOLDEN_APPLE) {
            return;
        }

        long cooldownUntil = FoxtrotPlugin.getInstance().getOppleMap().getCooldown(event.getPlayer().getName());

        if (cooldownUntil > System.currentTimeMillis()) {
            long millisLeft = cooldownUntil - System.currentTimeMillis();

            String msg = TimeUtils.getDurationBreakdown(millisLeft);

            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
            return;
        }

        FoxtrotPlugin.getInstance().getOppleMap().useGoldenApple(event.getPlayer().getName(), TimeUnit.HOURS.toSeconds(8));

        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "███" + ChatColor.BLACK + "██" + ChatColor.DARK_GREEN + "███");
        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "███" + ChatColor.BLACK + "█" + ChatColor.DARK_GREEN + "████");
        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "██" + ChatColor.GOLD + "████" + ChatColor.DARK_GREEN + "██" + ChatColor.GOLD + " Super Golden Apple:");
        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "█" + ChatColor.GOLD + "██" + ChatColor.WHITE + "█" + ChatColor.GOLD + "███" + ChatColor.DARK_GREEN + "█" + ChatColor.DARK_GREEN + "   Consumed");
        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "█" + ChatColor.GOLD + "█" + ChatColor.WHITE + "█" + ChatColor.GOLD + "████" + ChatColor.DARK_GREEN + "█" + ChatColor.YELLOW + " Cooldown Remaining:");
        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "█" + ChatColor.GOLD + "██████" + ChatColor.DARK_GREEN + "█" + ChatColor.BLUE + "   8 Hours");
        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "█" + ChatColor.GOLD + "██████" + ChatColor.DARK_GREEN + "█");
        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "██" + ChatColor.GOLD + "████" + ChatColor.DARK_GREEN + "██");
    }

}