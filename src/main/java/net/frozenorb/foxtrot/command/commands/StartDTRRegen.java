package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.BaseCommand;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macguy8 on 10/31/2014.
 */
public class StartDTRRegen extends BaseCommand {

    public StartDTRRegen() {
        super("startdtregen");
        setPermissionLevel("foxtrot.startdtrregen", "§cYou are not allowed to do this, ya silly goose!");
    }

    @Override
    public void syncExecute() {
        if (args.length == 1) {
            String teamName = args[0];

            net.frozenorb.foxtrot.team.Team t = FoxtrotPlugin.getInstance().getTeamManager().getTeam(teamName);

            if (t == null) {
                sender.sendMessage(ChatColor.RED + "That team doesn't exist!");
                return;
            }

            // We're adding 5s so DTRHandler's runnable has a time to pick up on it.
            t.setDeathCooldown(System.currentTimeMillis() + 5000L);
            t.setRaidableCooldown(System.currentTimeMillis() + 5000L);
            sender.sendMessage(ChatColor.GRAY + t.getFriendlyName() + ChatColor.GRAY + " is now regenerating DTR.");
        } else {
            sender.sendMessage(ChatColor.RED + "/startdtrregen <team>");
        }

    }

    @Override
    public List<String> getTabCompletions() {
        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            for (net.frozenorb.foxtrot.team.Team team : FoxtrotPlugin.getInstance().getTeamManager().getTeams()) {
                list.add(team.getFriendlyName());
            }
            return list;
        }
        return null;
    }
}
