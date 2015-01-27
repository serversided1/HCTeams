package net.frozenorb.foxtrot.team.claims;

import lombok.Getter;
import net.frozenorb.Utilities.DataSystem.Regioning.CuboidRegion;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.mBasic.Utilities.ItemDb;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class VisualSubclaimMap implements Listener {

    public static final int MAP_RADIUS = 50;
    public static final Material[] MAP_MATERIALS = { Material.DIAMOND_BLOCK,
            Material.GOLD_BLOCK, Material.LOG, Material.BRICK, Material.WOOD,
            Material.REDSTONE_BLOCK, Material.LAPIS_BLOCK, Material.CHEST,
            Material.MELON_BLOCK, Material.STONE, Material.COBBLESTONE,
            Material.COAL_BLOCK, Material.DIAMOND_ORE, Material.COAL_ORE,
            Material.GOLD_ORE, Material.REDSTONE_ORE, Material.FURNACE };

    @Getter private static Map<String, VisualSubclaimMap> currentMaps = new HashMap<String, VisualSubclaimMap>();

    @Getter private Player player;
    @Getter private List<Location> blockChanges = new ArrayList<Location>();

    public VisualSubclaimMap(Player player) {
        this.player = player;
    }

    public void draw(boolean silent) {
        if (currentMaps.containsKey(player.getName())) {
            currentMaps.get(player.getName()).cancel();

            if (!silent) {
                player.sendMessage(ChatColor.YELLOW + "Subclaims have been hidden!");
            }

            return;
        }

        currentMaps.put(player.getName(), this);
        FoxtrotPlugin.getInstance().getServer().getPluginManager().registerEvents(this, FoxtrotPlugin.getInstance());
        Team senderTeam = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());

        if (senderTeam == null) {
            if (!silent) {
                player.sendMessage(ChatColor.RED + "You must be on a team to view subclaims.");
            }

            cancel();
            return;
        }

        int subclaimIteration = 0;
        Map<Subclaim, Material> subclaimMaterialMap = new HashMap<Subclaim, Material>();

        for (Subclaim subclaim : senderTeam.getSubclaims()) {
            if (subclaim.getLoc1().distanceSquared(player.getLocation()) > MAP_RADIUS * MAP_RADIUS && subclaim.getLoc2().distanceSquared(player.getLocation()) > MAP_RADIUS * MAP_RADIUS) {
                continue;
            }

            Material mat = getMaterial(subclaimIteration);
            subclaimIteration++;

            subclaimMaterialMap.put(subclaim, mat);
            drawSubclaim(subclaim, mat);
        }

        if (subclaimIteration == 0) {
            if (!silent) {
                player.sendMessage(ChatColor.YELLOW + "There are no subclaims within " + MAP_RADIUS + " blocks of you!");
            }

            cancel();
            return;
        }

        if (!silent) {
            for (Map.Entry<Subclaim, Material> entry : subclaimMaterialMap.entrySet()) {
                player.sendMessage(ChatColor.YELLOW + "Subclaim " + ChatColor.BLUE + entry.getKey().getName() + ChatColor.GREEN + "(" + ChatColor.AQUA + ItemDb.getFriendlyName(new ItemStack(entry.getValue())) + ChatColor.GREEN + ") " + ChatColor.YELLOW + "is claimed by " + ChatColor.BLUE + senderTeam.getName());
            }
        }
    }

    public void cancel() {
        HandlerList.unregisterAll(this);
        currentMaps.remove(player.getName());
        clearAllBlocks();
    }

    public void clearAllBlocks() {
        for (Location location : blockChanges) {
            player.sendBlockChange(location, location.getBlock().getType(), location.getBlock().getData());
        }
    }

    private void drawSubclaim(Subclaim subclaim, Material material) {
        CuboidRegion cuboidRegion = new CuboidRegion(subclaim.getName(), subclaim.getLoc1(), subclaim.getLoc2());
        int glassIteration = 0;

        for (Location location : cuboidRegion) {
            int matches = 0;

            if (location.getBlockX() == cuboidRegion.getMinimumPoint().getBlockX() || location.getBlockX() == cuboidRegion.getMaximumPoint().getBlockX()) {
                matches++;
            }

            if (location.getBlockY() == cuboidRegion.getMinimumPoint().getBlockY() || location.getBlockY() == cuboidRegion.getMaximumPoint().getBlockY()) {
                matches++;
            }

            if (location.getBlockZ() == cuboidRegion.getMinimumPoint().getBlockZ() || location.getBlockZ() == cuboidRegion.getMaximumPoint().getBlockZ()) {
                matches++;
            }

            if (matches >= 2) {
                if (glassIteration++ % 3 == 0) {
                    player.sendBlockChange(location, material, (byte) 0);
                } else {
                    player.sendBlockChange(location, Material.GLASS, (byte) 0);
                }

                blockChanges.add(location.clone());
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (player == event.getPlayer()) {
            cancel();
        }
    }

    public Material getMaterial(int iteration) {
        if (iteration == -1) {
            return (Material.IRON_BLOCK);
        }

        while (iteration >= MAP_MATERIALS.length) {
            iteration = iteration - MAP_MATERIALS.length;
        }

        return (MAP_MATERIALS[iteration]);
    }

}