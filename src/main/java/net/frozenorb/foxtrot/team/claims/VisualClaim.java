package net.frozenorb.foxtrot.team.claims;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.frozenorb.Utilities.DataSystem.Regioning.CuboidRegion;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim.CuboidDirection;
import net.frozenorb.foxtrot.team.commands.team.TeamClaimCommand;
import net.frozenorb.foxtrot.team.commands.team.TeamResizeCommand;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionTracker;
import net.frozenorb.foxtrot.teamactiontracker.enums.TeamActionType;
import net.frozenorb.mBasic.Utilities.ItemDb;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@SuppressWarnings("deprecation")
@RequiredArgsConstructor
public class VisualClaim implements Listener {

    public static final int MAP_RADIUS = 50;
    public static final Material[] MAP_MATERIALS = { Material.DIAMOND_BLOCK,
            Material.GOLD_BLOCK, Material.LOG, Material.BRICK, Material.WOOD,
            Material.REDSTONE_BLOCK, Material.LAPIS_BLOCK, Material.CHEST,
            Material.MELON_BLOCK, Material.STONE, Material.COBBLESTONE,
            Material.COAL_BLOCK, Material.DIAMOND_ORE, Material.COAL_ORE,
            Material.GOLD_ORE, Material.REDSTONE_ORE, Material.FURNACE };

    @Getter private static Map<String, VisualClaim> currentMaps = new HashMap<String, VisualClaim>();
    @Getter private static Map<String, VisualClaim> currentSubclaimMaps = new HashMap<String, VisualClaim>();
    @Getter private static Map<String, VisualClaim> visualClaims = new HashMap<String, VisualClaim>();

    @Getter @NonNull private Player player;
    @Getter @NonNull private VisualClaimType type;
    @Getter @NonNull private boolean bypass;

    @Getter private List<Location> blockChanges = new ArrayList<Location>();

    @Getter @Setter private Claim resizing;
    @Getter @Setter private Location corner1;
    @Getter @Setter private Location corner2;

    public void draw(boolean silent) {
        // If they already have a map open and they're opening another
        if (currentMaps.containsKey(player.getName()) && type == VisualClaimType.MAP) {
            currentMaps.get(player.getName()).cancel();

            if (!silent) {
                player.sendMessage(ChatColor.YELLOW + "Claim pillars have been hidden!");
            }

            return;
        } else if (currentSubclaimMaps.containsKey(player.getName()) && type == VisualClaimType.SUBCLAIM_MAP) { // If they already have a subclaim map open and they're opening another
            currentSubclaimMaps.get(player.getName()).cancel();

            if (!silent) {
                player.sendMessage(ChatColor.YELLOW + "Subclaims have been hidden!");
            }

            return;
        }

        // If they have a visual claim open and this isn't a map (or subclaim map), cancel it.
        if (visualClaims.containsKey(player.getName()) && !(type == VisualClaimType.MAP || type == VisualClaimType.SUBCLAIM_MAP)) {
            visualClaims.get(player.getName()).cancel();
        }

        // Put this visual claim into the cache
        switch (type) {
            case MAP:
                currentMaps.put(player.getName(), this);
                break;
            case SUBCLAIM_MAP:
                currentSubclaimMaps.put(player.getName(), this);
                break;
            default:
                visualClaims.put(player.getName(), this);
                break;
        }

        FoxtrotPlugin.getInstance().getServer().getPluginManager().registerEvents(this, FoxtrotPlugin.getInstance());

        switch (type) {
            case CREATE:
                // Don't allow claiming during a kit map
                if (!bypass && FoxtrotPlugin.getInstance().getMapHandler().isKitMap()) {
                    if (!silent) {
                        player.sendMessage(ChatColor.RED + "Land claiming is disabled during kit maps");
                    }

                    cancel();
                    return;
                }

                player.sendMessage(ChatColor.GOLD + "Team land claim started.");
                player.sendMessage(ChatColor.YELLOW + "Left click at a corner of the land you'd like to claim.");
                player.sendMessage(ChatColor.YELLOW + "Right click on the second corner of the land you'd like to claim.");
                player.sendMessage(ChatColor.YELLOW + "Shift left click the air to purchase your claim.");
                break;
            case RESIZE:
                player.sendMessage(ChatColor.GOLD + "Team land resize started.");
                player.sendMessage(ChatColor.YELLOW + "Left click in the claim you'd like to resize.");
                player.sendMessage(ChatColor.YELLOW + "Right click on the corner you'd like to resize to.");
                player.sendMessage(ChatColor.YELLOW + "Shift left click the air to confirm your resize.");
                break;
            case MAP:
                int claimIteration = 0;
                Map<Map.Entry<Claim, Team>, Material> sendMaps = new HashMap<Map.Entry<Claim, Team>, Material>();

                for (Map.Entry<Claim, Team> regionData : LandBoard.getInstance().getRegionData(player.getLocation(), MAP_RADIUS, 256, MAP_RADIUS)) {
                    Material mat = getMaterial(claimIteration);
                    claimIteration++;

                    drawClaim(regionData.getKey(), mat);
                    sendMaps.put(regionData, mat);
                }

                if (sendMaps.isEmpty()) {
                    if (!silent) {
                        player.sendMessage(ChatColor.YELLOW + "There are no claims within " + MAP_RADIUS + " blocks of you!");
                    }

                    cancel();
                }

                if (!silent) {
                    for (Map.Entry<Map.Entry<Claim, Team>, Material> mapEntry : sendMaps.entrySet()) {
                        Team team = mapEntry.getKey().getValue();
                        Claim claim = mapEntry.getKey().getKey();

                        if (team.getOwner() == null) {
                            player.sendMessage(ChatColor.YELLOW + "Land " + ChatColor.BLUE + team.getName(player) + ChatColor.GREEN + "(" + ChatColor.AQUA + ItemDb.getFriendlyName(new ItemStack(mapEntry.getValue())) + ChatColor.GREEN + ") " + ChatColor.YELLOW + "is claimed by " + ChatColor.BLUE + team.getName(player));
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "Land " + ChatColor.BLUE + claim.getName() + ChatColor.GREEN + "(" + ChatColor.AQUA + ItemDb.getFriendlyName(new ItemStack(mapEntry.getValue())) + ChatColor.GREEN + ") " + ChatColor.YELLOW + "is claimed by " + ChatColor.BLUE + team.getName());
                        }
                    }
                }

                break;
            case SUBCLAIM_MAP:
                Team senderTeam = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());

                if (bypass) {
                    senderTeam = LandBoard.getInstance().getTeam(player.getLocation());
                }

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

                break;
        }
    }

    public boolean containsOtherClaim(Claim claim) {
        Team maxTeam = LandBoard.getInstance().getTeam(claim.getMaximumPoint());

        if (maxTeam != null && (type != VisualClaimType.RESIZE || !maxTeam.isMember(player))) {
            return (true);
        }

        Team minTeam = LandBoard.getInstance().getTeam(claim.getMinimumPoint());

        if (minTeam != null && (type != VisualClaimType.RESIZE || !minTeam.isMember(player))) {
            return (true);
        }

        // A Claim doesn't like being iterated when either its X or Z is 0.
        if (Math.abs(claim.getX1() - claim.getX2()) == 0 || Math.abs(claim.getZ1() - claim.getZ2()) == 0) {
            return (false);
        }

        for (Coordinate location : claim) {
            Location at = new Location(FoxtrotPlugin.getInstance().getServer().getWorld(claim.getWorld()), location.getX(), 80, location.getZ());
            Team teamAt = LandBoard.getInstance().getTeam(at);

            if (teamAt != null && (type != VisualClaimType.RESIZE || !teamAt.isMember(player))) {
                return (true);
            }
        }

        return (false);
    }

    public Set<Claim> getTouchingClaims(Claim claim) {
        Set<Claim> touchingClaims = new HashSet<Claim>();

        for (Coordinate coordinate : claim.outset(CuboidDirection.Horizontal, 1)) {
            Location loc = new Location(FoxtrotPlugin.getInstance().getServer().getWorld(claim.getWorld()), coordinate.getX(), 80, coordinate.getZ());
            Map.Entry<Claim, Team> claimAtLocation = LandBoard.getInstance().getRegionData(loc);

            if (claimAtLocation != null) {
                touchingClaims.add(claimAtLocation.getKey());
            }
        }

        return (touchingClaims);
    }

    public void setLoc(int locationId, Location clicked) {
        Team playerTeam = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());

        if (playerTeam == null) {
            player.sendMessage(ChatColor.RED + "You have to be on a team to " + type.name().toLowerCase() + " land!");
            cancel();
            return;
        }

        if (type == VisualClaimType.CREATE) {
            if (!bypass && !FoxtrotPlugin.getInstance().getServerHandler().isUnclaimed(clicked)) {
                player.sendMessage(ChatColor.RED + "You can only claim land in the Wilderness!");
                return;
            }

            if (locationId == 1) {
                if (corner2 != null && isIllegalClaim(new Claim(clicked, corner2), null)) {
                    return;
                }

                clearPillarAt(corner1);
                this.corner1 = clicked;
            } else if (locationId == 2) {
                if (corner1 != null && isIllegalClaim(new Claim(corner1, clicked), null)) {
                    return;
                }

                clearPillarAt(corner2);
                this.corner2 = clicked;
            }

            FoxtrotPlugin.getInstance().getServer().getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), () -> erectPillar(clicked, Material.EMERALD_BLOCK), 1L);
            player.sendMessage(ChatColor.YELLOW + "Set claim's location " + ChatColor.LIGHT_PURPLE + locationId + ChatColor.YELLOW + " to " + ChatColor.GREEN + "(" + ChatColor.WHITE + clicked.getBlockX() + ", " + clicked.getBlockY() + ", " + clicked.getBlockZ() + ChatColor.GREEN + ")" + ChatColor.YELLOW + ".");

            if (corner1 != null && corner2 != null) {
                int price = Claim.getPrice(new Claim(corner1, corner2), playerTeam, true);

                int x = Math.abs(corner1.getBlockX() - corner2.getBlockX());
                int z = Math.abs(corner1.getBlockZ() - corner2.getBlockZ());

                if (price > playerTeam.getBalance() && !bypass) {
                    player.sendMessage(ChatColor.YELLOW + "Claim cost: " + ChatColor.RED + "$" + price + ChatColor.YELLOW + ", Current size: (" + ChatColor.WHITE + x + ", " + z + ChatColor.YELLOW + "), " + ChatColor.WHITE + (x * z) + ChatColor.YELLOW + " blocks");
                } else {
                    player.sendMessage(ChatColor.YELLOW + "Claim cost: " + ChatColor.GREEN + "$" + price + ChatColor.YELLOW + ", Current size: (" + ChatColor.WHITE + x + ", " + z + ChatColor.YELLOW + "), " + ChatColor.WHITE + (x * z) + ChatColor.YELLOW + " blocks");
                }
            }
        } else if (type == VisualClaimType.RESIZE) {
            Map.Entry<Claim, Team> teamAtLocation = LandBoard.getInstance().getRegionData(clicked);

            if (locationId == 1) {
                if (teamAtLocation == null || !teamAtLocation.getValue().isMember(player)) {
                    player.sendMessage(ChatColor.YELLOW + "To resize your claim, please left click in the claim you'd like to resize.");
                    return;
                }

                resizing = teamAtLocation.getKey();
                drawClaim(resizing, Material.LAPIS_BLOCK);
            } else if (locationId == 2) {
                if (resizing == null) {
                    player.sendMessage(ChatColor.YELLOW + "Before you set the location you'd like to resize to, first left click in the claim you'd like to resize.");
                    return;
                }

                Claim claimClone = resizing.clone();

                applyResize(claimClone, clicked);

                if (isIllegalClaim(claimClone, Arrays.asList(resizing, claimClone))) {
                    return;
                }

                this.corner2 = clicked;

                new BukkitRunnable() {

                    public void run() {
                        clearAllBlocks();
                        drawClaim(resizing, Material.LAPIS_BLOCK);
                        drawClaim(claimClone, Material.EMERALD_BLOCK);
                    }

                }.runTaskLater(FoxtrotPlugin.getInstance(), 1L);
            }

            if (locationId == 1) {
                player.sendMessage(ChatColor.YELLOW + "Selected claim " + ChatColor.LIGHT_PURPLE + teamAtLocation.getKey().getName() + ChatColor.YELLOW + " to resize.");
            } else {
                player.sendMessage(ChatColor.YELLOW + "Set resize location to " + ChatColor.GREEN + "(" + ChatColor.WHITE + clicked.getBlockX() + ", " + clicked.getBlockY() + ", " + clicked.getBlockZ() + ChatColor.GREEN + ")" + ChatColor.YELLOW + ".");
            }

            if (resizing != null && corner2 != null) {
                int oldPrice = Claim.getPrice(resizing, null, false);
                Claim preview = resizing.clone();

                applyResize(preview, corner2);

                int newPrice = Claim.getPrice(preview, null, false);
                int cost = newPrice - oldPrice;

                if (cost > playerTeam.getBalance() && !bypass) {
                    player.sendMessage(ChatColor.YELLOW + "Resize cost: " + ChatColor.RED + "$" + cost);
                } else {
                    player.sendMessage(ChatColor.YELLOW + "Resize cost: " + ChatColor.GREEN + "$" + cost);
                }
            }
        }
    }

    public void cancel() {
        if (type == VisualClaimType.CREATE || type == VisualClaimType.RESIZE) {
            player.getInventory().remove(TeamClaimCommand.SELECTION_WAND);
            player.getInventory().remove(TeamResizeCommand.SELECTION_WAND);
        }

        HandlerList.unregisterAll(this);

        switch (type) {
            case MAP:
                currentMaps.remove(player.getName());
                break;
            case SUBCLAIM_MAP:
                currentSubclaimMaps.remove(player.getName());
                break;
            default:
                visualClaims.remove(player.getName());
                break;
        }

        clearAllBlocks();
    }

    public void clearAllBlocks() {
        for (Location location : blockChanges) {
            player.sendBlockChange(location, location.getBlock().getType(), location.getBlock().getData());
        }
    }

    public void purchaseClaim() {
        Team playerTeam = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());

        if (playerTeam == null) {
            player.sendMessage(ChatColor.RED + "You have to be on a team to claim land!");
            cancel();
            return;
        }

        if (corner1 != null && corner2 != null) {
            int price = Claim.getPrice(new Claim(corner1, corner2), playerTeam, true);

            if (!bypass) {
                if (playerTeam.getClaims().size() >= Team.MAX_CLAIMS) {
                    player.sendMessage(ChatColor.RED + "Your team has the maximum amount of claims, which is " + Team.MAX_CLAIMS + ".");
                    return;
                }

                if (!playerTeam.isCaptain(player.getName()) && !playerTeam.isOwner(player.getName())) {
                    player.sendMessage(ChatColor.RED + "Only team captains can claim land.");
                    return;
                }

                if (playerTeam.getBalance() < price) {
                    player.sendMessage(ChatColor.RED + "Your team does not have enough money to do this!");
                    return;
                }

                if (playerTeam.isRaidable()) {
                    player.sendMessage(ChatColor.RED + "You cannot claim land while raidable.");
                    return;
                }
            }

            Claim claim = new Claim(corner1, corner2);

            if (isIllegalClaim(claim, null)) {
                return;
            }

            claim.setName(playerTeam.getName() + "_" + (100 + FoxtrotPlugin.RANDOM.nextInt(800)));
            claim.setY1(0);
            claim.setY2(256);

            LandBoard.getInstance().setTeamAt(claim, playerTeam);
            playerTeam.getClaims().add(claim);

            playerTeam.flagForSave();

            player.sendMessage(ChatColor.YELLOW + "You have claimed this land for your team!");

            if (!bypass) {
                playerTeam.setBalance(playerTeam.getBalance() - price);
                player.sendMessage(ChatColor.YELLOW + "Your team's new balance is " + ChatColor.WHITE + "$" + (int) playerTeam.getBalance() + ChatColor.LIGHT_PURPLE + " (Price: $" + price + ")");
            }

            TeamActionTracker.logAction(playerTeam, TeamActionType.GENERAL, "Land Claim: [" + claim.getMinimumPoint().getBlockX() + ", " + claim.getMinimumPoint().getBlockY() + ", " + claim.getMinimumPoint().getBlockZ() + "] -> [" + claim.getMaximumPoint().getBlockX() + ", " + claim.getMaximumPoint().getBlockY() + ", " + claim.getMaximumPoint().getBlockZ() + "] [Claimed by: " + player.getName() + ", Cost: " + price + "]");
            cancel();

            new BukkitRunnable() {

                public void run() {
                    if (VisualClaim.getCurrentMaps().containsKey(player.getName())) {
                        VisualClaim.getCurrentMaps().get(player.getName()).cancel();
                    }

                    new VisualClaim(player, VisualClaimType.MAP, false).draw(true);
                }

            }.runTaskLater(FoxtrotPlugin.getInstance(), 1L);
        } else {
            player.sendMessage(ChatColor.RED + "You have not selected both corners of your claim yet!");
        }
    }

    public void resizeClaim() {
        Team playerTeam = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());

        if (playerTeam == null) {
            player.sendMessage(ChatColor.RED + "You have to be on a team to resize land!");
            cancel();
            return;
        }

        if (resizing != null && corner2 != null) {
            int oldPrice = Claim.getPrice(resizing, null, false);
            Claim preview = resizing.clone();

            applyResize(preview, corner2);

            int newPrice = Claim.getPrice(preview, null, false);
            int cost = newPrice - oldPrice;

            if (!bypass) {
                if (!playerTeam.isCaptain(player.getName()) && !playerTeam.isOwner(player.getName())) {
                    player.sendMessage(ChatColor.RED + "Only team captains can resize land.");
                    return;
                }

                if (playerTeam.getBalance() < cost) {
                    player.sendMessage(ChatColor.RED + "Your team does not have enough money to do this!");
                    return;
                }

                if (playerTeam.isRaidable()) {
                    player.sendMessage(ChatColor.RED + "You cannot resize land while raidable.");
                    return;
                }
            }

            if (isIllegalClaim(preview, null)) {
                return;
            }

            applyResize(resizing, corner2);

            LandBoard.getInstance().setTeamAt(resizing, null);
            LandBoard.getInstance().setTeamAt(resizing, playerTeam);
            playerTeam.flagForSave();

            player.sendMessage(ChatColor.YELLOW + "You have resized this land!");

            if (!bypass) {
                playerTeam.setBalance(playerTeam.getBalance() - cost);
                player.sendMessage(ChatColor.YELLOW + "Your team's new balance is " + ChatColor.WHITE + "$" + (int) playerTeam.getBalance() + ChatColor.LIGHT_PURPLE + " (Price: $" + cost + ")");
            }

            TeamActionTracker.logAction(playerTeam, TeamActionType.GENERAL, "Land Resize: [" + resizing.getMinimumPoint().getBlockX() + ", " + resizing.getMinimumPoint().getBlockY() + ", " + resizing.getMinimumPoint().getBlockZ() + "] -> [" + resizing.getMaximumPoint().getBlockX() + ", " + resizing.getMaximumPoint().getBlockY() + ", " + resizing.getMaximumPoint().getBlockZ() + "] [Resized by: " + player.getName() + ", Cost: " + cost + "]");
            cancel();

            new BukkitRunnable() {

                public void run() {
                    if (VisualClaim.getCurrentMaps().containsKey(player.getName())) {
                        VisualClaim.getCurrentMaps().get(player.getName()).cancel();
                    }

                    new VisualClaim(player, VisualClaimType.MAP, false).draw(true);
                }

            }.runTaskLater(FoxtrotPlugin.getInstance(), 1L);
        } else {
            player.sendMessage(ChatColor.RED + "You have not selected both corners of your claim yet!");
        }
    }

    private void drawClaim(Claim claim, Material material) {
        for (Location loc : claim.getCornerLocations()) {
            erectPillar(loc, material);
        }
    }

    private void drawSubclaim(Subclaim subclaim, Material material) {
        CuboidRegion cuboidRegion = new CuboidRegion(subclaim.getName(), subclaim.getLoc1(), subclaim.getLoc2());
        int glassIteration = 0;

        for (Location location : cuboidRegion) {
            int matches = 0;

            if (location.getBlockX() == cuboidRegion.getMinimumPoint().getBlockX()) {
                matches++;
            }

            if (location.getBlockX() == cuboidRegion.getMaximumPoint().getBlockX()) {
                matches++;
            }

            if (location.getBlockY() == cuboidRegion.getMinimumPoint().getBlockY()) {
                matches++;
            }

            if (location.getBlockY() == cuboidRegion.getMaximumPoint().getBlockY()) {
                matches++;
            }

            if (location.getBlockZ() == cuboidRegion.getMinimumPoint().getBlockZ()) {
                matches++;
            }

            if (location.getBlockZ() == cuboidRegion.getMaximumPoint().getBlockZ()) {
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

    private void erectPillar(Location loc, Material mat) {
        Location set = loc.clone();

        for (int y = 0; y < 256; y++) {
            set.setY(y);

            if (set.getBlock().getType() == Material.AIR || set.getBlock().getType().isTransparent() || set.getBlock().getType() == Material.WATER || set.getBlock().getType() == Material.STATIONARY_WATER) {
                if (y % 5 == 0) {
                    player.sendBlockChange(set, mat, (byte) 0);
                } else {
                    player.sendBlockChange(set, Material.GLASS, (byte) 0);
                }

                blockChanges.add(set.clone());
            }
        }
    }

    private void clearPillarAt(Location location) {
        if (location == null) {
            return;
        }

        Iterator<Location> blockChangeIterator = blockChanges.iterator();

        blockChanges.removeIf(blockChange -> {

            if (blockChange.getBlockX() == location.getBlockX() && blockChange.getBlockZ() == location.getBlockZ()) {
                player.sendBlockChange(blockChange, blockChange.getBlock().getType(), blockChange.getBlock().getData());
                return (true);
            }

            return (false);

        });
    }

    public boolean isIllegalClaim(Claim claim, List<Claim> ignoreNearby) {
        if (bypass) {
            return (false);
        }

        Team playerTeam = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());

        if (containsOtherClaim(claim)) {
            player.sendMessage(ChatColor.RED + "This claim contains unclaimable land!");
            return (true);
        }

        if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
            player.sendMessage(ChatColor.RED + "Land can only be claimed in the overworld.");
            return (true);
        }

        Set<Claim> touchingClaims = getTouchingClaims(claim);
        Iterator<Claim> teamClaims = touchingClaims.iterator();
        boolean removedSelfClaims = false;

        while (teamClaims.hasNext()) {
            Claim possibleClaim = teamClaims.next();

            if (ignoreNearby != null && ignoreNearby.contains(possibleClaim)) {
                removedSelfClaims = true;
                teamClaims.remove();
            } else if (playerTeam.ownsClaim(possibleClaim)) {
                removedSelfClaims = true;
                teamClaims.remove();
            }
        }

        if (playerTeam.getClaims().size() != (type == VisualClaimType.RESIZE ? 1 : 0) && !removedSelfClaims) {
            player.sendMessage(ChatColor.RED + "All of your claims must be touching each other!");
            return (true);
        }

        if (touchingClaims.size() > 1 || (touchingClaims.size() == 1 && !removedSelfClaims)) {
            player.sendMessage(ChatColor.RED + "Your claim must be at least 1 block away from enemy claims!");
            return (true);
        }

        int x = Math.abs(claim.getX1() - claim.getX2());
        int z = Math.abs(claim.getZ1() - claim.getZ2());

        if (x < 5 || z < 5) {
            player.sendMessage(ChatColor.RED + "Your claim is too small! The claim has to be at least 5 x 5!");
            return (true);
        }

        if (x > 3 * z || z > 3 * x) {
            player.sendMessage(ChatColor.RED + "One side of your claim cannot be more than 3 times larger than the other!");
            return (true);
        }

        return (false);
    }

    public void applyResize(Claim claim, Location location) {
        double furthestDistance = 0D;
        Location furthestCorner = null;

        for (Location corner : claim.getCornerLocations()) {
            double distance = location.distanceSquared(corner);

            if (furthestCorner == null || distance > furthestDistance) {
                furthestDistance = distance;
                furthestCorner = corner;
            }
        }

        claim.setLocations(location, furthestCorner);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer() == player && player.getItemInHand() != null) {
            if (player.getItemInHand().getType() == TeamClaimCommand.SELECTION_WAND.getType() && type == VisualClaimType.CREATE) {
                switch (event.getAction()) {
                    case RIGHT_CLICK_BLOCK:
                        setLoc(2, event.getClickedBlock().getLocation());
                        break;
                    case RIGHT_CLICK_AIR:
                        cancel();
                        player.sendMessage(ChatColor.RED + "You have unset your first and second locations!");
                        break;
                    case LEFT_CLICK_BLOCK:
                        if (player.isSneaking()) {
                            purchaseClaim();
                        } else {
                            setLoc(1, event.getClickedBlock().getLocation());
                        }

                        break;
                    case LEFT_CLICK_AIR:
                        if (player.isSneaking()) {
                            purchaseClaim();
                        }

                        break;
                }

                event.setCancelled(true);
            } else if (player.getItemInHand().getType() == TeamResizeCommand.SELECTION_WAND.getType() && type == VisualClaimType.RESIZE) {
                switch (event.getAction()) {
                    case RIGHT_CLICK_BLOCK:
                        setLoc(2, event.getClickedBlock().getLocation());
                        break;
                    case RIGHT_CLICK_AIR:
                        cancel();
                        player.sendMessage(ChatColor.RED + "You have cancelled the resizing process.");

                        break;
                    case LEFT_CLICK_BLOCK:
                        if (player.isSneaking()) {
                            resizeClaim();
                        } else {
                            setLoc(1, event.getClickedBlock().getLocation());
                        }

                        break;
                    case LEFT_CLICK_AIR:
                        if (player.isSneaking()) {
                            resizeClaim();
                        }

                        break;
                }

                event.setCancelled(true);
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

    public static VisualClaim getVisualClaim(String name) {
        return (visualClaims.get(name));
    }

}