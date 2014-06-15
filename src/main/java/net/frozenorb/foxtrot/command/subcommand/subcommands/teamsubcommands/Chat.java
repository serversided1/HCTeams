package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;

public class Chat extends Subcommand {

	public Chat(String name, String errorMessage, String... aliases) {
		super(name, errorMessage, aliases);
	}

	@Override
	public void syncExecute() {
		final Player p = (Player) sender;

		if (FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName()) == null) {
			boolean first = true;
			StringBuilder sb = new StringBuilder();
			for (String a : args) {
				if (!first)
					sb.append(a + " ");
				first = false;
			}
			p.chat("/t create " + sb.toString());
			return;
		}
		if (p.hasMetadata("teamChat")) {
			p.removeMetadata("teamChat", FoxtrotPlugin.getInstance());
			p.sendMessage(ChatColor.DARK_AQUA + "You are now in public chat.");
		} else {
			p.setMetadata("teamChat", new FixedMetadataValue(FoxtrotPlugin.getInstance(), true));
			p.sendMessage(ChatColor.DARK_AQUA + "You are now in team chat only mode.");
		}

	}

	@Override
	public List<String> tabComplete() {
		return new ArrayList<String>();
	}

}
