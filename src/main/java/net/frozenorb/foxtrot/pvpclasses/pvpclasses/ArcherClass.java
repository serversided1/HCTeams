package net.frozenorb.foxtrot.pvpclasses.pvpclasses;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.deathmessage.DeathMessageHandler;
import net.frozenorb.foxtrot.deathmessage.trackers.ArrowTracker;
import net.frozenorb.foxtrot.pvpclasses.PvPClass;
import net.frozenorb.foxtrot.pvpclasses.PvPClassHandler;
import net.frozenorb.foxtrot.team.bitmask.DTRBitmaskType;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
public class ArcherClass extends PvPClass {

    private static Map<String, Long> lastSpeedUsage = new HashMap<String, Long>();
    public static final Map<Integer, Float> TRUE_DAMAGE_VALUES = new HashMap<Integer, Float>();

    static {
        TRUE_DAMAGE_VALUES.put(10, 2F);
        TRUE_DAMAGE_VALUES.put(11, 2F);
        TRUE_DAMAGE_VALUES.put(12, 2F);
        TRUE_DAMAGE_VALUES.put(13, 2F);
        TRUE_DAMAGE_VALUES.put(14, 2F);
        TRUE_DAMAGE_VALUES.put(15, 2F);
        TRUE_DAMAGE_VALUES.put(16, 2F);
        TRUE_DAMAGE_VALUES.put(17, 2.5F);
        TRUE_DAMAGE_VALUES.put(18, 2.5F);
        TRUE_DAMAGE_VALUES.put(19, 2.5F);
        TRUE_DAMAGE_VALUES.put(20, 2.5F);
        TRUE_DAMAGE_VALUES.put(21, 3F);
        TRUE_DAMAGE_VALUES.put(22, 3F);
        TRUE_DAMAGE_VALUES.put(23, 3F);
        TRUE_DAMAGE_VALUES.put(24, 3F);
        TRUE_DAMAGE_VALUES.put(25, 3F);
        TRUE_DAMAGE_VALUES.put(26, 3.5F);
        TRUE_DAMAGE_VALUES.put(27, 3.5F);
        TRUE_DAMAGE_VALUES.put(28, 3.5F);
        TRUE_DAMAGE_VALUES.put(29, 4F);
        TRUE_DAMAGE_VALUES.put(30, 4F);
        TRUE_DAMAGE_VALUES.put(31, 4F);
        TRUE_DAMAGE_VALUES.put(32, 4.5F);
        TRUE_DAMAGE_VALUES.put(33, 4.5F);
        TRUE_DAMAGE_VALUES.put(34, 4.5F);
        TRUE_DAMAGE_VALUES.put(35, 4.5F);
        TRUE_DAMAGE_VALUES.put(36, 5F);
        TRUE_DAMAGE_VALUES.put(37, 5F);
        TRUE_DAMAGE_VALUES.put(38, 5F);
        TRUE_DAMAGE_VALUES.put(39, 5.5F);
        TRUE_DAMAGE_VALUES.put(40, 5.5F);
        TRUE_DAMAGE_VALUES.put(41, 5.5F);
        TRUE_DAMAGE_VALUES.put(42, 5.5F);
        TRUE_DAMAGE_VALUES.put(43, 6F);
        TRUE_DAMAGE_VALUES.put(44, 6F);
        TRUE_DAMAGE_VALUES.put(45, 6F);
        TRUE_DAMAGE_VALUES.put(46, 6F);
        TRUE_DAMAGE_VALUES.put(47, 6F);
        TRUE_DAMAGE_VALUES.put(48, 6.5F);
        TRUE_DAMAGE_VALUES.put(49, 6.5F);
        TRUE_DAMAGE_VALUES.put(50, 6.5F);
        TRUE_DAMAGE_VALUES.put(51, 6.5F);
        TRUE_DAMAGE_VALUES.put(52, 7F);
        TRUE_DAMAGE_VALUES.put(53, 7F);
        TRUE_DAMAGE_VALUES.put(54, 7F);
        TRUE_DAMAGE_VALUES.put(55, 7F);
        TRUE_DAMAGE_VALUES.put(56, 7F);
        TRUE_DAMAGE_VALUES.put(57, 7F);
        TRUE_DAMAGE_VALUES.put(58, 7.5F);
        TRUE_DAMAGE_VALUES.put(59, 7.5F);
        TRUE_DAMAGE_VALUES.put(60, 7.5F);
        TRUE_DAMAGE_VALUES.put(61, 7.5F);
        TRUE_DAMAGE_VALUES.put(62, 7.5F);
        TRUE_DAMAGE_VALUES.put(63, 7.5F);
        TRUE_DAMAGE_VALUES.put(64, 7.5F);
        TRUE_DAMAGE_VALUES.put(65, 7.5F);
        TRUE_DAMAGE_VALUES.put(66, 7.5F);
        TRUE_DAMAGE_VALUES.put(67, 7.5F);
        TRUE_DAMAGE_VALUES.put(68, 7.5F);
        TRUE_DAMAGE_VALUES.put(69, 8F);
        TRUE_DAMAGE_VALUES.put(70, 8F);
        TRUE_DAMAGE_VALUES.put(71, 8F);
        TRUE_DAMAGE_VALUES.put(72, 8F);
        TRUE_DAMAGE_VALUES.put(73, 8F);
        TRUE_DAMAGE_VALUES.put(74, 8F);
        TRUE_DAMAGE_VALUES.put(75, 8F);
        TRUE_DAMAGE_VALUES.put(76, 8F);
        TRUE_DAMAGE_VALUES.put(77, 8F);
        TRUE_DAMAGE_VALUES.put(78, 8F);
        TRUE_DAMAGE_VALUES.put(79, 8F);
        TRUE_DAMAGE_VALUES.put(80, 8F);
        TRUE_DAMAGE_VALUES.put(81, 8F);
        TRUE_DAMAGE_VALUES.put(82, 8F);
        TRUE_DAMAGE_VALUES.put(83, 8F);
        TRUE_DAMAGE_VALUES.put(84, 8F);
        TRUE_DAMAGE_VALUES.put(85, 8F);
        TRUE_DAMAGE_VALUES.put(86, 8F);
        TRUE_DAMAGE_VALUES.put(87, 8F);
        TRUE_DAMAGE_VALUES.put(88, 8F);
        TRUE_DAMAGE_VALUES.put(89, 8F);
        TRUE_DAMAGE_VALUES.put(90, 8F);
        TRUE_DAMAGE_VALUES.put(91, 8F);
        TRUE_DAMAGE_VALUES.put(92, 8F);
        TRUE_DAMAGE_VALUES.put(93, 8F);
        TRUE_DAMAGE_VALUES.put(94, 8F);
        TRUE_DAMAGE_VALUES.put(95, 8F);
        TRUE_DAMAGE_VALUES.put(96, 8F);
        TRUE_DAMAGE_VALUES.put(97, 8F);
        TRUE_DAMAGE_VALUES.put(98, 8F);
        TRUE_DAMAGE_VALUES.put(99, 8F);
        TRUE_DAMAGE_VALUES.put(100, 8F);
        TRUE_DAMAGE_VALUES.put(101, 8F);
        TRUE_DAMAGE_VALUES.put(102, 8F);
        TRUE_DAMAGE_VALUES.put(103, 8F);
        TRUE_DAMAGE_VALUES.put(104, 8F);
        TRUE_DAMAGE_VALUES.put(105, 8F);
    }

    private float getDamage(int range) {
        return (TRUE_DAMAGE_VALUES.containsKey(range) ? TRUE_DAMAGE_VALUES.get(range) : -1F);
    }

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

    @Override
    public void remove(Player player) {
        removeInfiniteEffects(player);
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player && event.getEntity() instanceof Arrow) {
            Player player = (Player) event.getEntity().getShooter();
            Arrow arrow = (Arrow) event.getEntity();

            if (PvPClassHandler.hasKitOn(player, this)) {
                arrow.setMetadata("firedLoc", new FixedMetadataValue(FoxtrotPlugin.getInstance(), player.getLocation()));
            }
        }
    }

    @EventHandler(priority=EventPriority.LOW)
    public void onEntityArrowHit(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();
            Player player = (Player) event.getEntity();

            if (arrow.hasMetadata("firedLoc")) {
                Location firedFrom = (Location) arrow.getMetadata("firedLoc").get(0).value();
                boolean intoEvent = DTRBitmaskType.ARCHER_DAMAGE_NORMALIZED.appliesAt(player.getLocation()) != DTRBitmaskType.ARCHER_DAMAGE_NORMALIZED.appliesAt(firedFrom);
                int range = Math.round((float) firedFrom.distance(player.getLocation()));
                float rawDamage = getDamage(range);

                if (rawDamage == -1F || intoEvent) {
                    ((Player) arrow.getShooter()).sendMessage(ChatColor.YELLOW + "[" + ChatColor.BLUE + "Arrow Range" + ChatColor.YELLOW + " (" + ChatColor.RED + range + ChatColor.YELLOW + ")] Damage " + ChatColor.RED + "Neutralized");
                    return;
                }

                if (PvPClassHandler.hasKitOn(player, this)) {
                    if (rawDamage > 3.5F) {
                        rawDamage = 3.5F;
                    }

                    player.sendMessage(ChatColor.YELLOW + "Reduced " + ChatColor.BLUE + "Incoming Arrow Damage");
                }

                int damage = Math.round(rawDamage * 2);

                if (player.getHealth() - damage <= 0) {
                    event.setCancelled(true);
                } else {
                    event.setDamage(0D);
                }

                DeathMessageHandler.addDamage(player, new ArrowTracker.ArrowDamageByPlayer(player.getName(), damage, ((Player) arrow.getShooter()).getName(), firedFrom, range));
                player.setHealth(Math.max(0D, player.getHealth() - damage));

                ((Player) arrow.getShooter()).sendMessage(ChatColor.YELLOW + "[" + ChatColor.BLUE + "Arrow Range" + ChatColor.YELLOW + " (" + ChatColor.RED + range + ChatColor.YELLOW + ")] Damage Output => " + ChatColor.BLUE.toString() + ChatColor.BOLD + rawDamage + " Hearts");
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