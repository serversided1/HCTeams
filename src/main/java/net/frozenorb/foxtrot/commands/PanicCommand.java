package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.mBasic.CommandSystem.Commands.Freeze;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PanicCommand {

    @Command(names={ "panic", "p" }, permissionNode="foxtrot.panic")
    public static void panic(Player sender) {
        if (Freeze.isFrozen(sender)) {
            Freeze.unfreeze(sender);
        } else {
            new BukkitRunnable() {

                public void run() {
                    if (!Freeze.isFrozen(sender)) {
                        cancel();
                        return;
                    }

                    for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                        if (player.hasPermission("basic.staff")) {
                            player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Panic! " + sender.getDisplayName() + ChatColor.YELLOW + " has activated the panic feature!");
                        }
                    }
                }

            }.runTaskTimer(Foxtrot.getInstance(), 1L, 15 * 20L);

            Freeze.freeze(sender);
        }
    }

}