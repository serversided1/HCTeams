package net.frozenorb.foxtrot.conquest.game;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.conquest.ConquestHandler;
import net.frozenorb.foxtrot.conquest.enums.ConquestCapzone;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.foxtrot.koth.events.KOTHCapturedEvent;
import net.frozenorb.foxtrot.koth.events.KOTHControlLostEvent;
import net.frozenorb.foxtrot.koth.events.KOTHControlTickEvent;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ConquestGame implements Listener {

    @Getter private LinkedHashMap<ObjectId, Integer> teamPoints = new LinkedHashMap<>();

    public ConquestGame() {
        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(this, Foxtrot.getInstance());

        for (KOTH koth : Foxtrot.getInstance().getKOTHHandler().getKOTHs()) {
            if (koth.getName().startsWith(ConquestHandler.KOTH_NAME_PREFIX)) {
                if (!koth.isHidden()) {
                    koth.setHidden(true);
                }

                if (koth.getCapTime() != ConquestHandler.TIME_TO_CAP) {
                    koth.setCapTime(ConquestHandler.TIME_TO_CAP);
                }

                koth.activate();
            }
        }

        Foxtrot.getInstance().getServer().broadcastMessage(ConquestHandler.PREFIX + " " + ChatColor.GOLD + "Conquest has started! Use /conquest for more information.");
        Foxtrot.getInstance().getConquestHandler().setGame(this);
    }

    public void endGame(final Team winner) {
        if (winner == null) {
            Foxtrot.getInstance().getServer().broadcastMessage(ConquestHandler.PREFIX + " " + ChatColor.GOLD + "Conquest has ended.");
        } else {
            Foxtrot.getInstance().getServer().broadcastMessage(ConquestHandler.PREFIX + " " + ChatColor.GOLD.toString() + ChatColor.BOLD + winner.getName() + ChatColor.GOLD + " has won Conquest!");
        }

        HandlerList.unregisterAll(this);
        Foxtrot.getInstance().getConquestHandler().setGame(null);
    }

    @EventHandler
    public void onKOTHCaptured(final KOTHCapturedEvent event) {
        if (!event.getKOTH().getName().startsWith(ConquestHandler.KOTH_NAME_PREFIX)) {
            return;
        }

        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());
        ConquestCapzone capzone = ConquestCapzone.valueOf(event.getKOTH().getName().replace(ConquestHandler.KOTH_NAME_PREFIX, "").toUpperCase());

        if (team == null) {
            return;
        }

        if (teamPoints.containsKey(team.getUniqueId())) {
            teamPoints.put(team.getUniqueId(), teamPoints.get(team.getUniqueId()) + 1);
        } else {
            teamPoints.put(team.getUniqueId(), 1);
        }

        teamPoints = sortByValues(teamPoints);
        Foxtrot.getInstance().getServer().broadcastMessage(ConquestHandler.PREFIX + " " + ChatColor.GOLD + team.getName() + ChatColor.GOLD + " captured " + capzone.getColor() + capzone.getName() + ChatColor.GOLD + " and earned a point!" + ChatColor.AQUA + " (" + teamPoints.get(team.getUniqueId()) + "/" + ConquestHandler.POINTS_TO_WIN + ")");

        if (teamPoints.get(team.getUniqueId()) >= ConquestHandler.POINTS_TO_WIN) {
            endGame(team);
        } else {
            new BukkitRunnable() {

                public void run() {
                    if (Foxtrot.getInstance().getConquestHandler().getGame() != null) {
                        event.getKOTH().activate();
                    }
                }

            }.runTaskLater(Foxtrot.getInstance(), 10L);
        }
    }

    @EventHandler
    public void onKOTHControlLost(KOTHControlLostEvent event) {
        if (!event.getKOTH().getName().startsWith(ConquestHandler.KOTH_NAME_PREFIX)) {
            return;
        }

        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(UUIDUtils.uuid(event.getKOTH().getCurrentCapper()));
        ConquestCapzone capzone = ConquestCapzone.valueOf(event.getKOTH().getName().replace(ConquestHandler.KOTH_NAME_PREFIX, "").toUpperCase());

        if (team == null) {
            return;
        }

        team.sendMessage(ConquestHandler.PREFIX + ChatColor.GOLD + " " + event.getKOTH().getCurrentCapper() + " was knocked off of " + capzone.getColor() + capzone.getName() + ChatColor.GOLD + "!");
    }
    @EventHandler
    public void onKOTHControlTick(KOTHControlTickEvent event) {
        if (!event.getKOTH().getName().startsWith(ConquestHandler.KOTH_NAME_PREFIX) || event.getKOTH().getRemainingCapTime() % 5 != 0) {
            return;
        }

        ConquestCapzone capzone = ConquestCapzone.valueOf(event.getKOTH().getName().replace(ConquestHandler.KOTH_NAME_PREFIX, "").toUpperCase());
        Player capper = Foxtrot.getInstance().getServer().getPlayerExact(event.getKOTH().getCurrentCapper());

        if (capper != null) {
            capper.sendMessage(ConquestHandler.PREFIX + " " + ChatColor.GOLD + "Attempting to capture " + capzone.getColor() + capzone.getName() + ChatColor.GOLD + "!" + ChatColor.AQUA + " (" + event.getKOTH().getRemainingCapTime() + "s)");
        }
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getEntity());

        if (team == null || !teamPoints.containsKey(team.getUniqueId())) {
            return;
        }

        teamPoints.put(team.getUniqueId(), Math.max(0, teamPoints.get(team.getUniqueId()) - ConquestHandler.POINTS_DEATH_PENALTY));
        teamPoints = sortByValues(teamPoints);
        team.sendMessage(ConquestHandler.PREFIX + ChatColor.GOLD + " Your team has lost " + ConquestHandler.POINTS_DEATH_PENALTY + " points because of " + event.getEntity().getName() + "'s death!" + ChatColor.AQUA + " (" + teamPoints.get(team.getUniqueId()) + "/" + ConquestHandler.POINTS_TO_WIN + ")");
    }

    private static LinkedHashMap<ObjectId, Integer> sortByValues(Map<ObjectId, Integer> map) {
        LinkedList<Map.Entry<ObjectId, Integer>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        LinkedHashMap<ObjectId, Integer> sortedHashMap = new LinkedHashMap<>();
        Iterator<Map.Entry<ObjectId, Integer>> iterator = list.iterator();

        while (iterator.hasNext()) {
            java.util.Map.Entry<ObjectId, Integer> entry = iterator.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }

        return sortedHashMap;
    }

}