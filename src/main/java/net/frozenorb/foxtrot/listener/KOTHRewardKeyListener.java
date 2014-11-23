package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.dtr.bitmask.DTRBitmaskType;
import net.frozenorb.foxtrot.util.InvUtils;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macguy8 on 11/4/2014.
 */
public class KOTHRewardKeyListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || event.getItem() == null || event.getClickedBlock().getType() != Material.ENDER_CHEST || !DTRBitmaskType.SAFE_ZONE.appliesAt(event.getClickedBlock().getLocation()) || !InvUtils.isSimilar(event.getItem(), ChatColor.RED + "KOTH Reward Key")) {
            return;
        }

        event.setCancelled(true);

        int open = 0;

        for (ItemStack itemStack : event.getPlayer().getInventory().getContents()) {
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                open++;
            }
        }

        if (open < 5) {
            event.getPlayer().sendMessage(ChatColor.RED + "You must have at least 5 open inventory slots to use a KOTH reward key!");
            return;
        }

        int tier = InvUtils.getKOTHRewardKeyTier(event.getItem());

        event.getPlayer().setItemInHand(null);
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.FIREWORK_BLAST, 1F, 1F);

        Block block = event.getClickedBlock().getRelative(BlockFace.DOWN, tier + 3);

        if (block.getType() != Material.CHEST) {
            return;
        }

        Chest chest = (Chest) block.getState();
        ItemStack[] lootTables = chest.getBlockInventory().getContents();
        List<ItemStack> loot = new ArrayList<ItemStack>();
        int given = 0;
        int tries = 0;

        while (given < 5 && tries < 500) {
            tries++;

            ItemStack chosenItem = lootTables[FoxtrotPlugin.RANDOM.nextInt(lootTables.length)];

            if (chosenItem == null || chosenItem.getType() == Material.AIR || chosenItem.getAmount() == 0) {
                continue;
            }

            given++;

            if (chosenItem.getAmount() > 1) {
                ItemStack targetClone = chosenItem.clone();

                targetClone.setAmount(FoxtrotPlugin.RANDOM.nextInt(chosenItem.getAmount()));
                loot.add(targetClone);
            } else {
                loot.add(chosenItem);
            }
        }

        StringBuilder builder = new StringBuilder();

        for (ItemStack itemStack : loot) {
            String displayName = itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() ? ChatColor.RED.toString() + ChatColor.ITALIC + ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()) : ChatColor.BLUE.toString() + itemStack.getAmount() + "x " + ChatColor.YELLOW + WordUtils.capitalize(itemStack.getType().name().replace("_", " ").toLowerCase());

            builder.append(ChatColor.YELLOW).append(displayName).append(ChatColor.GOLD).append(", ");
        }

        if (builder.length() > 2) {
            builder.setLength(builder.length() - 2);
        }

        FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.GOLD + event.getPlayer().getName() + ChatColor.YELLOW + " is obtaining loot for a " + ChatColor.BLUE.toString() + ChatColor.ITALIC + "Level " + tier + " Key" + ChatColor.YELLOW + " obtained from " + ChatColor.GOLD + InvUtils.getLoreData(event.getItem(), 1) + ChatColor.YELLOW + " at " + ChatColor.GOLD + InvUtils.getLoreData(event.getItem(), 3) + ChatColor.YELLOW + ".");

        new BukkitRunnable() {

            public void run() {
                FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.GOLD + event.getPlayer().getName() + ChatColor.YELLOW + " obtained " + builder.toString() + ChatColor.GOLD + "," + ChatColor.YELLOW + " from a " + ChatColor.BLUE.toString() + ChatColor.ITALIC + "Level " + tier + " Key" + ChatColor.YELLOW + ".");

                for (ItemStack lootItem : loot) {
                    event.getPlayer().getInventory().addItem(lootItem);
                }

                event.getPlayer().updateInventory();
            }

        }.runTaskLater(FoxtrotPlugin.getInstance(), 20 * 5L);
    }

}