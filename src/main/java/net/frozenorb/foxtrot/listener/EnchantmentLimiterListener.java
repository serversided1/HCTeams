package net.frozenorb.foxtrot.listener;

import com.google.common.collect.ImmutableSet;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentLimiterListener implements Listener {

    public static final ImmutableSet<Character> ITEM_NAME_CHARACTER_BLACKLIST = ImmutableSet.of(
            '卍'
    );

    private Map<String, Long> lastArmorCheck = new HashMap<>();
    private Map<String, Long> lastSwordCheck = new HashMap<>();

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && checkArmor((Player) event.getEntity())) {
            ItemStack[] armor = ((Player) event.getEntity()).getInventory().getArmorContents();
            boolean fixed = false;

            for (ItemStack armorItem : armor) {
                if (InventoryUtils.conformEnchants(armorItem)) {
                    fixed = true;
                }
            }

            if (fixed) {
                ((Player) event.getEntity()).sendMessage(ChatColor.YELLOW + "We detected that your armor had some illegal enchantments, and have reduced the invalid enchantments.");
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && checkSword((Player) event.getDamager())) {
            Player player = (Player) event.getDamager();
            ItemStack hand = player.getItemInHand();

            if (InventoryUtils.conformEnchants(hand)) {
                player.setItemInHand(hand);
                player.sendMessage(ChatColor.YELLOW + "We detected that your sword had some illegal enchantments, and have reduced the invalid enchantments.");
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && event.getItem() != null && event.getItem().getType() == Material.BOW) {
            ItemStack hand = event.getPlayer().getItemInHand();

            if (InventoryUtils.conformEnchants(hand)) {
                event.getPlayer().setItemInHand(hand);
                event.getPlayer().sendMessage(ChatColor.YELLOW + "We detected that your bow had some illegal enchantments, and have reduced the invalid enchantments.");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory() instanceof MerchantInventory) {
            for (ItemStack item : event.getInventory()) {
                if (item != null) {
                    InventoryUtils.conformEnchants(item);
                }
            }
        } else if (event.getInventory() instanceof AnvilInventory) {
            InventoryView view = event.getView();

            if (event.getCurrentItem() == null || event.getRawSlot() != view.convertSlot(event.getRawSlot()) || event.getRawSlot() != 2) {
                return;
            }

            ItemStack item = event.getCurrentItem();
            ItemMeta meta = item.getItemMeta();

            if (meta != null && meta.hasDisplayName()) {
                ItemStack previous = event.getInventory().getItem(0);

                if (previous != null && previous.hasItemMeta() && previous.getItemMeta().hasDisplayName() && containsColor(previous.getItemMeta().getDisplayName())) {
                    /* Admin item, dont allow repair or rename */
                    event.setCancelled(false);
                    event.setResult(Event.Result.DENY);

                    /* Start stupid workaround to update exp */
                    view.close();

                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            ((Player) event.getWhoClicked()).giveExp(5);
                        }

                    }.runTaskLater(Foxtrot.getInstance(), 2L);

                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            ((Player) event.getWhoClicked()).giveExp(-5);
                        }

                    }.runTaskLater(Foxtrot.getInstance(), 6L);
                    /* End stupid workaround to update exp */

                    return;
                } else {
                    meta.setDisplayName(fixName(meta.getDisplayName()));
                }

                item.setItemMeta(meta);
                event.setCurrentItem(item);
            }
        }
    }

    private boolean containsColor(String displayName) {
        return !ChatColor.stripColor(displayName).equals(displayName);
    }

    private String fixName(String name) {
        StringBuilder result = new StringBuilder();

        for (char nameCharacter : name.toCharArray()) {
            boolean blacklisted = false;

            for (char blacklistCharacter : ITEM_NAME_CHARACTER_BLACKLIST) {
                if (nameCharacter == blacklistCharacter) {
                    blacklisted = true;
                    break;
                }
            }

            if (!blacklisted) {
                result.append(nameCharacter);
            }
        }

        return (result.toString());
    }

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent event) {
        for (ItemStack drop : event.getDrops()) {
            InventoryUtils.conformEnchants(drop);
        }
    }

    @EventHandler
    public void onPlayerFishEvent(PlayerFishEvent event) {
        if (event.getCaught() instanceof Item) {
            InventoryUtils.conformEnchants(((Item) event.getCaught()).getItemStack());
        }
    }

    public boolean checkArmor(Player player) {
        boolean check = !lastArmorCheck.containsKey(player.getName()) || (System.currentTimeMillis() - lastArmorCheck.get(player.getName())) > 5000L;

        if (check) {
            lastArmorCheck.put(player.getName(), System.currentTimeMillis());
        }

        return (check);
    }

    public boolean checkSword(Player player) {
        boolean check = !lastSwordCheck.containsKey(player.getName()) || (System.currentTimeMillis() - lastSwordCheck.get(player.getName())) > 5000L;

        if (check) {
            lastSwordCheck.put(player.getName(), System.currentTimeMillis());
        }

        return (check);
    }

}