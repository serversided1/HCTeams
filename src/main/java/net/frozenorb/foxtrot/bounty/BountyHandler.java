package net.frozenorb.foxtrot.bounty;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.profile.Profile;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.frozenorb.qlib.command.Param;
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
    
    private static String bountyPrefix = "&6[Bounty] ";
    
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
        this.pickingNewBounty = true;
        
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6[Bounty] &eA &9Bounty &ewill be placed on a random player in &c30 seconds&e."));
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
    }
    
    private void checkBroadcast() {
        Player player = currentBountyPlayer == null ? null : Bukkit.getPlayer(currentBountyPlayer);
        
        if (player == null) {
            return;
        }
        
        if (15000 <= System.currentTimeMillis() - lastPositionBroadcastMessage) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6[Bounty] " + formatName(currentBountyPlayer) + " &ehas been spotted at &c" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ()  + "&e."));
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
        
        if (teamAt != null) {
            return false;
        }

        if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
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
        
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6[Bounty] &f" + formatName(died.getUniqueId()) + " &ehas been slain by &f" + formatName(killer.getUniqueId()) + "&e."));
        if (loot == null || loot.isEmpty()) {
            return;
        }
        
        killer.getInventory().addItem(loot.get(qLib.RANDOM.nextInt(loot.size())).clone());
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
        
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[Bounty] " + formatName(currentBountyPlayer) + " &ehas been spotted at &c" + player.getLocation().getBlockX() + "," + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ()  + "&e."));
    }

    private static String formatName(UUID uuid) {
        Profile profile = Hydrogen.getInstance().getProfileHandler().getProfile(uuid).orElse(null);
        if (profile == null) {
            return UUIDUtils.name(uuid);
        }
        
        return profile.getBestDisplayRank().getGameColor() + UUIDUtils.name(uuid);
    }
}
