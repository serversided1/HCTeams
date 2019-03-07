package net.frozenorb.foxtrot.pvpclasses.pvpclasses;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import net.frozenorb.foxtrot.deathmessage.event.PlayerKilledEvent;
import net.frozenorb.foxtrot.map.kit.stats.StatsEntry;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.archer.ArcherUpgrade;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.archer.upgrades.ApolloArcherUpgrade;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.archer.upgrades.PythonArcherUpgrade;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.archer.upgrades.MedusaArcherUpgrade;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.archer.upgrades.HadesArcherUpgrade;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.archer.command.ArcherUpgradesCommand;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.com.google.gson.internal.Pair;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.deathmessage.DeathMessageHandler;
import net.frozenorb.foxtrot.deathmessage.trackers.ArrowTracker;
import net.frozenorb.foxtrot.pvpclasses.PvPClass;
import net.frozenorb.foxtrot.pvpclasses.PvPClassHandler;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.qlib.nametag.FrozenNametagHandler;
import net.frozenorb.qlib.util.TimeUtils;

@SuppressWarnings("deprecation")
public class ArcherClass extends PvPClass {

    private static final int MARK_SECONDS = 10;

    @Getter
    private static final List<ArcherUpgrade> archerUpgrades = Arrays.asList(
            new PythonArcherUpgrade(),
            new MedusaArcherUpgrade(),
            new ApolloArcherUpgrade(),
            new HadesArcherUpgrade()
    );

    private static final Map<String, Long> lastSpeedUsage = new HashMap<>();
    private static final Map<String, Long> lastJumpUsage = new HashMap<>();
    @Getter private static final Map<String, Long> markedPlayers = new ConcurrentHashMap<>();
    @Getter private static final Map<String, Set<Pair<String, Long>>> markedBy = new HashMap<>();

    public ArcherClass() {
        super("Archer", 15, Arrays.asList(Material.SUGAR, Material.FEATHER));

        if (Foxtrot.getInstance().getMapHandler().isKitMap() || Foxtrot.getInstance().getServerHandler().isVeltKitMap()) {
            FrozenCommandHandler.registerClass(ArcherUpgradesCommand.class);
        }
    }

    @Override
    public boolean qualifies(PlayerInventory armor) {
        return wearingAllArmor(armor) &&
               armor.getHelmet().getType() == Material.LEATHER_HELMET &&
               armor.getChestplate().getType() == Material.LEATHER_CHESTPLATE &&
               armor.getLeggings().getType() == Material.LEATHER_LEGGINGS &&
               armor.getBoots().getType() == Material.LEATHER_BOOTS;
    }

    @Override
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0), true);
    }

    @Override
    public void tick(Player player) {
        if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
        }

        if (!player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
        }

        super.tick(player);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onEntityArrowHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();
            final Player player = (Player) event.getEntity();

            if (!(arrow.getShooter() instanceof Player)) {
                return;
            }

            Player shooter = (Player) arrow.getShooter();
            float pullback = arrow.getMetadata("Pullback").get(0).asFloat();

            if (!PvPClassHandler.hasKitOn(shooter, this)) {
                return;
            }

            // 2 hearts for a marked shot
            // 1.5 hearts for a marking / unmarked shot.
            int damage = isMarked(player) ? 4 : 3; // Ternary for getting damage!

            // If the bow isn't 100% pulled back we do 1 heart no matter what.
            if (pullback < 0.5F) {
                damage = 2; // 1 heart
            }

            if (player.getHealth() - damage <= 0D) {
                event.setCancelled(true);
            } else {
                event.setDamage(0D);
            }

            // The 'ShotFromDistance' metadata is applied in the deathmessage module.
            Location shotFrom = (Location) arrow.getMetadata("ShotFromDistance").get(0).value();
            double distance = shotFrom.distance(player.getLocation());

            DeathMessageHandler.addDamage(player, new ArrowTracker.ArrowDamageByPlayer(player.getName(), damage, ((Player) arrow.getShooter()).getName(), shotFrom, distance));
            player.setHealth(Math.max(0D, player.getHealth() - damage));

            if (PvPClassHandler.hasKitOn(player, this)) {
                shooter.sendMessage(ChatColor.YELLOW + "[" + ChatColor.BLUE + "Arrow Range" + ChatColor.YELLOW + " (" + ChatColor.RED + (int) distance + ChatColor.YELLOW + ")] " + ChatColor.RED + "Cannot mark other Archers. " + ChatColor.BLUE.toString() + ChatColor.BOLD + "(" + damage / 2 + " heart" + ((damage / 2 == 1) ? "" : "s") + ")");
            } else if (pullback >= 0.5F) {
                shooter.sendMessage(ChatColor.YELLOW + "[" + ChatColor.BLUE + "Arrow Range" + ChatColor.YELLOW + " (" + ChatColor.RED + (int) distance + ChatColor.YELLOW + ")] " + ChatColor.GOLD + "Marked player for " + MARK_SECONDS + " seconds. " + ChatColor.BLUE.toString() + ChatColor.BOLD + "(" + damage / 2 + " heart" + ((damage / 2 == 1) ? "" : "s") + ")");

                // Only send the message if they're not already marked.
                if (!isMarked(player)) {
                    player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Marked! " + ChatColor.YELLOW + "An archer has shot you and marked you (+25% damage) for " + MARK_SECONDS + " seconds.");
                }

                PotionEffect invis = null;

                for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                    if (potionEffect.getType().equals(PotionEffectType.INVISIBILITY)) {
                        invis = potionEffect;
                        break;
                    }
                }

                if (invis != null) {
                    PvPClass playerClass = PvPClassHandler.getPvPClass(player);

                    player.removePotionEffect(invis.getType());

                    final PotionEffect invisFinal = invis;

                    /* Handle returning their invisibility after the archer tag is done */
                    if (playerClass instanceof MinerClass) {
                        /* Queue player to have invis returned. (MinerClass takes care of this) */
                        ((MinerClass) playerClass).getInvis().put(player.getName(), MARK_SECONDS);
                    } else {
                        /* player has no class but had invisibility, return it after their tag expires */
                        new BukkitRunnable() {

                            public void run() {
                                if(invisFinal.getDuration() > 1_000_000) {
                                    return; // Ensure we never apply an infinite invis to a non miner
                                }

                                player.addPotionEffect(invisFinal);
                            }

                        }.runTaskLater(Foxtrot.getInstance(), (MARK_SECONDS * 20) + 5);
                    }
                }

                getMarkedPlayers().put(player.getName(), System.currentTimeMillis() + (MARK_SECONDS * 1000));

                getMarkedBy().putIfAbsent(shooter.getName(), new HashSet<>());
                getMarkedBy().get(shooter.getName()).add(new Pair<>(player.getName(), System.currentTimeMillis() + (MARK_SECONDS * 1000)));

                FrozenNametagHandler.reloadPlayer(player);

                new BukkitRunnable() {
                    public void run() {
                        FrozenNametagHandler.reloadPlayer(player);
                    }
                }.runTaskLater(Foxtrot.getInstance(), (MARK_SECONDS * 20) + 5);

                // Check for special Archer archerUpgrades if kitmap
                if (Foxtrot.getInstance().getMapHandler().isKitMap() || Foxtrot.getInstance().getServerHandler().isVeltKitMap()) {
                    if (!ArcherUpgrade.canUseAbility(shooter)) {
                        player.sendMessage(ChatColor.RED + "You can't use your Archer Upgrade ability for another " + ChatColor.BOLD + TimeUtils.formatIntoDetailedString((int) (ArcherUpgrade.getRemainingTime(shooter) / 1000)) + ChatColor.RED + ".");
                        return;
                    }

                    for (ArcherUpgrade ability : archerUpgrades) {
                        if (Foxtrot.getInstance().getArcherKillsMap().getArcherKills(player.getUniqueId()) >= ability.getKillsNeeded()) {
                            if (ability.applies(shooter)) {
                                ability.onHit(shooter, player);
                                ArcherUpgrade.setCooldown(player, 10);
                                break;
                            }
                        }
                    }
                }
            } else {
                shooter.sendMessage(ChatColor.YELLOW + "[" + ChatColor.BLUE + "Arrow Range" + ChatColor.YELLOW + " (" + ChatColor.RED + (int) distance + ChatColor.YELLOW + ")] " + ChatColor.RED + "Bow wasn't fully drawn back. " + ChatColor.BLUE.toString() + ChatColor.BOLD + "(" + damage / 2 + " heart" + ((damage / 2 == 1) ? "" : "s") + ")");
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (isMarked(player)) {
                Player damager = null;
                if (event.getDamager() instanceof Player) {
                    damager = (Player) event.getDamager();
                } else if (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player) {
                    damager = (Player) ((Projectile) event.getDamager()).getShooter();
                }

                if (damager != null && !canUseMark(damager, player)) {
                    return;
                }

                // Apply 125% damage if they're 'marked'
                event.setDamage(event.getDamage() * 1.25D);
            }
        }
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        event.getProjectile().setMetadata("Pullback", new FixedMetadataValue(Foxtrot.getInstance(), event.getForce()));
    }

    @EventHandler
    public void onPlayerKilledEvent(PlayerKilledEvent event) {
        if (PvPClassHandler.hasKitOn(event.getKiller(), this)) {
            Foxtrot.getInstance().getArcherKillsMap().increment(event.getKiller().getUniqueId());
        }

        for (ArcherUpgrade upgrade : archerUpgrades) {
            if (Foxtrot.getInstance().getArcherKillsMap().getArcherKills(event.getKiller().getUniqueId()) == upgrade.getKillsNeeded()) {
                event.getKiller().sendMessage(ChatColor.GREEN + "You've unlocked the " + ChatColor.AQUA + ChatColor.BOLD + upgrade.getUpgradeName() + ChatColor.GREEN + " Archer Upgrade!");
                break;
            }
        }
    }

    @Override
    public boolean itemConsumed(Player player, Material material) {
        if (material == Material.SUGAR) {
            if (lastSpeedUsage.containsKey(player.getName()) && lastSpeedUsage.get(player.getName()) > System.currentTimeMillis()) {
                long millisLeft = lastSpeedUsage.get(player.getName()) - System.currentTimeMillis();
                String msg = TimeUtils.formatIntoDetailedString((int) millisLeft / 1000);

                player.sendMessage(ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
                return (false);
            }

            lastSpeedUsage.put(player.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 3), true);
            return (true);
        } else {
            if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
                player.sendMessage(ChatColor.RED + "You can't use this in spawn!");
                return (false);
            }

            if (lastJumpUsage.containsKey(player.getName()) && lastJumpUsage.get(player.getName()) > System.currentTimeMillis()) {
                long millisLeft = lastJumpUsage.get(player.getName()) - System.currentTimeMillis();
                String msg = TimeUtils.formatIntoDetailedString((int) millisLeft / 1000);

                player.sendMessage(ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
                return (false);
            }

            lastJumpUsage.put(player.getName(), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 5, 4));

            SpawnTagHandler.addPassiveSeconds(player, SpawnTagHandler.getMaxTagTime());
            return (false);
        }
    }

    public static boolean isMarked(Player player) {
        return (getMarkedPlayers().containsKey(player.getName()) && getMarkedPlayers().get(player.getName()) > System.currentTimeMillis());
    }

    private boolean canUseMark(Player player, Player victim) {
        if (Foxtrot.getInstance().getTeamHandler().getTeam(player) != null) {
            Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

            int amount = 0;
            for (Player member : team.getOnlineMembers()) {
                if (PvPClassHandler.hasKitOn(member, this)) {
                    amount++;

                    if (amount > 3) {
                        break;
                    }
                }
            }

            if (amount > 3) {
                player.sendMessage(ChatColor.RED + "Your team has too many archers. Archer mark was not applied.");
                return false;
            }
        }

        if (markedBy.containsKey(player.getName())) {
            for (Pair<String, Long> pair : markedBy.get(player.getName())) {
                if (victim.getName().equals(pair.first) && pair.second > System.currentTimeMillis()) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

}
