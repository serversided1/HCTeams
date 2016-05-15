package net.frozenorb.foxtrot.commands;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_7_R4.CraftChunk;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class DMCICommand {

    @Command(names={ "dmci" }, permission="op")
    public static void dmci(Player sender) {
        Chunk chunk = sender.getLocation().getChunk();
        CraftChunk cChunk = (CraftChunk) chunk;

        try {
            for (Field field : cChunk.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                sender.sendMessage(ChatColor.AQUA + field.getName() + " (" + field.getType().getSimpleName() + "): " + ChatColor.WHITE + field.get(cChunk));
            }

            for (Field field : cChunk.getHandle().getClass().getDeclaredFields()) {
                field.setAccessible(true);
                sender.sendMessage(ChatColor.RED + field.getName() + " (" + field.getType().getSimpleName() + "): " + ChatColor.WHITE + field.get(cChunk.getHandle()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}