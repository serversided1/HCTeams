package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chasechocolate.
 */
public class World extends BaseCommand {

    public World() {
        super("world");
    }

    @Override
    public void syncExecute() {
        if(!(sender.isOp())){
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
            return;
        }

        if(args.length == 1){
            org.bukkit.World world = Bukkit.getWorld(args[0]);

            if(world != null){
                ((Player) sender).teleport(world.getSpawnLocation());
            } else {
                sender.sendMessage(ChatColor.RED + "Unknown world!");
            }
        }

        sender.sendMessage(ChatColor.RED + "Usage: /world <name>");
    }

    @Override
    public List<String> tabComplete(){
        List<String> list = new ArrayList<>();

        for(org.bukkit.World world : Bukkit.getWorlds()){
            list.add(world.getName());
        }

        return list;
    }
}