package net.frozenorb.foxtrot.util;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.cheatbreaker.api.CheatBreakerAPI;
import com.cheatbreaker.api.message.SendCooldownMessage;
import com.cheatbreaker.api.object.CBCooldown;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CheatBreakerKey {
    
    ENDER_PEARL("Enderpearl", Material.ENDER_PEARL),
    HOME("Home", Material.CHEST),
    SPAWN_TAG("SpawnTag", Material.DIAMOND_SWORD),
    LOGOUT("Logout", Material.WOOD_DOOR),
    STUCK("Stuck", Material.SLIME_BALL);

    private String name;
    private Material icon;

    public void send(Player player, Long duration) {
        Plugin plugin;
        if ((plugin = Bukkit.getPluginManager().getPlugin("CheatBreakerAPI")) == null || !plugin.isEnabled()) return;
        if (player == null || duration == null || duration < 0)
            return;

        CheatBreakerAPI.getInstance().sendMessage(player, new SendCooldownMessage(new CBCooldown(this.getName(), duration, TimeUnit.MILLISECONDS, getIcon())));
    }

    public void clear(Player player) {
        send(player, 0L);
    }
}
