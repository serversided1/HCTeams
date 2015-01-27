package net.frozenorb.foxtrot.citadel.commands.citadel;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.citadel.CitadelHandler;
import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by macguy8 on 11/25/2014.
 */
public class CitadelSaveLoottableCommand {

    @Command(names={"citadel saveloottable"}, permissionNode="op")
    public static void citadelSaveLoottable(Player sender) {
        FoxtrotPlugin.getInstance().getCitadelHandler().getCitadelLoot().clear();

        for (ItemStack itemStack : sender.getInventory().getContents()) {
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                FoxtrotPlugin.getInstance().getCitadelHandler().getCitadelLoot().add(itemStack);
            }
        }

        FoxtrotPlugin.getInstance().getCitadelHandler().saveCitadelInfo();
        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Saved Citadel loot from your inventory.");
    }

}