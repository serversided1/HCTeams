package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chasechocolate.
 */
public class UnfreezeCommand {

    @Command(names={ "unfreeze" }, permissionNode="foxtrot.unfreeze")
    public static void spawn(Player sender, @Param(name="Params") String argString) {
        String[] args = argString.split(" ");

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /unfreeze <player | radius | all>");
            return;
        }

        //Check "all"
        if (args[0].equalsIgnoreCase("all")) {
            FreezeCommand.unfreezeAll();
            sender.sendMessage(ChatColor.GREEN + "Unfroze all players!");
            return;
        }

        //Check radius
        try {
            double radius = Double.parseDouble(args[0]);
            Set<Player> players = new HashSet<>();

            for (Entity nearby : sender.getNearbyEntities(radius, 256, radius)) {
                if (nearby instanceof Player) {
                    Player p = (Player) nearby;

                    if (!(p.hasMetadata("invisible"))) {
                        players.add((Player) nearby);
                    }
                }
            }

            if (players.size() == 0) {
                sender.sendMessage(ChatColor.RED + "No nearby players within a " + radius + " radius!");
                return;
            }

            for (Player target : players) {
                FreezeCommand.unfreeze(target);
            }

            sender.sendMessage(ChatColor.GREEN + "Successfully unfroze " + players.size() + " player" + (players.size() == 1 ? "" : "s") + "!");
            return;
        } catch (NumberFormatException e) {
            //Continue
        }

        //Check player
        Player target = Bukkit.getPlayer(args[0]);

        if (target == null || target.hasMetadata("invisible")) {
            sender.sendMessage(ChatColor.RED + "Could not find player '" + args[0] + "'!");
            return;
        }

        if (!(FreezeCommand.isFrozen(target))) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is not frozen!");
            return;
        }

        FreezeCommand.unfreeze(target);
        sender.sendMessage(ChatColor.GREEN + "You have unfrozen " + target.getDisplayName() + ChatColor.GREEN + "!");
    }

}