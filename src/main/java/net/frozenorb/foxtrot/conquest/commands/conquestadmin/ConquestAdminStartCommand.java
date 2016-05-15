package net.frozenorb.foxtrot.conquest.commands.conquestadmin;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.conquest.game.ConquestGame;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ConquestAdminStartCommand {

    @Command(names={ "conquestadmin start" }, permission="op")
    public static void conquestAdminStart(CommandSender sender) {
        ConquestGame game = Foxtrot.getInstance().getConquestHandler().getGame();

        if (game != null) {
            sender.sendMessage(ChatColor.RED + "Conquest is already active.");
            return;
        }

        new ConquestGame();
    }

}