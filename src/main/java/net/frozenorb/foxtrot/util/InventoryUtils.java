package net.frozenorb.foxtrot.util;

import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.frozenorb.foxtrot.Foxtrot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class InventoryUtils {

    public static final SimpleDateFormat DEATH_TIME_FORMAT = new SimpleDateFormat("MM.dd.yy HH:mm");
    public static final String KILLS_LORE_IDENTIFIER = ChatColor.YELLOW.toString() + ChatColor.BOLD + "Kills: " + ChatColor.WHITE.toString() + ChatColor.BOLD;

    public static final ItemStack CROWBAR;
    public static final ItemStack ANTIDOTE = ItemBuilder.of(Material.POTION).data((short) 8196).name(ChatColor.GREEN + "Antidote").setLore(new ArrayList<>()).addToLore(ChatColor.GRAY + "Drink to relieve yourself of potion debuffs.").build();

    public static final String CROWBAR_NAME = ChatColor.RED + "Crowbar";

    public static final int CROWBAR_PORTALS = 6;
    public static final int CROWBAR_SPAWNERS = 1;

    static {
        CROWBAR = new ItemStack(Material.DIAMOND_HOE);
        ItemMeta meta = CROWBAR.getItemMeta();

        meta.setDisplayName(CROWBAR_NAME);
        meta.setLore(getCrowbarLore(CROWBAR_PORTALS, CROWBAR_SPAWNERS));

        CROWBAR.setItemMeta(meta);
    }

    public static void fillBucket(Player player) {
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);

            if (item != null && item.getType() == Material.BUCKET) {
                item.setType(Material.WATER_BUCKET);
                player.updateInventory();
                break;
            }
        }
    }

    public static boolean conformEnchants(ItemStack item) {
        if (Boolean.TRUE) return false; // temp disable
        if (item == null) {
            return (false);
        }

        if (item.hasItemMeta()) {
            ItemMeta itemMeta = item.getItemMeta();

            if (itemMeta.hasDisplayName() && itemMeta.getDisplayName().contains(ChatColor.AQUA.toString())) {
                return (false);
            }
        }

        boolean fixed = false;
        Map<Enchantment, Integer> enchants = item.getEnchantments();

        for (Map.Entry<Enchantment, Integer> enchantmentSet : enchants.entrySet()) {
            if (enchantmentSet.getValue() > enchantmentSet.getKey().getMaxLevel()) {
                item.addUnsafeEnchantment(enchantmentSet.getKey(), enchantmentSet.getKey().getMaxLevel());
                fixed = true;
            }
        }

        return (fixed);
    }

    public static ItemStack addToPart(ItemStack item, String title, String key, int max) {
        ItemMeta meta = item.getItemMeta();

        if (meta.hasLore() && meta.getLore().size() != 0) {
            List<String> lore = meta.getLore();

            if (lore.contains(title)) {
                int titleIndex = lore.indexOf(title);
                int keys = 0;

                for (int i = titleIndex; i < lore.size(); i++) {
                    if (lore.get(i).equals("")) {
                        break;
                    }

                    keys += 1;
                }

                lore.add(titleIndex + 1, key);

                if (keys > max) {
                    lore.remove(titleIndex + keys);
                }
            } else {
                lore.add("");
                lore.add(title);
                lore.add(key);
            }

            meta.setLore(lore);
        } else {
            List<String> lore = new ArrayList<>();

            lore.add("");
            lore.add(title);
            lore.add(key);

            meta.setLore(lore);
        }

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack addDeath(ItemStack item, String key) {
        return (addToPart(item, ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Deaths:", key, 10));
    }

    public static ItemStack addKill(ItemStack item, String key) {
        int killsIndex = 1;
        int[] lastKills = { 3, 4, 5 };
        int currentKills = 1;
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        if (meta.hasLore()) {
            lore = meta.getLore();

            if (meta.getLore().size() > killsIndex) {
                String killStr = lore.get(killsIndex);
                currentKills += Integer.parseInt(ChatColor.stripColor(killStr.split(":")[1]).trim());
            }

            for (int j : lastKills) {
                if (j == lastKills[lastKills.length - 1]) {
                    continue;
                }
                if (lore.size() > j) {
                    String atJ = meta.getLore().get(j);

                    if (lore.size() <= j + 1) {
                        lore.add(null);
                    }

                    lore.set(j + 1, atJ);
                }

            }
        }

        if (lore.size() <= killsIndex) {
            for (int i = lore.size(); i <= killsIndex + 1; i++) {
                lore.add("");
            }
        }
        lore.set(killsIndex, "§6§lKills:§f " + currentKills);

        int firsKill = lastKills[0];

        if (lore.size() <= firsKill) {
            for (int i = lore.size(); i <= firsKill + 1; i++) {
                lore.add("");
            }
        }

        lore.set(firsKill, key);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return (item);
    }

    public static List<String> getCrowbarLore(int portals, int spawners) {
        List<String> lore = new ArrayList<>();

        lore.add("");
        lore.add(ChatColor.YELLOW + "Can Break:");
        lore.add(ChatColor.WHITE + " - " + ChatColor.AQUA + "End Portals: " + ChatColor.YELLOW + "{" + ChatColor.BLUE + portals + ChatColor.YELLOW + "}");
        lore.add(ChatColor.WHITE + " - " + ChatColor.AQUA + "Spawners: " + ChatColor.YELLOW + "{" + ChatColor.BLUE + spawners + ChatColor.YELLOW + "}");

        return (lore);
    }

    public static List<String> getKOTHRewardKeyLore(String koth, int tier) {
        List<String> lore = new ArrayList<>();
        DateFormat sdf = new SimpleDateFormat("M/d HH:mm:ss");

        lore.add("");
        lore.add(ChatColor.WHITE + " - " + ChatColor.AQUA + "Obtained from: " + ChatColor.YELLOW + "{" + ChatColor.BLUE + koth + ChatColor.YELLOW + "}");
        lore.add(ChatColor.WHITE + " - " + ChatColor.AQUA + "Time: " + ChatColor.YELLOW + "{" + ChatColor.BLUE + sdf.format(new Date()).replace(" AM", "").replace(" PM", "") + ChatColor.YELLOW + "}");
        lore.add(ChatColor.WHITE + " - " + ChatColor.AQUA + "Tier: " + ChatColor.YELLOW + "{" + tier + "}");

        return (lore);
    }

    public static ItemStack generateKOTHRewardKey(String koth, int tier) {
        ItemStack key = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta meta = key.getItemMeta();

        meta.setDisplayName(ChatColor.RED + "KOTH Reward Key");
        meta.setLore(getKOTHRewardKeyLore(koth, tier));

        key.setItemMeta(meta);
        return (key);
    }

    public static int getCrowbarUsesPortal(ItemStack item){
        return (Integer.parseInt(getLoreData(item, 2)));
    }

    public static int getCrowbarUsesSpawner(ItemStack item){
        return (Integer.parseInt(getLoreData(item, 3)));
    }

    public static boolean isArmor(ItemStack item) {
        return item.getTypeId() > 297 && item.getTypeId() < 318;
    }

    public static boolean isBoots(ItemStack item) {
        return item.getTypeId() == 301 || item.getTypeId() == 305 || item.getTypeId() == 309 || item.getTypeId() == 313 || item.getTypeId() == 317;
    }

    public static String getLoreData(ItemStack item, int index) {
        List<String> lore = item.getItemMeta().getLore();

        if (lore != null && index < lore.size()) {
            String str = ChatColor.stripColor(lore.get(index));
            return (str.split("\\{")[1].replace("}", ""));
        }

        return ("");
    }

    public static boolean isSimilar(ItemStack item, String name){
        if (item.hasItemMeta()) {
            if (item.getItemMeta().hasDisplayName()) {
                return (item.getItemMeta().getDisplayName().equals(name));
            }
        }

        return (false);
    }

}