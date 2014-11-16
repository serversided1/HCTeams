package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.util.InvUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
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
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DateFormat;
import java.util.*;

/**
 * Created by macguy8 on 11/5/2014.
 */
public class EnchantmentLimiterListener implements Listener {

    private Map<String, Long> lastArmorCheck = new HashMap<String, Long>();
    private Map<String, Long> lastSwordCheck = new HashMap<String, Long>();

    @EventHandler(priority=EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && !event.isCancelled() && checkArmor((Player) event.getEntity())) {
            ItemStack[] armor = ((Player) event.getEntity()).getInventory().getArmorContents();
            boolean fixed = false;

            for (int i = 0; i < armor.length; i++) {
                if (InvUtils.conformEnchants(armor[i], true)) {
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

            if (InvUtils.conformEnchants(hand, true)) {
                player.setItemInHand(hand);
                player.sendMessage(ChatColor.YELLOW + "We detected that your sword had some illegal enchantments, and have reduced the invalid enchantments.");
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && !event.isCancelled() && event.getItem() != null && event.getItem().getType() == Material.BOW) {
            ItemStack hand = event.getPlayer().getItemInHand();

            if (InvUtils.conformEnchants(hand, true)) {
                event.getPlayer().setItemInHand(hand);
                event.getPlayer().sendMessage(ChatColor.YELLOW + "We detected that your bow had some illegal enchantments, and have reduced the invalid enchantments.");
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();

        if (!(humanEntity instanceof Player)) {
            return;
        }

        Player player = (Player) humanEntity;
        Inventory inventory = event.getInventory();

        if (event.getInventory().getType() == InventoryType.MERCHANT) {
            for (ItemStack item : event.getInventory()) {
                if (item != null) {
                    InvUtils.conformEnchants(item, true);
                }
            }
        }

        if (!(inventory instanceof AnvilInventory)) {
            return;
        }

        InventoryView view = event.getView();

        if (event.getRawSlot() != view.convertSlot(event.getRawSlot()) || event.getRawSlot() != 2) {
            return;
        }

        ItemStack item = event.getCurrentItem();
        ItemStack baseItem = inventory.getItem(0);

        if (item == null) {
            return;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null || !meta.hasDisplayName()) {
            return;
        }

        String displayName = fixName(meta.getDisplayName());

        if (baseItem.hasItemMeta() && baseItem.getItemMeta().getDisplayName() != null && FoxtrotPlugin.getInstance().getServerHandler().getUsedNames().contains(fixName(baseItem.getItemMeta().getDisplayName())) && !baseItem.getItemMeta().getDisplayName().equals(meta.getDisplayName())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot rename an item with a name!");
            return;
        }

        if (FoxtrotPlugin.getInstance().getServerHandler().getUsedNames().contains(displayName) && (baseItem.hasItemMeta() && baseItem.getItemMeta().getDisplayName() != null ? !baseItem.getItemMeta().getDisplayName().equals(meta.getDisplayName()) : true)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "An item with that name already exists.");
        } else {
            List<String> lore = new ArrayList<String>();
            boolean hasForgedMeta = false;

            if (meta.hasLore()) {
                for (String s : meta.getLore()) {
                    if (s.toLowerCase().contains("forged")) {
                        hasForgedMeta = true;
                    }
                }
            }

            if (meta.getLore() != null && !hasForgedMeta) {
                lore = meta.getLore();
            }

            DateFormat sdf = DateFormat.getDateTimeInstance();

            lore.add(0, "§eForged by " + player.getDisplayName() + "§e on " + sdf.format(new Date()));

            meta.setLore(lore);
            item.setItemMeta(meta);

            event.setCurrentItem(item);
            FoxtrotPlugin.getInstance().getServerHandler().getUsedNames().add(displayName);
            FoxtrotPlugin.getInstance().getServerHandler().save();
            player.sendMessage(ChatColor.GREEN + "Claimed the name '" + displayName + "'.");
        }
    }

    private final char[] allowed = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()-_'".toCharArray();

    private String fixName(String name) {
        String b = name.toLowerCase().trim();
        char[] charArray = b.toCharArray();
        StringBuilder result = new StringBuilder();

        for (char c : charArray) {
            for (char a : allowed) {
                if (c == a) {
                    result.append(a);
                }
            }
        }

        return (result.toString());
    }

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent event) {
        Iterator<ItemStack> iter = event.getDrops().iterator();

        while (iter.hasNext()) {
            InvUtils.conformEnchants(iter.next(), true);
        }
    }

    @EventHandler
    public void onPlayerFishEvent(PlayerFishEvent event) {
        if (event.getCaught() instanceof Item) {
            InvUtils.conformEnchants(((Item) event.getCaught()).getItemStack(), true);
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