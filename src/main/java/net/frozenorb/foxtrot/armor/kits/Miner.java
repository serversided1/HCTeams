package net.frozenorb.foxtrot.armor.kits;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.armor.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Miner extends Kit implements Listener {
    private static final int Y_HEIGHT = 20;

    private ConcurrentMap<Player, Integer> noDamage = new ConcurrentHashMap<>();
    private ConcurrentMap<Player, Integer> invis = new ConcurrentHashMap<>();

    public Miner() {
        Runnable run = new Runnable() {
            public void run() {
                for (Player key : noDamage.keySet()) {
                    int left = noDamage.remove(key);

                    if (left == 0) {
                        if(key.getLocation().getY() <= Y_HEIGHT){
                            invis.put(key, 10);
                            key.sendMessage(ChatColor.BLUE + "Miner Invisibility" + ChatColor.YELLOW + " will be activated in 10 seconds!");
                        }

                        continue;
                    }

                    noDamage.put(key, left-1);
                }

                //Manage invisibility
                for(Player player : invis.keySet()){
                    if(player != null && player.isOnline()){
                        int secs = invis.get(player);

                        if(secs == 0){
                            if(player.getLocation().getY() <= Y_HEIGHT){
                                if(!(player.hasPotionEffect(PotionEffectType.INVISIBILITY))){
                                    player.sendMessage(ChatColor.BLUE + "Miner Invisibility" + ChatColor.YELLOW + " has been enabled!");
                                    player.addPotionEffect(PotionEffectType.INVISIBILITY.createEffect(Integer.MAX_VALUE, 0));
                                }
                            }
                        } else {
                            invis.put(player, secs - 1);
                        }
                    }
                }

                /*
                for (String player : Kit.getEquippedKits().keySet()) {
                    Kit on = Kit.getEquippedKits().get(player);

                    if (!(on instanceof Miner))
                        continue;

                    Player p = Bukkit.getPlayerExact(player);

                    if(p != null){
                        if (p.getLocation().getY() <= 20 && !noDamage.containsKey(p) && !p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                            p.addPotionEffect(PotionEffectType.INVISIBILITY.createEffect(Integer.MAX_VALUE, 0));
                        }

                        if (p.hasPotionEffect(PotionEffectType.INVISIBILITY) && (noDamage.containsKey(p) || p.getLocation().getY() > 20)) {
                            p.removePotionEffect(PotionEffectType.INVISIBILITY);
                        }
                    }
                }
                */
            }
        };

        Bukkit.getScheduler().scheduleSyncRepeatingTask(FoxtrotPlugin.getInstance(), run, 20, 20);
        Bukkit.getPluginManager().registerEvents(this, FoxtrotPlugin.getInstance());
    }

	@Override
	public boolean qualifies(Armor armor) {
		return armor.isFullSet(ArmorMaterial.IRON);
	}

	@Override
	public String getName() {
		return "Miner";
	}

	@Override
	public void apply(Player p) {
		p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
		p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1));
	}

	@Override
	public void remove(Player p) {
		p.removePotionEffect(PotionEffectType.NIGHT_VISION);
		p.removePotionEffect(PotionEffectType.FAST_DIGGING);
        p.removePotionEffect(PotionEffectType.INVISIBILITY);
        noDamage.remove(p);
        invis.remove(p);
	}

	@Override
	public int getWarmup() {
		return 10;
	}

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        Entity damage = event.getEntity();
        if (!(damage instanceof Player))
            return;

        Player pDamage = (Player)damage;

        if(!(hasKitOn(pDamage))){
            return;
        }

        if (noDamage.containsKey(pDamage)) {
            noDamage.remove(pDamage);
        }

        noDamage.put(pDamage, 15);

        //Invisibility
        if(invis.containsKey(pDamage) && invis.get(pDamage) != 0){
            invis.put(pDamage, 10);
            pDamage.removePotionEffect(PotionEffectType.INVISIBILITY);
            pDamage.sendMessage(ChatColor.BLUE + "Miner Invisibility" + ChatColor.YELLOW + " has been temporarily removed!");
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity hurt = event.getEntity();
        Entity damager = event.getDamager();

        if (!(damager instanceof Player))
            return;

        Player pDamager = (Player)damager;

        if(!(hasKitOn(pDamager))){
            return;
        }

        if(noDamage.containsKey(pDamager)){
            noDamage.remove(pDamager);
        }

        noDamage.put(pDamager, 15);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        Location to = event.getTo();
        Location from = event.getFrom();

        if(!(hasKitOn(player))){
            return;
        }

        if(to.getBlockY() <= Y_HEIGHT){ //Going below 20
            if(!(invis.containsKey(player))){
                invis.put(player, 10);
                player.sendMessage(ChatColor.BLUE + "Miner Invisibility" + ChatColor.YELLOW + " will be activated in 10 seconds!");
            }
        } else if(to.getBlockY() > Y_HEIGHT){ //Going above 20
            if(invis.containsKey(player)){
                noDamage.remove(player);
                invis.remove(player);
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                player.sendMessage(ChatColor.BLUE + "Miner Invisibility" + ChatColor.YELLOW + " has been removed!");
            }
        }
    }
}
