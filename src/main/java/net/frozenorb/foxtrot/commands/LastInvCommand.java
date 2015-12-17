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
                    if (!redis.exists("lastInv:" + player + ":*")) {
                        sender.sendMessage(ChatColor.RED + "No last inventory recorded for " + FrozenUUIDCache.name(player));
                        return null;
                    }

                    sender.getInventory().setContents(qLib.PLAIN_GSON.fromJson(redis.get("lastInv:" + player + ":contents"), ItemStack[].class));
                    sender.getInventory().setArmorContents(qLib.PLAIN_GSON.fromJson(redis.get("lastInv:" + player + ":armorContents"), ItemStack[].class));
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
                    redis.set("lastInv:" + player.getUniqueId().toString() + ":contents", qLib.PLAIN_GSON.toJson(contents));
                    redis.set("lastInv:" + player.getUniqueId().toString() + ":armorContents", qLib.PLAIN_GSON.toJson(armor));
                    return null;
                });
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

}