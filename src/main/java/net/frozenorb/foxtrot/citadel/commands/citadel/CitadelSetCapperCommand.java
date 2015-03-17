package net.frozenorb.foxtrot.citadel.commands.citadel;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.citadel.CitadelHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CitadelSetCapperCommand {

    @Command(names={ "citadel setcapper" }, permissionNode="op")
    public static void citadelSetCapper(Player sender, @Parameter(name="team") Team target) {
        Foxtrot.getInstance().getCitadelHandler().setCapper(target.getUniqueId());
        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Set " + ChatColor.GREEN + target.getName() + ChatColor.YELLOW + " as the Citadel capper.");
    }

}