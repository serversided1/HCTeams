package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import com.google.common.collect.Lists;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.BaseCommand;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.team.TeamManager;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Connor Hollasch
 * @since 10/14/14
 */
public class Stuck extends Subcommand implements Listener {

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
    }

    public Stuck(String arg1, String arg2, String... arg3) {
        super(arg1, arg2, arg3);
        Bukkit.getPluginManager().registerEvents(this, FoxtrotPlugin.getInstance());
    }

    private List<Player> warping = Lists.newArrayList();
    private List<Player> damaged = Lists.newArrayList();

    @Override
    public void syncExecute() {
        Player player = (Player)sender;

        if (warping.contains(player)) {
            player.sendMessage(ChatColor.RED+"You are already being warped!");
            return;
        }

        Runnable async = new Runnable() {
            public void run() {
                final Location nearest = nearestSafeLocation(player.getLocation());
                if (nearest == null) {
                    player.sendMessage(ChatColor.RED+"Could not find a safe location near you!");
                    return;
                }

                player.sendMessage(ChatColor.YELLOW + "You will be teleported to the nearest safe area in 5 minutes!");
                warping.add(player);

                BukkitTask tp = new BukkitRunnable() {
                    private int seconds = 300;

                    private int xStart = (int) player.getLocation().getX();
                    private int zStart = (int) player.getLocation().getZ();

                    public void run() {
                        if (damaged.contains(player)) {
                            player.sendMessage(ChatColor.RED+"You took damage, teleportation cancelled!");
                            damaged.remove(player);
                            cancel();
                            return;
                        }

                        if (!(player.isOnline())) {
                            warping.remove(player);
                            cancel();
                            return;
                        }

                        if (seconds <= 0) {
                            player.teleport(nearest);
                            player.sendMessage(ChatColor.GREEN+"Teleported you to the nearest safe area!");
                            warping.remove(player);
                            cancel();
                            return;
                        }

                        if ((player.getLocation().getX() >= xStart+5 || player.getLocation().getX() <= xStart-5)
                                || (player.getLocation().getZ() >= zStart+5 || player.getLocation().getZ() <= zStart-5)) {

                            cancel();
                            player.sendMessage(ChatColor.RED+"You moved more than 5 blocks, teleport cancelled!");
                            warping.remove(player);
                            return;
                        }

                        if (warn.contains(seconds)) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou will be teleported in &c&l" + TimeUtils.getMMSS(seconds) + "&r&c!"));
                        }

                        seconds--;
                    }
                }.runTaskTimer(FoxtrotPlugin.getInstance(), 20l, 20l);
            }
        };
        Bukkit.getScheduler().scheduleAsyncDelayedTask(FoxtrotPlugin.getInstance(), async);
    }

    private static Location nearestSafeLocation(Location origin) {
        TeamManager tm = FoxtrotPlugin.getInstance().getTeamManager();
        if (!tm.isTaken(origin))
            return origin.getWorld().getHighestBlockAt(origin).getLocation();

        if (FoxtrotPlugin.getInstance().getServerManager().isSpawn(origin))
            return origin.getWorld().getHighestBlockAt(origin).getLocation();

        for (int xPos = 0, xNeg = 0; xPos < 500; xPos++, xNeg--) {
            for (int zPos = 0, zNeg = 0; zPos < 500; zPos++, zNeg--) {
                Location atPos = origin.clone().add(xPos, 0, zPos);
                Location atNeg = origin.clone().add(xNeg, 0, zNeg);
                if (!(tm.isTaken(atPos)) || FoxtrotPlugin.getInstance().getServerManager().isSpawn(atPos))
                    return atPos.getWorld().getHighestBlockAt(atPos).getLocation();
                if (!(tm.isTaken(atNeg)) || FoxtrotPlugin.getInstance().getServerManager().isSpawn(atNeg))
                    return atNeg.getWorld().getHighestBlockAt(atNeg).getLocation();
            }
        }

        return null;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player)event.getEntity();
            if (warping.contains(player)) {
                damaged.add(player);
            }
        }
    }
}
