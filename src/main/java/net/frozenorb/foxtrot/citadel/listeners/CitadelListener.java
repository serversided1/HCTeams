package net.frozenorb.foxtrot.citadel.listeners;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.citadel.CitadelHandler;
import net.frozenorb.foxtrot.citadel.events.CitadelActivatedEvent;
import net.frozenorb.foxtrot.citadel.events.CitadelCapturedEvent;
import net.frozenorb.foxtrot.koth.events.KOTHActivatedEvent;
import net.frozenorb.foxtrot.koth.events.KOTHCapturedEvent;
import net.frozenorb.foxtrot.raffle.enums.RaffleAchievement;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by macguy8 on 11/15/2014.
 */
public class CitadelListener implements Listener {

    @EventHandler
    public void onKOTHActivate(KOTHActivatedEvent event) {
        if (event.getKoth().getName().equalsIgnoreCase("Citadel")) {
            FoxtrotPlugin.getInstance().getServer().getPluginManager().callEvent(new CitadelActivatedEvent());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onKOTHCaptured(KOTHCapturedEvent event) {
        if (event.getKoth().getName().equalsIgnoreCase("Citadel")) {
            // Raffle
            FoxtrotPlugin.getInstance().getRaffleHandler().giveRaffleAchievementProgress(event.getPlayer(), RaffleAchievement.CITADEL, 1);

            Team playerTeam = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(event.getPlayer().getName());

            if (playerTeam != null) {
                FoxtrotPlugin.getInstance().getCitadelHandler().setCapper(playerTeam.getUniqueId(), event.getKoth().getLevel());
            }
        }
    }

    @EventHandler
    public void onCitadelActivated(CitadelActivatedEvent event) {
        FoxtrotPlugin.getInstance().getCitadelHandler().setCapper(null, 0);
    }

    @EventHandler
    public void onCitadelCaptured(CitadelCapturedEvent event) {
        Date townLootable = FoxtrotPlugin.getInstance().getCitadelHandler().getTownLootable();
        Date courtyardLootable = FoxtrotPlugin.getInstance().getCitadelHandler().getCourtyardLootable();

        FoxtrotPlugin.getInstance().getServer().broadcastMessage(CitadelHandler.PREFIX + " " + ChatColor.RED + "CitadelTown " + ChatColor.YELLOW + "is " + ChatColor.DARK_RED + "closed " + ChatColor.YELLOW + "until " + ChatColor.WHITE + (new SimpleDateFormat()).format(townLootable) + ChatColor.YELLOW + ".");
        FoxtrotPlugin.getInstance().getServer().broadcastMessage(CitadelHandler.PREFIX + " " + ChatColor.RED + "CitadelCourtyard " + ChatColor.YELLOW + "is " + ChatColor.DARK_RED + "closed " + ChatColor.YELLOW + "until " + ChatColor.WHITE + (new SimpleDateFormat()).format(courtyardLootable) + ChatColor.YELLOW + ".");
    }

    @EventHandler(priority=EventPriority.MONITOR) // The monitor is here so we get called 'after' most join events.
    public void onPlayerJoin(PlayerJoinEvent event) {
        Team playerTeam = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(event.getPlayer().getName());
        Object capper = FoxtrotPlugin.getInstance().getCitadelHandler().getCapper();

        if (playerTeam != null && capper == playerTeam.getUniqueId()) {
            event.getPlayer().sendMessage(CitadelHandler.PREFIX + " " + ChatColor.DARK_GREEN + "Your team currently controls Citadel.");
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        BlockState blockState = event.getClickedBlock().getState();

        if (!(blockState instanceof Chest)) {
            return;
        }

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
                FoxtrotPlugin.getInstance().getCitadelHandler().getCitadelChests().put(event.getClickedBlock().getLocation(), System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)); // 1 hour
            }
        }
    }

}