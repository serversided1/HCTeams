package net.frozenorb.foxtrot.citadel.commands.citadel;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.citadel.CitadelHandler;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CitadelSetCapperCommand {

    @Command(names={ "citadel setcapper" }, permissionNode="op")
    public static void citadelSetCapper(Player sender, @Param(name="team") Team target) {
        FoxtrotPlugin.getInstance().getCitadelHandler().setCapper(target.getUniqueId());
        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Set " + ChatColor.GREEN + target.getName() + ChatColor.YELLOW + " as the Citadel capper.");
    }

}