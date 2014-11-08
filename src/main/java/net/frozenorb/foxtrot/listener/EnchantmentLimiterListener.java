package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.frozenorb.foxtrot.util.InvUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DateFormat;
import java.util.*;

/**
 * Created by macguy8 on 11/5/2014.
 */
public class EnchantmentLimiterListener implements Listener {

    public EnchantmentLimiterListener() {
        new BukkitRunnable(){
            @Override
            public void run() {
                for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                    boolean fixed = false;
                    ItemStack hand = player.getItemInHand();

                    if (conformEnchants(hand)) {
                        player.setItemInHand(hand);
                        fixed = true;
                    }

                    for (ItemStack item : player.getInventory()) {
                        if (conformEnchants(item)) {
                            fixed = true;
                        }
                    }

                    ItemStack[] armor = player.getInventory().getArmorContents();

                    for (int i = 0; i < armor.length; i++) {
                        if (conformEnchants(armor[i])) {
                            fixed = true;
                        }
                    }

                    if (fixed) {
                        player.getInventory().setArmorContents(armor);
                        player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "We detected illegal enchantments on items in your inventory, and have removed those enchantments.");
                    }
                }
            }

            private boolean conformEnchants(ItemStack item){
                if (item == null) {
                    return (false);
                }

                boolean fixed = false;
                Map<Enchantment, Integer> enchants = item.getEnchantments();

                for (Enchantment enchantment : enchants.keySet()) {
                    int level = enchants.get(enchantment);

                    if (ServerHandler.getMaxEnchantments().containsKey(enchantment)) {
                        int max = ServerHandler.getMaxEnchantments().get(enchantment);

                        if (level > max) {
                            item.addUnsafeEnchantment(enchantment, max);
                            fixed = true;
                        }
                    } else {
                        item.removeEnchantment(enchantment);
                        fixed = true;
                    }
                }

                return (fixed);
            }
        }.runTaskTimer(FoxtrotPlugin.getInstance(), 200L, 200L); //10 seconds
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onEnchantItem(EnchantItemEvent event) {
        Map<Enchantment, Integer> enchants = new HashMap<>();

        for (Enchantment enchantment : event.getEnchantsToAdd().keySet()) {
            int level = event.getEnchantsToAdd().get(enchantment);

            if (ServerHandler.getMaxEnchantments().containsKey(enchantment)) {
                if (level > ServerHandler.getMaxEnchantments().get(enchantment)) {
                    enchants.put(enchantment, ServerHandler.getMaxEnchantments().get(enchantment));
                } else {
                    enchants.put(enchantment, level);
                }
            }
        }

        event.getEnchantsToAdd().clear();
        event.getEnchantsToAdd().putAll(enchants);
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
                    InvUtils.fixItem(item);
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

        boolean book = item.getType() == Material.ENCHANTED_BOOK;

        for (Map.Entry<Enchantment, Integer> entry : ServerHandler.getMaxEnchantments().entrySet()) {
            if (book) {
                EnchantmentStorageMeta esm = (EnchantmentStorageMeta) item.getItemMeta();

                if (esm.hasStoredEnchant(entry.getKey()) && esm.getStoredEnchantLevel(entry.getKey()) > entry.getValue()) {
                    player.sendMessage(ChatColor.RED + "That book would be too strong to use!");
                    event.setCancelled(true);
                    return;
                }
            } else {
                if (item.containsEnchantment(entry.getKey()) && item.getEnchantmentLevel(entry.getKey()) > entry.getValue()) {
                    item.addEnchantment(entry.getKey(), entry.getValue());
                }
            }
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
            InvUtils.fixItem(iter.next());
        }
    }

    @EventHandler
    public void onPlayerFishEvent(PlayerFishEvent event) {
        if (event.getCaught() instanceof Item) {
            InvUtils.fixItem(((Item) event.getCaught()).getItemStack());
        }
    }

}