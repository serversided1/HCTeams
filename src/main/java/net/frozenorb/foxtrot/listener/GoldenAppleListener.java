package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.concurrent.TimeUnit;

public class GoldenAppleListener implements Listener {

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (event.getItem() == null || event.getItem().getDurability() != (short) 1 || event.getItem().getType() != Material.GOLDEN_APPLE) {
            return;
        }

        if (Foxtrot.getInstance().getMapHandler().getGoppleCooldown() == -1) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Super golden apples are currently disabled..");
            return;
        }

        long cooldownUntil = Foxtrot.getInstance().getOppleMap().getCooldown(event.getPlayer().getUniqueId());

        if (cooldownUntil > System.currentTimeMillis()) {
            long millisLeft = cooldownUntil - System.currentTimeMillis();
            String msg = TimeUtils.formatIntoDetailedString((int) millisLeft / 1000);

            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
            return;
        }

        Foxtrot.getInstance().getOppleMap().useGoldenApple(event.getPlayer().getUniqueId(), Foxtrot.getInstance().getMapHandler().isKitMap() ? TimeUnit.MINUTES.toSeconds(5) : Foxtrot.getInstance().getMapHandler().getGoppleCooldown());
        long millisLeft = Foxtrot.getInstance().getOppleMap().getCooldown(event.getPlayer().getUniqueId()) - System.currentTimeMillis();

        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "███" + ChatColor.BLACK + "██" + ChatColor.DARK_GREEN + "███");
        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "███" + ChatColor.BLACK + "█" + ChatColor.DARK_GREEN + "████");
        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "██" + ChatColor.GOLD + "████" + ChatColor.DARK_GREEN + "██" + ChatColor.GOLD + " Super Golden Apple:");
        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "█" + ChatColor.GOLD + "██" + ChatColor.WHITE + "█" + ChatColor.GOLD + "███" + ChatColor.DARK_GREEN + "█" + ChatColor.DARK_GREEN + "   Consumed");
        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "█" + ChatColor.GOLD + "█" + ChatColor.WHITE + "█" + ChatColor.GOLD + "████" + ChatColor.DARK_GREEN + "█" + ChatColor.YELLOW + " Cooldown Remaining:");
        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "█" + ChatColor.GOLD + "██████" + ChatColor.DARK_GREEN + "█" + ChatColor.BLUE + "   " + TimeUtils.formatIntoDetailedString((int) millisLeft / 1000));
        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "█" + ChatColor.GOLD + "██████" + ChatColor.DARK_GREEN + "█");
        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "██" + ChatColor.GOLD + "████" + ChatColor.DARK_GREEN + "██");
    }

}