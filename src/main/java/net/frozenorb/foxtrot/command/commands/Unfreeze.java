package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chasechocolate.
 */
public class Unfreeze extends BaseCommand {
    public Unfreeze(){
        super("unfreeze");
    }

    @Override
    public void syncExecute() {
        if(!(sender.isOp() || sender.getName().equals("Nauss") || sender.hasPermission("foxtrot.freeze"))){
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
            return;
        }

        if(args.length != 1){
            sender.sendMessage(ChatColor.RED + "Usage: /unfreeze <player | radius | all>");
            return;
        }

        //Check "all"
        if(args[0].equalsIgnoreCase("all")){
            Freeze.unfreezeAll();
            sender.sendMessage(ChatColor.GREEN + "Unfroze all players!");
            return;
        }

        //Check radius
        try{
            double radius = Double.parseDouble(args[0]);
            Set<Player> players = new HashSet<>();

            for(Entity nearby : ((Player) sender).getNearbyEntities(radius, 256, radius)){
                if(nearby instanceof Player){
                    Player p = (Player) nearby;

                    if(!(p.hasMetadata("invisible"))){
                        players.add((Player) nearby);
                    }
                }
            }

            if(players.size() == 0){
                sender.sendMessage(ChatColor.RED + "No nearby players within a " + radius + " radius!");
                return;
            }

            for(Player target : players){
                Freeze.unfreeze(target);
            }

            sender.sendMessage(ChatColor.GREEN + "Successfully unfroze " + players.size() + " player" + (players.size() == 1 ? "" : "s") + "!");
            return;
        } catch(NumberFormatException e){
            //Continue
        }

        //Check player
        Player target = Bukkit.getPlayer(args[0]);

        if(target == null || target.hasMetadata("invisible")){
            sender.sendMessage(ChatColor.RED + "Could not find player '" + args[0] + "'!");
            return;
        }

        if(!(Freeze.isFrozen(target))){
            sender.sendMessage(ChatColor.RED + target.getName() + " is not frozen!");
            return;
        }

        Freeze.unfreeze(target);
        sender.sendMessage(ChatColor.GREEN + "You have unfrozen " + target.getDisplayName() + ChatColor.GREEN + "!");
    }
}