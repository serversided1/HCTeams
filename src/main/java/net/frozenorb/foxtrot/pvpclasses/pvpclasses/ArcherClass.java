package net.frozenorb.foxtrot.pvpclasses.pvpclasses;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.deathmessage.DeathMessageHandler;
import net.frozenorb.foxtrot.deathmessage.trackers.ArrowTracker;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
public class ArcherClass extends PvPClass {

    private static Map<String, Long> lastSpeedUsage = new HashMap<String, Long>();
    public static final Map<Integer, Float> TRUE_DAMAGE_VALUES = new HashMap<Integer, Float>();

    static {
        TRUE_DAMAGE_VALUES.put(11, 2F);
        TRUE_DAMAGE_VALUES.put(12, 2F);
        TRUE_DAMAGE_VALUES.put(13, 2F);
        TRUE_DAMAGE_VALUES.put(14, 2F);
        TRUE_DAMAGE_VALUES.put(15, 2F);
        TRUE_DAMAGE_VALUES.put(16, 2F);
        TRUE_DAMAGE_VALUES.put(17, 2F);
        TRUE_DAMAGE_VALUES.put(18, 2F);
        TRUE_DAMAGE_VALUES.put(19, 2F);
        TRUE_DAMAGE_VALUES.put(20, 2.5F);
        TRUE_DAMAGE_VALUES.put(21, 2.5F);
        TRUE_DAMAGE_VALUES.put(22, 2.5F);
        TRUE_DAMAGE_VALUES.put(23, 2.5F);
        TRUE_DAMAGE_VALUES.put(24, 2.5F);
        TRUE_DAMAGE_VALUES.put(25, 2.5F);
        TRUE_DAMAGE_VALUES.put(26, 2.5F);
        TRUE_DAMAGE_VALUES.put(27, 2.5F);
        TRUE_DAMAGE_VALUES.put(28, 3F);
        TRUE_DAMAGE_VALUES.put(29, 3F);
        TRUE_DAMAGE_VALUES.put(30, 3F);
        TRUE_DAMAGE_VALUES.put(31, 3F);
        TRUE_DAMAGE_VALUES.put(32, 3F);
        TRUE_DAMAGE_VALUES.put(33, 3F);
        TRUE_DAMAGE_VALUES.put(34, 3F);
        TRUE_DAMAGE_VALUES.put(35, 3F);
        TRUE_DAMAGE_VALUES.put(36, 3F);
        TRUE_DAMAGE_VALUES.put(37, 3F);
        TRUE_DAMAGE_VALUES.put(38, 3F);
        TRUE_DAMAGE_VALUES.put(39, 3F);
        TRUE_DAMAGE_VALUES.put(40, 3F);
        TRUE_DAMAGE_VALUES.put(41, 3.5F);
        TRUE_DAMAGE_VALUES.put(42, 3.5F);
        TRUE_DAMAGE_VALUES.put(43, 3.5F);
        TRUE_DAMAGE_VALUES.put(44, 3.5F);
        TRUE_DAMAGE_VALUES.put(45, 3.5F);
        TRUE_DAMAGE_VALUES.put(46, 3.5F);
        TRUE_DAMAGE_VALUES.put(47, 3.5F);
        TRUE_DAMAGE_VALUES.put(48, 3.5F);
        TRUE_DAMAGE_VALUES.put(49, 4F);
        TRUE_DAMAGE_VALUES.put(50, 4F);
        TRUE_DAMAGE_VALUES.put(51, 4F);
        TRUE_DAMAGE_VALUES.put(52, 4F);
        TRUE_DAMAGE_VALUES.put(53, 4F);
        TRUE_DAMAGE_VALUES.put(54, 4F);
        TRUE_DAMAGE_VALUES.put(55, 4F);
        TRUE_DAMAGE_VALUES.put(56, 4F);
        TRUE_DAMAGE_VALUES.put(57, 4.5F);
        TRUE_DAMAGE_VALUES.put(58, 4.5F);
        TRUE_DAMAGE_VALUES.put(59, 4.5F);
        TRUE_DAMAGE_VALUES.put(60, 4.5F);
        TRUE_DAMAGE_VALUES.put(61, 5F);
        TRUE_DAMAGE_VALUES.put(62, 5F);
        TRUE_DAMAGE_VALUES.put(63, 5F);
        TRUE_DAMAGE_VALUES.put(64, 5F);
        TRUE_DAMAGE_VALUES.put(65, 5F);
        TRUE_DAMAGE_VALUES.put(66, 5.5F);
        TRUE_DAMAGE_VALUES.put(67, 5.5F);
        TRUE_DAMAGE_VALUES.put(68, 5.5F);
        TRUE_DAMAGE_VALUES.put(69, 5.5F);
        TRUE_DAMAGE_VALUES.put(70, 5.5F);
        TRUE_DAMAGE_VALUES.put(71, 6F);
        TRUE_DAMAGE_VALUES.put(72, 6F);
        TRUE_DAMAGE_VALUES.put(73, 6F);
        TRUE_DAMAGE_VALUES.put(74, 6F);
        TRUE_DAMAGE_VALUES.put(75, 6F);
        TRUE_DAMAGE_VALUES.put(76, 6F);
        TRUE_DAMAGE_VALUES.put(77, 6F);
        TRUE_DAMAGE_VALUES.put(78, 6F);
        TRUE_DAMAGE_VALUES.put(79, 6F);
        TRUE_DAMAGE_VALUES.put(80, 6F);
        TRUE_DAMAGE_VALUES.put(81, 6F);
        TRUE_DAMAGE_VALUES.put(82, 6F);
        TRUE_DAMAGE_VALUES.put(83, 6F);
        TRUE_DAMAGE_VALUES.put(84, 6F);
        TRUE_DAMAGE_VALUES.put(85, 6F);
        TRUE_DAMAGE_VALUES.put(86, 6F);
        TRUE_DAMAGE_VALUES.put(87, 6F);
        TRUE_DAMAGE_VALUES.put(88, 6F);
        TRUE_DAMAGE_VALUES.put(89, 6.5F);
        TRUE_DAMAGE_VALUES.put(90, 6.5F);
        TRUE_DAMAGE_VALUES.put(91, 6.5F);
        TRUE_DAMAGE_VALUES.put(92, 6.5F);
        TRUE_DAMAGE_VALUES.put(93, 6.5F);
        TRUE_DAMAGE_VALUES.put(94, 6.5F);
        TRUE_DAMAGE_VALUES.put(95, 6.5F);
        TRUE_DAMAGE_VALUES.put(96, 6.5F);
        TRUE_DAMAGE_VALUES.put(97, 6.5F);
        TRUE_DAMAGE_VALUES.put(98, 6.5F);
        TRUE_DAMAGE_VALUES.put(99, 6.5F);
        TRUE_DAMAGE_VALUES.put(100, 6.5F);
        TRUE_DAMAGE_VALUES.put(101, 6.5F);
        TRUE_DAMAGE_VALUES.put(102, 6.5F);
        TRUE_DAMAGE_VALUES.put(103, 6.5F);
        TRUE_DAMAGE_VALUES.put(104, 6.5F);
        TRUE_DAMAGE_VALUES.put(105, 6.5F);
        TRUE_DAMAGE_VALUES.put(105, 6.5F);
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

    @EventHandler(priority=EventPriority.MONITOR)
    public void onEntityArrowHit(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();
            Player player = (Player) event.getEntity();

            if (arrow.getShooter() instanceof Player && arrow.hasMetadata("firedLoc")) {
                Location firedFrom = (Location) arrow.getMetadata("firedLoc").get(0).value();
                boolean intoEvent = DTRBitmaskType.ARCHER_DAMAGE_NORMALIZED.appliesAt(player.getLocation()) != DTRBitmaskType.ARCHER_DAMAGE_NORMALIZED.appliesAt(firedFrom);
                int range = Math.round((float) firedFrom.distance(player.getLocation()));
                float rawDamage = getDamage(range);

                if (rawDamage == -1F || intoEvent) {
                    ((Player) arrow.getShooter()).sendMessage(ChatColor.YELLOW + "[" + ChatColor.BLUE + "Arrow Range" + ChatColor.YELLOW + " (" + ChatColor.RED + range + ChatColor.YELLOW + ")] Damage " + ChatColor.RED + "Neutralized");
                    return;
                }

                if (PvPClassHandler.hasKitOn(player, this)) {
                    if (rawDamage > 2F) {
                        rawDamage = 2F;
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