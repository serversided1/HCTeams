package net.frozenorb.foxtrot.pvpclasses.pvpclasses;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.listener.FoxListener;
import net.frozenorb.foxtrot.pvpclasses.PvPClass;
import net.frozenorb.foxtrot.pvpclasses.PvPClassHandler;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class BaseBardClass extends PvPClass {

    public final HashMap<Material, PotionEffect> BARD_CLICK_EFFECTS = new HashMap<Material, PotionEffect>();
    public final HashMap<Material, PotionEffect> BARD_PASSIVE_EFFECTS = new HashMap<Material, PotionEffect>();

    public static final int BARD_RANGE = 20;

    public BaseBardClass(String name, String armorContains) {
        super(name, 15, armorContains, null);
    }

    // This purposely has no @EventHandler (called by subclasses)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT_") || !event.hasItem() || !BARD_CLICK_EFFECTS.containsKey(event.getItem().getType()) || !PvPClassHandler.hasKitOn(event.getPlayer(), this)) {
            return;
        }

        if (!FoxtrotPlugin.getInstance().getServerHandler().isEOTW() && DTRBitmaskType.SAFE_ZONE.appliesAt(event.getPlayer().getLocation())) {
            event.getPlayer().sendMessage(ChatColor.RED + "Bard effects cannot be used while in spawn.");
            return;
        }

        boolean negative = BARD_CLICK_EFFECTS.get(event.getItem().getType()) != null && Arrays.asList(FoxListener.DEBUFFS).contains(BARD_CLICK_EFFECTS.get(event.getItem().getType()).getType());

        if (negative) {
            if (PvPClassHandler.getLastBardNegativeEffectUsage().containsKey(event.getPlayer().getName()) && PvPClassHandler.getLastBardNegativeEffectUsage().get(event.getPlayer().getName()) > System.currentTimeMillis() && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                long millisLeft = PvPClassHandler.getLastBardNegativeEffectUsage().get(event.getPlayer().getName()) - System.currentTimeMillis();

                double value = (millisLeft / 1000D);
                double sec = Math.round(10.0 * value) / 10.0;

                event.getPlayer().sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD + sec + ChatColor.RED + " seconds!");
                return;
            }

            PvPClassHandler.getLastBardPositiveEffectUsage().put(event.getPlayer().getName(), System.currentTimeMillis() + (1000L * 60));
            ParticleEffects.sendToLocation(ParticleEffects.WITCH_MAGIC, event.getPlayer().getLocation(), 1, 1, 1, 1, 50);
        } else {
            if (PvPClassHandler.getLastBardPositiveEffectUsage().containsKey(event.getPlayer().getName()) && PvPClassHandler.getLastBardPositiveEffectUsage().get(event.getPlayer().getName()) > System.currentTimeMillis() && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                long millisLeft = PvPClassHandler.getLastBardPositiveEffectUsage().get(event.getPlayer().getName()) - System.currentTimeMillis();

                double value = (millisLeft / 1000D);
                double sec = Math.round(10.0 * value) / 10.0;

                event.getPlayer().sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD + sec + ChatColor.RED + " seconds!");
                return;
            }

            PvPClassHandler.getLastBardPositiveEffectUsage().put(event.getPlayer().getName(), System.currentTimeMillis() + (1000L * 60));
            ParticleEffects.sendToLocation(ParticleEffects.HAPPY_VILLAGER, event.getPlayer().getLocation(), 1, 1, 1, 1, 50);
        }

        SpawnTagHandler.addSeconds(event.getPlayer(), negative ? 60 : 30);
        giveBardEffect(event.getPlayer(), BARD_CLICK_EFFECTS.get(event.getItem().getType()), !negative);

        if (event.getItem().getType() != Material.FEATHER) {
            if (event.getPlayer().getItemInHand().getAmount() == 1) {
                event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                event.getPlayer().updateInventory();
            } else {
                event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1);
            }
        }
    }

    public void giveBardEffect(Player source, PotionEffect potionEffect, boolean friendly) {
        for (Player player : getNearbyPlayers(source, friendly)) {
            if (!FoxtrotPlugin.getInstance().getServerHandler().isEOTW() && DTRBitmaskType.SAFE_ZONE.appliesAt(player.getLocation())) {
                continue;
            }

            if (potionEffect != null) {
                smartAddPotion(player, potionEffect);
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

        for (Entity entity : player.getNearbyEntities(BARD_RANGE, BARD_RANGE, BARD_RANGE)) {
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