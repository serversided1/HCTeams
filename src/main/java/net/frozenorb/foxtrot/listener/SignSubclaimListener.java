package net.frozenorb.foxtrot.listener;

import com.google.common.collect.ImmutableSet;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashSet;
import java.util.Set;

public class SignSubclaimListener implements Listener {

    public static final Set<BlockFace> OUTSIDE_FACES = ImmutableSet.of(BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH);
    public static final String SUBCLAIM_IDENTIFIER = ChatColor.YELLOW.toString() + ChatColor.BOLD + "[Subclaim]";
    public static final String NO_ACCESS = ChatColor.RED + "You do not have access to this chest subclaim!";

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onSignUpdate(SignChangeEvent event) {
        if (!event.getLine(0).toLowerCase().contains("subclaim")) {
            return;
        }

        Team playerTeam = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(event.getPlayer().getName());
        Sign sign = (Sign) event.getBlock().getState();

        if (playerTeam == null) {
            event.getBlock().breakNaturally();
            event.getPlayer().sendMessage(ChatColor.RED + "You are not on a team!");
            return;
        }

        if (!playerTeam.ownsLocation(sign.getLocation())) {
            event.getBlock().breakNaturally();
            event.getPlayer().sendMessage(ChatColor.RED + "You do not own this land!");
            return;
        }

        BlockFace attachedFace = ((org.bukkit.material.Sign) sign.getData()).getAttachedFace();
        Block attachedTo = event.getBlock().getRelative(attachedFace);

        if (subclaimSigns(attachedTo).size() != 0) {
            event.getBlock().breakNaturally();
            event.getPlayer().sendMessage(ChatColor.RED + "This chest is already subclaimed!");
            return;
        }

        /*if (!playerTeam.isCaptain(event.getPlayer().getName()) && !playerTeam.isOwner(event.getPlayer().getName())) {
            event.getBlock().breakNaturally();
            event.getPlayer().sendMessage(ChatColor.RED + "You must be a team captain to be able to do this!");
            return;
        }*/

        boolean found = false;

        for (int i = 1; i <= 3; i++) {
            if (sign.getLine(i) != null && sign.getLine(i).equalsIgnoreCase(event.getPlayer().getName())) {
                found = true;
                break;
            }
        }

        if (!found && !(playerTeam.isOwner(event.getPlayer().getName()) || playerTeam.isCaptain(event.getPlayer().getName()))) {
            if (event.getPlayer().getName().length() > 15) {
                event.getPlayer().sendMessage("§cYour name is too long for sign subclaims. Consider changing your username.");
                return;
            }
            event.getPlayer().sendMessage(ChatColor.YELLOW + "It appears you've forgotten to add yourself to this subclaim! You're not an officer, so this would lock you out of your subclaim.");
            event.getPlayer().sendMessage(ChatColor.YELLOW + "We've automatically added you to your subclaim.");
            event.setLine(1, event.getPlayer().getName());
        }

        event.setLine(0, SUBCLAIM_IDENTIFIER);
        event.getPlayer().sendMessage(ChatColor.GREEN + "Sign subclaim created!");
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBlockBreakChest(BlockBreakEvent event) {
        if (!(event.getBlock().getState() instanceof Chest) || FoxtrotPlugin.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getBlock().getLocation()) || FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        Team owningTeam = LandBoard.getInstance().getTeam(event.getBlock().getLocation());

        subclaimSigns(event.getBlock()).forEach(sign -> {

            if (!(owningTeam.isOwner(event.getPlayer().getName()) || owningTeam.isCaptain(event.getPlayer().getName()))) {
                event.getPlayer().sendMessage(NO_ACCESS);
                event.setCancelled(true);
            }

        });
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBlockBreakSign(BlockBreakEvent event) {
        if (!(event.getBlock().getState() instanceof Sign) || FoxtrotPlugin.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getBlock().getLocation()) || FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        Team owningTeam = LandBoard.getInstance().getTeam(event.getBlock().getLocation());
        Sign sign = (Sign) event.getBlock().getState();

        if (sign.getLine(0).equals(SUBCLAIM_IDENTIFIER)) {
            boolean canAccess = owningTeam.isOwner(event.getPlayer().getName()) || owningTeam.isCaptain(event.getPlayer().getName());

            for (int i = 0; i <= 3; i++) {
                if (sign.getLine(i) != null && sign.getLine(i).equalsIgnoreCase(event.getPlayer().getName())) {
                    canAccess = true;
                    break;
                }
            }

            if (!canAccess) {
                event.getPlayer().sendMessage(NO_ACCESS);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || !(event.getClickedBlock().getState() instanceof Chest) || FoxtrotPlugin.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getClickedBlock().getLocation()) || FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        // Will never be null, we check isUnclaimedOrRaidable above.
        Team owningTeam = LandBoard.getInstance().getTeam(event.getClickedBlock().getLocation());

        subclaimSigns(event.getClickedBlock()).forEach(sign -> {

            boolean canAccess = owningTeam.isOwner(event.getPlayer().getName()) || owningTeam.isCaptain(event.getPlayer().getName());

            for (int i = 0; i <= 3; i++) {
                if (sign.getLine(i) != null && sign.getLine(i).equalsIgnoreCase(event.getPlayer().getName())) {
                    canAccess = true;
                    break;
                }
            }

            if (!canAccess) {
                event.getPlayer().sendMessage(NO_ACCESS);
                event.setCancelled(true);
            }

        });
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (event.getSource().getType() == InventoryType.CHEST && event.getDestination().getType() == InventoryType.HOPPER) {
            InventoryHolder inventoryHolder = event.getSource().getHolder();
            Block moveBlock;

            // Special-case double chests
            if (inventoryHolder instanceof DoubleChest) {
                moveBlock = ((DoubleChest) inventoryHolder).getLocation().getBlock();
            } else {
                moveBlock = ((BlockState) inventoryHolder).getBlock();
            }

            subclaimSigns(moveBlock).forEach(sign -> event.setCancelled(true));
        }
    }

    public static Set<Sign> subclaimSigns(Block check) {
        Set<Sign> signs = new HashSet<Sign>();

        for (BlockFace blockFace : OUTSIDE_FACES) {
            Block relBlock = check.getRelative(blockFace);

            if (relBlock.getType() == check.getType()) {
                subclaimSigns0(signs, relBlock);
            }
        }

        subclaimSigns0(signs, check);

        return (signs);
    }

    public static void subclaimSigns0(Set<Sign> signs, Block check) {
        for (BlockFace blockFace : OUTSIDE_FACES) {
            Block relBlock = check.getRelative(blockFace);

            if (relBlock.getType() == Material.WALL_SIGN || relBlock.getType() == Material.SIGN) {
                Sign sign = (Sign) relBlock.getState();

                if (sign.getLine(0).equals(SUBCLAIM_IDENTIFIER)) {
                    signs.add(sign);
                }
            }
        }
    }

}