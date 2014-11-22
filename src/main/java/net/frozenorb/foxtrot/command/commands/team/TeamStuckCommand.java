package net.frozenorb.foxtrot.command.commands.team;

import com.google.common.collect.Lists;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.team.TeamHandler;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Connor Hollasch
 * @since 10/14/14
 */
public class TeamStuckCommand implements Listener {

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

        FoxtrotPlugin.getInstance().getServer().getPluginManager().registerEvents(new TeamStuckCommand(), FoxtrotPlugin.getInstance());
    }

    private static List<String> warping = Lists.newArrayList();
    private static List<String> damaged = Lists.newArrayList();

    @Command(names={ "team stuck", "t stuck", "f stuck", "faction stuck", "fac stuck", "stuck" }, permissionNode="")
    public static void teamStuck(Player sender) {
        if (warping.contains(sender.getName())) {
            sender.sendMessage(ChatColor.RED +"You are already being warped!");
            return;
        }

        if (sender.getWorld().getEnvironment() != World.Environment.NORMAL) {
            sender.sendMessage(ChatColor.RED +"You can only use this command from the overworld.");
            return;
        }

        warping.add(sender.getName());

        new BukkitRunnable(){
            private int seconds = 300;

            Location loc = sender.getLocation();
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
                if(damaged.contains(sender.getName())){
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

                //Begin asynchronously searching for an available location prior to the actual teleport
                if (seconds == 5) {
                    new BukkitRunnable(){
                        @Override
                        public void run(){
                            nearest = nearestSafeLocation(sender.getLocation());
                            nearestFound = true;

                            if(tpOnceFound){
                                new BukkitRunnable(){
                                    @Override
                                    public void run(){
                                        if(nearest == null){
                                            kick(sender);
                                        } else {
                                            sender.sendMessage(ChatColor.GREEN + "Found location, sorry for delay! Teleported you to the nearest safe area!");
                                            sender.teleport(nearest);
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
                            kick(sender);
                        } else {
                            sender.teleport(nearest);
                            sender.sendMessage(ChatColor.GREEN + "Teleported you to the nearest safe area!");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Still searching for a safe location, this is taking fairly long. Please report this to a staff member.");
                        sender.sendMessage(ChatColor.RED + "You will be teleported once a location is found.");
                        tpOnceFound = true;
                    }

                    warping.remove(sender.getName());
                    cancel();
                    return;
                }

                Location loc = sender.getLocation();

                //More than 5 blocks away
                if((loc.getX() >= xStart + MAX_DISTANCE || loc.getX() <= xStart - MAX_DISTANCE) || (loc.getY() >= yStart + MAX_DISTANCE || loc.getY() <= yStart - MAX_DISTANCE) || (loc.getZ() >= zStart + MAX_DISTANCE || loc.getZ() <= zStart - MAX_DISTANCE)){
                    cancel();
                    sender.sendMessage(ChatColor.RED + "You moved more than " + MAX_DISTANCE + " blocks, teleport cancelled!");
                    warping.remove(sender.getName());
                    return;
                }

                //More than 20 blocks of accumulated movement
                if(prevLoc != null){
                    totalMovement += loc.distanceSquared(prevLoc);
                    prevLoc = loc;

                    if(totalMovement >= TOTAL_MOVEMENT * TOTAL_MOVEMENT){
                        sender.sendMessage(ChatColor.RED + "You walked more than " + TOTAL_MOVEMENT + " total meters, teleport cancelled!");
                        warping.remove(sender.getName());
                        cancel();
                        return;
                    }
                }

                if (warn.contains(seconds)){
                    sender.sendMessage(ChatColor.YELLOW + "You will be teleported in " + ChatColor.RED + "" + ChatColor.BOLD + TimeUtils.getMMSS(seconds) + ChatColor.RED + "!");
                }

                seconds--;
            }
        }.runTaskTimer(FoxtrotPlugin.getInstance(), 20L, 20L);
    }

    private static Location nearestSafeLocation(Location origin) {
        LandBoard landBoard = LandBoard.getInstance();

        if (landBoard.getClaim(origin) == null) {
            return origin.getWorld().getHighestBlockAt(origin).getLocation();
        }

        for (int xPos = 0, xNeg = 0; xPos < 500; xPos++, xNeg--) {
            for (int zPos = 0, zNeg = 0; zPos < 500; zPos++, zNeg--) {
                Location atPos = origin.clone().add(xPos, 0, zPos);
                Location atNeg = origin.clone().add(xNeg, 0, zNeg);

                if (landBoard.getClaim(origin) == null) {
                    return atPos.getWorld().getHighestBlockAt(atPos).getLocation();
                } else if (landBoard.getClaim(origin) == null) {
                    return atNeg.getWorld().getHighestBlockAt(atNeg).getLocation();
                }
            }
        }

        return null;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (warping.contains(player.getName())) {
                damaged.add(player.getName());
            }
        }
    }

    private static void kick(Player player){
        player.setMetadata("loggedout", new FixedMetadataValue(FoxtrotPlugin.getInstance(), true));
        player.kickPlayer("§cWe couldn't find a location to TP you, so we safely logged you out for now." + "\n" + "§cContact a staff member before logging back on!" + "\n" + "§bTeamSpeak: ts.minehq.com");
    }
}
