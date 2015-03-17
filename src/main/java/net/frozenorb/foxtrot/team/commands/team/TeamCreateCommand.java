package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionTracker;
import net.frozenorb.foxtrot.teamactiontracker.enums.TeamActionType;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.spigotmc.CustomTimingsHandler;

import java.util.regex.Pattern;

public class TeamCreateCommand {

    public static final Pattern ALPHA_NUMERIC = Pattern.compile("[^a-zA-Z0-9]");

    private static CustomTimingsHandler timings1 = new CustomTimingsHandler("Team Creation - Part 1");
    private static CustomTimingsHandler timings2 = new CustomTimingsHandler("Team Creation - Part 2");
    private static CustomTimingsHandler timings3 = new CustomTimingsHandler("Team Creation - Part 3");
    private static CustomTimingsHandler timings4 = new CustomTimingsHandler("Team Creation - Part 4");

    @Command(names={ "team create", "t create", "f create", "faction create", "fac create" }, permissionNode="")
    public static void teamCreate(Player sender, @Parameter(name="team") String name) {
        if (Foxtrot.getInstance().getTeamHandler().getTeam(sender) != null) {
            sender.sendMessage(ChatColor.GRAY + "You're already in a team!");
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

        if (Foxtrot.getInstance().getTeamHandler().getTeam(name) != null) {
            sender.sendMessage(ChatColor.GRAY + "That team already exists!");
            return;
        }

        timings1.startTiming();

        if (ALPHA_NUMERIC.matcher(name).find()) {
            sender.sendMessage(ChatColor.RED + "Team names must be alphanumeric!");
            timings1.stopTiming();
            return;
        }

        timings1.stopTiming();
        timings2.startTiming();

        sender.sendMessage(ChatColor.DARK_AQUA + "Team Created!");
        sender.sendMessage(ChatColor.GRAY + "To learn more about teams, do /team");

        timings2.stopTiming();
        timings3.startTiming();

        Team team = new Team(name);

        TeamActionTracker.logActionAsync(team, TeamActionType.GENERAL, "Team created. [Created by: " + sender.getName() + "]");

        team.setOwner(sender.getUniqueId());
        team.setName(name);
        team.setDTR(1);
        team.setUniqueId(new ObjectId());

        timings3.stopTiming();
        timings4.startTiming();

        Foxtrot.getInstance().getTeamHandler().setupTeam(team);
        Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.YELLOW + "Team " + ChatColor.BLUE + team.getName() + ChatColor.YELLOW + " has been " + ChatColor.GREEN + "created" + ChatColor.YELLOW + " by " + sender.getDisplayName());

        timings4.stopTiming();
    }

}