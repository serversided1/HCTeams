package net.frozenorb.foxtrot.team.commands.team;

import com.google.common.collect.ImmutableMap;

import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionTracker;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionType;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class TeamForceInviteCommand {

    @Command(names = {"team forceinvite", "t forceinvite", "f forceinvite", "faction forceinvite", "fac forceinvite"}, permission = "")
    public static void teamForceInvite(Player sender, @Param(name="player") UUID player) {
        if (!Foxtrot.getInstance().getServerHandler().isForceInvitesEnabled()) {
            sender.sendMessage(ChatColor.RED + "Force-invites are not enabled on this server.");
            return;
        }

        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (Foxtrot.getInstance().getMapHandler().isKitMap() || Foxtrot.getInstance().getServerHandler().isVeltKitMap()) {
            sender.sendMessage(ChatColor.RED + "You don't need to use this during kit maps.");
            return;
        }

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.getMembers().size() >= Foxtrot.getInstance().getMapHandler().getTeamSize()) {
            sender.sendMessage(ChatColor.RED + "The max team size is " + Foxtrot.getInstance().getMapHandler().getTeamSize() + "!");
            return;
        }

        if (!(team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId()))) {
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

        if (!team.getHistoricalMembers().contains(player)) {
            sender.sendMessage(ChatColor.RED + "That player has never been a member of your faction. Please use /f invite.");
            return;
        }

        /*if (team.isRaidable()) {
            sender.sendMessage(ChatColor.RED + "You may not invite players while your team is raidable!");
            return;
        }*/

        if (team.getForceInvites() == 0) {
            sender.sendMessage(ChatColor.RED + "You do not have any force-invites left!");
            return;
        }

        team.setForceInvites(team.getForceInvites() - 1);
        TeamActionTracker.logActionAsync(team, TeamActionType.PLAYER_INVITE_SENT, ImmutableMap.of(
                "playerId", player,
                "invitedById", sender.getUniqueId(),
                "invitedByName", sender.getName(),
                "betrayOverride", "false",
                "usedForceInvite", "true"
        ));

        // we use a runnable so this message gets displayed at the end
        new BukkitRunnable() {
            @Override
            public void run() {
                sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "You have used a force-invite.");

                if (team.getForceInvites() != 0) {
                    sender.sendMessage(ChatColor.YELLOW + "You have " + ChatColor.RED + team.getForceInvites() + ChatColor.YELLOW + " of those left.");
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "You have " + ChatColor.RED + "none" + ChatColor.YELLOW + " of those left.");
                }
            }
        }.runTask(Foxtrot.getInstance());

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
