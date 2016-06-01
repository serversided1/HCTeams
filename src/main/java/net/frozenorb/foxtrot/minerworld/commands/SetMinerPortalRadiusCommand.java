package net.frozenorb.foxtrot.minerworld.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SetMinerPortalRadiusCommand {

    @Command(names = {"setminerportalradius"}, permission = "op")
    public static void setMinerPortalRadius(Player sender, @Param(name = "radius") int radius) {
        Foxtrot.getInstance().getMinerWorldHandler().setPortalRadius(radius);
        sender.sendMessage(ChatColor.GREEN + "Miner World portal radius set to " + radius + ".");
    }

}
