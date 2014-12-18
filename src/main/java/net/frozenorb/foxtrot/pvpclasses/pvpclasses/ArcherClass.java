package net.frozenorb.foxtrot.pvpclasses.pvpclasses;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.deathmessage.DeathMessageHandler;
import net.frozenorb.foxtrot.deathmessage.trackers.ArrowTracker;
import net.frozenorb.foxtrot.nametag.NametagManager;
import net.frozenorb.foxtrot.pvpclasses.PvPClass;
import net.frozenorb.foxtrot.pvpclasses.PvPClassHandler;
import net.frozenorb.foxtrot.team.dtr.bitmask.DTRBitmaskType;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.yaml.snakeyaml.error.Mark;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
public class ArcherClass extends PvPClass {

    public static final int MARK_SECONDS = 6;

    private static Map<String, Long> lastSpeedUsage = new HashMap<String, Long>();
    @Getter private static Map<String, Long> markedPlayers = new HashMap<String, Long>();

    public ArcherClass() {
        super("Archer", 15, "LEATHER_", Arrays.asList(Material.SUGAR));
    }

    @Override
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
    }

    @Override
    public void tick(Player player) {
        if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onEntityArrowHit(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();
            Player player = (Player) event.getEntity();

            if (!(arrow.getShooter() instanceof Player)) {
                return;
            }

            Player shooter = (Player) arrow.getShooter();

            if (!PvPClassHandler.hasKitOn(shooter, this)) {
                return;
            }

            int damage = 4; // 2 hearts

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

            shooter.sendMessage(ChatColor.YELLOW + "[" + ChatColor.BLUE + "Arrow Range" + ChatColor.YELLOW + " (" + ChatColor.RED + (int) distance + ChatColor.YELLOW + ")] " + ChatColor.BLUE + "Marked player for " + MARK_SECONDS + " seconds.");
            getMarkedPlayers().put(player.getName(), System.currentTimeMillis() + (MARK_SECONDS * 1000));

            NametagManager.reloadPlayer(player);

            new BukkitRunnable() {

                public void run() {
                    NametagManager.reloadPlayer(player);
                }

            }.runTaskLater(FoxtrotPlugin.getInstance(), (MARK_SECONDS * 20) + 1);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (ArcherClass.getMarkedPlayers().containsKey(player.getName()) && ArcherClass.getMarkedPlayers().get(player.getName()) > System.currentTimeMillis()) {
                // Apply 150% damage if they're 'marked'
                event.setDamage(event.getDamage() * 1.5D);
            }
        }
    }

    @Override
    public boolean itemConsumed(Player player, Material material) {
        if (lastSpeedUsage.containsKey(player.getName()) && lastSpeedUsage.get(player.getName()) > System.currentTimeMillis()) {
            Long millisLeft = ((lastSpeedUsage.get(player.getName()) - System.currentTimeMillis()) / 1000L) * 1000L;
            String msg = TimeUtils.getDurationBreakdown(millisLeft);

            player.sendMessage(ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
            return (false);
        }

        lastSpeedUsage.put(player.getName(), System.currentTimeMillis() + (1000L * 60 * 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 3), true);
        return (true);
    }

}