package net.frozenorb.foxtrot.team.commands.team;

import lombok.Getter;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.bitmask.DTRBitmaskType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.*;

public class TeamMapCommand {

    public static final List<BlockFace> BLOCK_FACES_TO_CHECK = Arrays.asList(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);
    public static final int MAP_RADIUS = 300;

    @Getter private static Map<String, Set<Location>> sentLocations = new HashMap<String, Set<Location>>();

    @Command(names={ "team map", "t map", "f map", "faction map", "fac map", "map" }, permissionNode="")
    public static void teamMap(Player sender) {
        toggleMap(sender, false);
    }

    public static void toggleMap(Player player, boolean silent) {
        if (sentLocations.containsKey(player.getName())) {
            for (Location location : sentLocations.get(player.getName())) {
                if (!location.getChunk().isLoaded()) {
                    continue;
                }

                player.sendBlockChange(location, location.getBlock().getType(), location.getBlock().getData());
            }

            sentLocations.remove(player.getName());

            if (!silent) {
                player.sendMessage(ChatColor.YELLOW + "Claims have been hidden.");
            }

            return;
        }

        Collection<Claim> nearbyClaims = LandBoard.getInstance().getNearbyClaims(player.getLocation(), MAP_RADIUS);
        Set<Location> changedLocations = new HashSet<Location>();

        for (Claim claim : nearbyClaims) {
            Chunk chunk = claim.getChunk();

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    if (x == 0 || x == 15 || z == 0 || z == 15) {
                        Block block = chunk.getBlock(x, player.getLocation().getBlockY() + 50, z);
                        Claim claimAtBlock = LandBoard.getInstance().getClaim(block);
                        boolean allClaimed = true;

                        for (BlockFace blockFace : BLOCK_FACES_TO_CHECK) {
                            Claim claimAtRelative = LandBoard.getInstance().getClaim(block.getRelative(blockFace));

                            if (claimAtRelative == null || !claimAtRelative.getOwner().equals(claimAtBlock.getOwner())) {
                                allClaimed = false;
                                break;
                            }
                        }

                        if (allClaimed) {
                            continue;
                        }

                        while (!block.getType().isSolid() || block.getType() == Material.LEAVES) {
                            block = block.getRelative(BlockFace.DOWN);
                        }

                        Team claimOwner = claim.getOwner();
                        DyeColor carpetColor;

                        if (claimOwner.getOwner() != null) {
                            if (claimOwner.isMember(player)) {
                                carpetColor = DyeColor.GREEN;
                            } else if (claimOwner.isAlly(player)) {
                                carpetColor = DyeColor.BLUE;
                            } else if (claimOwner.isTrading()) {
                                carpetColor = DyeColor.PINK;
                            } else {
                                carpetColor = DyeColor.RED;
                            }
                        } else if (claimOwner.hasDTRBitmask(DTRBitmaskType.SAFE_ZONE)) {
                            carpetColor = DyeColor.LIME;
                        } else if (claimOwner.hasDTRBitmask(DTRBitmaskType.CITADEL)) {
                            carpetColor = DyeColor.PURPLE;
                        } else if (claimOwner.hasDTRBitmask(DTRBitmaskType.KOTH)) {
                            carpetColor = DyeColor.YELLOW;
                        } else {
                            carpetColor = DyeColor.BLUE;
                        }

                        player.sendBlockChange(block.getLocation(), Material.WOOL, carpetColor.getWoolData());
                        player.sendBlockChange(block.getRelative(BlockFace.UP).getLocation(), Material.CARPET, carpetColor.getWoolData());

                        changedLocations.add(block.getLocation());
                        changedLocations.add(block.getRelative(BlockFace.UP).getLocation());
                    }
                }
            }
        }

        if (changedLocations.size() == 0) {
            if (!silent) {
                player.sendMessage(ChatColor.YELLOW + "There are no claims near you!");
            }

            return;
        }

        sentLocations.put(player.getName(), changedLocations);

        if (!silent) {
            player.sendMessage(ChatColor.YELLOW + "Claims have been shown.");
        }
    }

}