package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.factionactiontracker.FactionActionTracker;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Map;

public class TeamUnMuteCommand {

    @Command(names={ "team unmute", "t unmute", "f unmute", "faction unmute", "fac unmute" }, permissionNode="foxtrot.mutefaction")
    public static void teamUnmute(Player sender, @Param(name="team") Team target) {
        FactionActionTracker.logAction(target, "actions", "Mute: Team mute removed. [Unmuted by: " + sender.getName() + "]");
        Iterator<Map.Entry<String, String>> mutesIterator = TeamMuteCommand.getTeamMutes().entrySet().iterator();

        while (mutesIterator.hasNext()) {
            Map.Entry<String, String> mute = mutesIterator.next();

            if (mute.getValue().equalsIgnoreCase(target.getName())) {
                Player bukkitPlayer = FoxtrotPlugin.getInstance().getServer().getPlayerExact(mute.getKey());

                if (bukkitPlayer != null) {
                    bukkitPlayer.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Your team's mute has been removed!");
                }

                mutesIterator.remove();
            }
        }

        sender.sendMessage(ChatColor.GRAY + "Unmuted the team " + target.getName() + ChatColor.GRAY  + ".");
    }

}