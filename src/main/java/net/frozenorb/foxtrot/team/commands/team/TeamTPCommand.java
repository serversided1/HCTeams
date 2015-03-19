package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamTPCommand {

    @Command(names={ "team tp", "t tp", "f tp", "faction tp", "fac tp" }, permissionNode="worldedit.*")
    public static void teamTP(Player sender, @Parameter(name="team", defaultValue="self") Team target) {
        if (target.getHQ() != null) {
            sender.sendMessage(ChatColor.YELLOW + "Teleported to " + ChatColor.LIGHT_PURPLE + target.getName() + ChatColor.YELLOW + "'s HQ.");
            sender.teleport(target.getHQ());
        } else if (target.getClaims().size() != 0) {
            sender.sendMessage(ChatColor.YELLOW + "Teleported to " + ChatColor.LIGHT_PURPLE + target.getName() + ChatColor.YELLOW + "'s claim.");
            sender.teleport(target.getClaims().get(0).getMaximumPoint().add(0, 100, 0));
        } else {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + target.getName() + ChatColor.YELLOW + " doesn't have a HQ or any claims.");
        }
    }

}