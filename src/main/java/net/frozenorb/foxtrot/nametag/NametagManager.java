package net.frozenorb.foxtrot.nametag;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.ArcherClass;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.spigotmc.CustomTimingsHandler;

import java.util.*;

@SuppressWarnings("deprecation")
public class NametagManager {

    private static List<TeamInfo> registeredTeams = new ArrayList<TeamInfo>();
    private static int teamCreateIndex = 1;

    private static CustomTimingsHandler reloadPlayerColorGrab = new CustomTimingsHandler("Nametags - reloadPlayer - colorGrab");
    private static CustomTimingsHandler reloadPlayerSendPackets = new CustomTimingsHandler("Nametags - reloadPlayer - sendPackets");

    @Getter private static Map<String, Map<String, TeamInfo>> teamMap = new HashMap<String, Map<String, TeamInfo>>();

    public static void reloadPlayer(Player toRefresh) {
        for (Player refreshFor : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
            reloadPlayer(toRefresh, refreshFor);
        }
    }

    public static void reloadPlayer(Player toRefresh, Player refreshFor) {
        reloadPlayerColorGrab.startTiming();
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

        reloadPlayerColorGrab.stopTiming();
        reloadPlayerSendPackets.startTiming();

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
        reloadPlayerSendPackets.stopTiming();
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