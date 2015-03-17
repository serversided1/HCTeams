package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionTracker;
import net.frozenorb.foxtrot.teamactiontracker.enums.TeamActionType;
import net.frozenorb.qlib.util.UUIDUtils;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class TeamUninviteCommand {

    @Command(names={ "team uninvite", "t uninvite", "f uninvite", "faction uninvite", "fac uninvite", "team revoke", "t revoke", "f revoke", "faction revoke", "fac revoke" }, permissionNode="")
    public static void teamUninvite(Player sender, @Parameter(name="all | player") String name) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isOwner(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId())) {
            if (name.equalsIgnoreCase("all")) {
                team.getInvitations().clear();
                sender.sendMessage(ChatColor.GRAY + "You have cleared all pending invitations.");
            } else {
                new BukkitRunnable() {

                    public void run() {
                        UUID nameUUID = UUIDUtils.uuid(name);

                        new BukkitRunnable() {

                            public void run() {
                                if (team.getInvitations().remove(nameUUID)) {
                                    TeamActionTracker.logActionAsync(team, TeamActionType.GENERAL, "Player Uninvited: " + name + " [Uninvited by: " + sender.getName() + "]");
                                    team.getInvitations().remove(nameUUID);
                                    team.flagForSave();
                                    sender.sendMessage(ChatColor.GREEN + "Cancelled pending invitation for " + name + "!");
                                } else {
                                    sender.sendMessage(ChatColor.RED + "No pending invitation for '" + name + "'!");
                                }
                            }

                        }.runTask(Foxtrot.getInstance());
                    }

                }.runTaskAsynchronously(Foxtrot.getInstance());
            }
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
    }

}