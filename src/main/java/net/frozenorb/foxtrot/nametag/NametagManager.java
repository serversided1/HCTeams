package net.frozenorb.foxtrot.nametag;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.listener.EnderpearlListener;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.ArcherClass;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("deprecation")
public class NametagManager {

    private static List<TeamInfo> registeredTeams = new ArrayList<TeamInfo>();
    private static int teamCreateIndex = 1;

    @Getter private static HashMap<String, HashMap<String, TeamInfo>> teamMap = new HashMap<String, HashMap<String, TeamInfo>>();

    static {
        new BukkitRunnable() {

            public void run() {
                for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                    if (player.getGameMode() == GameMode.CREATIVE && player.getItemInHand() != null && player.getItemInHand().getType() == Material.REDSTONE_BLOCK) {
                        for (Entity entity : player.getNearbyEntities(20, 40, 20)) {
                            if (entity instanceof Player) {
                                NametagManager.reloadPlayer((Player) entity, player);
                            }
                        }
                    }
                }
            }

        }.runTaskTimer(FoxtrotPlugin.getInstance(), 20L, 20L);
    }

    public static void reloadPlayer(Player toRefresh) {
        for (Player refreshFor : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
            reloadPlayer(toRefresh, refreshFor);
        }
    }

    public static void reloadPlayer(Player toRefresh, Player refreshFor) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(toRefresh.getName());
        TeamInfo teamInfo = getOrCreate(ChatColor.YELLOW.toString(), "");

        if (team != null) {
            if (team.isMember(refreshFor.getName())) {
                teamInfo = getOrCreate(ChatColor.DARK_GREEN.toString(), "");
            } else if (team.isAlly(refreshFor.getName())) {
                teamInfo = getOrCreate(ChatColor.LIGHT_PURPLE.toString(), "");
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

        if (refreshFor.getGameMode() == GameMode.CREATIVE && refreshFor.getItemInHand() != null && refreshFor.getItemInHand().getType() == Material.REDSTONE_BLOCK) {
            String enderpearlString = "";
            String combatTagString = "";

            if (EnderpearlListener.getEnderpearlCooldown().containsKey(toRefresh.getName()) && EnderpearlListener.getEnderpearlCooldown().get(toRefresh.getName()) > System.currentTimeMillis()) {
                long millisLeft = EnderpearlListener.getEnderpearlCooldown().get(toRefresh.getName()) - System.currentTimeMillis();
                double value = (millisLeft / 1000D);
                double sec = Math.round(10.0 * value) / 10.0;

                enderpearlString = sec + " ";
            }

            if (SpawnTagHandler.isTagged(toRefresh)) {
                long millisLeft = SpawnTagHandler.getTag(toRefresh);
                double value = (millisLeft / 1000D);
                double sec = Math.round(10.0 * value) / 10.0;

                combatTagString = " " + sec;
            }

            teamInfo = getOrCreate(ChatColor.GREEN.toString() + enderpearlString + teamInfo.getPrefix(), ChatColor.DARK_RED + combatTagString);
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

        TeamInfo newTeam = new TeamInfo(String.valueOf(teamCreateIndex), prefix, suffix);
        teamCreateIndex++;
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