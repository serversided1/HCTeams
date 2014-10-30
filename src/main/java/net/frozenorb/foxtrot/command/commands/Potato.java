package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class Potato extends BaseCommand {

    public static HashSet<String> potatoSet = new HashSet<String>();

    public Potato() {
        super("potato", "tater");
        setPermissionLevel("raven.potato", "§cYou are not allowed to do this!");
    }

    @Override
    public void syncExecute() {
        if (args.length > 0) {
            String pName = args[0];
            Player p = Bukkit.getPlayer(pName);

            if (p == null) {
                sender.sendMessage(ChatColor.RED + "Player could not be found.");
                return;
            }

            if (potatoSet.contains(p.getName().toLowerCase())) {
                sender.sendMessage(ChatColor.RED + "That player is already potatoed.");
                return;
            }

            potatoSet.add(p.getName().toLowerCase());
            sender.sendMessage(ChatColor.GREEN + "You have potatoed " + p.getName() + "!");

        } else {
            sender.sendMessage(ChatColor.RED + "/potato <playerName>");
        }
    }

}
