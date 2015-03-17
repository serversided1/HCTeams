package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.qlib.qLib;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class CannonCommand {

    public static final int SPAWN_CANNON_MAX_DISTANCE = 600;
    public static final int SPAWN_CANNON_MIN_DISTANCE = 100;

    @Command(names={ "cannon launch", "spawncannon launch" }, permissionNode="")
    public static void cannonLaunch(Player sender) {
        if (Foxtrot.getInstance().getServerHandler().isEOTW()) {
            sender.sendMessage(ChatColor.RED + "Spawn cannon disabled: Server is in EOTW mode.");
            return;
        }

        if (!DTRBitmask.SAFE_ZONE.appliesAt(sender.getLocation()) || sender.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.BEACON) {
            sender.sendMessage(ChatColor.RED + "You must be standing on the spawn cannon.");
            return;
        }

        int x = qLib.RANDOM.nextInt(SPAWN_CANNON_MAX_DISTANCE - SPAWN_CANNON_MIN_DISTANCE) + SPAWN_CANNON_MIN_DISTANCE;
        int z = qLib.RANDOM.nextInt(SPAWN_CANNON_MAX_DISTANCE - SPAWN_CANNON_MIN_DISTANCE) + SPAWN_CANNON_MIN_DISTANCE;

        if (qLib.RANDOM.nextBoolean()) {
            x = -x;
        }

        if (qLib.RANDOM.nextBoolean()) {
            z = -z;
        }

        spawnCannon(sender, x, z);
    }

    @Command(names={ "cannon aim", "spawncannon aim" }, permissionNode="foxtrot.spawncannon.aim")
    public static void cannonAim(Player sender, @Parameter(name="x") int x, @Parameter(name="z") int z) {
        if (Foxtrot.getInstance().getServerHandler().isEOTW()) {
            sender.sendMessage(ChatColor.RED + "Spawn cannon disabled: Server is in EOTW mode.");
            return;
        }

        if (!DTRBitmask.SAFE_ZONE.appliesAt(sender.getLocation()) || sender.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.BEACON) {
            sender.sendMessage(ChatColor.RED + "You must be standing on the spawn cannon.");
            return;
        }

        int maxDistance = getMaxCannonDistance(sender);

        if (Math.abs(x) > maxDistance || Math.abs(z) > maxDistance) {
            sender.sendMessage(ChatColor.RED + "You cannot cannon that far. Your spawn cannon limit is " + maxDistance + ".");
            return;
        }

        if (Math.abs(x) < SPAWN_CANNON_MIN_DISTANCE || Math.abs(z) < SPAWN_CANNON_MIN_DISTANCE) {
            sender.sendMessage(ChatColor.RED + "You cannot cannon to a location that close to spawn! Cannon to a distance at least " + SPAWN_CANNON_MIN_DISTANCE + " blocks from spawn");
        }

        spawnCannon(sender, x, z);
    }

    public static void spawnCannon(final Player player, final int x, final int z) {
        player.sendMessage(ChatColor.YELLOW + "Cannoning to " + ChatColor.GREEN + x + ", " + z + ChatColor.YELLOW + ".");
        player.setVelocity(new Vector(0F, 1F, 0F));

        new BukkitRunnable() {

            int timer = 0;

            public void run() {
                timer++;

                if (timer < 40) {
                    if (timer % 2 == 0) {
                        player.setVelocity(new Vector(0F, 1F, 0F));
                        player.playSound(player.getLocation(), Sound.EXPLODE, 1F, 1F);
                    }
                } else if (timer == 40) {
                    Block block = player.getWorld().getBlockAt(x, 200, z);

                    while (block.getType() == Material.AIR && block.getY() > 1) {
                        block = block.getRelative(BlockFace.DOWN);
                    }

                    player.teleport(new Location(player.getWorld(), x, block.getY() + 100, z));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 4, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 10, 1000));
                } else if (timer > 40) {
                    if (player.isOnGround() || timer > 600) {
                        player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                        cancel();
                    }
                }
            }

        }.runTaskTimer(Foxtrot.getInstance(), 1L, 1L);
    }

    public static int getMaxCannonDistance(Player player) {
        if (player.hasPermission("foxtrot.spawncannon.1250")) {
            return (1250);
        } else if (player.hasPermission("foxtrot.spawncannon.1000")) {
            return (1000);
        } else if (player.hasPermission("foxtrot.spawncannon.750")) {
            return (750);
        } else if (player.hasPermission("foxtrot.spawncannon.500")) {
            return (500);
        }

        // Should never happen
        return (100);
    }

}