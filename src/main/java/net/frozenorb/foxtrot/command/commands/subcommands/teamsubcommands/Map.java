package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.team.claims.VisualClaim;
import net.frozenorb.foxtrot.team.claims.VisualClaim.VisualType;
import org.bukkit.entity.Player;

public class Map {

    @Command(names={ "team map", "t map", "f map", "faction map", "fac map", "map" }, permissionNode="")
    public static void teamInvite(Player sender) {
		final Player p = (Player) sender;

		VisualClaim vc = new VisualClaim(p, VisualType.MAP);
		vc.draw(false);

	}

}