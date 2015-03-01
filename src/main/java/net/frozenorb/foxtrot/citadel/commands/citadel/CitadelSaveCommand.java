package net.frozenorb.foxtrot.citadel.commands.citadel;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.citadel.CitadelHandler;
import net.frozenorb.qlib.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CitadelSaveCommand {

    @Command(names={"citadel save"}, permissionNode="op")
    public static void citadelSave(Player sender) {
        FoxtrotPlugin.getInstance().getCitadelHandler().saveCitadelInfo();
        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Saved Citadel info to file.");
    }

}