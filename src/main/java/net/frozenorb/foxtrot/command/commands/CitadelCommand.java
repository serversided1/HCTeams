package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.foxtrot.koth.KOTHHandler;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/15/2014.
 */
public class CitadelCommand {

    @Command(names={ "Citadel" }, permissionNode="")
    public static void citadel(Player sender) {
        String capper = FoxtrotPlugin.getInstance().getCitadelHandler().getCapper();

        if (capper == null) {
            KOTH citadel = KOTHHandler.getKOTH("Citadel");

            if (citadel != null && citadel.isActive()) {
                sender.sendMessage(ChatColor.YELLOW + "Citadel can be captured now.");
            } else {
                sender.sendMessage(ChatColor.YELLOW + "Citadel was not captured last week.");
            }
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Citadel was captured by " + ChatColor.GREEN + capper + ChatColor.YELLOW + ".");
        }
    }

    @Command(names={ "Citadel SetCapper" }, permissionNode="op")
    public static void citadelSetCapper(Player sender, @Param(name="target") Team target) {
        FoxtrotPlugin.getInstance().getCitadelHandler().setCapper(target.getName());
        sender.sendMessage(ChatColor.YELLOW + "Set " + ChatColor.GREEN + target.getFriendlyName() + " (" + target.getName() + ")" + ChatColor.YELLOW + " as the Citadel capper.");
    }

}