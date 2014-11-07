package net.frozenorb.foxtrot.nametag.listener;

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
            Bukkit.broadcastMessage(ChatColor.RED + "███████");
            Bukkit.broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█");
            Bukkit.broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████ " + ChatColor.DARK_RED + event.getKoth().getName());
            Bukkit.broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "████" + ChatColor.RED + "██ " + ChatColor.GOLD + "can be contested now.");
            Bukkit.broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████");
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
        FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + (citadel ? "[Citadel]" : "[KingOfTheHill]") + ChatColor.BLUE + " " + event.getKoth().getName() + ChatColor.YELLOW + " has been controlled by " + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName() + ChatColor.YELLOW + "!");
        FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + (citadel ? "[Citadel]" : "[KingOfTheHill]") + ChatColor.YELLOW + " Awarded" + ChatColor.BLUE + " Level " + event.getKoth().getTier() + " Key" + ChatColor.YELLOW + " to " + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName() + ChatColor.YELLOW + ".");

        if (!citadel) {
            ItemStack rewardKey = InvUtils.generateKOTHRewardKey(event.getKoth().getName() + " KOTH", event.getKoth().getTier());
            ItemStack kothSign = FoxtrotPlugin.getInstance().getServerHandler().generateKOTHSign(event.getKoth().getName(), team == null ? event.getPlayer().getName() : team.getFriendlyName());

            event.getPlayer().getInventory().addItem(rewardKey);
            event.getPlayer().getInventory().addItem(kothSign);

            if (!event.getPlayer().getInventory().contains(rewardKey)) {
                event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), rewardKey);
            }

            if (!event.getPlayer().getInventory().contains(kothSign)) {
                event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), kothSign);
            }
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