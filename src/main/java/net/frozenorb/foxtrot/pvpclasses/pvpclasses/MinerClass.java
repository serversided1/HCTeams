package net.frozenorb.foxtrot.pvpclasses.pvpclasses;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.pvpclasses.PvPClass;
import net.frozenorb.foxtrot.pvpclasses.PvPClassHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class MinerClass extends PvPClass implements Listener {

    private static final int Y_HEIGHT = 20;

    private Map<String, Integer> noDamage = new HashMap<String, Integer>();
    private Map<String, Integer> invis = new HashMap<String, Integer>();

    public MinerClass() {
        super("Miner", 10, "IRON_", null);

        new BukkitRunnable() {

            public void run() {
                for (String key : noDamage.keySet()) {
                    int left = noDamage.remove(key);
                    Player player = FoxtrotPlugin.getInstance().getServer().getPlayerExact(key);

                    if (player == null) {
                        continue;
                    }

                    if (left == 0) {
                        if (player.getLocation().getY() <= Y_HEIGHT) {
                            invis.put(player.getName(), 10);
                            player.sendMessage(ChatColor.BLUE + "Miner Invisibility" + ChatColor.YELLOW + " will be activated in 10 seconds!");
                        }
                    } else {
                        noDamage.put(player.getName(), left - 1);
                    }
                }

                //Manage invisibility
                for (String key : invis.keySet()){
                    Player player = FoxtrotPlugin.getInstance().getServer().getPlayerExact(key);

                    if (player != null) {
                        int secs = invis.get(player.getName());

                        if (secs == 0) {
                            if (player.getLocation().getY() <= Y_HEIGHT) {
                                if (!(player.hasPotionEffect(PotionEffectType.INVISIBILITY))) {
                                    player.sendMessage(ChatColor.BLUE + "Miner Invisibility" + ChatColor.YELLOW + " has been enabled!");
                                    player.addPotionEffect(PotionEffectType.INVISIBILITY.createEffect(Integer.MAX_VALUE, 0));
                                }
                            }
                        } else {
                            invis.put(player.getName(), secs - 1);
                        }
                    }
                }
            }

        }.runTaskTimer(FoxtrotPlugin.getInstance(), 20L, 20L);
    }

	@Override
	public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1));
	}

    @Override
    public void tick(Player player) {
        if (!player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0), true);
        }

        if (!player.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1), true);
        }
    }

	@Override
	public void remove(Player player) {
        removeInfiniteEffects(player);
        noDamage.remove(player.getName());
        invis.remove(player.getName());
	}

    @EventHandler(priority= EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player) || event.isCancelled()) {
            return;
        }

        Player player  = (Player) event.getEntity();

        if (!PvPClassHandler.hasKitOn(player, this)) {
            return;
        }

        noDamage.put(player.getName(), 15);

        if (invis.containsKey(player.getName()) && invis.get(player.getName()) != 0){
            invis.put(player.getName(), 10);
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            player.sendMessage(ChatColor.BLUE + "Miner Invisibility" + ChatColor.YELLOW + " has been temporarily removed!");
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || event.isCancelled()) {
            return;
        }

        Player player = (Player) event.getDamager();

        if (!PvPClassHandler.hasKitOn(player, this)) {
            return;
        }

        noDamage.put(player.getName(), 15);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        Location to = event.getTo();

        if(!PvPClassHandler.hasKitOn(player, this)){
            return;
        }

        if (to.getBlockY() <= Y_HEIGHT) { // Going below 20
            if (!invis.containsKey(player.getName())) {
                invis.put(player.getName(), 10);
                player.sendMessage(ChatColor.BLUE + "Miner Invisibility" + ChatColor.YELLOW + " will be activated in 10 seconds!");
            }
        } else if (to.getBlockY() > Y_HEIGHT) { // Going above 20
            if (invis.containsKey(player.getName())) {
                noDamage.remove(player.getName());
                invis.remove(player.getName());
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                player.sendMessage(ChatColor.BLUE + "Miner Invisibility" + ChatColor.YELLOW + " has been removed!");
            }
        }
    }

}