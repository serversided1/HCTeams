package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by macguy8 on 11/1/2014.
 */
public class Repair extends BaseCommand {

    public Repair() {
        super("repair");
        setPermissionLevel("foxtrot.repair", "§cYou are not allowed to do this!");
    }

    @Override
    public void syncExecute() {
        Player player = (Player) sender;
        ItemStack inHand = player.getItemInHand();

        if (inHand == null || inHand.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You're not holding anything!");
            return;
        }

        inHand.setDurability((short) 0);
        player.sendMessage(ChatColor.GRAY + "Repaired your item in hand.");
    }

}