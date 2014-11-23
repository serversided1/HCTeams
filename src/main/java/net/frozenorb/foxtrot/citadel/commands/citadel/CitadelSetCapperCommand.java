package net.frozenorb.foxtrot.citadel.commands.citadel;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.citadel.CitadelHandler;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/23/2014.
 */
public class CitadelSetCapperCommand {

    @Command(names={ "citadel setcapper" }, permissionNode="op")
    public static void citadelSetCapper(Player sender, @Param(name="team") Team target, @Param(name="level", defaultValue="2") int level) {
        FoxtrotPlugin.getInstance().getCitadelHandler().setCapper(target.getUniqueId(), level);
        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Set " + ChatColor.GREEN + target.getName() + " (" + target.getUniqueId() + ")" + ChatColor.YELLOW + " as the Citadel capper.");
    }

}