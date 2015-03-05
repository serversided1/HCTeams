package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.team.claims.VisualClaim;
import net.frozenorb.foxtrot.team.claims.VisualClaimType;
import net.frozenorb.qlib.command.annotations.Command;
import org.bukkit.entity.Player;

public class TeamMapCommand {

    @Command(names={ "team map", "t map", "f map", "faction map", "fac map", "map" }, permissionNode="")
    public static void teamMap(Player sender) {
        (new VisualClaim(sender, VisualClaimType.MAP, false)).draw(false);
    }

}