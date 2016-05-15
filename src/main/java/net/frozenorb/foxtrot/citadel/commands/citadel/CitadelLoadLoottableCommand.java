package net.frozenorb.foxtrot.citadel.commands.citadel;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.citadel.CitadelHandler;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CitadelLoadLoottableCommand {

    @Command(names={"citadel loadloottable"}, permission="op")
    public static void citadelLoadLoottable(Player sender) {
        sender.getInventory().clear();
        int itemIndex = 0;

        for (ItemStack itemStack : Foxtrot.getInstance().getCitadelHandler().getCitadelLoot()) {
            sender.getInventory().setItem(itemIndex, itemStack);
            itemIndex++;
        }

        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Loaded Citadel loot into your inventory.");
    }

}