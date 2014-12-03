package net.frozenorb.foxtrot.citadel.commands.citadel;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.citadel.CitadelHandler;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by macguy8 on 11/25/2014.
 */
public class CitadelLoadLoottableCommand {

    @Command(names={"citadel loadloottable"}, permissionNode="op")
    public static void citadelLoadLoottable(Player sender, @Param(name="loottable") String loottable) {
        sender.getInventory().clear();

        for (ItemStack itemStack : FoxtrotPlugin.getInstance().getCitadelHandler().getCitadelLoot().get(loottable)) {
            sender.getInventory().addItem(itemStack);
        }

        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Loaded Citadel loot into your inventory.");
    }

}