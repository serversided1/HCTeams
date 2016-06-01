package net.frozenorb.foxtrot.minerworld.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SetMinerPortalCommand {

    @Command(names = {"setminerportal"}, permission = "op")
    public static void setMinerPortal(Player sender) {
        Foxtrot.getInstance().getMinerWorldHandler().setPortalLocation(sender.getLocation());
        sender.sendMessage(ChatColor.GREEN + "Miner World portal set to your location.");
    }

}
