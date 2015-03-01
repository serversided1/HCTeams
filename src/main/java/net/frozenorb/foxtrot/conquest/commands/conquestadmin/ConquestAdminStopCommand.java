package net.frozenorb.foxtrot.conquest.commands.conquestadmin;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.foxtrot.conquest.game.ConquestGame;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ConquestAdminStopCommand {

    @Command(names={ "conquestadmin stop" }, permissionNode="op")
    public static void conquestAdminStop(Player sender) {
        ConquestGame game = FoxtrotPlugin.getInstance().getConquestHandler().getGame();

        if (game == null) {
            sender.sendMessage(ChatColor.RED + "Conquest is not active.");
            return;
        }

        game.endGame(null);
    }

}