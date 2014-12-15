package net.frozenorb.foxtrot.team.claims;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.frozenorb.Utilities.DataSystem.Regioning.CuboidRegion;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.factionactiontracker.FactionActionTracker;
import net.frozenorb.foxtrot.raffle.enums.RaffleAchievement;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim.CuboidDirection;
import net.frozenorb.foxtrot.team.commands.team.TeamClaimCommand;
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
    @NonNull private VisualClaimType type;
    @NonNull private boolean bypass;

    private List<Location> blockChanges = new ArrayList<Location>();

    private Location corner1;
    private Location corner2;

    public void draw(boolean silent) {
        // If they already have a map open, close it
        if (currentMaps.containsKey(player.getName()) && type == VisualClaimType.MAP) {
            currentMaps.get(player.getName()).cancel(true);

            if (!silent) {
                player.sendMessage(ChatColor.YELLOW + "Claim pillars have been hidden!");
            }

            return;
        } else if (currentSubclaimMaps.containsKey(player.getName()) && type == VisualClaimType.SUBCLAIM_MAP) {
            currentSubclaimMaps.get(player.getName()).cancel(true);

            if (!silent) {
                player.sendMessage(ChatColor.YELLOW + "Subclaims have been hidden!");
            }

            return;
        }

        // If they have a visual claim open and this isn't a map (or subclaim map), cancel it.
        if (visualClaims.containsKey(player.getName()) && type != VisualClaimType.MAP && type != VisualClaimType.SUBCLAIM_MAP) {
            visualClaims.get(player.getName()).cancel(true);
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
                player.sendMessage(ChatColor.GOLD + "Team land claim started.");
                player.sendMessage(ChatColor.YELLOW + "Left click at a corner of the land you'd like to claim.");
                player.sendMessage(ChatColor.YELLOW + "Right click on the second corner of the land you'd like to claim.");
                player.sendMessage(ChatColor.YELLOW + "Shift left click the air to purchase your claim.");
                break;
            case RESIZE:
                player.sendMessage(ChatColor.GOLD + "Team land resize started.");
                player.sendMessage(ChatColor.YELLOW + "Left click on the corner of the claim you'd like to resize.");
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

                    cancel(true);
                }

                if (!silent) {
                    for (Map.Entry<Map.Entry<Claim, Team>, Material> claim : sendMaps.entrySet()) {
                        player.sendMessage(ChatColor.YELLOW + "Land " + ChatColor.BLUE + claim.getKey().getKey().getName() + ChatColor.GREEN + "(" + ChatColor.AQUA + ItemDb.getFriendlyName(new ItemStack(claim.getValue())) + ChatColor.GREEN + ") " + ChatColor.YELLOW + "is claimed by " + ChatColor.BLUE + claim.getKey().getValue().getName());
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

                    cancel(true);
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

                    cancel(true);
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
        if (!FoxtrotPlugin.getInstance().getServerHandler().isUnclaimed(claim.getMaximumPoint())) {
            return (true);
        }

        if (!FoxtrotPlugin.getInstance().getServerHandler().isUnclaimed(claim.getMinimumPoint())) {
            return (true);
        }

        // A Claim doesn't like being iterated when either its X or Z is 0.
        if (Math.abs(claim.getX1() - claim.getX2()) == 0 || Math.abs(claim.getZ1() - claim.getZ2()) == 0) {
            return (false);
        }

        for (Coordinate location : claim) {
            if (!FoxtrotPlugin.getInstance().getServerHandler().isUnclaimed(new Location(FoxtrotPlugin.getInstance().getServer().getWorld(claim.getWorld()), location.getX(), 80, location.getZ()))) {
                return (true);
            }
        }

        return (false);
    }

    public Set<Claim> touchesOtherClaim(Claim claim) {
        Set<Claim> touchingClaims = new HashSet<Claim>();

        for (Coordinate coordinate : claim.outset(CuboidDirection.Horizontal, 1)) {
            Location loc = new Location(FoxtrotPlugin.getInstance().getServer().getWorld(claim.getWorld()), coordinate.getX(), 80, coordinate.getZ());
            Claim claimAtLocation = LandBoard.getInstance().getClaim(loc);

            if (claimAtLocation != null) {
                touchingClaims.add(claimAtLocation);
            }
        }

        return (touchingClaims);
    }

    public void setLoc(int locationId, Location clicked) {
        Team playerTeam = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());

        if (playerTeam == null) {
            player.sendMessage(ChatColor.RED + "You have to be on a team to " + (type == VisualClaimType.CREATE ? "claim" : "resize") + " land!");
            cancel(true);
            return;
        }

        if (locationId == 1) {
            if (corner2 != null && isIllegal(new Claim(clicked, corner2))) {
                return;
            }

            clearPillarAt(corner1);
            this.corner1 = clicked;
        } else if (locationId == 2) {
            if (corner1 != null && isIllegal(new Claim(corner1, clicked))) {
                return;
            }

            clearPillarAt(corner2);
            this.corner2 = clicked;
        }

        player.sendMessage(ChatColor.YELLOW + "Set claim's location " +ChatColor.LIGHT_PURPLE + locationId + ChatColor.YELLOW + " to " + ChatColor.GREEN + "(" + ChatColor.WHITE + clicked.getBlockX() + ", " + clicked.getBlockY() + ", " + clicked.getBlockZ() + ChatColor.GREEN + ")" + ChatColor.YELLOW + ".");
        FoxtrotPlugin.getInstance().getServer().getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), () -> erectPillar(clicked, Material.EMERALD_BLOCK), 1L);

        int price = getPrice();

        if (price != -1) {
            int x = Math.abs(corner1.getBlockX() - corner2.getBlockX());
            int z = Math.abs(corner1.getBlockZ() - corner2.getBlockZ());

            if (type == VisualClaimType.CREATE) {
                if (price > playerTeam.getBalance() && !bypass) {
                    player.sendMessage(ChatColor.YELLOW + "Claim cost: " + ChatColor.RED + "$" + price + ChatColor.YELLOW + ", Current size: (" + ChatColor.WHITE + x + ", " + z + ChatColor.YELLOW + "), " + ChatColor.WHITE + (x * z) + ChatColor.YELLOW + " blocks");
                } else {
                    player.sendMessage(ChatColor.YELLOW + "Claim cost: " + ChatColor.GREEN + "$" + price + ChatColor.YELLOW + ", Current size: (" + ChatColor.WHITE + x + ", " + z + ChatColor.YELLOW + "), " + ChatColor.WHITE + (x * z) + ChatColor.YELLOW + " blocks");
                }
            } else if (type == VisualClaimType.RESIZE) {
                Map.Entry<Claim, Team> resizing = LandBoard.getInstance().getRegionData(corner1);
                int oldPrice = Claim.getPrice(resizing.getKey(), resizing.getValue(), false);
                int cost = price - oldPrice;

                if (cost > playerTeam.getBalance() && !bypass) {
                    player.sendMessage(ChatColor.YELLOW + "Resize cost: " + ChatColor.RED + "$" + cost + ChatColor.YELLOW + ", Current size: (" + ChatColor.WHITE + x + ", " + z + ChatColor.YELLOW + "), " + ChatColor.WHITE + (x * z) + ChatColor.YELLOW + " blocks");
                } else {
                    player.sendMessage(ChatColor.YELLOW + "Resize cost: " + ChatColor.GREEN + "$" + cost + ChatColor.YELLOW + ", Current size: (" + ChatColor.WHITE + x + ", " + z + ChatColor.YELLOW + "), " + ChatColor.WHITE + (x * z) + ChatColor.YELLOW + " blocks");
                }
            }
        }
    }

    public void cancel(boolean complete) {
        if (complete && (type == VisualClaimType.CREATE || type == VisualClaimType.RESIZE)) {
            clearPillarAt(corner1);
            clearPillarAt(corner2);
        }

        if (type == VisualClaimType.CREATE || type == VisualClaimType.RESIZE) {
            player.getInventory().remove(TeamClaimCommand.SELECTION_WAND);
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

        for (Location location : blockChanges) {
            player.sendBlockChange(location, location.getBlock().getType(), location.getBlock().getData());
        }
    }

    public void purchaseClaim() {
        if (FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName()) == null) {
            player.sendMessage(ChatColor.RED + "You have to be on a team to claim land!");
            cancel(true);
            return;
        }

        if (corner1 != null && corner2 != null) {
            int price = getPrice();
            Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());

            if (!bypass) {
                if (team.getClaims().size() >= Team.MAX_CLAIMS) {
                    player.sendMessage(ChatColor.RED + "Your team has the maximum amount of claims, which is " + Team.MAX_CLAIMS + ".");
                    return;
                }

                if (!team.isCaptain(player.getName()) && !team.isOwner(player.getName())) {
                    player.sendMessage(ChatColor.RED + "Only team captains can claim land.");
                    return;
                }

                if (team.getBalance() < price) {
                    player.sendMessage(ChatColor.RED + "Your team does not have enough money to do this!");
                    return;
                }

                if (team.isRaidable()) {
                    player.sendMessage(ChatColor.RED + "You cannot claim land while raidable.");
                    return;
                }
            }

            Claim claim = new Claim(corner1, corner2);

            if (isIllegal(claim)) {
                return;
            }

            claim.setName(team.getName() + "_" + (100 + FoxtrotPlugin.RANDOM.nextInt(800)));
            claim.setY1(0);
            claim.setY2(256);

            LandBoard.getInstance().setTeamAt(claim, team);
            team.getClaims().add(claim);

            team.flagForSave();

            player.sendMessage(ChatColor.YELLOW + "You have claimed this land for your team!");

            if (!bypass) {
                team.setBalance(team.getBalance() - price);
                player.sendMessage(ChatColor.YELLOW + "Your team's new balance is " + ChatColor.WHITE + "$" + (int) team.getBalance() + ChatColor.LIGHT_PURPLE + " (Price: $" + price + ")");
            }

            // Raffle
            FoxtrotPlugin.getInstance().getRaffleHandler().giveRaffleAchievement(player, RaffleAchievement.BROKER);

            FactionActionTracker.logAction(team, "actions", "Land Claim: [" + claim.getMinimumPoint().getBlockX() + ", " + claim.getMinimumPoint().getBlockY() + ", " + claim.getMinimumPoint().getBlockZ() + "] -> [" + claim.getMaximumPoint().getBlockX() + ", " + claim.getMaximumPoint().getBlockY() + ", " + claim.getMaximumPoint().getBlockZ() + "] [Claimed by: " + player.getName() + ", Cost: " + price + "]");
            cancel(true);
        } else {
            player.sendMessage(ChatColor.RED + "You have not selected both corners of your claim yet!");
        }
    }

    public void resizeClaim() {
        if (FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName()) == null) {
            player.sendMessage(ChatColor.RED + "You have to be on a team to resize land!");
            cancel(true);
            return;
        }

        if (corner1 != null && corner2 != null) {
            int price = getPrice();
            Map.Entry<Claim, Team> resizing = LandBoard.getInstance().getRegionData(corner1);
            int oldPrice = Claim.getPrice(resizing.getKey(), resizing.getValue(), false);
            int cost = price - oldPrice;

            if (!bypass) {
                if (!resizing.getValue().isCaptain(player.getName()) && !resizing.getValue().isOwner(player.getName())) {
                    player.sendMessage(ChatColor.RED + "Only team captains can resize land.");
                    return;
                }

                if (resizing.getValue().getBalance() < cost) {
                    player.sendMessage(ChatColor.RED + "Your team does not have enough money to do this!");
                    return;
                }

                if (resizing.getValue().isRaidable()) {
                    player.sendMessage(ChatColor.RED + "You cannot resize land while raidable.");
                    return;
                }
            }

            if (!resizing.getValue().isMember(player)) {
                player.sendMessage(ChatColor.RED + "You don't own this land!");
                return;
            }

            Claim claim = new Claim(corner1, corner2);

            if (isIllegal(claim)) {
                return;
            }

            resizing.getKey().setLocations(corner1, corner2);
            LandBoard.getInstance().setTeamAt(claim, resizing.getValue());
            resizing.getValue().flagForSave();

            player.sendMessage(ChatColor.YELLOW + "You have resized this land!");

            if (!bypass) {
                resizing.getValue().setBalance(resizing.getValue().getBalance() - price);
                player.sendMessage(ChatColor.YELLOW + "Your team's new balance is " + ChatColor.WHITE + "$" + (int) resizing.getValue().getBalance() + ChatColor.LIGHT_PURPLE + " (Price: $" + price + ")");
            }

            FactionActionTracker.logAction(resizing.getValue(), "actions", "Land Resize: [" + claim.getMinimumPoint().getBlockX() + ", " + claim.getMinimumPoint().getBlockY() + ", " + claim.getMinimumPoint().getBlockZ() + "] -> [" + claim.getMaximumPoint().getBlockX() + ", " + claim.getMaximumPoint().getBlockY() + ", " + claim.getMaximumPoint().getBlockZ() + "] [Claimed by: " + player.getName() + ", Cost: " + price + "]");
            cancel(true);
        } else {
            player.sendMessage(ChatColor.RED + "You have not selected both corners of your claim yet!");
        }
    }

    public int getPrice() {
        if (corner1 == null || corner2 == null) {
            return (-1);
        }

        return (Claim.getPrice(new Claim(corner1, corner2), FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName()), true));
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

            if (set.getBlock().getType() == Material.AIR || set.getBlock().getType().isTransparent()) {
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

        blockChanges.removeIf(blockChange -> {

            if (blockChange.getBlockX() == location.getBlockX() && blockChange.getBlockZ() == location.getBlockZ()) {
                player.sendBlockChange(blockChange, blockChange.getBlock().getType(), blockChange.getBlock().getData());
                return (true);
            }

            return (false);

        });
    }

    public boolean isIllegal(Claim claim) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());

        if (!bypass) {
            if (containsOtherClaim(claim)) {
                player.sendMessage(ChatColor.RED + "This claim contains unclaimable land!");
                return (true);
            }

            if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
                player.sendMessage(ChatColor.RED + "Land can only be claimed in the overworld.");
                return (true);
            }

            Set<Claim> touching = touchesOtherClaim(claim);
            Set<Claim> cloneCheck = new HashSet<Claim>();

            touching.forEach(tee -> cloneCheck.add(tee.clone()));

            boolean contains = cloneCheck.removeIf(c -> team.ownsClaim(c));

            if (team.getClaims().size() > 0 && !contains) {
                player.sendMessage(ChatColor.RED + "All of your claims must be touching each other!");
                return (true);
            }

            if (touching.size() > 1 || (touching.size() == 1 && !contains)) {
                player.sendMessage(ChatColor.RED + "Your claim must be at least 1 block away from enemy claims!");
                return (true);
            }

            int x = Math.abs(claim.getX1() - claim.getX2());
            int z = Math.abs(claim.getZ1() - claim.getZ2());

            if (x < 4 || z < 4) {
                player.sendMessage(ChatColor.RED + "Your claim is too small! The claim has to be at least 5 x 5!");
                return (true);
            }

            if (x >= 3 * z || z >= 3 * x) {
                player.sendMessage(ChatColor.RED + "One side of your claim cannot be more than 3 times larger than the other!");
                return (true);
            }
        }

        return (false);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer() == player && player.getItemInHand() != null) {
            if (player.getItemInHand().getType() == Material.WOOD_HOE && type == VisualClaimType.CREATE) {
                switch (event.getAction()) {
                    case RIGHT_CLICK_BLOCK:
                        if (!bypass && !FoxtrotPlugin.getInstance().getServerHandler().isUnclaimed(event.getClickedBlock().getLocation())) {
                            player.sendMessage(ChatColor.RED + "You can only claim land in the Wilderness!");
                            break;
                        }

                        setLoc(2, event.getClickedBlock().getLocation());
                        break;
                    case RIGHT_CLICK_AIR:
                        cancel(false);
                        player.sendMessage(ChatColor.RED + "You have unset your first and second locations!");
                        break;
                    case LEFT_CLICK_BLOCK:
                        if (player.isSneaking()) {
                            purchaseClaim();
                        } else {
                            if (!bypass && !FoxtrotPlugin.getInstance().getServerHandler().isUnclaimed(event.getClickedBlock().getLocation())) {
                                player.sendMessage(ChatColor.RED + "You can only claim land in the Wilderness!");
                                break;
                            }

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
            } else if (player.getItemInHand().getType() == Material.WOOD_AXE && type == VisualClaimType.RESIZE) {
                switch (event.getAction()) {
                    case RIGHT_CLICK_BLOCK:
                        if (!bypass && !FoxtrotPlugin.getInstance().getServerHandler().isUnclaimed(event.getClickedBlock().getLocation())) {
                            player.sendMessage(ChatColor.RED + "You can only claim land in the Wilderness!");
                            break;
                        }

                        setLoc(2, event.getClickedBlock().getLocation());
                        break;
                    case RIGHT_CLICK_AIR:
                        cancel(false);
                        player.sendMessage(ChatColor.RED + "You have unset your first and second locations!");
                        break;
                    case LEFT_CLICK_BLOCK:
                        if (player.isSneaking()) {
                            resizeClaim();
                        } else {
                            Team ownerAt = LandBoard.getInstance().getTeam(event.getClickedBlock().getLocation());

                            if (ownerAt == null || !ownerAt.isMember(event.getPlayer())) {
                                player.sendMessage(ChatColor.RED + "Left click at the corner of the claim you'd like to resize.");
                                break;
                            }

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
            cancel(true);
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