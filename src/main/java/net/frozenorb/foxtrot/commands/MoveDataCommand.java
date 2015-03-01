package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.qlib.command.annotations.Parameter;
import net.frozenorb.foxtrot.jedis.persist.FriendLivesMap;
import net.frozenorb.foxtrot.jedis.persist.PlaytimeMap;
import net.frozenorb.foxtrot.jedis.persist.SoulboundLivesMap;
import net.frozenorb.foxtrot.jedis.persist.TransferableLivesMap;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.mBasic.Basic;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MoveDataCommand {

    @Command(names={ "movedata" }, permissionNode="op")
    public static void moveData(Player sender, @Parameter(name="oldName") String oldName, @Parameter(name="newName") String newName) {
        sender.sendMessage(ChatColor.GOLD + "Beginning data transfer for " + ChatColor.AQUA + newName + ChatColor.GOLD + ".");

        PlaytimeMap playtimeMap = FoxtrotPlugin.getInstance().getPlaytimeMap();

        if (!playtimeMap.hasPlayed(oldName)) {
            sender.sendMessage(ChatColor.AQUA + oldName + ChatColor.GOLD + " has never been on HCTeams!");
            return;
        }

        if (!playtimeMap.hasPlayed(newName)) {
            sender.sendMessage(ChatColor.AQUA + newName + ChatColor.GOLD + " has never been on HCTeams!");
            return;
        }

        sender.sendMessage(ChatColor.YELLOW + "Transferring team data...");
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(oldName);

        if (team != null) {
            if (FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(newName) == null) {
                boolean leader = team.isOwner(oldName);
                boolean captain = team.isCaptain(oldName);

                for (Subclaim subclaim : team.getSubclaims()) {
                    if (subclaim.isMember(oldName)) {
                        subclaim.addMember(newName);
                    }
                }

                team.removeMember(oldName);
                team.addMember(newName);
                FoxtrotPlugin.getInstance().getTeamHandler().setTeam(newName, team);

                if (leader) {
                    team.setOwner(newName);
                } else if (captain) {
                    team.addCaptain(newName);
                }
            } else {
                sender.sendMessage(ChatColor.RED + "New player is already on a team.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Player not on a team.");
        }

        sender.sendMessage(ChatColor.YELLOW + "Transferring economy data...");
        double oldBal = Basic.get().getEconomyManager().getBalance(oldName);

        Basic.get().getEconomyManager().setBalance(oldName, 0);
        Basic.get().getEconomyManager().setBalance(newName, oldBal);

        sender.sendMessage(ChatColor.YELLOW + "Transferring live data...");
        SoulboundLivesMap soulboundLivesMap = FoxtrotPlugin.getInstance().getSoulboundLivesMap();
        FriendLivesMap friendLivesMap = FoxtrotPlugin.getInstance().getFriendLivesMap();
        TransferableLivesMap transferableLivesMap = FoxtrotPlugin.getInstance().getTransferableLivesMap();

        int oldSoulbound = soulboundLivesMap.getLives(oldName);
        int oldFriend = friendLivesMap.getLives(oldName);
        int oldTransferable = transferableLivesMap.getLives(oldName);

        soulboundLivesMap.setLives(oldName, 0);
        friendLivesMap.setLives(oldName, 0);
        transferableLivesMap.setLives(oldName, 0);

        soulboundLivesMap.setLives(newName, oldSoulbound);
        friendLivesMap.setLives(newName, oldFriend);
        transferableLivesMap.setLives(newName, oldTransferable);

        sender.sendMessage(ChatColor.YELLOW + "Transferring playtime data...");
        FoxtrotPlugin.getInstance().getPlaytimeMap().setPlaytime(newName, FoxtrotPlugin.getInstance().getPlaytimeMap().getPlaytime(oldName));

        sender.sendMessage(ChatColor.YELLOW + "Transferring kill data...");
        FoxtrotPlugin.getInstance().getKillsMap().setKills(newName, FoxtrotPlugin.getInstance().getKillsMap().getKills(oldName));

        sender.sendMessage(ChatColor.GOLD + "Data transfer complete.");
    }

}