package net.frozenorb.foxtrot.relic.commands.relic;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.relic.enums.Relic;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MyRelicsCommand {

    @Command(names={ "myrelics" }, permissionNode="op")
    public static void myRelics(Player sender) {
        sender.sendMessage(ChatColor.YELLOW + "Your relics:");

        for (Relic relic : Relic.values()) {
            int tier = FoxtrotPlugin.getInstance().getRelicHandler().getTier(sender, relic);

            if (tier != -1) {
                sender.sendMessage(ChatColor.GREEN + relic.getName() + ": " + ChatColor.WHITE + tier);
            }
        }
    }

}