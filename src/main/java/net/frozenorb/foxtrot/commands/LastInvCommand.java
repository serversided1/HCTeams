package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.uuid.FrozenUUIDCache;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class LastInvCommand {

    @Command(names={ "lastinv" }, permissionNode="foxtrot.lastinv")
    public static void lastInv(Player sender, @Parameter(name="player") UUID player) {
        new BukkitRunnable() {

            public void run() {
                qLib.getInstance().runRedisCommand((redis) -> {
                    String key = "lastInv:" + player.toString();

                    if (!redis.exists(key)) {
                        sender.sendMessage(ChatColor.RED + "No last inventory recorded for " + FrozenUUIDCache.name(player));
                        return null;
                    }

                    String json = redis.get(key);
                    ItemStack[] inventory = qLib.PLAIN_GSON.fromJson(json, ItemStack[].class);

                    sender.getInventory().setContents(inventory);
                    sender.updateInventory();
                    sender.sendMessage(ChatColor.GREEN + "Loaded " + FrozenUUIDCache.name(player) + "'s last inventory");

                    return null;
                });
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

    public static void recordInventory(Player player) {
        new BukkitRunnable() {

            public void run() {
                qLib.getInstance().runRedisCommand((redis) -> {
                    String json = qLib.PLAIN_GSON.toJson(player.getInventory().getContents());
                    String key = "lastInv:" + player.getUniqueId().toString();

                    redis.set(key, json);
                    return null;
                });
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

}