package net.frozenorb.foxtrot.citadel.listeners;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.citadel.CitadelHandler;
import net.frozenorb.foxtrot.koth.events.KOTHActivatedEvent;
import net.frozenorb.foxtrot.koth.events.KOTHCapturedEvent;
import net.frozenorb.foxtrot.serialization.serializers.ItemStackSerializer;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.bitmask.DTRBitmaskType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by macguy8 on 11/15/2014.
 */
public class CitadelListener implements Listener {

    @EventHandler
    public void onKOTHActivate(KOTHActivatedEvent event) {
        if (event.getKoth().getName().equalsIgnoreCase("Citadel")) {
            FoxtrotPlugin.getInstance().getCitadelHandler().setCapper(null, 0);
        }
    }

    @EventHandler
    public void onKOTHCaptured(KOTHCapturedEvent event) {
        if (event.getKoth().getName().equalsIgnoreCase("Citadel")) {
            Team playerTeam = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(event.getPlayer().getName());

            if (playerTeam != null) {
                FoxtrotPlugin.getInstance().getCitadelHandler().setCapper(playerTeam.getUniqueId(), event.getKoth().getLevel());

                Date townLootable = FoxtrotPlugin.getInstance().getCitadelHandler().getTownLootable();
                Date courtyardLootable = FoxtrotPlugin.getInstance().getCitadelHandler().getCourtyardLootable();

                FoxtrotPlugin.getInstance().getServer().broadcastMessage(CitadelHandler.PREFIX + " " + ChatColor.RED + "CitadelTown " + ChatColor.YELLOW + "is " + ChatColor.DARK_RED + "closed " + ChatColor.YELLOW + "until " + ChatColor.WHITE + (new SimpleDateFormat()).format(townLootable) + ChatColor.YELLOW + ".");
                FoxtrotPlugin.getInstance().getServer().broadcastMessage(CitadelHandler.PREFIX + " " + ChatColor.RED + "CitadelCourtyard " + ChatColor.YELLOW + "is " + ChatColor.DARK_RED + "closed " + ChatColor.YELLOW + "until " + ChatColor.WHITE + (new SimpleDateFormat()).format(courtyardLootable) + ChatColor.YELLOW + ".");
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Team playerTeam = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(event.getPlayer().getName());
        Object capper = FoxtrotPlugin.getInstance().getCitadelHandler().getCapper();

        if (playerTeam != null && capper == playerTeam.getUniqueId()) {
            // Send the message on a delay so other login info (IE the /f who every player runs) doesn't block it out.
            new BukkitRunnable() {

                public void run() {
                    event.getPlayer().sendMessage(CitadelHandler.PREFIX + " " + ChatColor.DARK_GREEN + "Your team currently controls Citadel.");
                }

            }.runTaskLater(FoxtrotPlugin.getInstance(), 1L);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            BlockState blockState = event.getClickedBlock().getState();

            if (blockState instanceof Chest) {
                Chest chest = (Chest) blockState;

                Team team = LandBoard.getInstance().getTeam(event.getClickedBlock().getLocation());

                if (team.getOwner() != null) {
                    return;
                }

                if (team.hasDTRBitmask(DTRBitmaskType.CITADEL_TOWN) || team.hasDTRBitmask(DTRBitmaskType.CITADEL_COURTYARD) || team.hasDTRBitmask(DTRBitmaskType.CITADEL_KEEP)) {
                    int items = 0;

                    for (ItemStack itemStack : chest.getBlockInventory().getContents()) {
                        if (itemStack != null && itemStack.getType() != Material.AIR) {
                            items++;
                        }
                    }

                    if (items != 0) {
                        FoxtrotPlugin.getInstance().getCitadelHandler().getCitadelChests().put(event.getClickedBlock().getLocation(), System.currentTimeMillis() + (1000 * 60 * 60)); // 1 hour
                    }
                }
            }
        }
    }

}