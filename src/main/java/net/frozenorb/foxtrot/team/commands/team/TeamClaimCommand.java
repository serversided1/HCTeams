package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class TeamClaimCommand implements Listener {

    @Command(names={ "team claim", "t claim", "f claim", "faction claim", "fac claim" }, permissionNode="")
    public static void teamClaim(Player sender) {
        TeamClaimCommand.claim(sender, false);
    }

    public static void claim(Player sender, boolean force) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isOwner(sender.getName()) || team.isCaptain(sender.getName()) || force) {
            if (LandBoard.getInstance().getClaim(sender.getLocation()) != null) {
                sender.sendMessage(ChatColor.RED + "This location is already claimed!");
                return;
            }

            if (!force) {
                if (team.getClaims().size() >= team.getMaxClaims()) {
                    sender.sendMessage(ChatColor.RED + "Your team has the maximum amount of claims for its size, which is " + team.getMaxClaims() + ".");
                    return;
                }

                if (team.isRaidable()) {
                    sender.sendMessage(ChatColor.RED + "You may not claim land while your faction is raidable!");
                    return;
                }

                if (sender.getWorld().getEnvironment() != World.Environment.NORMAL) {
                    sender.sendMessage(ChatColor.RED + "Land can only be claimed in the overworld.");
                    return;
                }

                if (FoxtrotPlugin.getInstance().getServerHandler().isWarzone(sender.getLocation())) {
                    sender.sendMessage(ChatColor.RED + "You cannot claim land this close to spawn. Go at least " + ServerHandler.WARZONE_RADIUS + " blocks from spawn to be able to claim land!");
                    return;
                }

                Claim north = LandBoard.getInstance().getClaim(sender.getWorld().getChunkAt(sender.getLocation().getChunk().getX() + 1, sender.getLocation().getChunk().getZ() + 1));
                Claim south = LandBoard.getInstance().getClaim(sender.getWorld().getChunkAt(sender.getLocation().getChunk().getX() - 1, sender.getLocation().getChunk().getZ() + 1));
                Claim east = LandBoard.getInstance().getClaim(sender.getWorld().getChunkAt(sender.getLocation().getChunk().getX() + 1, sender.getLocation().getChunk().getZ() - 1));
                Claim west = LandBoard.getInstance().getClaim(sender.getWorld().getChunkAt(sender.getLocation().getChunk().getX() - 1, sender.getLocation().getChunk().getZ() - 1));

                if (team.getClaims().size() != 0 && (north == null || !north.getOwner().equals(team)) && (south == null || !south.getOwner().equals(team)) && (east == null || !east.getOwner().equals(team)) && (west == null || !west.getOwner().equals(team))) {
                    sender.sendMessage(ChatColor.RED + "All of your claims must be touching.");
                    return;
                }
            }

            Claim claim = new Claim(sender.getLocation().getChunk(), team);

            LandBoard.getInstance().addClaim(claim);
            team.getClaims().add(claim);
            team.flagForSave();

            sender.sendMessage(ChatColor.YELLOW + "You have claimed this land for your team. " + ChatColor.GOLD + "Claims: " + team.getClaims().size() + "/" + team.getMaxClaims());
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
    }

}