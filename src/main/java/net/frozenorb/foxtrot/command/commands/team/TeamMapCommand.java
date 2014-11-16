package net.frozenorb.foxtrot.command.commands.team;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.team.claims.VisualClaim;
import net.frozenorb.foxtrot.team.claims.VisualClaim.VisualType;
import org.bukkit.entity.Player;

public class TeamMapCommand {

    @Command(names={ "team map", "t map", "f map", "faction map", "fac map", "map" }, permissionNode="")
    public static void teamMap(Player sender) {
        (new VisualClaim(sender, VisualType.MAP, false)).draw(false);
	}

}