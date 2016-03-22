package net.frozenorb.foxtrot.glowmtn.listeners;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.glowmtn.GlowHandler;
import net.frozenorb.foxtrot.glowmtn.GlowMountain;
import net.frozenorb.foxtrot.packetborder.PacketBorder;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.qlib.event.HourEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.BlockVector;

import static org.bukkit.ChatColor.*;

public class GlowListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGlowstoneBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        Team teamAt = LandBoard.getInstance().getTeam(location);
        GlowHandler glow = Foxtrot.getInstance().getGlowHandler();

        if (teamAt != null && glow.hasGlowMountain() && teamAt.getName().equals(GlowHandler.getGlowTeamName())) {
            if (event.getBlock().getType() == Material.GLOWSTONE) {
                GlowMountain mtn = glow.getGlowMountain();
                if (mtn.getGlowstone().contains(location.toVector().toBlockVector())) {
                    event.setCancelled(false); // allow them to mine the glowstone
                    mtn.getMined().add(location.toVector().toBlockVector());

                    // Let's announce when a glow mountain is a half and fully mined
                    double total = glow.getGlowMountain().getGlowstone().size();
                    double mined = glow.getGlowMountain().getMined().size();
                    double half; // 50% of the total glowstone

                    if(total % 2 == 0) {
                        half = total / 2;
                    } else {
                        half = Math.ceil(total / 2) - 1;
                    }

                    if (total - mined == half) {
                        Bukkit.broadcastMessage(GOLD + "[GlowMountain]" + AQUA + " 50% of Glowstone has been mined!");
                    } else if (total - mined == 0) {
                        Bukkit.broadcastMessage(GOLD + "[GlowMountain]" + RED + "  All Glowstone has been mined!");
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onGlowstonePlace(BlockPlaceEvent event) {
        if (event.getPlayer().isOp() && event.getBlock().getType() == Material.GLOWSTONE) {
            Location location = event.getBlock().getLocation();
            Team teamAt = LandBoard.getInstance().getTeam(location);
            GlowHandler glow = Foxtrot.getInstance().getGlowHandler();

            if (teamAt != null && glow.hasGlowMountain() && teamAt.getName().equals(GlowHandler.getGlowTeamName())) {
                BlockVector vector = location.toVector().toBlockVector();

                // Only "add" a new glowstone if there wasn't already a glowstone there
                if(!glow.getGlowMountain().getGlowstone().contains(vector)) {
                    glow.getGlowMountain().getGlowstone().add(vector);
                    event.getPlayer().sendMessage(GOLD + "[GlowMountain]" + GREEN + " Manually added a new glowstone to the claim!");
                    glow.save(); // Save updated glow mountain to file
                } else {
                    event.getPlayer().sendMessage(GOLD + "[GlowMountain]" + GREEN + " A Glowstone already existed there, it'll regen on reset :P ");
                }
            }
        }
    }

    @EventHandler
    public void onHour(HourEvent event) {
        // Every other odd hour
        GlowHandler handler = Foxtrot.getInstance().getGlowHandler();
        if (event.getHour() % 2 == 1 && handler.hasGlowMountain()) {
            handler.getGlowMountain().reset(); // reset all glowstone every other odd hour (offsetting koths)

            Bukkit.broadcastMessage(GOLD + "[GlowMountain]" + GREEN + " All glowstone has been reset!");
        }
    }
}
