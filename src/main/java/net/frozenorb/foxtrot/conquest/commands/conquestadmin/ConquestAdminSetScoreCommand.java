package net.frozenorb.foxtrot.conquest.commands.conquestadmin;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.conquest.ConquestHandler;
import net.frozenorb.foxtrot.conquest.game.ConquestGame;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ConquestAdminSetScoreCommand {

    @Command(names={ "conquestadmin setscore" }, permissionNode="op")
    public static void conquestAdminSetScore(CommandSender sender, @Parameter(name="team") Team team, @Parameter(name="score") int score) {
        ConquestGame game = Foxtrot.getInstance().getConquestHandler().getGame();

        if (game == null) {
            sender.sendMessage(ChatColor.RED + "Conquest is not active.");
            return;
        }

        game.getTeamPoints().put(team.getUniqueId(), score);
        sender.sendMessage(ConquestHandler.PREFIX + " " + ChatColor.GOLD + "Updated the score for " + team.getName() + ChatColor.GOLD + ".");
    }

}