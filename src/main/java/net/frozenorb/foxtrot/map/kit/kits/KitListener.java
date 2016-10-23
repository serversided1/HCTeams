package net.frozenorb.foxtrot.map.kit.kits;

import com.google.common.collect.Maps;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class KitListener implements Listener {

    private Map<UUID, Long> lastClicked = Maps.newHashMap();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getClickedBlock() == null || !(event.getClickedBlock().getState() instanceof Sign)) {
            return;
        }

        Sign sign = (Sign) event.getClickedBlock().getState();

        if (!sign.getLine(0).startsWith(ChatColor.BLUE + "- Kit")) {
            return;
        }

        Kit kit = Foxtrot.getInstance().getMapHandler().getKitManager().get(sign.getLine(1));

        if (kit == null) {
            player.sendMessage(ChatColor.RED + "Unknown kit.");
            return;
        }

        if (player.hasMetadata("modmode")) {
            player.sendMessage(ChatColor.RED + "You cannot use this while in mod mode.");
            return;
        }

        if (lastClicked.containsKey(player.getUniqueId()) && (System.currentTimeMillis() - lastClicked.get(player.getUniqueId()) < TimeUnit.SECONDS.toMillis(15))) {
            player.sendMessage(ChatColor.RED + "Please wait before using this again.");
            return;
        }

        kit.apply(player);

        lastClicked.put(player.getUniqueId(), System.currentTimeMillis());
    }

}
