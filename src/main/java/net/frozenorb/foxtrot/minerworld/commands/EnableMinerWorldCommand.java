package net.frozenorb.foxtrot.minerworld.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class EnableMinerWorldCommand {

    @Command(names = {"enableminerworld"}, permission = "op")
    public static void enableMinerWorld(Player sender) {
        Foxtrot.getInstance().getMinerWorldHandler().setEnabled(true);
        sender.sendMessage(ChatColor.YELLOW + "Miner World has been enabled.");
    }

}
