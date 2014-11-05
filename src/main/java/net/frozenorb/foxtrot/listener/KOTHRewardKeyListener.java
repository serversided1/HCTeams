package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macguy8 on 11/4/2014.
 */
public class KOTHRewardKeyListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getItem() != null && event.getClickedBlock().getType() == Material.ENDER_CHEST && FoxtrotPlugin.getInstance().getServerHandler().isOverworldSpawn(event.getClickedBlock().getLocation())) {
            if (InvUtils.isSimilar(event.getItem(), ChatColor.RED + "KOTH Reward Key")) {
                int tier = InvUtils.getKOTHRewardKeyTier(event.getItem());

                event.setCancelled(true);
                event.getPlayer().setItemInHand(null);
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.FIREWORK_BLAST, 1F, 1F);

                Block block = event.getClickedBlock().getRelative(BlockFace.DOWN, tier + 3);

                if (block.getType() == Material.CHEST) {
                    Chest chest = (Chest) block.getState();
                    ItemStack[] lootTables = chest.getBlockInventory().getContents();
                    List<ItemStack> loot = new ArrayList<ItemStack>();

                    int given = 0;

                    while (given < 5) {
                        ItemStack chosenItem = lootTables[FoxtrotPlugin.RANDOM.nextInt(lootTables.length)];

                        if (chosenItem == null || chosenItem.getType() == Material.AIR) {
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
                        event.getPlayer().getInventory().addItem(itemStack);
                        String displayName = itemStack.getItemMeta().hasDisplayName() ? ChatColor.RED.toString() + ChatColor.ITALIC + ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()) : ChatColor.BLUE.toString() + itemStack.getAmount() + "x " + ChatColor.YELLOW + WordUtils.capitalize(itemStack.getType().name().replace("_", " ").toLowerCase());

                        builder.append(ChatColor.YELLOW).append(displayName).append(ChatColor.GOLD).append(", ");
                    }

                    if (builder.length() > 2) {
                        builder.setLength(builder.length() - 2);
                    }

                    FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.GOLD + event.getPlayer().getName() + ChatColor.YELLOW + " is obtaining loot for a level " + tier + " key obtained from " + ChatColor.GOLD + InvUtils.getLoreData(event.getItem(), 1) + ChatColor.YELLOW + ".");
                    FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + "Loot: " + builder.toString());
                    event.getPlayer().updateInventory();
                }
            }
        }
    }

}