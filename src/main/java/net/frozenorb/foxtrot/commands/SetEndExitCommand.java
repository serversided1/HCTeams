package net.frozenorb.foxtrot.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.listener.EndListener;
import net.frozenorb.qlib.command.Command;

public class SetEndExitCommand {

    @Command(names = {"setendexit"}, permission = "op")
    public static void setendexit(Player sender) {
        Location previous = EndListener.getEndReturn();
        EndListener.setEndReturn(sender.getLocation());
        Location current = EndListener.getEndReturn();

        sender.sendMessage(
                ChatColor.GREEN + "End exit (" + ChatColor.WHITE + previous.getBlockX() + ":" + previous.getBlockY() + ":" + previous.getBlockZ() + ChatColor.GREEN + " -> " +
                        ChatColor.WHITE + current.getBlockX() + ":" + current.getBlockY() + ":" + current.getBlockZ() + ChatColor.GREEN + ")"
        );

        EndListener.saveEndReturn();
    }

}