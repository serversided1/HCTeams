package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.util.InvUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
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

import java.util.HashMap;
import java.util.Map;

public class EnchantmentLimiterListener implements Listener {

    private Map<String, Long> lastArmorCheck = new HashMap<>();
    private Map<String, Long> lastSwordCheck = new HashMap<>();

    @EventHandler(priority=EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && !event.isCancelled() && checkArmor((Player) event.getEntity())) {
            ItemStack[] armor = ((Player) event.getEntity()).getInventory().getArmorContents();
            boolean fixed = false;

            for (ItemStack armorItem : armor) {
                if (InvUtils.conformEnchants(armorItem)) {
                    fixed = true;
                }
            }

            if (fixed) {
                ((Player) event.getEntity()).sendMessage(ChatColor.YELLOW + "We detected that your armor had some illegal enchantments, and have reduced the invalid enchantments.");
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!event.isCancelled() && event.getDamager() instanceof Player && checkSword((Player) event.getDamager())) {
            Player player = (Player) event.getDamager();
            ItemStack hand = player.getItemInHand();

            if (InvUtils.conformEnchants(hand)) {
                player.setItemInHand(hand);
                player.sendMessage(ChatColor.YELLOW + "We detected that your sword had some illegal enchantments, and have reduced the invalid enchantments.");
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && !event.isCancelled() && event.getItem() != null && event.getItem().getType() == Material.BOW) {
            ItemStack hand = event.getPlayer().getItemInHand();

            if (InvUtils.conformEnchants(hand)) {
                event.getPlayer().setItemInHand(hand);
                event.getPlayer().sendMessage(ChatColor.YELLOW + "We detected that your bow had some illegal enchantments, and have reduced the invalid enchantments.");
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory() instanceof MerchantInventory) {
            for (ItemStack item : event.getInventory()) {
                if (item != null) {
                    InvUtils.conformEnchants(item);
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

                if (previous != null && previous.hasItemMeta() && previous.getItemMeta().hasDisplayName() && previous.getItemMeta().getDisplayName().contains(ChatColor.AQUA.toString())) {
                    meta.setDisplayName(previous.getItemMeta().getDisplayName());
                } else {
                    meta.setDisplayName(fixName(meta.getDisplayName()));
                }

                item.setItemMeta(meta);
                event.setCurrentItem(item);
            }
        }
    }

    private final char[] allowed = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()-_' ".toCharArray();

    private String fixName(String name) {
        StringBuilder result = new StringBuilder();

        for (char nameCharacter : name.toCharArray()) {
            for (char whitelistCharacter : allowed) {
                if (nameCharacter == whitelistCharacter) {
                    result.append(whitelistCharacter);
                }
            }
        }

        return (result.toString());
    }

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent event) {
        for (ItemStack drop : event.getDrops()) {
            InvUtils.conformEnchants(drop);
        }
    }

    @EventHandler
    public void onPlayerFishEvent(PlayerFishEvent event) {
        if (event.getCaught() instanceof Item) {
            InvUtils.conformEnchants(((Item) event.getCaught()).getItemStack());
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