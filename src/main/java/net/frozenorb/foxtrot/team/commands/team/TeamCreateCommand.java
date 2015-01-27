package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionTracker;
import net.frozenorb.foxtrot.teamactiontracker.enums.TeamActionType;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class TeamCreateCommand {

    public static final Pattern ALPHA_NUMERIC = Pattern.compile("[^a-zA-Z0-9]");

    @Command(names={ "team create", "t create", "f create", "faction create", "fac create" }, permissionNode="")
    public static void teamCreate(Player sender, @Param(name="team") String name) {
        if (FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName()) == null) {
            if (ALPHA_NUMERIC.matcher(name).find()) {
                sender.sendMessage(ChatColor.RED + "Team names must be alphanumeric!");
                return;
            }

            if (name.length() > 16) {
                sender.sendMessage(ChatColor.RED + "Maximum team name size is 16 characters!");
                return;
            }

            if (name.length() < 3) {
                sender.sendMessage(ChatColor.RED + "Minimum team name size is 3 characters!");
                return;
            }

            if (FoxtrotPlugin.getInstance().getTeamHandler().getTeam(name) == null) {
                sender.sendMessage(ChatColor.DARK_AQUA + "Team Created!");
                sender.sendMessage(ChatColor.GRAY + "To learn more about teams, do /team");

                Team team = new Team(name);

                TeamActionTracker.logAction(team, TeamActionType.GENERAL, "Team created. [Created by: " + sender.getName() + "]");

                team.setOwner(sender.getName());
                team.setName(name);
                team.setDTR(1);
                team.setUniqueId(new ObjectId());

                FoxtrotPlugin.getInstance().getTeamHandler().setupTeam(team);
                FoxtrotPlugin.getInstance().getTeamHandler().setTeam(sender.getName(), team);

                FoxtrotPlugin.getInstance().getServer().broadcastMessage("§eTeam §9" + team.getName() + "§e has been §acreated §eby §f" + sender.getDisplayName());
            } else {
                sender.sendMessage(ChatColor.GRAY + "That team already exists!");
            }
        } else {
            sender.sendMessage(ChatColor.GRAY + "You're already in a team!");
        }
    }

}