package net.frozenorb.foxtrot.minerworld.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SetMinerMaxFactionAmount {

    @Command(names = {"setminermaxfactionamount"}, permission = "op")
    public static void setMinerMaxFactionAmount(Player sender, @Param(name = "members") int members) {
        Foxtrot.getInstance().getMinerWorldHandler().setMaxFactionAmount(members);
        Foxtrot.getInstance().getMinerWorldHandler().save();

        sender.sendMessage(ChatColor.GREEN + "Miner World maximum amount of members per faction set to " + members + ".");
    }

}
