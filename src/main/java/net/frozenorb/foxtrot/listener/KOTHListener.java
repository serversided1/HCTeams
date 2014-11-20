package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.koth.events.KOTHActivatedEvent;
import net.frozenorb.foxtrot.koth.events.KOTHCapturedEvent;
import net.frozenorb.foxtrot.koth.events.KOTHControlLostEvent;
import net.frozenorb.foxtrot.koth.events.KOTHControlTickEvent;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.InvUtils;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 * Created by macguy8 on 10/31/2014.
 */
public class KOTHListener implements Listener {

    @EventHandler
    public void onKOTHActivated(KOTHActivatedEvent event) {
        boolean citadel = event.getKoth().getName().equalsIgnoreCase("Citadel");
        boolean eotw = event.getKoth().getName().equalsIgnoreCase("EOTW");

        if (eotw) {
            for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.WITHER_SPAWN, 1F, 1F);
            }

            Bukkit.broadcastMessage(ChatColor.RED + "███████");
            Bukkit.broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█" + " " + ChatColor.DARK_RED + "[EOTW]");
            Bukkit.broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "The cap point at spawn");
            Bukkit.broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "████" + ChatColor.RED + "██" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "is now active.");
            Bukkit.broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.DARK_RED + "EOTW " + ChatColor.GOLD + "can be contested now.");
            Bukkit.broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█");
            Bukkit.broadcastMessage(ChatColor.RED + "███████");
        } else if (citadel) {
            Bukkit.broadcastMessage(ChatColor.GRAY + "███████");
            Bukkit.broadcastMessage(ChatColor.GRAY + "██" + ChatColor.DARK_PURPLE + "████" + ChatColor.GRAY + "█");
            Bukkit.broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.GOLD + "[Citadel]");
            Bukkit.broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.DARK_PURPLE + event.getKoth().getName());
            Bukkit.broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.GOLD + "can be contested now.");
            Bukkit.broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████");
            Bukkit.broadcastMessage(ChatColor.GRAY + "██" + ChatColor.DARK_PURPLE + "████" + ChatColor.GRAY + "█");
            Bukkit.broadcastMessage(ChatColor.GRAY + "███████");
        } else {
            Bukkit.broadcastMessage(ChatColor.GRAY + "███████");
            Bukkit.broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "███" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "█");
            Bukkit.broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██" + " " + ChatColor.GOLD + "[KingOfTheHill]");
            Bukkit.broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "███" + ChatColor.GRAY + "███" + " " + ChatColor.YELLOW + event.getKoth().getName() + " KOTH");
            Bukkit.broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██" + " " + ChatColor.GOLD + "can be contested now.");
            Bukkit.broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "███" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "█");
            Bukkit.broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "███" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "█");
            Bukkit.broadcastMessage(ChatColor.GRAY + "███████");
        }
    }

    @EventHandler
    public void onKOTHCap(KOTHCapturedEvent event) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(event.getPlayer().getName());
        String teamName = ChatColor.GOLD + "[" + ChatColor.YELLOW + "-" + ChatColor.GOLD + "]";

        if (team != null) {
            teamName = ChatColor.GOLD + "[" + ChatColor.YELLOW + team.getFriendlyName() + ChatColor.GOLD + "]";
        }

        for (int i = 0; i < 6; i++) {
            FoxtrotPlugin.getInstance().getServer().broadcastMessage("");
        }

        boolean citadel = event.getKoth().getName().equalsIgnoreCase("Citadel");

        if (!citadel) {
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "[KingOfTheHill]" + ChatColor.BLUE + " " + event.getKoth().getName() + ChatColor.YELLOW + " has been controlled by " + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName() + ChatColor.YELLOW + "!");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "[KingOfTheHill]" + ChatColor.YELLOW + " Awarded" + ChatColor.BLUE + " Level " + event.getKoth().getLevel() + " Key" + ChatColor.YELLOW + " to " + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName() + ChatColor.YELLOW + ".");

            ItemStack rewardKey = InvUtils.generateKOTHRewardKey(event.getKoth().getName() + " KOTH", event.getKoth().getLevel());
            ItemStack kothSign = FoxtrotPlugin.getInstance().getServerHandler().generateKOTHSign(event.getKoth().getName(), team == null ? event.getPlayer().getName() : team.getFriendlyName());

            event.getPlayer().getInventory().addItem(rewardKey);
            event.getPlayer().getInventory().addItem(kothSign);

            if (!event.getPlayer().getInventory().contains(rewardKey)) {
                event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), rewardKey);
            }

            if (!event.getPlayer().getInventory().contains(kothSign)) {
                event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), kothSign);
            }
        } else {
            Bukkit.broadcastMessage(ChatColor.GRAY + "███████");
            Bukkit.broadcastMessage(ChatColor.GRAY + "██" + ChatColor.DARK_PURPLE + "████" + ChatColor.GRAY + "█");
            Bukkit.broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.GOLD + "[Citadel]");
            Bukkit.broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.YELLOW + "controlled by");
            Bukkit.broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName());
            Bukkit.broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████");
            Bukkit.broadcastMessage(ChatColor.GRAY + "██" + ChatColor.DARK_PURPLE + "████" + ChatColor.GRAY + "█");
            Bukkit.broadcastMessage(ChatColor.GRAY + "███████");
        }
    }

    @EventHandler
    public void onKOTHControlList(KOTHControlLostEvent event) {
        if (event.getKoth().getRemainingCapTime() <= (event.getKoth().getCapTime() - 30)) {
            boolean citadel = event.getKoth().getName().equalsIgnoreCase("Citadel");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + (citadel ? "[Citadel]" : "[KingOfTheHill]") + " Control of " + ChatColor.YELLOW + event.getKoth().getName() + ChatColor.GOLD + " lost.");
        }
    }

    @EventHandler
    public void onKOTHControlTick(KOTHControlTickEvent event) {
        if (event.getKoth().getRemainingCapTime() <= (event.getKoth().getCapTime() - 30)) {
            boolean citadel = event.getKoth().getName().equalsIgnoreCase("Citadel");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + (citadel ? "[Citadel]" : "[KingOfTheHill]") + " " + ChatColor.YELLOW + event.getKoth().getName() + ChatColor.GOLD + " is trying to be controlled.");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + " - Time left: " + ChatColor.BLUE + TimeUtils.getMMSS((int) event.getKoth().getRemainingCapTime()));
        }
    }

}