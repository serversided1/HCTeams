package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.factionactiontracker.FactionActionTracker;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Created by macguy8 on 11/5/2014.
 */
public class TeamShadowMuteCommand {

    public static HashMap<String, String> factionShadowMutes = new HashMap<String, String>();

    @Command(names={ "team shadowmute", "t shadowmute", "f shadowmute", "faction shadowmute", "fac shadowmute" }, permissionNode="foxtrot.mutefaction")
    public static void teamShadowMuteFaction(Player sender, @Param(name="team") final Team target, @Param(name="minutes") String time) {
        int timeSeconds = Integer.valueOf(time) * 60;

        for (String player : target.getMembers()) {
            factionShadowMutes.put(player, target.getName());
        }

        FactionActionTracker.logAction(target, "actions", "Mute: Faction shadowmute added. [Duration: " + time + ", Muted by: " + sender.getName() + "]");

        new BukkitRunnable() {

            public void run() {
                FactionActionTracker.logAction(target, "actions", "Mute: Faction shadowmute expired.");

                Iterator<java.util.Map.Entry<String, String>> mutesIterator = factionShadowMutes.entrySet().iterator();

                while (mutesIterator.hasNext()) {
                    java.util.Map.Entry<String, String> mute = mutesIterator.next();

                    if (mute.getValue().equalsIgnoreCase(target.getName())) {
                        mutesIterator.remove();
                    }
                }
            }

        }.runTaskLater(FoxtrotPlugin.getInstance(), timeSeconds * 20L);

        sender.sendMessage(ChatColor.GRAY + "Shadow muted the faction " + target.getName() + ChatColor.GRAY + " for " + TimeUtils.getDurationBreakdown(timeSeconds * 1000L) + ".");
    }

    @Command(names={ "team unshadowmute", "t unshadowmute", "f unshadowmute", "faction unshadowmute", "fac unshadowmute" }, permissionNode="foxtrot.mutefaction")
    public static void teamUnShadowmuteFaction(Player sender, @Param(name="team") final Team target) {
        FactionActionTracker.logAction(target, "actions", "Mute: Faction shadowmute removed. [Unmuted by: " + sender.getName() + "]");
        Iterator<java.util.Map.Entry<String, String>> mutesIterator = factionShadowMutes.entrySet().iterator();

        while (mutesIterator.hasNext()) {
            java.util.Map.Entry<String, String> mute = mutesIterator.next();

            if (mute.getValue().equalsIgnoreCase(target.getName())) {
                mutesIterator.remove();
            }
        }

        sender.sendMessage(ChatColor.GRAY + "Un-shadowmuted the faction " + target.getName() + ChatColor.GRAY  + ".");
    }

}