package net.frozenorb.foxtrot.glowmtn.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.glowmtn.GlowHandler;
import net.frozenorb.foxtrot.glowmtn.GlowMountain;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.*;

public class GlowCommand {

    @Command(names = "glow scan", permission = "op")
    public static void glowScan(Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(GlowHandler.getGlowTeamName());

        if (team != null) {
            if (!Foxtrot.getInstance().getGlowHandler().hasGlowMountain()) {
                if (team.getClaims().size() > 0) {
                    Foxtrot.getInstance().getGlowHandler().setGlowMountain(new GlowMountain(team.getClaims().get(0)));
                } else {
                    sender.sendMessage(RED + "Error: Cannot scan for glowstone if no land is claimed!");
                    return; // cannot scan a team with no claims..
                }
            }

            Foxtrot.getInstance().getGlowHandler().getGlowMountain().scan();
            Foxtrot.getInstance().getGlowHandler().save(); // save to file :D

            sender.sendMessage(GREEN + "[GlowMtn]" + GREEN + " Scanned all glowstone and saved the glow mountain to file!");
        } else {
            sender.sendMessage(RED + "Create the team '" + GlowHandler.getGlowTeamName() + "' and make a claim first!");
        }
    }

    @Command(names = "glow reset", permission = "op")
    public static void glowReset(Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(GlowHandler.getGlowTeamName());

        if (team != null) {
            if (!Foxtrot.getInstance().getGlowHandler().hasGlowMountain()) {
                sender.sendMessage(RED + "Error: You need to create the team, claim, and scan first!");
                return;
            }

            Foxtrot.getInstance().getGlowHandler().getGlowMountain().reset();

            sender.sendMessage(GOLD + "[GlowMtn]" + GREEN + " Reset all glowstone for the glow mountain!");
        } else {
            sender.sendMessage(RED + "Create the team '" + GlowHandler.getGlowTeamName() + "' and make a claim first!");
        }
    }
}
