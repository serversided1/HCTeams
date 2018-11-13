package net.frozenorb.foxtrot.listener;

import com.google.common.collect.ImmutableMap;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.cavern.CavernHandler;
import net.frozenorb.foxtrot.events.Event;
import net.frozenorb.foxtrot.events.koth.KOTH;
import net.frozenorb.foxtrot.glowmtn.GlowHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.team.event.PlayerBuildInOthersClaimEvent;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionTracker;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionType;
import net.frozenorb.foxtrot.util.InventoryUtils;
import net.frozenorb.foxtrot.util.RegenUtils;
import net.frozenorb.qlib.util.PlayerUtils;
import net.frozenorb.qlib.uuid.FrozenUUIDCache;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TeamListener implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());

        if (team != null && team.getMaxOnline() > 0 && team.getOnlineMemberAmount() >= team.getMaxOnline()) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.YELLOW + "Your team currently has too many players logged in. Try again later!");
        }
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());

        if (team != null) {
            for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                if (team.isMember(player.getUniqueId())) {
                    player.sendMessage(ChatColor.GREEN + "Member Online: " + ChatColor.WHITE + event.getPlayer().getName());
                } else if (team.getAllies().size() != 0 && team.isAlly(player.getUniqueId())) {
                    player.sendMessage(Team.ALLY_COLOR + "Ally Online: " + ChatColor.WHITE + event.getPlayer().getName());
                }
            }

            TeamActionTracker.logActionAsync(team, TeamActionType.MEMBER_CONNECTED, ImmutableMap.of(
                    "playerId", event.getPlayer().getUniqueId(),
                    "playerName", event.getPlayer().getName()
            ));

            new BukkitRunnable() {

                public void run() {
                    team.sendTeamInfo(event.getPlayer());
                }

            }.runTaskAsynchronously(Foxtrot.getInstance());
        } else {
            event.getPlayer().sendMessage(ChatColor.GRAY + "You are not on a team!");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());

        if (team != null) {
            for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                if (player.equals(event.getPlayer())) {
                    continue;
                }

                if (team.isMember(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "Member Offline: " + ChatColor.WHITE + event.getPlayer().getName());
                } else if (team.isAlly(player.getUniqueId())) {
                    player.sendMessage(Team.ALLY_COLOR + "Ally Offline: " + ChatColor.WHITE + event.getPlayer().getName());
                }
            }

            TeamActionTracker.logActionAsync(team, TeamActionType.MEMBER_DISCONNECTED, ImmutableMap.of(
                    "playerId", event.getPlayer().getUniqueId(),
                    "playerName", event.getPlayer().getName()
            ));
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getPlayer() != null) {
            if (Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
                return;
            }
        }

        if (Foxtrot.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getBlock().getLocation())) {
            return;
        }

        if (LandBoard.getInstance().getTeam(event.getBlock().getLocation()) != null) {
            Team owner = LandBoard.getInstance().getTeam(event.getBlock().getLocation());

            if (event.getCause() == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL && owner.isMember(event.getPlayer().getUniqueId())) {
                return;
            }

            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer()) || Foxtrot.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getBlock().getLocation())) {
            return;
        }

        Team team = LandBoard.getInstance().getTeam(event.getBlock().getLocation());

        if (!team.isMember(event.getPlayer().getUniqueId())) {
            if (!DTRBitmask.SAFE_ZONE.appliesAt(event.getBlock().getLocation()) && event.getItemInHand() != null && event.getItemInHand().getType() == Material.WEB && (Foxtrot.getInstance().getMapHandler().isKitMap() || Foxtrot.getInstance().getServerHandler().isVeltKitMap())) {
                for (Event playableEvent : Foxtrot.getInstance().getEventHandler().getEvents()) {
                    if (!playableEvent.isActive() || !(playableEvent instanceof KOTH)) {
                        continue;
                    }
                    
                    KOTH koth = (KOTH) playableEvent;
                    
                    if (koth.onCap(event.getBlockPlaced().getLocation())) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ChatColor.YELLOW + "You can't place web on cap!");
                        event.getPlayer().setItemInHand(null);
                        event.getPlayer().setMetadata("ImmuneFromGlitchCheck", new FixedMetadataValue(Foxtrot.getInstance(), new Object()));
                        
                        Bukkit.getScheduler().runTask(Foxtrot.getInstance(), () -> {
                            event.getPlayer().removeMetadata("ImmuneFromGlitchCheck", Foxtrot.getInstance());
                        });
                        
                        return;
                    }
                }

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        if (event.getBlock().getType() == Material.WEB) {
                            event.getBlock().setType(Material.AIR);
                        }
                    }

                }.runTaskLater(Foxtrot.getInstance(), 10 * 20L);
            } else {
                event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build in " + team.getName(event.getPlayer()) + ChatColor.YELLOW + "'s territory!");
                event.setCancelled(true);
            }
            return;
        }

        if (!team.isCoLeader(event.getPlayer().getUniqueId()) && !team.isCaptain(event.getPlayer().getUniqueId()) && !team.isOwner(event.getPlayer().getUniqueId())) {
            Subclaim subclaim = team.getSubclaim(event.getBlock().getLocation());

            if (subclaim != null && !subclaim.isMember(event.getPlayer().getUniqueId())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.YELLOW + "You do not have access to the subclaim " + ChatColor.GREEN + subclaim.getName() + ChatColor.YELLOW  + "!");
            }
        }
    }

    @EventHandler(ignoreCancelled=true) // normal priority
    public void onBlockBreak(BlockBreakEvent event) {
        if (Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer()) || Foxtrot.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getBlock().getLocation())) {
            return;
        }

        Team team = LandBoard.getInstance().getTeam(event.getBlock().getLocation());

        if (event.getBlock().getType() == Material.GLOWSTONE && Foxtrot.getInstance().getGlowHandler().hasGlowMountain() && team.getName().equals(GlowHandler.getGlowTeamName())) {
            return; // don't concern ourselves with glowstone breaks in glowstone mountains
        }

        if (team.hasDTRBitmask(DTRBitmask.ROAD) && event.getBlock().getY() <= 40) {
            return; // allow players to mine under roads
        }

        if (!team.isMember(event.getPlayer().getUniqueId())) {
            PlayerBuildInOthersClaimEvent buildEvent = new PlayerBuildInOthersClaimEvent(event.getPlayer(), event.getBlock(), team);
            Bukkit.getPluginManager().callEvent(buildEvent);

            if (buildEvent.isWillIgnore()) {
                return;
            }

            if (!team.getName().equals(CavernHandler.getCavernTeamName())) {
                event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build in " + team.getName(event.getPlayer()) + ChatColor.YELLOW + "'s territory!");
            }
            
            event.setCancelled(true);

            if (!FoxListener.ATTACK_DISABLING_BLOCKS.contains(event.getBlock().getType())) {
                if (event.getBlock().isEmpty() || event.getBlock().getType().isTransparent() || !event.getBlock().getType().isSolid()) {
                    return;
                }
            }

            // We disable this to prevent block glitching
            Foxtrot.getInstance().getServerHandler().disablePlayerAttacking(event.getPlayer(), 1);
            return;
        }

        if (!team.isCoLeader(event.getPlayer().getUniqueId()) && !team.isCaptain(event.getPlayer().getUniqueId()) && !team.isOwner(event.getPlayer().getUniqueId())) {
            Subclaim subclaim = team.getSubclaim(event.getBlock().getLocation());

            if (subclaim != null && !subclaim.isMember(event.getPlayer().getUniqueId())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.YELLOW + "You do not have access to the subclaim " + ChatColor.GREEN + subclaim.getName() + ChatColor.YELLOW  + "!");
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (!event.isSticky()) {
            return;
        }

        Block retractBlock = event.getRetractLocation().getBlock();

        if (retractBlock.isEmpty() || retractBlock.isLiquid()) {
            return;
        }

        Team pistonTeam = LandBoard.getInstance().getTeam(event.getBlock().getLocation());
        Team targetTeam = LandBoard.getInstance().getTeam(retractBlock.getLocation());

        if (pistonTeam == targetTeam) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        Team pistonTeam = LandBoard.getInstance().getTeam(event.getBlock().getLocation());
        int i = 0;

        for (Block block : event.getBlocks()) {
            i++;

            Block targetBlock = event.getBlock().getRelative(event.getDirection(), i + 1);
            Team targetTeam = LandBoard.getInstance().getTeam(targetBlock.getLocation());

            if (targetTeam == pistonTeam || targetTeam == null || targetTeam.isRaidable()) {
                continue;
            }

            if (targetBlock.isEmpty() || targetBlock.isLiquid()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onHangingPlace(HangingPlaceEvent event) {
        if (Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer()) || Foxtrot.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getEntity().getLocation())) {
            return;
        }

        Team team = LandBoard.getInstance().getTeam(event.getEntity().getLocation());

        if (!team.isMember(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player) || Foxtrot.getInstance().getServerHandler().isAdminOverride((Player) event.getRemover())) {
            return;
        }

        if (Foxtrot.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getEntity().getLocation())) {
            return;
        }

        Team team = LandBoard.getInstance().getTeam(event.getEntity().getLocation());

        if (!team.isMember(event.getRemover().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType() != EntityType.ITEM_FRAME || Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (Foxtrot.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getRightClicked().getLocation())) {
            return;
        }

        Team team = LandBoard.getInstance().getTeam(event.getRightClicked().getLocation());

        if (!team.isMember(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    // Used for item frames
    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity().getType() != EntityType.ITEM_FRAME) {
            return;
        }

        Player damager = null;

        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();

            if (projectile.getShooter() instanceof Player) {
                damager = (Player) projectile.getShooter();
            }
        }

        if (damager == null || Foxtrot.getInstance().getServerHandler().isAdminOverride(damager) || Foxtrot.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getEntity().getLocation())) {
            return;
        }

        Team team = LandBoard.getInstance().getTeam(event.getEntity().getLocation());

        if (!team.isMember(event.getDamager().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onEntityDamageByEntity2(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player damager = PlayerUtils.getDamageSource(event.getDamager()); // find the player damager if one exists

        if (damager != null) {
            Team team = Foxtrot.getInstance().getTeamHandler().getTeam(damager);
            Player victim = (Player) event.getEntity();

            if (team != null && event.getCause() != EntityDamageEvent.DamageCause.FALL) {
                if (team.isMember(victim.getUniqueId())) {
                    if (Foxtrot.getInstance().getServerHandler().isAllowBoosting()) {
                        if (event.getDamager() instanceof FishHook) {
                            // allow fishing rod boosting
                            return;
                        } else if (event.getDamager() instanceof Player && !Enchantment.DURABILITY.canEnchantItem(damager.getItemInHand())) {
                            // allow melee boosting
                            event.setDamage(0.0D);
                            return;
                        }
                    }

                    damager.sendMessage(ChatColor.YELLOW + "You cannot hurt " + ChatColor.DARK_GREEN + victim.getName() + ChatColor.YELLOW + ".");
                    event.setCancelled(true);
                } else if (team.isAlly(victim.getUniqueId())) {
                    damager.sendMessage(ChatColor.YELLOW + "Be careful, that's your ally " + Team.ALLY_COLOR + victim.getName() + ChatColor.YELLOW + ".");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityHorseDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Horse)) {
            return;
        }

        Player damager = PlayerUtils.getDamageSource(event.getDamager()); // find the player damager if one exists
        Horse victim = (Horse) event.getEntity();

        if (damager != null && victim.isTamed()) {
            Team damagerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(damager);
            UUID horseOwner = victim.getOwner().getUniqueId();

            if(!damager.getUniqueId().equals(horseOwner) && damagerTeam != null && damagerTeam.isMember(horseOwner)) {
                event.setCancelled(true);
                damager.sendMessage(ChatColor.YELLOW + "This horse belongs to " + ChatColor.DARK_GREEN + FrozenUUIDCache.name(horseOwner) + ChatColor.YELLOW + " who is in your faction.");
            }
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Location checkLocation = event.getBlockClicked().getRelative(event.getBlockFace()).getLocation();

        if (Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer()) || Foxtrot.getInstance().getServerHandler().isUnclaimedOrRaidable(checkLocation)) {
            return;
        }

        Team owner = LandBoard.getInstance().getTeam(checkLocation);

        boolean canPlace = owner.hasDTRBitmask(DTRBitmask.KOTH) && Foxtrot.getInstance().getServerHandler().isWaterPlacementInClaimsAllowed();

        if (!owner.isMember(event.getPlayer().getUniqueId())) {
            if (!canPlace) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build in " + owner.getName(event.getPlayer()) + ChatColor.YELLOW + "'s territory!");
            } else {
                final Block waterBlock = event.getBlockClicked().getRelative(event.getBlockFace());

                if (waterBlock.getRelative(BlockFace.NORTH).isLiquid() || waterBlock.getRelative(BlockFace.SOUTH).isLiquid() || waterBlock.getRelative(BlockFace.EAST).isLiquid() || waterBlock.getRelative(BlockFace.WEST).isLiquid()) {
                    event.setCancelled(true);
                    return;
                }

                RegenUtils.schedule(waterBlock, 30, TimeUnit.SECONDS, (block) -> InventoryUtils.fillBucket(event.getPlayer()), (block) -> true);
            }
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        Location checkLocation = event.getBlockClicked().getRelative(event.getBlockFace()).getLocation();

        if (Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer()) || Foxtrot.getInstance().getServerHandler().isUnclaimedOrRaidable(checkLocation)) {
            return;
        }

        Team owner = LandBoard.getInstance().getTeam(checkLocation);

        if (!owner.isMember(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build in " + owner.getName(event.getPlayer()) + ChatColor.YELLOW + "'s territory!");
        }
    }
}