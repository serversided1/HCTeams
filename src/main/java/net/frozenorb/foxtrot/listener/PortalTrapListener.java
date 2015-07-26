package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.util.Portal;
import net.frozenorb.foxtrot.util.PortalDirection;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PortalTrapListener implements Listener {

    public static final BlockFace[] FACES = new BlockFace[]{
            BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST
    };
    Map<UUID, Long> lastMessaged = new HashMap<>();

    @EventHandler
    public void onPortal(final PlayerPortalEvent event) {
        // Calculating blocks async from now on
        
        new BukkitRunnable() {
            public void run() {
                if (event.getCause() != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) return;

                final Player player = event.getPlayer();
                Location center = player.getLocation();

                Block block = null;
                for (BlockFace face : FACES) {
                    if (center.getBlock().getRelative(face).getType() == Material.PORTAL) {
                        block = center.getBlock().getRelative(face);
                    }
                }
                if (block == null) {
                    if (center.getBlock().getType() == Material.PORTAL) {
                        block = center.getBlock();
                    }
                }
                if (block == null) {
                    return;
                }

                PortalDirection dir = null;
                if (block.getRelative(BlockFace.NORTH).getType() == Material.PORTAL || block.getRelative(BlockFace.SOUTH).getType() == Material.PORTAL) {
                    dir = PortalDirection.NORTH_SOUTH;
                } else if (block.getRelative(BlockFace.EAST).getType() == Material.PORTAL || block.getRelative(BlockFace.WEST).getType() == Material.PORTAL) {
                    dir = PortalDirection.EAST_WEST;
                }

                final Portal portal = new Portal(block, dir);

                // Setting blocks sync because of the op catch
                new BukkitRunnable() {
                    public void run() {
                        if (player.getWorld().getEnvironment() == World.Environment.NETHER) {
                            portal.patchNether();
                        } else if (player.getWorld().getEnvironment() == World.Environment.NORMAL) {
                            portal.patchOverworld();
                        }
                    }
                }.runTask(Foxtrot.getInstance());
            }
        }.runTaskLaterAsynchronously
                (Foxtrot.getInstance(), 1l);
    }

    @EventHandler
    public void onPortalCreate(final PortalCreateEvent event) {
        final List<Block> blocks = new ArrayList<>(event.getBlocks());

        for (Block block : event.getBlocks()) {
            if (block.getType() != Material.AIR && block.getType() != Material.FIRE) blocks.remove(block);
        }

        for (Block block : blocks) {
            for (BlockFace face : FACES) {
                if (block.getRelative(face).getType() == Material.PORTAL) {
                    event.setCancelled(true);
                    for (Entity entity : getNearbyEntities(block.getLocation(), 5)) {
                        if (entity instanceof Player) {
                            Player player = (Player) entity;
                            if (!lastMessaged.containsKey(player.getUniqueId())) {
                                lastMessaged.put(player.getUniqueId(), -1l);
                            }

                            if (System.currentTimeMillis() - lastMessaged.get(player.getUniqueId()) > 5000) {
                                player.sendMessage(ChatColor.RED + "You can't create a portal touching another portal!");
                                lastMessaged.put(player.getUniqueId(), System.currentTimeMillis());
                            }
                        }
                    }
                    break;
                }
            }
        }

        new BukkitRunnable() {
            public void run() {
                PortalDirection dir = PortalDirection.fromPortalData(blocks.get(blocks.size() - 1).getData());

                for(Block b : blocks) {
                    Claim claimAtPortal = LandBoard.getInstance().getClaim(b.getLocation());
                    if(dir == PortalDirection.NORTH_SOUTH) {
                        Block b1 = b.getRelative(BlockFace.EAST);

                        if((claimAtPortal == null || claimAtPortal.contains(b1)) && b1.getType() != Material.AIR) {
                            b1.getWorld().playEffect(b1.getLocation(), Effect.STEP_SOUND, b1.getTypeId());
                            b1.setTypeIdAndData(0, (byte) 0, false);
                        }

                        Block b2 = b.getRelative(BlockFace.WEST);

                        if((claimAtPortal == null || claimAtPortal.contains(b2)) && b2.getType() != Material.AIR) {
                            b2.getWorld().playEffect(b2.getLocation(), Effect.STEP_SOUND, b2.getTypeId());
                            b2.setTypeIdAndData(0, (byte) 0, false);
                        }
                    } else {
                        Block b1 = b.getRelative(BlockFace.NORTH);

                        if((claimAtPortal == null || claimAtPortal.contains(b1)) && b1.getType() != Material.AIR) {
                            b1.getWorld().playEffect(b1.getLocation(), Effect.STEP_SOUND, b1.getTypeId());
                            b1.setTypeIdAndData(0, (byte) 0, false);
                        }

                        Block b2 = b.getRelative(BlockFace.SOUTH);

                        if((claimAtPortal == null || claimAtPortal.contains(b2)) && b2.getType() != Material.AIR) {
                            b2.getWorld().playEffect(b2.getLocation(), Effect.STEP_SOUND, b2.getTypeId());
                            b2.setTypeIdAndData(0, (byte) 0, false);
                        }
                    }
                }
            }
        }.runTaskLater(Foxtrot.getInstance(), 1l);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        for(BlockFace face : FACES) {
            if(event.getBlock().getRelative(face).getType() == Material.PORTAL) {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onBlockForm(EntityChangeBlockEvent event) {
        for(BlockFace face : FACES) {
            if(event.getBlock().getRelative(face).getType() == Material.PORTAL) {
                event.setCancelled(true);
                break;
            }
        }
    }

    public List<Entity> getNearbyEntities(Location l, int radius) {
        List<Entity> entities = new ArrayList<>();

        for (Entity e : l.getWorld().getEntities()) {
            if(l.distance(e.getLocation()) <= radius) {
                entities.add(e);
            }
        }

        return entities;
    }
}
