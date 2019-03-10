package net.frozenorb.foxtrot.events.citadel.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.events.citadel.CitadelHandler;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CitadelRescanChestsCommand {

    @Command(names={"citadel rescanchests"}, permission="op")
    public static void citadelRescanChests(Player sender) {
        Foxtrot.getInstance().getCitadelHandler().scanLoot();
        Foxtrot.getInstance().getCitadelHandler().saveCitadelInfo();
        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Rescanned all Citadel chests.");
    }

}