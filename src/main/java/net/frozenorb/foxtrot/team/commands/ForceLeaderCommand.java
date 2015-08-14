package net.frozenorb.foxtrot.team.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ForceLeaderCommand {

    @Command(names={ "ForceLeader" }, permissionNode="foxtrot.forceleader")
    public static void forceLeader(Player sender, @Parameter(name="player", defaultValue="self") UUID player) {
        Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (playerTeam == null) {
            sender.sendMessage(ChatColor.GRAY + "That player is not on a team.");
            return;
        }

        playerTeam.setOwner(player);
        sender.sendMessage(ChatColor.LIGHT_PURPLE + UUIDUtils.name(player) + ChatColor.YELLOW + " is now the owner of " + ChatColor.LIGHT_PURPLE + playerTeam.getName() + ChatColor.YELLOW + ".");
    }

}