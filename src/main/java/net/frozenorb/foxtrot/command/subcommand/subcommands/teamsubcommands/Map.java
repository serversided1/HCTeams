package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import java.util.ArrayList;
import java.util.List;

import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.team.claims.VisualClaim;
import net.frozenorb.foxtrot.team.claims.VisualClaim.VisualType;

import org.bukkit.entity.Player;

public class Map extends Subcommand {

	public Map(String name, String errorMessage, String... aliases) {
		super(name, errorMessage, aliases);
	}

	@Override
	public void syncExecute() {
		final Player p = (Player) sender;

		VisualClaim vc = new VisualClaim(p, VisualType.MAP);
		vc.draw(false);

	}

	@Override
	public List<String> tabComplete() {
		return new ArrayList<String>();
	}

}
