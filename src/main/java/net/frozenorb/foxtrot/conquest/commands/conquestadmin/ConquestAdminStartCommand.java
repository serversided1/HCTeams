package net.frozenorb.foxtrot.conquest.commands.conquestadmin;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.conquest.game.ConquestGame;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ConquestAdminStartCommand {

    @Command(names={ "conquestadmin start" }, permissionNode="op")
    public static void conquestAdminStart(Player sender) {
        ConquestGame game = FoxtrotPlugin.getInstance().getConquestHandler().getGame();

        if (game != null) {
            sender.sendMessage(ChatColor.RED + "Conquest is already active.");
            return;
        }

        new ConquestGame();
    }

}