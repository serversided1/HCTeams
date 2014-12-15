package net.frozenorb.foxtrot.relic;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.relic.enums.Relic;
import net.frozenorb.foxtrot.relic.tasks.RelicTask;
import net.frozenorb.foxtrot.util.InvUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class RelicHandler implements Listener {

    @Getter private Map<String, Map<Relic, Integer>> playerRelics = new HashMap<String, Map<Relic, Integer>>();

    public RelicHandler() {
        (new RelicTask()).runTaskTimer(FoxtrotPlugin.getInstance(), 0L, 10 * 20);
        FoxtrotPlugin.getInstance().getServer().getPluginManager().registerEvents(this, FoxtrotPlugin.getInstance());
    }

    public void updatePlayer(Player player) {
        getPlayerRelics().put(player.getName(), getRelicTierMap(player));
    }

    public Map<Relic, Integer> getRelicTierMap(Player player) {
        Map<Relic, Integer> relicTierMap = new HashMap<Relic, Integer>();

        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }

            boolean isPossibleRelic = false;

            for (Relic possibleRelic : Relic.values()) {
                if (possibleRelic.getMaterial() == itemStack.getType()) {
                    isPossibleRelic = true;
                    break;
                }
            }

            if (!isPossibleRelic) {
                continue;
            }

            Relic actualRelic = InvUtils.getRelicType(itemStack);

            if (actualRelic == null) {
                continue;
            }

            int tier = InvUtils.getRelicTier(itemStack);

            if (relicTierMap.containsKey(actualRelic) && tier > relicTierMap.get(actualRelic)) {
                relicTierMap.put(actualRelic, tier);
            } else {
                relicTierMap.put(actualRelic, tier);
            }
        }

        return (relicTierMap);
    }

    public int getTier(Player player, Relic relic) {
        if (!getPlayerRelics().containsKey(player.getName())) {
            return (-1);
        }

        Map<Relic, Integer> playerRelicTiers = getPlayerRelics().get(player.getName());

        if (!playerRelicTiers.containsKey(relic)) {
            return (-1);
        }

        return (playerRelicTiers.get(relic));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        updatePlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        getPlayerRelics().remove(event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        updatePlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        updatePlayer(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || !(event.getWhoClicked() instanceof Player)) {
            return;
        }

        boolean isPossibleRelic = false;

        for (Relic possibleRelic : Relic.values()) {
            if (possibleRelic.getMaterial() == event.getCurrentItem().getType()) {
                isPossibleRelic = true;
                break;
            }
        }

        if (isPossibleRelic) {
            updatePlayer((Player) event.getWhoClicked());
        }
    }

}