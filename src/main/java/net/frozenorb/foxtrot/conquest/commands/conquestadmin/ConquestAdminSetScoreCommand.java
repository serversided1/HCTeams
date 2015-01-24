package net.frozenorb.foxtrot.conquest.commands.conquestadmin;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.conquest.ConquestHandler;
import net.frozenorb.foxtrot.conquest.game.ConquestGame;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ConquestAdminSetScoreCommand {

    @Command(names={ "conquestadmin setscore" }, permissionNode="op")
    public static void conquestAdminSetScore(Player sender, @Param(name="team") Team target, @Param(name="score") int score) {
        ConquestGame game = FoxtrotPlugin.getInstance().getConquestHandler().getGame();

        if (game == null) {
            sender.sendMessage(ChatColor.RED + "Conquest is not active.");
            return;
        }

        game.getTeamPoints().put(target.getUniqueId(), score);
        sender.sendMessage(ConquestHandler.PREFIX + " " + ChatColor.GOLD + "Updated the score for " + target.getName(sender) + ChatColor.GOLD + ".");
    }

}