package net.frozenorb.foxtrot.nametag;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.listener.EnderpearlListener;
import net.frozenorb.foxtrot.server.SpawnTag;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("deprecation")
public class NametagManager {

	private static List<TeamInfo> registeredTeams = new ArrayList<TeamInfo>();

	@Getter private static HashMap<String, HashMap<String, TeamInfo>> teamMap = new HashMap<String, HashMap<String, TeamInfo>>();

    static {
        new BukkitRunnable() {

            public void run() {
                for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                    if (player.getGameMode() == GameMode.CREATIVE && player.getItemInHand() != null && player.getItemInHand().getType() == Material.REDSTONE_BLOCK) {
                        for (Player player2 : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                            NametagManager.reloadPlayer(player2, player);
                        }
                    }
                }
            }

        }.runTaskTimer(FoxtrotPlugin.getInstance(), 2L, 2L);
    }

	public static void reloadPlayer(Player toRefresh) {
        for (Player refreshFor : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
            reloadPlayer(toRefresh, refreshFor);
        }
	}

    public static void reloadPlayer(Player toRefresh, Player refreshFor) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(toRefresh.getName());
        TeamInfo teamInfo = getOrCreate(ChatColor.RED.toString(), "");

        if (team != null && team.isMember(refreshFor.getName()) || refreshFor == toRefresh) {
            teamInfo = getOrCreate(ChatColor.GREEN.toString(), "");
        }

        if (refreshFor.getGameMode() == GameMode.CREATIVE && refreshFor.getItemInHand() != null && refreshFor.getItemInHand().getType() == Material.REDSTONE_BLOCK) {
            String enderpearlString = "";
            String combatTagString = "";

            if (EnderpearlListener.getEnderpearlCooldown().containsKey(toRefresh.getName()) && EnderpearlListener.getEnderpearlCooldown().get(toRefresh.getName()) > System.currentTimeMillis()) {
                long millisLeft = EnderpearlListener.getEnderpearlCooldown().get(toRefresh.getName()) - System.currentTimeMillis();
                double value = (millisLeft / 1000D);
                double sec = Math.round(10.0 * value) / 10.0;

                enderpearlString = sec + " ";
            }

            if (SpawnTag.isTagged(toRefresh)) {
                SpawnTag spawnTag = SpawnTag.getSpawnTags().get(toRefresh.getName());

                if (spawnTag.getExpires() > System.currentTimeMillis()) {
                    long millisLeft = spawnTag.getExpires() - System.currentTimeMillis();
                    double value = (millisLeft / 1000D);
                    double sec = Math.round(10.0 * value) / 10.0;

                    combatTagString = " " + sec;
                }
            }

            teamInfo = getOrCreate(ChatColor.YELLOW.toString() + enderpearlString + teamInfo.getPrefix(), ChatColor.DARK_RED + combatTagString);
        }

        HashMap<String, TeamInfo> teamInfoMap = new HashMap<String, TeamInfo>();

        if (teamMap.containsKey(refreshFor.getName())) {
            teamInfoMap = teamMap.get(refreshFor.getName());

            if (teamInfoMap.containsKey(toRefresh.getName())) {
                TeamInfo tem = teamInfoMap.get(toRefresh.getName());

                if (tem != teamInfo) {
                    sendPacketsRemoveFromTeam(tem, toRefresh.getName(), refreshFor);
                    teamInfoMap.remove(toRefresh.getName());
                }
            }
        }

        sendPacketsAddToTeam(teamInfo, new String[] { toRefresh.getName() }, refreshFor);
        teamInfoMap.put(toRefresh.getName(), teamInfo);
        teamMap.put(refreshFor.getName(), teamInfoMap);
    }

    public static void initPlayer(Player player) {
        for (TeamInfo teamInfo : registeredTeams) {
            sendPacketsAddTeam(teamInfo, player);
        }
    }

    public static TeamInfo getOrCreate(String prefix, String suffix) {
        for (TeamInfo teamInfo : registeredTeams) {
            if (teamInfo.getPrefix().equals(prefix) && teamInfo.getSuffix().equals(suffix)) {
                return (teamInfo);
            }
        }

        TeamInfo newTeam = new TeamInfo(prefix + "." + suffix, prefix, suffix);
        registeredTeams.add(newTeam);

        for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
            sendPacketsAddTeam(newTeam, player);
        }

        return (newTeam);
    }

	public static void sendTeamsToPlayer(Player player) {
        for (Player toRefresh : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
            reloadPlayer(toRefresh, player);
        }
	}

	public static void sendPacketsAddTeam(TeamInfo team, Player p) {
		try {
            (new ScoreboardTeamPacketMod(team.getName(), team.getPrefix(), team.getSuffix(), new ArrayList<String>(), 0)).sendToPlayer(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendPacketsAddToTeam(TeamInfo team, String[] player, Player p) {
		try {
            (new ScoreboardTeamPacketMod(team.getName(), Arrays.asList(player), 3)).sendToPlayer(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendPacketsRemoveFromTeam(TeamInfo team, String player, Player tp) {
		try {
            (new ScoreboardTeamPacketMod(team.getName(), Arrays.asList(player), 4)).sendToPlayer(tp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}