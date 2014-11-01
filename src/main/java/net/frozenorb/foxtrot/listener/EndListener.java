package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.jedis.persist.JoinTimerMap;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public class EndListener implements Listener {

	@EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof EnderDragon) {
            Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(event.getEntity().getKiller().getName());
            String teamName = ChatColor.GOLD + "[" + ChatColor.YELLOW + "-" + ChatColor.GOLD + "]";

            if (team != null) {
                teamName = ChatColor.GOLD + "[" + ChatColor.YELLOW + team.getFriendlyName() + ChatColor.GOLD + "]";
            }

            Bukkit.broadcastMessage(ChatColor.BLACK + "████████");
            Bukkit.broadcastMessage(ChatColor.BLACK + "████████");
            Bukkit.broadcastMessage(ChatColor.BLACK + "████████" + ChatColor.GOLD + " [Enderdragon]");
            Bukkit.broadcastMessage(ChatColor.BLACK + "████████" + ChatColor.YELLOW + " killed by");
            Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.LIGHT_PURPLE + "█" + ChatColor.BLACK + "██" + ChatColor.LIGHT_PURPLE + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.LIGHT_PURPLE + "█" + " " + teamName);
            Bukkit.broadcastMessage(ChatColor.BLACK + "████████" + " " + event.getEntity().getKiller().getDisplayName());
            Bukkit.broadcastMessage(ChatColor.BLACK + "██" + ChatColor.GRAY + "████" + ChatColor.BLACK + "██");
            Bukkit.broadcastMessage(ChatColor.BLACK + "████████");

            ItemStack dragonEgg = new ItemStack(Material.DRAGON_EGG);
            ItemMeta itemMeta = dragonEgg.getItemMeta();

            //itemMeta.getLore().add(ChatColor.DARK_PURPLE)

            dragonEgg.setItemMeta(itemMeta);
            event.getEntity().getKiller().getInventory().addItem(dragonEgg);
        }
    }

    @EventHandler
    public void onCreatePortal(EntityCreatePortalEvent event) {
        switch (event.getEntityType()) {
            case ENDER_DRAGON:
                event.setCancelled(true);
        }
    }

    HashMap<String, Long> msgCooldown = new HashMap<>();

    @EventHandler
    public void onPortal(PlayerPortalEvent event){
        Player player = event.getPlayer();

        if(event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL){
            if (FoxtrotPlugin.getInstance().getJoinTimerMap().hasTimer(player) || FoxtrotPlugin.getInstance().getJoinTimerMap().getValue(player.getName()) == JoinTimerMap.PENDING_USE) {
                event.setCancelled(true);

                if(!(msgCooldown.containsKey(player.getName())) || msgCooldown.get(player.getName()) < System.currentTimeMillis()){
                    event.getPlayer().sendMessage(ChatColor.RED + "You cannot enter the end while you have pvp protection.");
                    msgCooldown.put(player.getName(), System.currentTimeMillis() + 3000L);
                }
            }

            if (event.getTo().getWorld().getEnvironment() == World.Environment.NORMAL) {
                for (Entity entity : event.getFrom().getWorld().getEntitiesByClass(EnderDragon.class)) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.RED + "You cannot leave the end before the dragon is killed.");
                    return;
                }
            }
        }
    }
}
