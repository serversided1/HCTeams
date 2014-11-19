package net.frozenorb.foxtrot.command.commands;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
public class FocusCommand {

    public static Map<String, Focusable> currentTrackers = new HashMap<>();

    @Command(names={ "Focus", "Track", "Hunt" }, permissionNode="")
    public static void focus(Player sender, @Param(name="<reset | x,z | player>") String param) {
        //Check for compass
        if (sender.getItemInHand() == null || sender.getItemInHand().getType() != Material.COMPASS) {
            sender.sendMessage(ChatColor.RED + "You must be holding a compass to do this!");
            return;
        }

        //Check for "reset"
        if (param.equalsIgnoreCase("reset")) {
            Focusable focusable = new Focusable(ChatColor.YELLOW + "the " + ChatColor.RED + "Warzone") {
                Location loc = null;

                @Override
                public Location updateLocation() {
                    if (loc == null) {
                        loc = new Location(Bukkit.getWorlds().get(0), 0, 70, 0);
                    }

                    return loc;
                }

                @Override
                public FocusType getFocusType() {
                    return (FocusType.WARZONE);
                }
            };

            focus(sender, focusable);
            return;
        }

        //Check for x,z
        if (param.contains(",")) {
            String[] split = param.split(",");

            try {
                double x = Double.parseDouble(split[0]);
                double z = Double.parseDouble(split[1]);

                Focusable focusable = new Focusable(ChatColor.LIGHT_PURPLE + "(" + ChatColor.AQUA + (int) x + ChatColor.LIGHT_PURPLE + ", " + ChatColor.AQUA + (int) z + ChatColor.LIGHT_PURPLE + ")") {
                    Location loc = null;

                    @Override
                    public Location updateLocation() {
                        if (loc == null) {
                            loc = new Location(Bukkit.getWorlds().get(0), x, 70, z);
                        }

                        return loc;
                    }

                    @Override
                    public FocusType getFocusType() {
                        return (FocusType.LOCATION);
                    }
                };

                focus(sender, focusable);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid X and Z coordinates!");
            }

            return;
        }

        //Check for player
        Player target = Bukkit.getPlayer(param);

        //Invalid player
        if (target == null || target.hasMetadata("invisible")) {
            sender.sendMessage(ChatColor.RED + "Player '" + param + "' could not be found.");
            return;
        }

        //Team check
        if (FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName()) == null) {
            sender.sendMessage(ChatColor.RED + "You are not on a team!");
            return;
        }

        net.frozenorb.foxtrot.team.Team senderTeam = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());
        net.frozenorb.foxtrot.team.Team targetTeam = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(target.getName());

        if (senderTeam !=  targetTeam) {
            sender.sendMessage(ChatColor.RED + "You can only track players that are on the same team as you!");
            return;
        }

        if (sender.equals(target)) {
            sender.sendMessage(ChatColor.RED + "You may not focus on yourself!");
            return;
        }

        //World check
        if (!target.getWorld().equals(sender.getWorld())) {
            sender.sendMessage(ChatColor.RED + "That player is not in the same world as you!");
            return;
        }

        //Start tracking
        final String cacheName = target.getName();

        Focusable focusable = new Focusable(target.getDisplayName()) {
            @Override
            public FocusType getFocusType() {
                return (FocusType.PLAYER);
            }

            @Override
            public Location updateLocation() {
                Player pss = Bukkit.getPlayerExact(cacheName);

                if (pss == null) {
                    return null;
                }

                return pss.getLocation();
            }
        };

        focus(sender, focusable);
    }

    private static void focus(Player player, Focusable focusable) {
        if (currentTrackers.containsKey(player.getName())) {
            currentTrackers.remove(player.getName()).cancel();
        }

        currentTrackers.put(player.getName(), focusable);
        focusable.start(player);
        player.sendMessage(ChatColor.YELLOW + "You have begun to focus on " + ChatColor.RED + focusable.data + ChatColor.YELLOW + ".");
    }

    @RequiredArgsConstructor
    public static abstract class Focusable extends BukkitRunnable {
        private Player p;
        @NonNull
        private String data;
        private Location lastLocation;

        public abstract FocusType getFocusType();

        public abstract Location updateLocation();

        public void start(Player p) {
            this.p = p;
            lastLocation = updateLocation();
            runTaskTimer(FoxtrotPlugin.getInstance(), 0L, 20L);
        }

        @Override
        public void run() {
            Location l = updateLocation();

            if (l == null) {
                if (getFocusType() == FocusType.TEAM) {
                    p.sendMessage(ChatColor.YELLOW + "Focus cancelled! §c" + data + "§e no longer has claimed territory.");
                    currentTrackers.remove(p.getName());
                    cancel();
                    return;
                } else if (getFocusType() == FocusType.PLAYER) {
                    if (lastLocation != null) {
                        p.sendMessage(data + " §elogged out and will be refocused when they log in!");
                    }

                    lastLocation = null;
                    return;
                }
            } else {
                if (lastLocation == null) {
                    p.sendMessage(data + " §alogged back in and is now being focused!");

                }
            }

            this.lastLocation = l;
            p.setCompassTarget(l);
        }
    }

    private static enum FocusType {
        WARZONE,
        TEAM,
        PLAYER,
        LOCATION;
    }

}