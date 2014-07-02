package net.frozenorb.foxtrot.command.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.BaseCommand;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.TeamManager;
import net.frozenorb.foxtrot.team.claims.PhysicalChunk;

@SuppressWarnings("deprecation")
public class Focus extends BaseCommand {

	public static HashMap<String, Focusable> currentTrackers = new HashMap<String, Focusable>();

	public Focus() {
		super("focus", "track", "hunt", "whitepages", "whereismonica");
	}

	public List<String> tabComplete() {

		LinkedList<String> params = new LinkedList<String>(Arrays.asList(args));
		LinkedList<String> results = new LinkedList<String>();
		String action = null;

		if (params.size() >= 1) {
			action = params.pop().toLowerCase();
		} else {
			return results;

		}

		if (params.size() == 1) {
			String newAction = params.pop().toLowerCase();

			if (action.equalsIgnoreCase("t") || action.equalsIgnoreCase("team")) {

				ArrayList<String> teamNames = new ArrayList<String>();
				for (Team tem : FoxtrotPlugin.getInstance().getTeamManager().getTeams()) {

					if (tem.getFriendlyName().toLowerCase().startsWith(newAction)) {
						teamNames.add(tem.getFriendlyName());
					}
				}
				return teamNames;

			}
			if (action.equalsIgnoreCase("player") || action.equalsIgnoreCase("p")) {

				ArrayList<String> strs = new ArrayList<String>();

				for (Player p : Bukkit.getOnlinePlayers()) {
					if (!p.hasMetadata("invisible") && p.getName().toLowerCase().startsWith(newAction)) {
						strs.add(p.getName());
					}
				}
				return strs;
			}
			if (action.toLowerCase().startsWith("loc")) {
				return new ArrayList<String>();
			}
		}

		ArrayList<String> strs = new ArrayList<String>();

		for (String str : new String[] { "team", "player", "location", "reset" }) {
			if (str.toLowerCase().startsWith(action)) {
				strs.add(str);
			}
		}

		return strs;
	}

	@Override
	public void syncExecute() {
		if (args.length > 1) {

			Player p = (Player) sender;
			TeamManager tm = FoxtrotPlugin.getInstance().getTeamManager();

			if (p.getItemInHand() == null || p.getItemInHand().getType() != Material.COMPASS) {
				p.sendMessage(ChatColor.RED + "You must be holding a compass to do this!");
				return;
			}

			Focusable focusable = null;

			if (args[0].equalsIgnoreCase("reset")) {
				focusable = new Focusable("§ethe §cWarzone§e") {

					@Override
					public Location updateLocation() {
						return new Location(Bukkit.getWorld("world"), 0, 0, 0);
					}

					@Override
					public FocusType getFocusType() {
						return FocusType.WARZONE;
					}
				};
			} else if (args[0].equalsIgnoreCase("team") || args[0].equalsIgnoreCase("t")) {
				String teamName = args[1];

				if (!tm.teamExists(teamName)) {
					sender.sendMessage(ChatColor.RED + "Team '" + teamName + "' could not be found.");
					return;
				}
				final Team t = tm.getTeam(teamName);

				if (t.getChunks().size() == 0 && t.getHQ() == null) {
					sender.sendMessage(ChatColor.RED + "That team has nothing to focus on!");
					return;
				}
				focusable = new Focusable(t.getFriendlyName()) {

					@Override
					public FocusType getFocusType() {
						return FocusType.TEAM;
					}

					@Override
					public Location updateLocation() {
						if (t.getHQ() == null) {
							if (t.getChunks().size() > 0) {
								PhysicalChunk cc = t.getChunks().get(0);

								Location loc = new Location(Bukkit.getWorld("world"), cc.getX() * 16, 70, cc.getZ() * 16);
								return loc;
							}
							return null;
						}
						return t.getHQ();
					}
				};
			}

			else if (args[0].equalsIgnoreCase("player") || args[0].equalsIgnoreCase("p")) {
				String pName = args[1];

				final Player pl = Bukkit.getPlayer(pName);

				if (pl == null || pl.hasMetadata("invisible")) {
					sender.sendMessage(ChatColor.RED + "Player '" + pName + "' could not be found.");
					return;
				}

				if (pl.getWorld().getEnvironment() != ((Player) sender).getWorld().getEnvironment()) {
					sender.sendMessage(ChatColor.RED + "That player is not in the same world as you!");
					return;
				}
				final String cacheName = pl.getName();

				int amt = 0;
				for (Focusable fb : currentTrackers.values()) {
					if (fb.getFocusType() == FocusType.PLAYER) {
						if (ChatColor.stripColor(fb.data).equalsIgnoreCase(ChatColor.stripColor(pl.getDisplayName()))) {
							amt++;
						}
					}
				}
				if (amt > 4) {
					sender.sendMessage(ChatColor.RED + "There are already 5 players targeting " + pl.getName() + ".");
					return;
				}

				focusable = new Focusable(pl.getDisplayName()) {

					@Override
					public FocusType getFocusType() {
						return FocusType.PLAYER;
					}

					@Override
					public Location updateLocation() {
						Player pss = Bukkit.getPlayerExact(cacheName);

						if (pss == null) {
							return null;
						}
						return pss.getLocation();
					}
				};

			} else if (args[0].toLowerCase().startsWith("loc")) {
				double x = 0, z = 0;
				if (args.length == 2) {
					String check1 = args[1];

					if (check1.contains(",")) {
						String x1 = check1.split(",")[0];
						String z1 = check1.split(",")[1];

						try {
							x = Double.parseDouble(x1);
							z = Double.parseDouble(z1);
						}
						catch (NumberFormatException ex) {
							sender.sendMessage(ChatColor.RED + "Please specify numbers only!");
							return;

						}
					}
				} else if (args.length > 2) {
					String x1 = args[1].replace(",", "");
					String z1 = args[2].replace(",", "");

					try {
						x = Double.parseDouble(x1);
						z = Double.parseDouble(z1);
					}
					catch (NumberFormatException ex) {
						sender.sendMessage(ChatColor.RED + "Please specify numbers only!");
						return;
					}
				}
				final double trackX = x;
				final double trackZ = z;

				focusable = new Focusable("§d(" + (int) trackX + ", " + (int) trackZ + ")") {

					@Override
					public Location updateLocation() {
						return new Location(Bukkit.getWorlds().get(0), trackX, 70, trackZ);
					}

					@Override
					public FocusType getFocusType() {
						return FocusType.LOCATION;
					}
				};
			}

			else {
				sender.sendMessage("§c/focus <team|player|loc> <teamname|playername|x,z>");
				return;
			}

			if (currentTrackers.containsKey(p.getName())) {
				currentTrackers.get(p.getName()).cancel();
			}
			currentTrackers.put(p.getName(), focusable);
			focusable.start(p);
			Location l = focusable.updateLocation();
			p.sendMessage(ChatColor.YELLOW + "You have begun to focus on §c" + focusable.data + "§e" + (focusable.getFocusType() == FocusType.TEAM ? " §7(" + l.getBlockX() + ", " + l.getBlockZ() + ")" : "") + "§e.");
		} else {
			sender.sendMessage("§c/focus <team|player|loc> <teamname|playername|x,z>");
		}
	}

	@RequiredArgsConstructor
	public static abstract class Focusable extends BukkitRunnable {
		private Player p;
		@NonNull private String data;
		private Location lastLocation;

		public abstract FocusType getFocusType();

		public abstract Location updateLocation();

		public void start(Player p) {
			this.p = p;
			lastLocation = updateLocation();
			runTaskTimer(FoxtrotPlugin.getInstance(), 0L, 20L);
		}

		@Override
		public void run() {

			Location l = updateLocation();

			if (l == null) {

				if (getFocusType() == FocusType.TEAM) {
					p.sendMessage(ChatColor.YELLOW + "Focus cancelled! §c" + data + "§e no longer has claimed territory.");
					currentTrackers.remove(p.getName());
					cancel();
					return;
				} else if (getFocusType() == FocusType.PLAYER) {
					if (lastLocation != null) {
						p.sendMessage(data + " §elogged out and will be refocused when they log in!");
					}
					return;
				}
			} else {
				if (lastLocation == null) {
					p.sendMessage(data + " §alogged back in and is now being focused!");

				}
			}
			this.lastLocation = l;
			p.setCompassTarget(l);
		}
	}

	private static enum FocusType {
		WARZONE,
		TEAM,
		PLAYER,
		LOCATION;
	}

}
