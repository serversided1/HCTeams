package net.frozenorb.foxtrot.deathmessage.commands.damagetracker;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.deathmessage.DeathMessageHandler;
import net.frozenorb.foxtrot.deathmessage.objects.Damage;
import net.frozenorb.foxtrot.deathmessage.objects.MobDamage;
import net.frozenorb.foxtrot.deathmessage.objects.PlayerDamage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DamageTrackerCommand {

    @Command(names={ "damagetracker" }, permissionNode="op")
    public static void damageTracker(Player sender) {
        int totalTracked = 0;
        double totalTrackedDamage = 0D;
        int totalPlayer = 0;
        double totalPlayerDamage = 0D;
        int totalMob = 0;
        double totalMobDamage = 0D;
        Map<Class<?>, Integer> counts = new HashMap<Class<?>, Integer>();

        for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
            List<Damage> damageList = DeathMessageHandler.getDamage(player);

            if (damageList == null) {
                continue;
            }

            for (Damage damage : damageList) {
                if (counts.containsKey(damage.getClass())) {
                    counts.put(damage.getClass(), counts.get(damage.getClass()) + 1);
                } else {
                    counts.put(damage.getClass(), 1);
                }

                totalTracked++;
                totalTrackedDamage += damage.getDamage();

                if (damage instanceof PlayerDamage) {
                    totalPlayer++;
                    totalPlayerDamage += damage.getDamage();
                }

                if (damage instanceof MobDamage) {
                    totalMob++;
                    totalMobDamage += damage.getDamage();
                }
            }
        }

        sender.sendMessage(ChatColor.BLUE + "Total Events: " + ChatColor.YELLOW + totalTracked);
        sender.sendMessage(ChatColor.BLUE + "Total Damage: " + ChatColor.YELLOW + totalTrackedDamage);
        sender.sendMessage(ChatColor.BLUE + "Player Events: " + ChatColor.YELLOW + totalPlayer);
        sender.sendMessage(ChatColor.BLUE + "Player Damage: " + ChatColor.YELLOW + totalPlayerDamage);
        sender.sendMessage(ChatColor.BLUE + "Mob Events: " + ChatColor.YELLOW + totalMob);
        sender.sendMessage(ChatColor.BLUE + "Mob Damage: " + ChatColor.YELLOW + totalMobDamage);

        sender.sendMessage(ChatColor.BLUE + "Damage Counts:");

        for (Map.Entry<Class<?>, Integer> countEntry : counts.entrySet()) {
            sender.sendMessage(ChatColor.YELLOW + "   " + countEntry.getKey().getSimpleName() + " " + ChatColor.GREEN + "x" + countEntry.getValue());
        }
    }

}