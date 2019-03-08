package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.upgrades.menu.TeamUpgradesMenu;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamUpgradesCommand {

    @Command(names={ "team upgrade", "team upgrades", "t upgrade", "t upgrades", "faction upgrade", "faction upgrades", "f upgrade", "f upgrades" }, permission="")
    public static void teamUpgrades(Player sender) {
        if (!(Foxtrot.getInstance().getMapHandler().isKitMap() || Foxtrot.getInstance().getServerHandler().isVeltKitMap())) {
            sender.sendMessage(ChatColor.RED + "You can't use that command on this server.");
            return;
        }

        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team co-leaders (and above) can do this.");
            return;
        }

        new TeamUpgradesMenu().openMenu(sender);
    }

}