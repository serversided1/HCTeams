package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.BaseCommand;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Forcejoin extends BaseCommand {

    public Forcejoin() {
        super("forcejoin");
    }

    @Override
    public void syncExecute() {
        Player p = (Player) sender;

        if (!p.hasPermission("foxtrot.forcejoin")) {
            p.sendMessage(ChatColor.RED + "You are not allowed to do this.");
            return;
        }

        TeamManager teamManager = FoxtrotPlugin.getInstance().getTeamManager();

        if (args.length > 1) {
            String name = args[1];

            p = Bukkit.getPlayer(name);
            if (p == null) {
                sender.sendMessage(ChatColor.RED + "No such player could be found!");
                return;
            }
        }
        if (args.length > 0) {
            if (teamManager.teamExists(args[0])) {
                Team team = teamManager.getTeam(args[0]);

                if (teamManager.isOnTeam(p.getName())) {
                    if (p.getName().equalsIgnoreCase(sender.getName())) {
                        sender.sendMessage(ChatColor.RED + "Leave your current team before attempting to forcejoin.");
                    } else {
                        sender.sendMessage(ChatColor.RED + "That player needs to leave their current team first!");
                    }
                    return;
                }

                team.addMember(p.getName());
                teamManager.setTeam(p.getName(), team);
                p.sendMessage(ChatColor.GREEN + "You are now a member of §b" + team.getFriendlyName() + "§a!");

                if (!p.getName().equalsIgnoreCase(sender.getName())) {
                    sender.sendMessage("§aPlayer added to team!");
                }
            }
        } else
            sender.sendMessage(ChatColor.RED + "/forcejoin <tName> [player]");
    }

    @Override
    public List<String> getTabCompletions() {
        ArrayList<String> teamNames = new ArrayList<String>();
        for (Team tem : FoxtrotPlugin.getInstance().getTeamManager().getTeams()) {
            teamNames.add(tem.getFriendlyName());
        }
        return teamNames;
    }
}
