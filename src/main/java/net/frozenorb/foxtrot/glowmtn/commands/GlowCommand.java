package net.frozenorb.foxtrot.glowmtn.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.glowmtn.GlowMountain;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GlowCommand {

    @Command(names = "glow scan", permissionNode = "op")
    public static void glowScan(Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam("glowstone");

        if(team != null) {
            if(!Foxtrot.getInstance().getGlowHandler().hasGlowMountain()) {
                if(team.getClaims().size() > 0) {
                    Foxtrot.getInstance().getGlowHandler().setGlowMountain(new GlowMountain(team.getClaims().get(0)));
                } else {
                    sender.sendMessage(ChatColor.RED + "Error: Cannot scan for glowstone if no land is claimed!");
                    return; // cannot scan a team with no claims..
                }
            }

            Foxtrot.getInstance().getGlowHandler().getGlowMountain().scan();
            Foxtrot.getInstance().getGlowHandler().save(); // save to file :D
        } else {
            sender.sendMessage(ChatColor.RED + "Create the team 'glowstone' and make a claim first!");
        }
    }

    @Command(names = "glow reset", permissionNode = "op")
    public static void glowReset(Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam("glowstone");

        if(team != null) {
            if(!Foxtrot.getInstance().getGlowHandler().hasGlowMountain()) {
                sender.sendMessage(ChatColor.RED + "Error: You need to create the glowmtn first!");
                return;
            }

            Foxtrot.getInstance().getGlowHandler().getGlowMountain().reset();
        } else {
            sender.sendMessage(ChatColor.RED + "Create the team 'glowstone' and make a claim first!");
        }
    }
}
