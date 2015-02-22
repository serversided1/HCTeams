package net.frozenorb.foxtrot.nametag;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.ArcherClass;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.spigotmc.CustomTimingsHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("deprecation")
public class NametagManager {

    private static List<TeamInfo> registeredTeams = Collections.synchronizedList(new ArrayList<>());
    private static int teamCreateIndex = 1;

    @Getter private static Map<String, Map<String, TeamInfo>> teamMap = new ConcurrentHashMap<>();

    public static void applyUpdate(NametagUpdate nametagUpdate) {
        Player toRefreshPlayer = FoxtrotPlugin.getInstance().getServer().getPlayerExact(nametagUpdate.getToRefresh());

        // Just ignore it if they logged off since the request to update was submitted
        if (toRefreshPlayer == null) {
            return;
        }

        if (nametagUpdate.getRefreshFor() == null) {
            for (Player refreshFor : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                reloadPlayer0(toRefreshPlayer, refreshFor);
            }
        } else {
            Player refreshForPlayer = FoxtrotPlugin.getInstance().getServer().getPlayerExact(nametagUpdate.getRefreshFor());

            if (refreshForPlayer != null) {
                reloadPlayer0(toRefreshPlayer, refreshForPlayer);
            }
        }
    }

    public static void reloadPlayer(Player toRefresh) {
        NametagThread.getPendingUpdates().add(new NametagUpdate(toRefresh));
    }

    public static void reloadPlayer(Player toRefresh, Player refreshFor) {
        NametagThread.getPendingUpdates().add(new NametagUpdate(toRefresh, refreshFor));
    }

    public static void reloadPlayer0(Player toRefresh, Player refreshFor) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(toRefresh.getName());
        TeamInfo teamInfo = getOrCreate(ChatColor.YELLOW.toString(), "");

        if (team != null) {
            if (team.isMember(refreshFor.getName())) {
                teamInfo = getOrCreate(ChatColor.DARK_GREEN.toString(), "");
            } else if (team.isAlly(refreshFor.getName())) {
                teamInfo = getOrCreate(Team.ALLY_COLOR.toString(), "");
            } else if (team.isTrading()) {
                teamInfo = getOrCreate(Team.TRADING_COLOR.toString(), "");
            } else if (ArcherClass.getMarkedPlayers().containsKey(toRefresh.getName()) && ArcherClass.getMarkedPlayers().get(toRefresh.getName()) > System.currentTimeMillis()) {
                teamInfo = getOrCreate(ChatColor.RED.toString(), "");
            }
        } else if (ArcherClass.getMarkedPlayers().containsKey(toRefresh.getName()) && ArcherClass.getMarkedPlayers().get(toRefresh.getName()) > System.currentTimeMillis()) {
            teamInfo = getOrCreate(ChatColor.RED.toString(), "");
        }

        // You always see yourself as green, even if you're not on a team.
        if (refreshFor == toRefresh) {
            teamInfo = getOrCreate(ChatColor.DARK_GREEN.toString(), "");
        }

        Map<String, TeamInfo> teamInfoMap = new HashMap<String, TeamInfo>();

        if (teamMap.containsKey(refreshFor.getName())) {
            teamInfoMap = teamMap.get(refreshFor.getName());

            if (teamInfoMap.containsKey(toRefresh.getName())) {
                TeamInfo tem = teamInfoMap.get(toRefresh.getName());

                if (tem != teamInfo) {
                    (new ScoreboardTeamPacketMod(tem.getName(), Arrays.asList(toRefresh.getName()), 4)).sendToPlayer(refreshFor);
                    teamInfoMap.remove(toRefresh.getName());
                }
            }
        }

        (new ScoreboardTeamPacketMod(teamInfo.getName(), Arrays.asList(toRefresh.getName()), 3)).sendToPlayer(refreshFor);
        teamInfoMap.put(toRefresh.getName(), teamInfo);
        teamMap.put(refreshFor.getName(), teamInfoMap);
    }

    public static void initPlayer(Player player) {
        for (TeamInfo teamInfo : registeredTeams) {
            teamInfo.getTeamAddPacket().sendToPlayer(player);
        }
    }

    public static TeamInfo getOrCreate(String prefix, String suffix) {
        for (TeamInfo teamInfo : registeredTeams) {
            if (teamInfo.getPrefix().equals(prefix) && teamInfo.getSuffix().equals(suffix)) {
                return (teamInfo);
            }
        }

        TeamInfo newTeam = new TeamInfo(String.valueOf(teamCreateIndex++), prefix, suffix);
        registeredTeams.add(newTeam);

        ScoreboardTeamPacketMod addPacket = newTeam.getTeamAddPacket();

        for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
            addPacket.sendToPlayer(player);
        }

        return (newTeam);
    }

    public static void sendTeamsToPlayer(Player player) {
        for (Player toRefresh : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
            reloadPlayer(toRefresh, player);
        }
    }

}