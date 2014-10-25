package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.BaseCommand;
import net.frozenorb.foxtrot.jedis.persist.ToggleLightningMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by chasechocolate.
 */
public class ToggleLightning extends BaseCommand {
    public ToggleLightning(){
        super("togglelightning");
    }

    @Override
    public void syncExecute(){
        boolean val = !(((Player) sender).hasMetadata(ToggleLightningMap.META));

        sender.sendMessage(ChatColor.YELLOW + "You are now " + (val ? ChatColor.RED + "unable" : ChatColor.GREEN + "able") + ChatColor.YELLOW + " to see lightning strikes on deaths!");

        if(val){
            ((Player) sender).setMetadata(ToggleLightningMap.META, ToggleLightningMap.META_OBJ);
        } else {
            ((Player) sender).removeMetadata(ToggleLightningMap.META, ToggleLightningMap.META_OBJ.getOwningPlugin());
        }
    }
}