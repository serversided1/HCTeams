package net.frozenorb.foxtrot.team.commands.pvp;

import net.frozenorb.qlib.command.Command;
import org.bukkit.entity.Player;

public class PvPCommand {

    @Command(names={ "pvptimer", "timer", "pvp" }, permission="")
    public static void pvp(Player sender) {
        String[] msges = {
                "§c/pvp lives [target] - Shows amount of lives that a player has",
                "§c/pvp revive <player> - Revives targeted player",
                "§c/pvp time - Shows time left on PVP Timer",
                "§c/pvp enable - Remove PVP Timer"};

        sender.sendMessage(msges);
    }

}