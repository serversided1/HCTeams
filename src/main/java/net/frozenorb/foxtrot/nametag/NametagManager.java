package net.frozenorb.foxtrot.nametag;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This class dynamically creates teams with numerical names and certain
 * prefixes/suffixes (it ignores teams with other characters) to assign unique
 * prefixes and suffixes to specific players in the game. This class makes edits
 * to the <b>scoreboard.dat</b> file, adding and removing teams on the fly.
 * 
 * @author Kerem Celik
 * 
 */
@SuppressWarnings("deprecation")
public class NametagManager {
	private static final String TEAM_NAME_PREFIX = "K_MONEY_";

	private static List<String> list = new ArrayList<String>();

	@Getter private static HashMap<String, HashMap<String, TeamInfo>> teamMap = new HashMap<String, HashMap<String, TeamInfo>>();

	private static final TeamInfo FRIENDLY_TEAM = new TeamInfo(TEAM_NAME_PREFIX + "FRIENDLY", "§a", "");
	private static final TeamInfo ENEMY_TEAM = new TeamInfo(TEAM_NAME_PREFIX + "ENEMY", "§c", "");

	/**
	 * This is player team packet -> p
	 * 
	 */
	public static void reloadPlayer(Player player) {

		Team t = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());

        Bukkit.getOnlinePlayers();

		for (Player p : Bukkit.getOnlinePlayers()) {
			TeamInfo teamInfo = ENEMY_TEAM;

			if (t != null && t == FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(p.getName())) {
				teamInfo = FRIENDLY_TEAM;
			}

			if (p == player) {
				teamInfo = FRIENDLY_TEAM;
			}

			HashMap<String, TeamInfo> ti = new HashMap<String, TeamInfo>();

			if (teamMap.containsKey(p.getName())) {
				ti = teamMap.get(p.getName());

				if (ti.containsKey(player.getName())) {
					TeamInfo tem = ti.get(player.getName());

					if (tem == teamInfo) {
						continue;
					}

					sendPacketsRemoveFromTeam(tem, player.getName(), p);
					ti.remove(player.getName());
					list.remove(tem.getName());

				}
			}

			sendPacketsAddToTeam(teamInfo, new String[] { player.getName() }, p);

			ti.put(player.getName(), teamInfo);
			teamMap.put(p.getName(), ti);

		}
	}

	public static void sendPacketsInitialize(Player p) {
		sendPacketsAddTeam(FRIENDLY_TEAM, p);
		sendPacketsAddTeam(ENEMY_TEAM, p);
	}

	public static void cleanupTeams(Player p) {
		sendPacketsRemoveTeam(FRIENDLY_TEAM, p);
		sendPacketsRemoveTeam(ENEMY_TEAM, p);

	}

	public static void clear(Player player) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (teamMap.containsKey(p.getName())) {
				HashMap<String, TeamInfo> ti = teamMap.get(p.getName());

				if (ti.containsKey(player.getName())) {
					TeamInfo tem = ti.get(player.getName());

					sendPacketsRemoveFromTeam(tem, player.getName(), p);
					ti.remove(player.getName());
					list.remove(tem.getName());

					teamMap.put(p.getName(), ti);

				}
			}
		}
	}

	/**
	 * This is p team packet -> player
	 * 
	 */
	public static void sendTeamsToPlayer(Player player) {
		Team t = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());

		for (Player p : Bukkit.getOnlinePlayers()) {
			TeamInfo teamInfo = ENEMY_TEAM;

			if (t != null && t == FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(p.getName())) {
				teamInfo = FRIENDLY_TEAM;
			}

			if (p == player) {
				teamInfo = FRIENDLY_TEAM;
			}

			HashMap<String, TeamInfo> ti = new HashMap<String, TeamInfo>();

			if (teamMap.containsKey(player.getName())) {
				ti = teamMap.get(player.getName());

				if (ti.containsKey(p.getName())) {
					TeamInfo tem = ti.get(p.getName());

					if (tem == teamInfo) {
						continue;
					}

					sendPacketsRemoveFromTeam(tem, p.getName(), player);
					ti.remove(p.getName());

					list.remove(tem.getName());
				}
			}

			sendPacketsAddToTeam(teamInfo, new String[] { p.getName() }, player);

			ti.put(p.getName(), teamInfo);
			teamMap.put(player.getName(), ti);

		}
	}

	/**
	 * Returns the next available team name that is not taken.
	 * 
	 * @return an integer that for a team name that is not taken.
	 */
	public static int nextName() {
		int at = 0;
		boolean cont = true;
		while (cont) {
			cont = false;
			for (String t : list.toArray(new String[list.size()])) {
				if (t.equals(at + "")) {
					at++;
					cont = true;
				}

			}
		}
		list.add(at + "");
		return at;
	}

	/**
	 * Sends packets out to players to add the given team
	 * 
	 * @param team
	 *            the team to add
	 */
	public static void sendPacketsAddTeam(TeamInfo team, Player p) {

		try {

			ScoreboardTeamPacketMod mod = new ScoreboardTeamPacketMod(team.getName(), team.getPrefix(), team.getSuffix(), new ArrayList<String>(), 0);
			mod.sendToPlayer(p);
		}
		catch (Exception e) {
			System.out.println("Failed to send packet for player : ");
			e.printStackTrace();
		}
	}

	/**
	 * Sends packets out to players to remove the given team
	 * 
	 * @param team
	 *            the team to remove
	 */
	public static void sendPacketsRemoveTeam(TeamInfo team, Player p) {

		try {
			ScoreboardTeamPacketMod mod = new ScoreboardTeamPacketMod(team.getName(), team.getPrefix(), team.getSuffix(), new ArrayList<String>(), 1);
			mod.sendToPlayer(p);
		}
		catch (Exception e) {
			System.out.println("Failed to send packet for player : ");
			e.printStackTrace();
		}
	}

	/**
	 * Sends out packets to players to add the given player to the given team
	 * 
	 * @param team
	 *            the team to use
	 * @param player
	 *            the player to add
	 */
	public static void sendPacketsAddToTeam(TeamInfo team, String[] player, Player p) {

		try {

			ScoreboardTeamPacketMod mod = new ScoreboardTeamPacketMod(team.getName(), Arrays.asList(player), 3);
			mod.sendToPlayer(p);
		}
		catch (Exception e) {
			System.out.println("Failed to send packet for player : ");
			e.printStackTrace();
		}
	}

	/**
	 * Sends out packets to players to remove the given player from the given
	 * team.
	 * 
	 * @param team
	 *            the team to remove from
	 * @param player
	 *            the player to remove
	 */
	public static void sendPacketsRemoveFromTeam(TeamInfo team, String player, Player tp) {

		try {

			ScoreboardTeamPacketMod mod = new ScoreboardTeamPacketMod(team.getName(), Arrays.asList(player), 4);
			mod.sendToPlayer(tp);
		}
		catch (Exception e) {
			System.out.println("Failed to send packet for player : ");
			e.printStackTrace();
		}
	}
}
