package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.team.Team;

@SuppressWarnings("deprecation")
public class Msg extends Subcommand {

	public Msg(String name, String errorMessage, String... aliases) {
		super(name, errorMessage, aliases);
	}

	@Override
	public void syncExecute() {
		Player p = (Player) sender;

		Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName());

		if (args.length > 1) {
			if (team != null) {
				LinkedList<String> params = new LinkedList<String>(Arrays.asList(args));
				params.removeFirst();
				String msg = StringUtils.join(params, " ");
				for (String name : team.getMembers()) {
					Player pl = Bukkit.getPlayerExact(name);
					if (pl != null) {
						pl.sendMessage(ChatColor.DARK_AQUA + "(Team) " + p.getName() + ":§e " + msg);
					}
				}
			} else
				sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
		} else
			sender.sendMessage(ChatColor.RED + "/t msg <message>");
	}

	@Override
	public List<String> tabComplete() {
		return new ArrayList<String>();
	}
}
