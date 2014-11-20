package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.foxtrot.koth.KOTHHandler;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by macguy8 on 11/15/2014.
 */
public class CitadelCommand {

    @Command(names={ "Citadel" }, permissionNode="")
    public static void citadel(Player sender) {
        Team capper = FoxtrotPlugin.getInstance().getTeamHandler().getTeam(FoxtrotPlugin.getInstance().getCitadelHandler().getCapper());

        if (capper == null) {
            KOTH citadel = KOTHHandler.getKOTH("Citadel");

            if (citadel != null && citadel.isActive()) {
                sender.sendMessage(ChatColor.YELLOW + "Citadel can be captured now.");
            } else {
                sender.sendMessage(ChatColor.YELLOW + "Citadel was not captured last week.");
            }
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Citadel was captured by " + ChatColor.GREEN + capper.getFriendlyName() + ChatColor.YELLOW + ".");
        }

        Date townLootable = FoxtrotPlugin.getInstance().getCitadelHandler().getTownLootable();
        Date courtyardLootable = FoxtrotPlugin.getInstance().getCitadelHandler().getCourtyardLootable();

        sender.sendMessage(ChatColor.GOLD + "Citadel Town: " + ChatColor.WHITE + "Lootable " + (townLootable.before(new Date()) ? "now" : "at " + (new SimpleDateFormat()).format(townLootable)) + ".");
        sender.sendMessage(ChatColor.GOLD + "Citadel Courtyard: " + ChatColor.WHITE + "Lootable " + (courtyardLootable.before(new Date()) ? "now" : "at " + (new SimpleDateFormat()).format(courtyardLootable)) + ".");
        sender.sendMessage(ChatColor.GOLD + "Citadel Lootable: " + ChatColor.WHITE + "Lootable by " + (capper == null ? "no one" : capper.getFriendlyName()) + ".");
    }

    @Command(names={ "Citadel SetCapper" }, permissionNode="op")
    public static void citadelSetCapper(Player sender, @Param(name="target") Team target, @Param(name="level", defaultValue="2") int level) {
        FoxtrotPlugin.getInstance().getCitadelHandler().setCapper(target.getUniqueId(), level);
        sender.sendMessage(ChatColor.YELLOW + "Set " + ChatColor.GREEN + target.getFriendlyName() + " (" + target.getUniqueId() + ")" + ChatColor.YELLOW + " as the Citadel capper.");
    }

}