package net.frozenorb.foxtrot.glowmtn.listeners;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.glowmtn.GlowHandler;
import net.frozenorb.foxtrot.glowmtn.GlowMountain;
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

import java.util.HashSet;
import java.util.Set;

import static org.bukkit.ChatColor.*;

public class GlowListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onGlowstoneBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        Team teamAt = LandBoard.getInstance().getTeam(location);
        GlowHandler glow = Foxtrot.getInstance().getGlowHandler();

        if (teamAt != null && glow.hasGlowMountain() && teamAt.getName().equals("glowmtn") && teamAt.getOwner() == null) {
            if (event.getBlock().getType() == Material.GLOWSTONE) {
                GlowMountain mtn = glow.getGlowMountain();
                if (mtn.getGlowstone().contains(location.toVector().toBlockVector())) {
                    event.setCancelled(false); // allow them to mine the glowstone
                    Set<BlockVector> mined;

                    if (mtn.getMinedMap().containsKey(event.getPlayer().getUniqueId())) {
                        mined = mtn.getMinedMap().get(event.getPlayer().getUniqueId());
                    } else {
                        mined = new HashSet<>(); // user hasnt mined any glowstone yet, start a new set
                    }

                    mined.add(location.toVector().toBlockVector());
                    glow.getGlowMountain().getMinedMap().put(event.getPlayer().getUniqueId(), mined);
                }
            }

            // Let's announce when a glow mountain is a half and fully mined
            double total = glow.getGlowMountain().getGlowstone().size();
            double mined = glow.getGlowMountain().getMinedMap().size();
            if (total / mined == 0.50D) {
                Bukkit.broadcastMessage(GOLD + "[GlowMountain]" + AQUA + " 50% of Glowstone has been mined!");
            } else if (total / mined == 0) {
                Bukkit.broadcastMessage(GOLD + "[GlowMountain]" + RED + "  All Glowstone has been mined!");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onGlowstonePlace(BlockPlaceEvent event) {
        if (event.getPlayer().isOp() && event.getBlock().getType() == Material.GLOWSTONE) {
            Location location = event.getBlock().getLocation();
            Team teamAt = LandBoard.getInstance().getTeam(location);
            GlowHandler glow = Foxtrot.getInstance().getGlowHandler();

            if (teamAt != null && glow.hasGlowMountain() && teamAt.getName().equals("glowmtn") && teamAt.getOwner() == null) {
                glow.getGlowMountain().getGlowstone().add(location.toVector().toBlockVector());
                event.getPlayer().sendMessage(GOLD + "[GlowMountain] Manually added a new glowstone to the claim!");
                glow.save(); // Save updated glow mountain to file
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
