package net.frozenorb.foxtrot.citadel.commands.citadel;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.citadel.CitadelHandler;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CitadelCommand {

    @Command(names={ "citadel" }, permissionNode="")
    public static void citadel(Player sender) {
        Team capper = FoxtrotPlugin.getInstance().getTeamHandler().getTeam(FoxtrotPlugin.getInstance().getCitadelHandler().getCapper());

        if (capper == null) {
            KOTH citadel = FoxtrotPlugin.getInstance().getKOTHHandler().getKOTH("Citadel");

            if (citadel != null && citadel.isActive()) {
                sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Citadel can be captured now.");
            } else {
                sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Citadel was not captured last week.");
            }
        } else {
            sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Citadel was captured by " + ChatColor.GREEN + capper.getName() + ChatColor.YELLOW + ".");
        }

        Date lootable = FoxtrotPlugin.getInstance().getCitadelHandler().getLootable();
        sender.sendMessage(ChatColor.GOLD + "Citadel: " + ChatColor.WHITE + "Lootable " + (lootable.before(new Date()) ? "now" : "at " + (new SimpleDateFormat()).format(lootable) + (capper == null ? "." : ", and lootable now by " + capper.getName() + ".")));
    }

}