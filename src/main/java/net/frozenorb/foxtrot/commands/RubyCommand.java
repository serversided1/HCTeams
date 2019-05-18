package net.frozenorb.foxtrot.commands;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.uuid.FrozenUUIDCache;

public class RubyCommand {

    private static int COOLDOWN_MINUTES = 20;
    private static String DONATE_URL = "store.veltpvp.com";

    private static Map<UUID, Long> lastRevive = Maps.newHashMap();

    @Command(names={ "ruby" }, permission="")
    public static void Ruby(Player sender) {
        if (!sender.hasPermission("core.ruby.revive")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&ePurchase the &6Ruby &erank on " + DONATE_URL));
            return;
        }

        if (!sender.hasPermission("ruby.revive")) return;

        sender.sendMessage(ChatColor.RED + "/ruby revive [player]");
    }

    @Command(names= { "ruby revive" }, permission="core.ruby.revive")
    public static void ruby_revive(Player sender, @Param(name="player") UUID player) {
        if (lastRevive.containsKey(sender.getUniqueId()) && System.currentTimeMillis() < lastRevive.get(sender.getUniqueId())) {
            long difference = lastRevive.get(sender.getUniqueId()) - System.currentTimeMillis();
            long seconds = difference / 1000;
            sender.sendMessage(ChatColor.RED + "You cannot do this for another " + (seconds / 60) + " minute " + (1 == seconds / 60 ? "" : "s") + ".");
            return;
        }

        UUID target = player;

        long deathbannedUntil = Foxtrot.getInstance().getDeathbanMap().getDeathban(target);
        if (deathbannedUntil < System.currentTimeMillis()) {
            sender.sendMessage(ChatColor.RED + "That player is not deathbanned.");
            return;
        }

        if (Foxtrot.getInstance().getServerHandler().isPreEOTW() || Foxtrot.getInstance().getServerHandler().isEOTW()) {
            sender.sendMessage(ChatColor.RED + "You cannot do this during EOTW.");
            return;
        }

        lastRevive.put(sender.getUniqueId(), System.currentTimeMillis() + (COOLDOWN_MINUTES * 60 * 1000));
        Bukkit.broadcastMessage(sender.getDisplayName() + ChatColor.translateAlternateColorCodes('&', " &eused their &6Ruby &erank to revive &6" + FrozenUUIDCache.name(player) + "&e."));
        Foxtrot.getInstance().getDeathbanMap().revive(player);
        return;
    }
}
