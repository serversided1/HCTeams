package net.frozenorb.foxtrot.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.uuid.FrozenUUIDCache;

public class LastInvCommand {

    @Command(names={ "lastinv" }, permission="foxtrot.lastinv")
    public static void lastInv(Player sender, @Param(name="player") UUID player) {
        new BukkitRunnable() {

            public void run() {
                qLib.getInstance().runRedisCommand((redis) -> {
                    if (!redis.exists("lastInv:contents:" + player.toString())) {
                        sender.sendMessage(ChatColor.RED + "No last inventory recorded for " + FrozenUUIDCache.name(player));
                        return null;
                    }

                    ItemStack[] contents = qLib.PLAIN_GSON.fromJson(redis.get("lastInv:contents:" + player.toString()), ItemStack[].class);
                    for (ItemStack item : contents) {
                        if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
                            ItemMeta meta = item.getItemMeta();

                            List<String> lore = item.getItemMeta().getLore();
                            lore.remove(ChatColor.DARK_GRAY + "PVP Loot");
                            meta.setLore(lore);

                            item.setItemMeta(meta);
                        }
                    }

                    ItemStack[] armor = qLib.PLAIN_GSON.fromJson(redis.get("lastInv:armorContents:" + player.toString()), ItemStack[].class);
                    for (ItemStack item : armor) {
                        if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
                            ItemMeta meta = item.getItemMeta();

                            List<String> lore = item.getItemMeta().getLore();
                            lore.remove(ChatColor.DARK_GRAY + "PVP Loot");
                            meta.setLore(lore);

                            item.setItemMeta(meta);
                        }
                    }

                    sender.getInventory().setContents(contents);
                    sender.getInventory().setArmorContents(armor);
                    sender.updateInventory();

                    sender.sendMessage(ChatColor.GREEN + "Loaded " + FrozenUUIDCache.name(player) + "'s last inventory.");

                    return null;
                });
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

    public static void recordInventory(Player player) {
        ItemStack[] contents = player.getInventory().getContents();
        ItemStack[] armor = player.getInventory().getArmorContents();

        new BukkitRunnable() {

            public void run() {
                qLib.getInstance().runRedisCommand((redis) -> {
                    redis.set("lastInv:contents:" + player.getUniqueId().toString(), qLib.PLAIN_GSON.toJson(contents));
                    redis.set("lastInv:armorContents:" + player.getUniqueId().toString(), qLib.PLAIN_GSON.toJson(armor));
                    return null;
                });
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

    public static void recordInventory(UUID player, ItemStack[] contents, ItemStack[] armor) {
        new BukkitRunnable() {

            public void run() {
                qLib.getInstance().runRedisCommand((redis) -> {
                    redis.set("lastInv:contents:" + player.toString(), qLib.PLAIN_GSON.toJson(contents));
                    redis.set("lastInv:armorContents:" + player.toString(), qLib.PLAIN_GSON.toJson(armor));
                    return null;
                });
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

}