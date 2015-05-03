package net.frozenorb.foxtrot.commands;

import net.frozenorb.qlib.command.Command;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class GlintCommand {

    @Command(names={ "glint" }, permissionNode="")
    public static void glint(Player sender) {
        try {
            EntityPlayer entityPlayer = ((CraftPlayer) sender).getHandle();
            Field glintField = entityPlayer.getClass().getField("glintEnabled");
            boolean value = (Boolean) glintField.get(entityPlayer);

            glintField.set(entityPlayer, !value);

            sender.sendMessage(ChatColor.YELLOW + "You are now " + (value ? ChatColor.RED + "unable" : ChatColor.GREEN + "able") + ChatColor.YELLOW + " to see enchantment glint!");
            sender.sendMessage(ChatColor.GOLD + "Nearby players will not update until you relog.");
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Something broke :(");
            e.printStackTrace();
        }
    }

}