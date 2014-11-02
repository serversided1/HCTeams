package net.frozenorb.foxtrot.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import net.frozenorb.foxtrot.server.ServerHandler;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InvUtils {
    public static final SimpleDateFormat DEATH_TIME_FORMAT = new SimpleDateFormat("MM.dd.yy HH:mm");

    public static final ItemStack CROWBAR;

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

	public static void fixItem(ItemStack item) {
		for (Entry<Enchantment, Integer> entry : ServerHandler.getMaxEnchantments().entrySet()) {

			if (item != null && item.containsEnchantment(entry.getKey()) && item.getEnchantmentLevel(entry.getKey()) > entry.getValue()) {
				if (entry.getValue() == -1) {
					item.addEnchantment(Enchantment.DURABILITY, entry.getValue());
				} else {
					item.addEnchantment(entry.getKey(), entry.getValue());
				}
			}
		}
	}

    public static ItemStack addToPart(ItemStack item, String title, String key, int max){
        ItemMeta meta = item.getItemMeta();

        if(meta.hasLore() && meta.getLore().size() != 0){
            List<String> lore = meta.getLore();

            if(lore.contains(title)){
                int titleIndex = lore.indexOf(title);
                int keys = 0;

                for(int i = titleIndex; i < lore.size(); i++){
                    if(lore.get(i).equals("")){
                        break;
                    }

                    keys += 1;
                }

                lore.add(titleIndex + 1, key);

                if(keys > max){
                    lore.remove(titleIndex + keys);
                }
            } else {
                lore.add("");
                lore.add(title);
                lore.add(key);
            }

            meta.setLore(lore);
        } else {
            List<String> lore = new ArrayList<String>();

            lore.add("");
            lore.add(title);
            lore.add(key);
        }

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack addDeath(ItemStack item, String key){
        return addToPart(item, ChatColor.DARK_RED + "" + ChatColor.BOLD + "Deaths:", key, 10);
    }

    public static ItemStack addRepair(ItemStack item, String key){
        return addToPart(item, ChatColor.AQUA + "" + ChatColor.BOLD + "Repairs:", key, 10);
    }

    //Crowbar things
    public static List<String> getCrowbarLore(int portals, int spawners){
        List<String> lore = new ArrayList<>();

        lore.clear();
        lore.add("");
        lore.add(ChatColor.YELLOW + "Can Break:");
        lore.add(ChatColor.WHITE + " - " + ChatColor.AQUA + "End Portals: " + ChatColor.YELLOW + "{" + ChatColor.BLUE + portals + ChatColor.YELLOW + "}");
        lore.add(ChatColor.WHITE + " - " + ChatColor.AQUA + "Spawners: " + ChatColor.YELLOW + "{" + ChatColor.BLUE + spawners + ChatColor.YELLOW + "}");

        return lore;
    }

    public static int getCrowbarUsesPortal(ItemStack item){
        return getCrowbarData(item, 2);
    }

    public static int getCrowbarUsesSpawner(ItemStack item){
        return getCrowbarData(item, 3);
    }

    private static int getCrowbarData(ItemStack item, int index){
        if(isSimilar(item, CROWBAR_NAME)){
            List<String> lore = item.getItemMeta().getLore();

            if(lore.size() == 4){
                if(index < lore.size()){
                    String str = ChatColor.stripColor(lore.get(index));

                    return Integer.parseInt(str.split("\\{")[1].replace("}", ""));
                }
            }
        }

        return 0;
    }

    public static boolean isSimilar(ItemStack item, String name){
        if(item.hasItemMeta()){
            if(item.getItemMeta().hasDisplayName()){
                return item.getItemMeta().getDisplayName().equals(name);
            }
        }

        return false;
    }
}
