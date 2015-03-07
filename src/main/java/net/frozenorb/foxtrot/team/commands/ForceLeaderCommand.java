package net.frozenorb.foxtrot.team.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.UUIDUtils;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ForceLeaderCommand {

    @Command(names={ "ForceLeader" }, permissionNode="foxtrot.forceleader")
    public static void forceLeader(Player sender, @Parameter(name="Team", defaultValue="self") Team team,  @Parameter(name="Target", defaultValue="self") String target) {
        if (target.equals("self")) {
            target = sender.getName();
        } else if (target.equals("null")) {
            target = null;
        }

        UUID targetUUID = UUIDUtils.uuid(target);

        if (target != null && !FoxtrotPlugin.getInstance().getPlaytimeMap().hasPlayed(targetUUID)) {
            sender.sendMessage(ChatColor.RED + "That player has never played here!");
        } else {
            if (target != null && !team.isMember(targetUUID)) {
                sender.sendMessage(ChatColor.RED + "That player is not a member of " + team.getName() + ".");
                return;
            }

            team.setOwner(targetUUID);
            sender.sendMessage(ChatColor.LIGHT_PURPLE + target + ChatColor.YELLOW + " is now the owner of " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + ".");
        }
    }

}