package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.entity.Player;

public class TeamOpClaimCommand {

    @Command(names={ "team opclaim", "t opclaim", "f opclaim", "faction opclaim", "fac opclaim" }, permissionNode="op")
    public static void teamOpClaim(Player sender) {
        TeamClaimCommand.claim(sender, true);
    }

}