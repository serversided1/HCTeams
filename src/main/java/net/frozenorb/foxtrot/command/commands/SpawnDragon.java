package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macguy8 on 11/1/2014.
 */
public class SpawnDragon extends BaseCommand {

    public SpawnDragon() {
        super("spawndragon");
        setPermissionLevel("foxtrot.spawndragon", "§cYou are not allowed to do this, ya silly goose!");
    }

    @Override
    public void syncExecute() {
        Player p = (Player) sender;

        if (p.getWorld().getEnvironment() != World.Environment.THE_END) {
            p.sendMessage(ChatColor.RED + "You must be in the end.");
            return;
        }

        p.getWorld().spawnCreature(p.getLocation(), EntityType.ENDER_DRAGON);
        p.sendMessage("Spawned enderdragon.");
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
