package net.frozenorb.foxtrot.armor.kits;

import com.google.common.collect.Lists;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.armor.Armor;
import net.frozenorb.foxtrot.armor.ArmorMaterial;
import net.frozenorb.foxtrot.armor.Kit;
import net.frozenorb.foxtrot.util.ParticleEffects;
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
        Bukkit.getScheduler().scheduleSyncRepeatingTask(FoxtrotPlugin.getInstance(), new BardTask(), 20, 20);

        Runnable applyNearby = () -> {
            for (String pName : Kit.getEquippedKits().keySet()) {
                Player player = Bukkit.getPlayer(pName);
                if (player == null)
                    continue;

                if (Kit.getEquippedKits().get(pName) instanceof Bard) {
                    if (FoxtrotPlugin.getInstance().getServerManager().isSpawn(player.getLocation()))
                        continue;

                    for (Player p : getNearby(player, true, 15)) {
                        if (Kit.getEquippedKits().get(p.getName()) instanceof Bard)
                            continue;

                        apply(p);
                    }
                }
            }
        };
        Bukkit.getScheduler().scheduleSyncRepeatingTask(FoxtrotPlugin.getInstance(), applyNearby, 20, 20);

        Runnable replenishStatics = () -> {
            for (String pName : Kit.getEquippedKits().keySet()) {
                Player player = Bukkit.getPlayer(pName);
                if (player == null)
                    continue;

                if (Kit.getEquippedKits().get(pName) instanceof Bard) {
                    if (!(player.hasPotionEffect(PotionEffectType.SPEED)))
                        player.addPotionEffect(PotionEffectType.SPEED.createEffect(Integer.MAX_VALUE, 0));
                    if (!(player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)))
                        player.addPotionEffect(PotionEffectType.DAMAGE_RESISTANCE.createEffect(Integer.MAX_VALUE, 0));
                    if (!(player.hasPotionEffect(PotionEffectType.WEAKNESS)))
                        player.addPotionEffect(PotionEffectType.WEAKNESS.createEffect(Integer.MAX_VALUE, 1));
                }
            }
        };
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(FoxtrotPlugin.getInstance(), replenishStatics, 1, 1);
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
        INSTANT_EFFECTS.put(Material.FEATHER, PotionEffectType.JUMP.createEffect(5*10, 5));
        INSTANT_EFFECTS.put(Material.RED_MUSHROOM, PotionEffectType.POISON.createEffect(20*2, 0));
        INSTANT_EFFECTS.put(Material.BROWN_MUSHROOM, PotionEffectType.WEAKNESS.createEffect(20*10, 0));
        INSTANT_EFFECTS.put(Material.SLIME_BALL, PotionEffectType.SLOW.createEffect(20*10, 0));
        INSTANT_EFFECTS.put(Material.RAW_FISH, PotionEffectType.WATER_BREATHING.createEffect(20*10, 5));
        INSTANT_EFFECTS.put(Material.SPIDER_EYE, PotionEffectType.WITHER.createEffect(20*50, 0));
        INSTANT_EFFECTS.put(Material.SUGAR, PotionEffectType.SPEED.createEffect(20*5, 3));
        INSTANT_EFFECTS.put(Material.MAGMA_CREAM, PotionEffectType.FIRE_RESISTANCE.createEffect(20 * 5, 1));
        INSTANT_EFFECTS.put(Material.GHAST_TEAR, PotionEffectType.REGENERATION.createEffect(20*5, 1));

        INSTANT_EFFECTS.put(Material.SPECKLED_MELON, null);
        INSTANT_EFFECTS.put(Material.EYE_OF_ENDER, null);
        INSTANT_EFFECTS.put(Material.WHEAT, null);

        TIMED_EFFECTS.put(Material.GHAST_TEAR, PotionEffectType.REGENERATION.createEffect(20*6, 0));
        TIMED_EFFECTS.put(Material.MAGMA_CREAM, PotionEffectType.FIRE_RESISTANCE.createEffect(20*6, 0));
        TIMED_EFFECTS.put(Material.SUGAR, PotionEffectType.SPEED.createEffect(20*6, 1));

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
    public void apply(Player p) {
        remove(p);

        p.addPotionEffect(PotionEffectType.SPEED.createEffect(Integer.MAX_VALUE, 0));
        p.addPotionEffect(PotionEffectType.DAMAGE_RESISTANCE.createEffect(Integer.MAX_VALUE, 1));
        p.addPotionEffect(PotionEffectType.REGENERATION.createEffect(Integer.MAX_VALUE, 1));
        p.addPotionEffect(PotionEffectType.WEAKNESS.createEffect(Integer.MAX_VALUE, 1));
    }

    @Override
    public void remove(Player p) {
        p.removePotionEffect(PotionEffectType.SPEED);
        p.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        p.removePotionEffect(PotionEffectType.REGENERATION);
        p.removePotionEffect(PotionEffectType.WEAKNESS);
    }

    @Override
    public int getWarmup() {
        return 5;
    }

    @Override
    public double getCooldownSeconds() {
        return 5;
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
            if (FoxtrotPlugin.getInstance().getServerManager().isSpawn(player.getLocation())) {
                player.sendMessage(ChatColor.RED+"You cannot use abilities in spawn!");
                return;
            }

            //Is instant effect
            if (!canUse) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eActivated abilities are on cooldown! &c" + ((duration - System.currentTimeMillis())/1000)+1 + " &9seconds remaining."));
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
                List<Player> applyTo = getNearby(player, false, 12);
                applyTo.add(player);

                for (Player p : applyTo) {
                    p.removePotionEffect(effect.getType());
                    p.addPotionEffect(effect);

                    playEffect(p, false);
                }
            } else {
                List<Player> applyTo = getNearby(player, true, 12);
                applyTo.add(player);

                for (Player p : applyTo) {
                    p.removePotionEffect(effect.getType());
                    p.addPotionEffect(effect);

                    playEffect(p, true);
                }
            }

            if (effect.getType().equals(PotionEffectType.DAMAGE_RESISTANCE)) {
                Runnable reset = () -> {
                    player.addPotionEffect(PotionEffectType.DAMAGE_RESISTANCE.createEffect(Integer.MAX_VALUE, 0));
                };
                Bukkit.getScheduler().scheduleSyncDelayedTask(FoxtrotPlugin.getInstance(), reset, effect.getDuration());
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
            player.setMetadata("bardCooldown", new FixedMetadataValue(FoxtrotPlugin.getInstance(), System.currentTimeMillis() + (1000 * (long)getCooldownSeconds())));

            player.playSound(player.getLocation(), Sound.BURP, 1, 1);
            event.setCancelled(true);
        }
    }

    private static void playEffect(Player player, boolean good) {
        if (good)
            ParticleEffects.sendToLocation(ParticleEffects.HAPPY_VILLAGER, player.getLocation().add(0.5, 0.5, 0.5), 1, 1, 1, 1, 50);
        if (!good)
            ParticleEffects.sendToLocation(ParticleEffects.WITCH_MAGIC, player.getLocation().add(0.5, 0.5, 0.5), 1, 1, 1, 1, 50);
    }

    private static boolean hasCooldown(Player player) {
        boolean canUse = true;

        if (player == null)
            return false;

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
            if (!hasTeam && !friendly) {
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

        private int tick = 0;

        public void run() {
            tick++;

            for (String pName : Kit.getEquippedKits().keySet()) {
                Player player = Bukkit.getPlayer(pName);
                if (player == null)
                    continue;

                if (Kit.getEquippedKits().get(pName) instanceof Bard) {
                    if (hasCooldown(player))
                        continue;

                    if (TIMED_EFFECTS.containsKey(player.getItemInHand().getType())) {
                        PotionEffect effect = TIMED_EFFECTS.get(player.getItemInHand().getType());

                        if (hasSpeedOne(player)) {
                            Runnable setup = () -> {
                                player.removePotionEffect(effect.getType());
                                player.addPotionEffect(effect);
                            };
                            Bukkit.getScheduler().scheduleSyncDelayedTask(FoxtrotPlugin.getInstance(), setup, 20);
                            continue;
                        }

                        if (tick % 5 != 0)
                            continue;

                        player.removePotionEffect(effect.getType());
                        player.addPotionEffect(effect);
                    }
                }

                if (!(player.hasPotionEffect(PotionEffectType.SPEED))) {
                    player.addPotionEffect(PotionEffectType.SPEED.createEffect(Integer.MAX_VALUE, 0));
                }

                if (!(player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE))) {
                    player.addPotionEffect(PotionEffectType.DAMAGE_RESISTANCE.createEffect(Integer.MAX_VALUE, 1));
                }

                if (player.getItemInHand().getType().equals(Material.FEATHER)) {
                    List<Player> team = getNearby(player, true, 12);
                    team.remove(player);

                    for (Player t : team) {
                        t.removePotionEffect(PotionEffectType.JUMP);
                        t.addPotionEffect(PotionEffectType.JUMP.createEffect(20*3, 1));
                    }
                }
            }
        }

        private boolean hasSpeedOne(Player p) {
            for (PotionEffect e : p.getActivePotionEffects()) {
                if (e.getAmplifier() == 0 && e.getType().equals(PotionEffectType.SPEED))
                    return true;
            }
            return false;
        }
    }
}
