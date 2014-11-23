package net.frozenorb.foxtrot.command.commands.team;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.factionactiontracker.FactionActionTracker;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamUnclaimCommand {

    @Command(names={ "team unclaim", "t unclaim", "f unclaim", "faction unclaim", "fac unclaim" }, permissionNode="")
    public static void teamUnclaim(Player sender, @Param(name="all?", defaultValue="f") String all) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!team.isOwner(sender.getName())) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only the team leader can do this.");
            return;
        }

        if (team.isRaidable()) {
            sender.sendMessage(ChatColor.RED + "You may not unclaim land while your faction is raidable!");
            return;
        }

        if (all.equalsIgnoreCase("all")) {
            int claims = team.getClaims().size();
            double refund = 0.0D;

            for (Claim claim : team.getClaims()) {
                refund += Claim.getPrice(claim, team, false);
                FactionActionTracker.logAction(team, "actions", "Land Unclaim: [" + claim.getMinimumPoint().getBlockX() + ", " + claim.getMinimumPoint().getBlockY() + ", " + claim.getMinimumPoint().getBlockZ() + "] -> [" + claim.getMaximumPoint().getBlockX() + ", " + claim.getMaximumPoint().getBlockY() + ", " + claim.getMaximumPoint().getBlockZ() + "] [Unclaimed by: " + sender.getName() + ", Refund: " + refund + "]");
            }

            team.setBalance(team.getBalance() + refund);
            team.getClaims().clear();
            team.setHQ(null);
            team.flagForSave();
            LandBoard.getInstance().clear(team);
            sender.sendMessage(ChatColor.RED + "You have unclaimed all of your claims (" + ChatColor.LIGHT_PURPLE + claims + " total" + ChatColor.RED + ")! Your team was refunded " + ChatColor.LIGHT_PURPLE + "$" + refund + ChatColor.RED + ".");
            return;
        }

        if (LandBoard.getInstance().getClaim(sender.getLocation()) != null && team.ownsLocation(sender.getLocation())) {
            Claim claim = LandBoard.getInstance().getClaim(sender.getLocation());
            int refund = Claim.getPrice(claim, team, false);

            team.setBalance(team.getBalance() + refund);
            team.getClaims().remove(claim);
            team.flagForSave();

            LandBoard.getInstance().setTeamAt(claim, null);

            FactionActionTracker.logAction(team, "actions", "Land Unclaim: [" + claim.getMinimumPoint().getBlockX() + ", " + claim.getMinimumPoint().getBlockY() + ", " + claim.getMinimumPoint().getBlockZ() + "] -> [" + claim.getMaximumPoint().getBlockX() + ", " + claim.getMaximumPoint().getBlockY() + ", " + claim.getMaximumPoint().getBlockZ() + "] [Unclaimed by: " + sender.getName() + ", Refund: " + refund + "]");
            sender.sendMessage(ChatColor.RED + "You have unclaimed the claim " + ChatColor.LIGHT_PURPLE + claim.getFriendlyName() + ChatColor.RED + "! Your team was refunded " + ChatColor.LIGHT_PURPLE + "$" + refund + ChatColor.RED + "!");

            if (team.getHq() != null && claim.contains(team.getHq())) {
                team.setHQ(null);
                sender.sendMessage(ChatColor.RED + "Your HQ was in this claim, so it has been unset.");
            }

            return;
        }

        sender.sendMessage(ChatColor.RED + "You do not own this claim.");
        sender.sendMessage(ChatColor.RED + "To unclaim all claims, type " + ChatColor.YELLOW + "/team unclaim all" + ChatColor.RED + ".");
    }

}