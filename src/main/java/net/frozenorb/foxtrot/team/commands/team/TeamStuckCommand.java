package net.frozenorb.foxtrot.team.commands.team;

import com.google.common.collect.Lists;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TeamStuckCommand implements Listener {

    private static final double MAX_DISTANCE = 5;

    private static final Set<Integer> warn = new HashSet<>();

    static {
        warn.add(300);
        warn.add(270);
        warn.add(240);
        warn.add(210);
        warn.add(180);
        warn.add(150);
        warn.add(120);
        warn.add(90);
        warn.add(60);
        warn.add(30);
        warn.add(10);
        warn.add(5);
        warn.add(4);
        warn.add(3);
        warn.add(2);
        warn.add(1);

        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new TeamStuckCommand(), Foxtrot.getInstance());
    }

    private static List<String> warping = Lists.newArrayList();
    private static List<String> damaged = Lists.newArrayList();

    @Command(names={ "team stuck", "t stuck", "f stuck", "faction stuck", "fac stuck", "stuck" }, permissionNode="")
    public static void teamStuck(final Player sender) {
        if (warping.contains(sender.getName())) {
            sender.sendMessage(ChatColor.RED +"You are already being warped!");
            return;
        }

        if (sender.getWorld().getEnvironment() != World.Environment.NORMAL) {
            sender.sendMessage(ChatColor.RED +"You can only use this command from the overworld.");
            return;
        }

        warping.add(sender.getName());

        new BukkitRunnable() {

            private int seconds = sender.isOp() && sender.getGameMode() == GameMode.CREATIVE ? 5 : 300;

            private Location loc = sender.getLocation();

            private int xStart = (int) loc.getX();
            private int yStart = (int) loc.getY();
            private int zStart = (int) loc.getZ();

            private Location nearest;

            @Override
            public void run() {
                if (damaged.contains(sender.getName())) {
                    sender.sendMessage(ChatColor.RED + "You took damage, teleportation cancelled!");
                    damaged.remove(sender.getName());
                    warping.remove(sender.getName());
                    cancel();
                    return;
                }

                if (!sender.isOnline()) {
                    warping.remove(sender.getName());
                    cancel();
                    return;
                }

                // Begin asynchronously searching for an available location prior to the actual teleport
                if (seconds == 5) {
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            nearest = nearestSafeLocation(sender.getLocation());
                        }

                    }.runTask(Foxtrot.getInstance());
                }

                if (seconds <= 0) {
                    if (nearest == null) {
                        kick(sender);
                    } else {
                        sender.teleport(nearest);
                        sender.sendMessage(ChatColor.YELLOW + "Teleported you to the nearest safe area!");
                    }

                    warping.remove(sender.getName());
                    cancel();
                    return;
                }

                Location loc = sender.getLocation();

                // More than 5 blocks away
                if ((loc.getX() >= xStart + MAX_DISTANCE || loc.getX() <= xStart - MAX_DISTANCE) || (loc.getY() >= yStart + MAX_DISTANCE || loc.getY() <= yStart - MAX_DISTANCE) || (loc.getZ() >= zStart + MAX_DISTANCE || loc.getZ() <= zStart - MAX_DISTANCE)) {
                    sender.sendMessage(ChatColor.RED + "You moved more than " + MAX_DISTANCE + " blocks, teleport cancelled!");
                    warping.remove(sender.getName());
                    cancel();
                    return;
                }

                if (warn.contains(seconds)) {
                    sender.sendMessage(ChatColor.YELLOW + "You will be teleported in " + ChatColor.RED.toString() + ChatColor.BOLD + TimeUtils.formatIntoMMSS(seconds) + ChatColor.YELLOW + "!");
                }

                seconds--;
            }

        }.runTaskTimer(Foxtrot.getInstance(), 0L, 20L);
    }

    private static Location nearestSafeLocation(Location origin) {
        LandBoard landBoard = LandBoard.getInstance();

        if (landBoard.getClaim(origin) == null) {
            return (getActualHighestBlock(origin.getBlock()).getLocation().add(0 , 1, 0));
        }

        // Start iterating outward on both positive and negative X & Z.
        for (int xPos = 0, xNeg = 0; xPos < 250; xPos++, xNeg--) {
            for (int zPos = 0, zNeg = 0; zPos < 250; zPos++, zNeg--) {
                Location atPos = origin.clone().add(xPos, 0, zPos);
                Location atNeg = origin.clone().add(xNeg, 0, zNeg);

                if (landBoard.getClaim(atPos) == null) {
                    return (getActualHighestBlock(atPos.getBlock()).getLocation().add(0, 1, 0));
                } else if (landBoard.getClaim(atNeg) == null) {
                    return (getActualHighestBlock(atNeg.getBlock()).getLocation().add(0, 1, 0));
                }
            }
        }

        return (null);
    }

    @EventHandler(priority= EventPriority.MONITOR, ignoreCancelled=true)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (warping.contains(player.getName())) {
                damaged.add(player.getName());
            }
        }
    }

    private static Block getActualHighestBlock(Block block) {
        block = block.getWorld().getHighestBlockAt(block.getLocation());

        while (block.getType() == Material.AIR && block.getY() > 0) {
            block = block.getRelative(BlockFace.DOWN);
        }

        return (block);
    }

    private static void kick(Player player){
        player.setMetadata("loggedout", new FixedMetadataValue(Foxtrot.getInstance(), true));
        player.kickPlayer(ChatColor.RED + "We couldn't find a safe location, so we safely logged you out for now. Contact a staff member before logging back on! " + ChatColor.BLUE + "TeamSpeak: TS.MineHQ.com");
    }

}