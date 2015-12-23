package net.frozenorb.foxtrot.team.commands.team;

import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionTracker;
import net.frozenorb.foxtrot.teamactiontracker.enums.TeamActionType;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class TeamInviteCommand {

    @Command(names={ "team invite", "t invite", "f invite", "faction invite", "fac invite", "team inv", "t inv", "f inv", "faction inv", "fac inv" }, permissionNode="")
    public static void teamInvite(Player sender, @Parameter(name="player") UUID player, @Parameter(name="override?", defaultValue="something-not-override") String override) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.getMembers().size() >= Foxtrot.getInstance().getMapHandler().getTeamSize()) {
            sender.sendMessage(ChatColor.RED + "The max team size is " + Foxtrot.getInstance().getMapHandler().getTeamSize() + "!");
            return;
        }

        if (!(team.isOwner(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }

        if (team.isMember(player)) {
            sender.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player) + " is already on your team.");
            return;
        }

        if (team.getInvitations().contains(player)) {
            sender.sendMessage(ChatColor.RED + "That player has already been invited.");
            return;
        }

        if (Foxtrot.getInstance().getServerHandler().getBetrayers().contains(player) && !override.equalsIgnoreCase("override")) {
            sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "WARNING! " + ChatColor.YELLOW + UUIDUtils.name(player) + " has previously betrayed another team. Are you sure you want to invite " + UUIDUtils.name(player) + "? Type '/t invite " + UUIDUtils.name(player) + " override' to ignore this warning.");
            return;
        }

        /*if (team.isRaidable()) {
            sender.sendMessage(ChatColor.RED + "You may not invite players while your team is raidable!");
            return;
        }*/

        if (team.getHistoricalMembers().contains(player) && team.getSize() > 10) {
            if (team.getForceInvites() == 0) {
                sender.sendMessage(ChatColor.RED + "You do not have any force-invites left, and that player was once a member of your team.");
                return;
            }

            team.setForceInvites(team.getForceInvites() - 1);

            new BukkitRunnable() {
                @Override
                public void run() {
                    sender.sendMessage(" ");
                    sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "As that player was previously a member of your team, you used a force-invite.");
                    sender.sendMessage(ChatColor.YELLOW + "You have " + ChatColor.RED + team.getForceInvites() + ChatColor.YELLOW + " of those left.");
                    sender.sendMessage(" ");
                }
            }.runTask(Foxtrot.getInstance());
        }

        TeamActionTracker.logActionAsync(team, TeamActionType.GENERAL, "Player Invited: " + UUIDUtils.name(player) + " [Invited by: " + sender.getName() + "]");
        team.getInvitations().add(player);
        team.flagForSave();

        Player bukkitPlayer = Foxtrot.getInstance().getServer().getPlayer(player);

        if (bukkitPlayer != null) {
            bukkitPlayer.sendMessage(ChatColor.DARK_AQUA + sender.getName() + " invited you to join '" + ChatColor.YELLOW + team.getName() + ChatColor.DARK_AQUA + "'.");

            FancyMessage clickToJoin =new FancyMessage("Type '").color(ChatColor.DARK_AQUA).then("/team join " + team.getName()).color(ChatColor.YELLOW);
            clickToJoin.then("' or ").color(ChatColor.DARK_AQUA);
            clickToJoin.then("click here").color(ChatColor.AQUA).command("/team join " + team.getName()).tooltip("§aJoin " + team.getName());
            clickToJoin.then(" to join.").color(ChatColor.DARK_AQUA);

            clickToJoin.send(bukkitPlayer);
        }

        team.sendMessage(ChatColor.YELLOW + UUIDUtils.name(player) + " has been invited to the team!");
    }

}