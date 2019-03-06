package net.frozenorb.foxtrot.pvpclasses.pvpclasses;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.pvpclasses.PvPClass;
import net.frozenorb.foxtrot.pvpclasses.PvPClassHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class MinerClass extends PvPClass implements Listener {

    private static final int Y_HEIGHT = 20;

    private Map<String, Integer> noDamage = new HashMap<>();
    @Getter private Map<String, Integer> invis = new HashMap<>();

    public MinerClass() {
        super("Miner", 10, null);

        new BukkitRunnable() {

            public void run() {
                for (String key : new HashMap<>(noDamage).keySet()) {
                    int left = noDamage.remove(key);
                    Player player = Foxtrot.getInstance().getServer().getPlayerExact(key);

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
                for (String key : new HashMap<>(invis).keySet()){
                    Player player = Foxtrot.getInstance().getServer().getPlayerExact(key);

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

        }.runTaskTimer(Foxtrot.getInstance(), 20L, 20L);
    }

    @Override
    public boolean qualifies(PlayerInventory armor) {
        return wearingAllArmor(armor) &&
               armor.getHelmet().getType() == Material.IRON_HELMET &&
               armor.getChestplate().getType() == Material.IRON_CHESTPLATE &&
               armor.getLeggings().getType() == Material.IRON_LEGGINGS &&
               armor.getBoots().getType() == Material.IRON_BOOTS;
    }

    @Override
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1), true);
        if (!Foxtrot.getInstance().getServerHandler().isHardcore()) player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0), true);
    }

    @Override
    public void tick(Player player) {
        if (!player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0), true);
        }

        int diamonds = Foxtrot.getInstance().getDiamondMinedMap().getMined( player.getUniqueId() );
        int level = 1;
        if (diamonds > 125) {
            level = 3;
        } else if (diamonds > 50) {
            level = 2;
        }

        if (shouldApplyPotion( player, PotionEffectType.FAST_DIGGING, level)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, level), true);
        }

        level = -1;

        if( diamonds > 400 ) {
            level = 1;
        } else if( diamonds >  100 ) {
            level = 0;
        }

        if( level != -1 && shouldApplyPotion(player, PotionEffectType.SPEED, level ) ) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, level), true);
        }

        if( diamonds > 250 ) {
            if( shouldApplyPotion(player, PotionEffectType.FIRE_RESISTANCE, 0) ) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0), true);
            }
        }

        if( diamonds > 600 ) {
            if (shouldApplyPotion(player, PotionEffectType.REGENERATION, 0)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0), true);
            }
        }

        if( diamonds >= 1000 && shouldApplyPotion(player,PotionEffectType.SATURATION, 0)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0), true);
        }

        super.tick(player);
    }

    public boolean shouldApplyPotion( Player player, PotionEffectType eff, int level ) {
        int potionLevel = -1;
        for( PotionEffect effect : player.getActivePotionEffects() ) {
            if( effect.getType().equals(eff)) {
                potionLevel = effect.getAmplifier();
                break;
            }
        }
        return !player.hasPotionEffect(eff) || potionLevel < level;
    }

    @Override
    public void remove(Player player) {
        removeInfiniteEffects(player);
        noDamage.remove(player.getName());
        invis.remove(player.getName());
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
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

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();

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

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockY() == event.getTo().getBlockY()) {
            return;
        }

        Player player = event.getPlayer();

        if (!PvPClassHandler.hasKitOn(player, this)) {
            return;
        }

        if (event.getTo().getBlockY() <= Y_HEIGHT) { // Going below 20
            if (!invis.containsKey(player.getName())) {
                invis.put(player.getName(), 10);
                player.sendMessage(ChatColor.BLUE + "Miner Invisibility" + ChatColor.YELLOW + " will be activated in 10 seconds!");
            }
        } else if (event.getTo().getBlockY() > Y_HEIGHT) { // Going above 20
            if (invis.containsKey(player.getName())) {
                noDamage.remove(player.getName());
                invis.remove(player.getName());
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                player.sendMessage(ChatColor.BLUE + "Miner Invisibility" + ChatColor.YELLOW + " has been removed!");
            }
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();

        if (!PvPClassHandler.hasKitOn(player, this) || event.getItem().getItemStack().getType() != Material.COBBLESTONE) {
            return;
        }

        if (!Foxtrot.getInstance().getCobblePickupMap().isCobblePickup(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

}