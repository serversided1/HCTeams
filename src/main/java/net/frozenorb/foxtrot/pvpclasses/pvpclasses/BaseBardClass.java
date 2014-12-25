package net.frozenorb.foxtrot.pvpclasses.pvpclasses;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.listener.FoxListener;
import net.frozenorb.foxtrot.pvpclasses.PvPClass;
import net.frozenorb.foxtrot.pvpclasses.PvPClassHandler;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.bard.BardEffect;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.bitmask.DTRBitmaskType;
import net.frozenorb.foxtrot.util.ParticleEffects;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class BaseBardClass extends PvPClass {

    public final HashMap<Material, BardEffect> BARD_CLICK_EFFECTS = new HashMap<Material, BardEffect>();
    public final HashMap<Material, BardEffect> BARD_PASSIVE_EFFECTS = new HashMap<Material, BardEffect>();

    @Getter private static Map<String, Long> lastEffectUsage = new HashMap<>();
    @Getter private static Map<String, Float> energy = new HashMap<>();

    public static final int BARD_RANGE = 20;
    public static final int EFFECT_COOLDOWN = 10 * 1000;
    public static final float MAX_ENERGY = 100;
    public static final float ENERGY_REGEN_PER_SECOND = 1;

    public BaseBardClass(String name, String armorContains) {
        super(name, 15, armorContains, null);

        new BukkitRunnable() {

            public void run() {
                for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                    if (!PvPClassHandler.hasKitOn(player, BaseBardClass.this)) {
                        continue;
                    }

                    if (BaseBardClass.getEnergy().containsKey(player.getName())) {
                        if (BaseBardClass.getEnergy().get(player.getName()) == MAX_ENERGY) {
                            continue;
                        }

                        BaseBardClass.getEnergy().put(player.getName(), Math.min(MAX_ENERGY, BaseBardClass.getEnergy().get(player.getName()) + ENERGY_REGEN_PER_SECOND));
                    } else {
                        BaseBardClass.getEnergy().put(player.getName(), 0F);
                    }

                    int manaInt = BaseBardClass.getEnergy().get(player.getName()).intValue();

                    if (manaInt % 10 == 0) {
                        player.sendMessage(ChatColor.AQUA + getName() + " Energy: " + ChatColor.GREEN + manaInt);
                    }
                }
            }

        }.runTaskTimer(FoxtrotPlugin.getInstance(), 15L, 20L);
    }

    @Override
    public void remove(Player player) {
        BaseBardClass.getEnergy().remove(player.getName());
    }

    // This purposely has no @EventHandler (called by subclasses)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT_") || !event.hasItem() || !BARD_CLICK_EFFECTS.containsKey(event.getItem().getType()) || !PvPClassHandler.hasKitOn(event.getPlayer(), this)) {
            return;
        }

        if (!FoxtrotPlugin.getInstance().getServerHandler().isEOTW() && DTRBitmaskType.SAFE_ZONE.appliesAt(event.getPlayer().getLocation())) {
            event.getPlayer().sendMessage(ChatColor.RED + getName() + " effects cannot be used while in spawn.");
            return;
        }

        if (getLastEffectUsage().containsKey(event.getPlayer().getName()) && getLastEffectUsage().get(event.getPlayer().getName()) > System.currentTimeMillis() && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            long millisLeft = getLastEffectUsage().get(event.getPlayer().getName()) - System.currentTimeMillis();

            double value = (millisLeft / 1000D);
            double sec = Math.round(10.0 * value) / 10.0;

            event.getPlayer().sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD + sec + ChatColor.RED + " seconds!");
            return;
        }

        BardEffect bardEffect = BARD_CLICK_EFFECTS.get(event.getItem().getType());

        if (bardEffect.getEnergy() > BaseBardClass.getEnergy().get(event.getPlayer().getName())) {
            event.getPlayer().sendMessage(ChatColor.RED + "You do not have enough energy for this! You need " + bardEffect.getEnergy() + " energy, but you only have " + BaseBardClass.getEnergy().get(event.getPlayer().getName()).intValue());
            return;
        }

        BaseBardClass.getEnergy().put(event.getPlayer().getName(), BaseBardClass.getEnergy().get(event.getPlayer().getName()) - bardEffect.getEnergy());

        boolean negative = bardEffect.getPotionEffect() != null && Arrays.asList(FoxListener.DEBUFFS).contains(bardEffect.getPotionEffect().getType());

        if (negative) {
            ParticleEffects.sendToLocation(ParticleEffects.WITCH_MAGIC, event.getPlayer().getLocation(), 1, 1, 1, 1, 50);
        } else {
            ParticleEffects.sendToLocation(ParticleEffects.HAPPY_VILLAGER, event.getPlayer().getLocation(), 1, 1, 1, 1, 50);
        }

        getLastEffectUsage().put(event.getPlayer().getName(), System.currentTimeMillis() + EFFECT_COOLDOWN);
        SpawnTagHandler.addSeconds(event.getPlayer(), negative ? 60 : 30);
        giveBardEffect(event.getPlayer(), bardEffect, !negative, true);

        if (event.getItem().getType() != Material.FEATHER) {
            if (event.getPlayer().getItemInHand().getAmount() == 1) {
                event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                event.getPlayer().updateInventory();
            } else {
                event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1);
            }
        }
    }

    // This purposely has no @EventHandler (called by subclasses)
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        /*ItemStack held = event.getPlayer().getInventory().getItem(event.getNewSlot());

        if (held == null || !BARD_CLICK_EFFECTS.containsKey(held.getType()) || !PvPClassHandler.hasKitOn(event.getPlayer(), this)) {
            return;
        }

        BardEffect bardEffect = BARD_CLICK_EFFECTS.get(held.getType());

        FoxtrotPlugin.getInstance().getItemMessage().sendMessage(event.getPlayer(), new ItemMessage.ItemMessageGetter() {

            public String getMessage(Player player) {
                if (!getEnergy().containsKey(player.getName())) {
                    return (ChatColor.RED + "Processing...");
                }

                if (getEnergy().get(player.getName()) >= bardEffect.getEnergy()) {
                    return (ChatColor.GREEN.toString() + bardEffect.getEnergy() + " Energy " + ChatColor.WHITE + "| " + bardEffect.getDescription());
                } else {
                    return (ChatColor.RED.toString() + getEnergy().get(player.getName()).intValue() + "/" + bardEffect.getEnergy() + " Energy " + ChatColor.WHITE + "| " + bardEffect.getDescription());
                }
            }

        }, event.getNewSlot());*/
    }

    public void giveBardEffect(Player source, BardEffect bardEffect, boolean friendly, boolean persistOldValues) {
        for (Player player : getNearbyPlayers(source, friendly)) {
            if (!FoxtrotPlugin.getInstance().getServerHandler().isEOTW() && DTRBitmaskType.SAFE_ZONE.appliesAt(player.getLocation())) {
                continue;
            }

            if (bardEffect.getPotionEffect() != null) {
                smartAddPotion(player, bardEffect.getPotionEffect(), persistOldValues);
            } else {
                Material material = source.getItemInHand().getType();
                giveCustomBardEffect(player, material);
            }
        }
    }

    public void giveCustomBardEffect(Player player, Material material) {

    }

    public List<Player> getNearbyPlayers(Player player, boolean friendly) {
        List<Player> valid = new ArrayList<Player>();
        Team sourceTeam = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());

        // We divide by 2 so that the range isn't as much on the Y level (and can't be abused by standing on top of / under events)
        for (Entity entity : player.getNearbyEntities(BARD_RANGE, BARD_RANGE / 2, BARD_RANGE)) {
            if (entity instanceof Player) {
                Player nearbyPlayer = (Player) entity;

                if (sourceTeam == null) {
                    if (!friendly) {
                        valid.add(nearbyPlayer);
                    }

                    continue;
                }

                boolean isFriendly = sourceTeam.isMember(nearbyPlayer.getName());
                boolean isAlly = sourceTeam.isAlly(nearbyPlayer.getName());

                if (friendly && isFriendly) {
                    valid.add(nearbyPlayer);
                } else if (!friendly && !isFriendly && !isAlly) { // the isAlly is here so you can't give your allies negative effects, but so you also can't give them positive effects.
                    valid.add(nearbyPlayer);
                }
            }
        }

        valid.add(player);
        return (valid);
    }

}