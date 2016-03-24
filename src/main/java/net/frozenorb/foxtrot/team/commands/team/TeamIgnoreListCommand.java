package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import org.bson.types.ObjectId;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.*;

public class TeamIgnoreListCommand {

    @Command(names = {"team ignore list", "t ignore list", "f ignore list", "faction ignore list", "fac ignore list"}, permissionNode = "")
    public static void ignoreTeam(Player sender) {
        Team senderTeam = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if(senderTeam != null) {
            StringBuilder ignoring = new StringBuilder();

            for(ObjectId oid : senderTeam.getIgnoring()) {
                Team team = Foxtrot.getInstance().getTeamHandler().getTeam(oid);
                ignoring.append(GRAY).append(team.getName()).append(GOLD).append(", ");
            }

            if (ignoring.length() > 2) {
                ignoring.setLength(ignoring.length() - 2);
            }

            sender.sendMessage(GOLD + "Ignoring: " + ignoring.toString());
        } else {
            sender.sendMessage(RED + "You dont have a team, so you're not ignoring any other teams.");
        }
    }
}
