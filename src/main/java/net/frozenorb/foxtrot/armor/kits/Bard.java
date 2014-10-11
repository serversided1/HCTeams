package net.frozenorb.foxtrot.armor.kits;

import com.google.common.collect.Lists;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.armor.Armor;
import net.frozenorb.foxtrot.armor.ArmorMaterial;
import net.frozenorb.foxtrot.armor.Kit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;

/**
 * @author Connor Hollasch
 * @since 10/10/14
 */
public class Bard extends Kit {

    public Bard() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(FoxtrotPlugin.getInstance(), new BardTask(), 20*5, 20*5);
    }

    private static final HashMap<Material, PotionEffect> INSTANT_EFFECTS = new HashMap<>();
    private static final HashMap<Material, PotionEffect> TIMED_EFFECTS = new HashMap<>();

    private static final List<PotionEffectType> NEGATIVE_EFFECTS = Lists.newArrayList();

    static {
        NEGATIVE_EFFECTS.add(PotionEffectType.POISON);
        NEGATIVE_EFFECTS.add(PotionEffectType.WEAKNESS);
        NEGATIVE_EFFECTS.add(PotionEffectType.SLOW);
        NEGATIVE_EFFECTS.add(PotionEffectType.WITHER);

        INSTANT_EFFECTS.put(Material.IRON_INGOT, PotionEffectType.DAMAGE_RESISTANCE.createEffect(20*5, 7));
        INSTANT_EFFECTS.put(Material.BLAZE_ROD, PotionEffectType.INCREASE_DAMAGE.createEffect(3*20, 0));
        INSTANT_EFFECTS.put(Material.FEATHER, PotionEffectType.JUMP.createEffect(3*10, 5));
        INSTANT_EFFECTS.put(Material.RED_MUSHROOM, PotionEffectType.POISON.createEffect(20*2, 0));
        INSTANT_EFFECTS.put(Material.BROWN_MUSHROOM, PotionEffectType.WEAKNESS.createEffect(20*10, 0));
        INSTANT_EFFECTS.put(Material.SLIME_BALL, PotionEffectType.SLOW.createEffect(20*10, 0));
        INSTANT_EFFECTS.put(Material.RAW_FISH, PotionEffectType.WATER_BREATHING.createEffect(20*10, 5));
        INSTANT_EFFECTS.put(Material.SPIDER_EYE, PotionEffectType.WITHER.createEffect(20*10, 0));

        INSTANT_EFFECTS.put(Material.SPECKLED_MELON, null);
        INSTANT_EFFECTS.put(Material.EYE_OF_ENDER, null);
        INSTANT_EFFECTS.put(Material.WHEAT, null);

        TIMED_EFFECTS.put(Material.GHAST_TEAR, PotionEffectType.REGENERATION.createEffect(20*5, 0));
        TIMED_EFFECTS.put(Material.MAGMA_CREAM, PotionEffectType.FIRE_RESISTANCE.createEffect(20*5, 0));
        TIMED_EFFECTS.put(Material.SUGAR, PotionEffectType.SPEED.createEffect(20*5, 1));

        //Custom code
        //Glistering Melon - Heals 6 Hearts Instantly
        //Eye Of Ender - Reveals Invisible Rouge Players within 80 blocks. (Forces a 30 second cool-down on the Rouge Player before they can go Invisible again)
        //Wheat - Heals 6 hunger points

        //Enemy is anyone not in your faction 15 block radius
    }

    @Override
    public boolean qualifies(Armor armor) {
        return armor.isFullSet(ArmorMaterial.GOLD);
    }

    @Override
    public String getName() {
        return "Bard";
    }

    @Override
    public int getWarmup() {
        return 60;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.PHYSICAL))
            return;

        Player player = event.getPlayer();

        if (Kit.getEquippedKits().get(player.getName()) == null || !(getEquippedKits().get(player.getName()) instanceof Bard))
            return;

        boolean canUse = true;
        long duration = 0;

        if (player.hasMetadata("bardCooldown")) {
            canUse = false;
            duration = (Long)player.getMetadata("bardCooldown").get(0).value();

            if (duration < System.currentTimeMillis()) {
                player.removeMetadata("bardCooldown", FoxtrotPlugin.getInstance());
                canUse = true;
            }
        }

        ItemStack holding = player.getItemInHand();
        if (holding == null)
            return;

        if (INSTANT_EFFECTS.containsKey(holding.getType())) {
            //Is instant effect
            if (!canUse) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eActivated abilities are on cooldown! &c" + ((duration - System.currentTimeMillis())/1000) + " &9seconds remaining."));
                return;
            }

            PotionEffect effect = INSTANT_EFFECTS.get(holding.getType());
            if (effect == null) {
                //Custom
                if (holding.getType().equals(Material.SPECKLED_MELON)) {
                    double add = 6.0;
                    if (player.getHealth() + add > player.getMaxHealth()) {
                        player.setHealth(player.getMaxHealth());
                    } else {
                        player.setHealth(player.getHealth()+add);
                    }
                }

                if (holding.getType().equals(Material.WHEAT)) {
                    int add = 6;
                    if (player.getFoodLevel() + add > 20)
                        player.setFoodLevel(20);
                    else
                        player.setFoodLevel(player.getFoodLevel()+add);
                }
            }
            if (NEGATIVE_EFFECTS.contains(effect.getType())) {
                //Is negative effect
                List<Player> applyTo = getNearby(player, false, 15);
                applyTo.add(player);

                for (Player p : applyTo) {
                    p.addPotionEffect(effect);
                }
            } else {
                List<Player> applyTo = getNearby(player, true, 15);
                applyTo.add(player);

                for (Player p : applyTo) {
                    p.addPotionEffect(effect);
                }
                player.addPotionEffect(effect);
            }

            //Remove the item the player is holding
            if (player.getInventory().getItemInHand().getAmount() > 1) {
                ItemStack set = player.getInventory().getItemInHand();
                set.setAmount(set.getAmount()-1);

                player.getInventory().setItem(player.getInventory().getHeldItemSlot(), set);
            } else
                player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.AIR));

            player.updateInventory();

            //Add cooldown as meta
            player.setMetadata("bardCooldown", new FixedMetadataValue(FoxtrotPlugin.getInstance(), System.currentTimeMillis() + (1000 * 60)));

            player.playSound(player.getLocation(), Sound.BURP, 1, 1);
            event.setCancelled(true);
        }
    }

    private static boolean hasCooldown(Player player) {
        boolean canUse = true;

        if (player.hasMetadata("bardCooldown")) {
            canUse = false;
            long duration = (Long)player.getMetadata("bardCooldown").get(0).value();

            if (duration < System.currentTimeMillis()) {
                player.removeMetadata("bardCooldown", FoxtrotPlugin.getInstance());
                canUse = true;
            }
        }

        return !canUse;
    }

    private static List<Player> getNearby(Player player, boolean friendly, int radius) {
        List<Player> nearby = Lists.newArrayList();
        List<Player> official = Lists.newArrayList();

        for (Entity ent : player.getNearbyEntities(radius, radius, radius)) {
            if (!(ent instanceof Player))
                continue;

            nearby.add((Player)ent);
        }

        boolean hasTeam = FoxtrotPlugin.getInstance().getTeamManager().isOnTeam(player.getName());

        for (Player p : nearby) {
            if (!hasTeam) {
                official.add(p);
                continue;
            }

            boolean isTeammate = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(player.getName()).getMembers().contains(p.getName());
            if (friendly && isTeammate)
                official.add(p);

            if (!friendly && !isTeammate)
                official.add(p);
        }

        official.remove(player);
        return official;
    }

    private class BardTask implements Runnable {

        @Override
        public void run() {
            for (String pName : Kit.getEquippedKits().keySet()) {
                Player player = Bukkit.getPlayer(pName);

                if (Kit.getEquippedKits().get(pName) instanceof Bard) {
                    if (hasCooldown(player))
                        continue;

                    if (TIMED_EFFECTS.containsKey(player.getItemInHand().getType())) {
                        PotionEffect effect = TIMED_EFFECTS.get(player.getItemInHand().getType());

                        player.addPotionEffect(effect);
                    }
                }
            }
        }
    }
}
