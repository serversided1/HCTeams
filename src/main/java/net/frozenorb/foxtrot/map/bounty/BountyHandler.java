package net.frozenorb.foxtrot.map.bounty;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import net.frozenorb.qlib.util.ItemBuilder;
import net.minecraft.util.com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionEffectAddEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.Lists;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.profile.Profile;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.economy.FrozenEconomyHandler;
import net.frozenorb.qlib.redis.RedisCommand;
import net.frozenorb.qlib.util.UUIDUtils;
import net.md_5.bungee.api.ChatColor;
import redis.clients.jedis.Jedis;

public class BountyHandler implements Listener {

    @Getter private static UUID currentBountyPlayer;
    private static List<ItemStack> loot;
    boolean pickingNewBounty = false;
    private long lastPositionBroadcastMessage = -1L;
    private long lastSuitablePositionTime = -1L;
    private int secondsUnsuitable = 0;
    private Reward reward;
    
    private static String bountyPrefix = "&7[&6Bounty&7] ";

    private interface RewardAction {
        void reward(Player player);
    }

    private enum Reward {
        TWO_HUNDRED_FIFTY_DOLLARS("$250", player -> {
            FrozenEconomyHandler.deposit(player.getUniqueId(), 250);
        }),

        FIVE_HUNDRED_DOLLARS("$500", player -> {
            FrozenEconomyHandler.deposit(player.getUniqueId(), 500);
        }),

        SEVEN_HUNDRED_FIFTY_DOLLARS("$750", player -> {
            FrozenEconomyHandler.deposit(player.getUniqueId(), 750);
        }),

        ONE_BOUNTY_KEY("1 Bounty Key", player -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " bounty 1");
        }),

        TWO_BOUNTY_KEYS("2 Bounty Keys", player -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " bounty 2");
        }),

        THREE_BOUNTY_KEYS("3 Bounty Keys", player -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " bounty 3");
        }),

        CRAPPLES("1 Golden Apple", player -> {
            player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE));
        }),

        GOD_APPLE("1 God Apple", player -> {
            player.getInventory().addItem(ItemBuilder.of(Material.GOLDEN_APPLE).data((short) 1).build());
        }),

        COBWEBS("8 Cobwebs", player -> {
            player.getInventory().addItem(ItemBuilder.of(Material.WEB).amount(8).build());
        }),

        REFILL_POTS("Potion Refill Token", player -> {
            player.getInventory().addItem(ItemBuilder.of(Material.NETHER_STAR).name("&c&lPotion Refill Token").setUnbreakable(true).setLore(ImmutableList.of("&cRight click this to fill your inventory with potions!")).build());
        });

        private final String name;
        private final RewardAction action;

        Reward(String name, RewardAction action) {
            this.name = name;
            this.action = action;
        }
    }
    
    public BountyHandler() {
        FrozenCommandHandler.registerClass(this.getClass());

        try {
            loot = Lists.newArrayList(qLib.GSON.fromJson(qLib.getInstance().runBackboneRedisCommand(new RedisCommand<String>() {

                @Override
                public String execute(Jedis redis) {
                    String lookupString = Bukkit.getServerName() + ":" + "bountyLoot";
                    Bukkit.getLogger().info("Lookup string: " + lookupString);
                    return redis.get(lookupString);
                }

            }), ItemStack[].class));
        } catch (Exception e) {
            loot = Lists.newArrayList();
            Bukkit.getLogger().info("No bounty loot is set up.");
        }
        
        Bukkit.getScheduler().runTaskTimer(Foxtrot.getInstance(), this::checkBounty, 20L, 20L);
    }

    private void checkBounty() {
        
        if (CustomTimerCreateCommand.isSOTWTimer()) {
            currentBountyPlayer = null;
            return;
        }
        
        Player targetBountyPlayer = currentBountyPlayer == null ? null : Bukkit.getPlayer(currentBountyPlayer);
        
        if ((targetBountyPlayer == null || !targetBountyPlayer.isOnline()) && !pickingNewBounty) {
            newBounty();
            return;
        }
        
        if (!isSuitable(targetBountyPlayer)) {
            if (1000 < System.currentTimeMillis() - lastSuitablePositionTime) {
                if (30 <= secondsUnsuitable++) {
                    currentBountyPlayer = null;
                    secondsUnsuitable = 0;
                    newBounty();
                }
            }
        } else {
            lastSuitablePositionTime = System.currentTimeMillis();
            secondsUnsuitable = 0;
        }
        
        checkBroadcast();
    }

    private void newBounty() {
        
        if (CustomTimerCreateCommand.isSOTWTimer()) {
            currentBountyPlayer = null;
            return;
        }
        
        this.pickingNewBounty = true;

        if (Bukkit.getOnlinePlayers().size() < 25) {
            return;
        }
        
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Bounty&7] &eA &9Bounty &ewill be placed on a random player in &c30 seconds&e."));
        Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            pickNewBounty();
        }, 30 * 20);
    }

    private void pickNewBounty() {
        List<Player> suitablePlayers = Bukkit.getOnlinePlayers().stream().filter(this::isSuitable).collect(Collectors.toList());
        if (suitablePlayers.isEmpty()) {
            Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), this::pickNewBounty, 20L);
            return;
        }
        
        if (!pickingNewBounty) {
            return;
        }
        
        Player bountyPlayer = suitablePlayers.get(qLib.RANDOM.nextInt(suitablePlayers.size()));
        pickingNewBounty = false;
        set(bountyPlayer);
        this.reward = Reward.values()[qLib.RANDOM.nextInt(Reward.values().length)];
    }
    
    private void checkBroadcast() {
        Player player = currentBountyPlayer == null ? null : Bukkit.getPlayer(currentBountyPlayer);
        
        if (player == null) {
            return;
        }
        
        if (15000 <= System.currentTimeMillis() - lastPositionBroadcastMessage) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Bounty&7] " + formatName(currentBountyPlayer) + " &ehas been spotted @ &c" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ()  + "&e."));
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&aKill Reward&7: &f" + reward.name));
            lastPositionBroadcastMessage = System.currentTimeMillis();
        }
    }
    
    private boolean isSuitable(Player player) {
        
        if (player == null) {
            return false;
        }
        
        if (player.getGameMode() == GameMode.CREATIVE) {
            return false;
        }
        
        if (player.hasMetadata("ModMode") || player.hasMetadata("modmode")) {
            return false;
        }
        
        if (150 <= player.getLocation().getY()) {
            return false;
        }
        
        if (player.getWorld().getEnvironment() != Environment.NORMAL) {
            return false;
        }
        
        Team teamAt = LandBoard.getInstance().getTeam(player.getLocation());
        
        if (teamAt != null && !teamAt.hasDTRBitmask(DTRBitmask.ROAD)) {
            return false;
        }


        if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            return false;
        }
        
        if (500 < Math.abs(player.getLocation().getX()) || 500 < Math.abs(player.getLocation().getZ())) {
            return false;
        }
        
        return Foxtrot.getInstance().getServerHandler().isWarzone(player.getLocation());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPotionAdd(PotionEffectAddEvent event) {
        LivingEntity player = event.getEntity();
        if (!player.getUniqueId().equals(currentBountyPlayer)) {
            return;
        }
        
        if (event.getEffect().getType().equals(PotionEffectType.INVISIBILITY)) {
            event.setCancelled(true);
            
            if (player instanceof Player) {
                ((Player) player).sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou can't pot invis whilst the bounty is on you..."));
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onKill(PlayerDeathEvent event) {
        Player died = event.getEntity();
        Player killer = event.getEntity().getKiller();
        
        if (killer == null) {
            return;
        }
        
        if (!died.getUniqueId().equals(currentBountyPlayer)) {
            return;
        }
        
        currentBountyPlayer = null;
        
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Bounty&7] &f" + formatName(died.getUniqueId()) + " &ehas been slain by &f" + formatName(killer.getUniqueId()) + "&e."));
        
        reward.action.reward(killer);
    }
    
    @Command(names = "bounty set", permission = "bounty.set", async = true)
    public static void set(@Param(name = "target") Player target) {
        currentBountyPlayer = target.getUniqueId();
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', bountyPrefix + " &eA &9Bounty &ehas been placed on " + formatName(target.getUniqueId()) + "&e."));
    }
    
    @Command(names = {"savebountyloot", "bounty setloot", "bounty loot"}, permission = "op", async = true)
    public static void save(Player sender) {
        qLib.getInstance().runBackboneRedisCommand((redis) -> {
            String lookupString = Bukkit.getServerName() + ":" + "bountyLoot";
            Bukkit.getLogger().info("Lookup string: " + lookupString);
            redis.set(lookupString, qLib.PLAIN_GSON.toJson(Arrays.stream(sender.getInventory().getContents()).filter(item -> item != null && item.getType() != Material.AIR).collect(Collectors.toList())));
            return null;
        });
        
        loot = Arrays.stream(sender.getInventory().getContents()).filter(item -> item != null && item.getType() != Material.AIR).collect(Collectors.toList());
        sender.sendMessage(ChatColor.GREEN + "Loot updated.");
    }
    
    @Command(names = {"bounty", "bounty coords"}, permission = "")
    public static void coords(Player sender) {
        Player player = currentBountyPlayer == null ? null : Bukkit.getPlayer(currentBountyPlayer);
        
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "There's no bounty active right now.");
            return;
        }
        
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Bounty&7] " + formatName(currentBountyPlayer) + " &ehas been spotted at &c" + player.getLocation().getBlockX() + "," + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ()  + "&e."));
    }

    private static String formatName(UUID uuid) {
        if (Hydrogen.getInstance().getProfileHandler() == null) {
            return UUIDUtils.name(uuid);
        }

        Profile profile = Hydrogen.getInstance().getProfileHandler().getProfile(uuid).orElse(null);
        if (profile == null) {
            return UUIDUtils.name(uuid);
        }
        
        return profile.getBestDisplayRank().getGameColor() + UUIDUtils.name(uuid);
    }
}
