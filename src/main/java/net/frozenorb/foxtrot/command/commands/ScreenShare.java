package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by macguy8 on 11/1/2014.
 */
public class ScreenShare extends BaseCommand {

    public ScreenShare() {
        super("screenshare", "ss");
        setPermissionLevel("foxtrot.screenshare", "§cYou are not allowed to do this!");
    }

    @Override
    public void syncExecute() {
        if (args.length > 0) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null || target.hasMetadata("invisible")) {
                sender.sendMessage(ChatColor.RED + "Could not find player '" + args[0] + "'!");
                return;
            }

            Freeze.freeze(target);

            new BukkitRunnable() {

                public void run() {
                    if (!Freeze.isFrozen(target)) {
                        cancel();
                        return;
                    }

                    for (int i = 0; i < 6; i++) {
                        target.sendMessage("");
                    }

                    target.sendMessage(ChatColor.WHITE + "████" + ChatColor.RED + "█" + ChatColor.WHITE + "████"); // W4 | R | W4
                    target.sendMessage(ChatColor.WHITE + "███" + ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█" + ChatColor.WHITE + "███"); // W3 | R | G | R | W3
                    target.sendMessage(ChatColor.WHITE + "██" + ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.BLACK + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█" + ChatColor.WHITE + "██" + ChatColor.DARK_RED + ChatColor.BOLD + " Do NOT log out!"); // W2 | R | G | B | G | R | W2
                    target.sendMessage(ChatColor.WHITE + "██" + ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.BLACK + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█" + ChatColor.WHITE + "██" + ChatColor.YELLOW + " If you log out, you will be banned!"); // W2 | R | G | B | G | R | W2
                    target.sendMessage(ChatColor.WHITE + "█" + ChatColor.RED + "█" + ChatColor.GOLD + "██" + ChatColor.BLACK + "█" + ChatColor.GOLD + "██" + ChatColor.RED + "█" + ChatColor.WHITE + "█" + ChatColor.WHITE + " Please download Teamspeak and connect to:"); // W-R-G-G-BLACK-G-G-R-W
                    target.sendMessage(ChatColor.WHITE + "█" + ChatColor.RED + "█" + ChatColor.GOLD + "█████" + ChatColor.RED + "█" + ChatColor.WHITE + "█" + ChatColor.WHITE + " ts.minehq.com"); // W-R-G5-R-W
                    target.sendMessage(ChatColor.RED + "█" + ChatColor.GOLD + "███" + ChatColor.BLACK + "█" + ChatColor.GOLD + "███" + ChatColor.RED + "█"); // R-G-G-G-B-G-G-G-R
                    target.sendMessage(ChatColor.RED + "█████████"); // BOTTOM RED LINE
                    target.sendMessage("");
                }

            }.runTaskTimer(FoxtrotPlugin.getInstance(), 0L, 20 * 5L);

            sender.sendMessage(ChatColor.GRAY + "Froze & sent message to " + target.getDisplayName() + ChatColor.GRAY + ".");
        } else {
            sender.sendMessage("/screenshare <player>");
        }

    }
}
