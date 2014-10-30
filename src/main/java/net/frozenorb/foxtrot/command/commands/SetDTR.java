package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.BaseCommand;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class SetDTR extends BaseCommand {

    public SetDTR() {
        super("setdtr");
        setPermissionLevel("foxtrot.setdtr", "§cYou are not allowed to do this, ya silly goose!");
    }

    @Override
    public void syncExecute() {
        if (args.length == 2) {
            String teamName = args[0];

            Team t = FoxtrotPlugin.getInstance().getTeamManager().getTeam(teamName);

            if (t == null) {
                sender.sendMessage(ChatColor.RED + "That team doesn't exist!");
                return;
            }

            try {
                double dtr = Double.parseDouble(args[1]);
                t.setDtr(dtr);
                sender.sendMessage(ChatColor.YELLOW + t.getName() + " has a new DTR of: " + dtr);
            } catch (NumberFormatException ex) {
                sender.sendMessage(ex.getMessage());
                return;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "/setdtr <team> <dtr>");
        }

    }

    @Override
    public List<String> getTabCompletions() {
        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            for (Team team : FoxtrotPlugin.getInstance().getTeamManager().getTeams()) {
                list.add(team.getFriendlyName());
            }
            return list;
        }
        return null;
    }
}
