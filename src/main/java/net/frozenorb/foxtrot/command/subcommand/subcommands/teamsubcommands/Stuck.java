package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import com.google.common.collect.Lists;
import net.frozenorb.foxtrot.FoxtrotPlugin;
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
import org.bukkit.metadata.FixedMetadataValue;
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
    private static final double MAX_DISTANCE = 5;
    private static final double TOTAL_MOVEMENT = 20;


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
            player.sendMessage(ChatColor.RED +"You are already being warped!");
            return;
        }

        new BukkitRunnable(){
            private int seconds = (player.getName().equals("Nauss") ? 10 : 300);

            Location loc = player.getLocation();
            private int xStart = (int) loc.getX();
            private int yStart = (int) loc.getY();
            private int zStart = (int) loc.getZ();

            private Location nearest;
            private boolean nearestFound = false;
            private boolean tpOnceFound = false;

            private Location prevLoc;
            private double totalMovement = 0;

            @Override
            public void run(){
                if(damaged.contains(player)){
                    player.sendMessage(ChatColor.RED + "You took damage, teleportation cancelled!");
                    damaged.remove(player);
                    warping.remove(player);
                    cancel();
                    return;
                }

                if(!(player.isOnline())){
                    warping.remove(player);
                    cancel();
                    return;
                }

                //Begin asynchronously searching for an available location prior to the actual teleport
                if(seconds == 5){
                    new BukkitRunnable(){
                        @Override
                        public void run(){
                            nearest = nearestSafeLocation(player.getLocation());
                            nearestFound = true;

                            if(tpOnceFound){
                                new BukkitRunnable(){
                                    @Override
                                    public void run(){
                                        if(nearest == null){
                                            kick(player);
                                        } else {
                                            player.sendMessage(ChatColor.GREEN + "Found location, sorry for delay! Teleported you to the nearest safe area!");
                                            player.teleport(nearest);
                                        }
                                    }
                                }.runTask(FoxtrotPlugin.getInstance());
                            }
                        }
                    }.runTaskAsynchronously(FoxtrotPlugin.getInstance());
                }

                if(seconds <= 0){
                    if(nearestFound){
                        if(nearest == null){
                            kick(player);
                        } else {
                            player.teleport(nearest);
                            player.sendMessage(ChatColor.GREEN + "Teleported you to the nearest safe area!");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Still searching for a safe location, this is taking fairly long. Please report this to a staff member.");
                        player.sendMessage(ChatColor.RED + "You will be teleported once a location is found.");
                        tpOnceFound = true;
                    }

                    warping.remove(player);
                    cancel();
                    return;
                }

                Location loc = player.getLocation();

                //More than 5 blocks away
                if((loc.getX() >= xStart + MAX_DISTANCE || loc.getX() <= xStart - MAX_DISTANCE) || (loc.getY() >= yStart + MAX_DISTANCE || loc.getY() <= yStart - MAX_DISTANCE) || (loc.getZ() >= zStart + MAX_DISTANCE || loc.getZ() <= zStart - MAX_DISTANCE)){
                    cancel();
                    player.sendMessage(ChatColor.RED + "You moved more than " + MAX_DISTANCE + " blocks, teleport cancelled!");
                    warping.remove(player);
                    return;
                }

                //More than 20 blocks of accumulated movement
                if(prevLoc != null){
                    totalMovement += loc.distanceSquared(prevLoc);
                    prevLoc = loc;

                    if(totalMovement >= TOTAL_MOVEMENT * TOTAL_MOVEMENT){
                        player.sendMessage(ChatColor.RED + "You walked more than " + TOTAL_MOVEMENT + " total meters, teleport cancelled!");
                        warping.remove(player);
                        cancel();
                        return;
                    }
                }

                if (warn.contains(seconds)){
                    player.sendMessage(ChatColor.YELLOW + "You will be teleported in " + ChatColor.RED + "" + ChatColor.BOLD + TimeUtils.getMMSS(seconds) + ChatColor.RED + "!");
                }

                seconds--;
            }
        }.runTaskTimer(FoxtrotPlugin.getInstance(), 20L, 20L);
    }

    private static Location nearestSafeLocation(Location origin) {
        TeamManager tm = FoxtrotPlugin.getInstance().getTeamManager();
        if (!tm.isTaken(origin))
            return origin.getWorld().getHighestBlockAt(origin).getLocation();

        if (FoxtrotPlugin.getInstance().getServerManager().isGlobalSpawn(origin))
            return origin.getWorld().getHighestBlockAt(origin).getLocation();

        for (int xPos = 0, xNeg = 0; xPos < 500; xPos++, xNeg--) {
            for (int zPos = 0, zNeg = 0; zPos < 500; zPos++, zNeg--) {
                Location atPos = origin.clone().add(xPos, 0, zPos);
                Location atNeg = origin.clone().add(xNeg, 0, zNeg);
                if (!(tm.isTaken(atPos)) || FoxtrotPlugin.getInstance().getServerManager().isGlobalSpawn(atPos))
                    return atPos.getWorld().getHighestBlockAt(atPos).getLocation();
                if (!(tm.isTaken(atNeg)) || FoxtrotPlugin.getInstance().getServerManager().isGlobalSpawn(atNeg))
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

    private void kick(Player player){
        player.setMetadata("loggedout", new FixedMetadataValue(FoxtrotPlugin.getInstance(), true));
        player.kickPlayer("§cWe couldn't find a location to TP you, so we safely logged you out for now." + "\n" + "§cContact a staff member before logging back on!" + "\n" + "§bTeamSpeak: ts.minehq.com");
    }
}
