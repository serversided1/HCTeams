package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionTracker;
import net.frozenorb.foxtrot.teamactiontracker.enums.TeamActionType;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class TeamUnclaimCommand {

    @Command(names={ "team unclaim", "t unclaim", "f unclaim", "faction unclaim", "fac unclaim" }, permissionNode="")
    public static void teamUnclaim(Player sender, @Param(name="all?", defaultValue="f") String all) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!(team.isOwner(sender.getName()) || team.isCaptain(sender.getName()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }

        if (team.isRaidable()) {
            sender.sendMessage(ChatColor.RED + "You may not unclaim land while your faction is raidable!");
            return;
        }

        if (all.equalsIgnoreCase("all")) {
            if (!team.isOwner(sender.getName())) {
                sender.sendMessage(ChatColor.RED + "Only team owners may unclaim all land.");
                return;
            }

            int claims = team.getClaims().size();

            for (Claim claim : team.getClaims()) {
                TeamActionTracker.logAction(team, TeamActionType.GENERAL, "Land Unclaim: " + claim.toString() + " [Unclaimed by: " + sender.getName() + "]");
            }

            LandBoard.getInstance().removeClaims(team.getClaims());

            for (Subclaim subclaim : team.getSubclaims()) {
                LandBoard.getInstance().notifySubclaimChange(subclaim);
            }

            team.getClaims().clear();
            team.getSubclaims().clear();
            team.setHQ(null);

            team.flagForSave();

            for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                if (team.isMember(player)) {
                    player.sendMessage(ChatColor.YELLOW + sender.getName() + " has unclaimed all of your team's land. (" + ChatColor.LIGHT_PURPLE + claims + " chunks total" + ChatColor.YELLOW + ")");
                }
            }

            return;
        }

        Claim atLocation = LandBoard.getInstance().getClaim(sender.getLocation());

        if (atLocation != null && atLocation.getOwner().equals(team)) {
            LandBoard.getInstance().removeClaim(atLocation);
            team.getClaims().remove(atLocation);

            for (Claim claim : team.getClaims()) {
                World world = FoxtrotPlugin.getInstance().getServer().getWorld(claim.getWorld());

                Claim north = LandBoard.getInstance().getClaim(world.getChunkAt(claim.getChunkX() + 1, claim.getChunkX() + 1));
                Claim south = LandBoard.getInstance().getClaim(world.getChunkAt(claim.getChunkX() - 1, claim.getChunkX() + 1));
                Claim east = LandBoard.getInstance().getClaim(world.getChunkAt(claim.getChunkX() + 1, claim.getChunkX() - 1));
                Claim west = LandBoard.getInstance().getClaim(world.getChunkAt(claim.getChunkX() - 1, claim.getChunkX() - 1));

                if (team.getClaims().size() != 0 && (north == null || !north.getOwner().equals(team)) && (south == null || !south.getOwner().equals(team)) && (east == null || !east.getOwner().equals(team)) && (west == null || !west.getOwner().equals(team))) {
                    sender.sendMessage(ChatColor.RED + "All of your claims must be touching.");

                    // Oh well. Add it back.
                    LandBoard.getInstance().addClaim(atLocation);
                    team.getClaims().add(atLocation);
                    return;
                }
            }

            for (Subclaim subclaim : new ArrayList<Subclaim>(team.getSubclaims())) {
                if (atLocation.contains(subclaim.getLoc1()) || atLocation.contains(subclaim.getLoc2())) {
                    team.getSubclaims().remove(subclaim);
                    LandBoard.getInstance().notifySubclaimChange(subclaim);
                }
            }

            team.flagForSave();

            TeamActionTracker.logAction(team, TeamActionType.GENERAL, "Land Unclaim: " + atLocation.toString() + " [Unclaimed by: " + sender.getName() + "]");

            for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                if (team.isMember(player)) {
                    player.sendMessage(ChatColor.YELLOW + sender.getName() + " has unclaimed the chunk " + ChatColor.LIGHT_PURPLE + atLocation.getChunkX() + ", " + atLocation.getChunkZ() + ChatColor.YELLOW + ".");
                }
            }

            if (team.getHQ() != null && atLocation.contains(team.getHQ())) {
                team.setHQ(null);
                sender.sendMessage(ChatColor.RED + "Your HQ was in this claim, so it has been unset.");
            }

            return;
        }

        sender.sendMessage(ChatColor.RED + "You do not own this claim.");
        sender.sendMessage(ChatColor.RED + "To unclaim all claims, type " + ChatColor.YELLOW + "/team unclaim all" + ChatColor.RED + ".");
    }

}
