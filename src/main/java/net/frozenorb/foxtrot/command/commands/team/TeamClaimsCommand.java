package net.frozenorb.foxtrot.command.commands.team;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamClaimsCommand {

    @Command(names={ "team claims", "t claims", "f claims", "faction claims", "fac claims" }, permissionNode="")
    public static void teamClaims(Player sender, @Param(name="team") Team target) {
        if (target.getClaims().size() == 0) {
            sender.sendMessage(ChatColor.RED + "That team has no claimed land!");
        } else {
            sender.sendMessage(ChatColor.GRAY + "-- " + ChatColor.DARK_AQUA + target.getFriendlyName() + "'s Claims" + ChatColor.GRAY + " --");

            for (net.frozenorb.foxtrot.team.claims.Claim claim : target.getClaims()) {
                sender.sendMessage(ChatColor.GRAY + " " + claim.getFriendlyName());
            }
        }
    }

}