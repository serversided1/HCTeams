package net.frozenorb.foxtrot.koth.listeners;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.events.HourEvent;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.foxtrot.koth.KOTHHandler;
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
 * Created by macguy8 on 12/2/2014.
 */
public class KOTHListener implements Listener {

    @EventHandler
    public void onKOTHActivated(KOTHActivatedEvent event) {
        boolean citadel = event.getKoth().getName().equals("Citadel");
        boolean eotw = event.getKoth().getName().equals("EOTW");

        if (eotw) {
            for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.WITHER_SPAWN, 1F, 1F);
            }

            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█" + " " + ChatColor.DARK_RED + "[EOTW]");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "The cap point at spawn");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "████" + ChatColor.RED + "██" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "is now active.");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.DARK_RED + "EOTW " + ChatColor.GOLD + "can be contested now.");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
        } else if (citadel) {
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "███████");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "██" + ChatColor.DARK_PURPLE + "████" + ChatColor.GRAY + "█");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.GOLD + "[Citadel]");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.DARK_PURPLE + event.getKoth().getName());
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.GOLD + "can be contested now.");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "██" + ChatColor.DARK_PURPLE + "████" + ChatColor.GRAY + "█");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "███████");
        } else {
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "███████");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "███" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "█");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██" + " " + ChatColor.GOLD + "[KingOfTheHill]");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "███" + ChatColor.GRAY + "███" + " " + ChatColor.YELLOW + event.getKoth().getName() + " KOTH");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██" + " " + ChatColor.GOLD + "can be contested now.");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "███" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "█");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "███" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "█");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "███████");
        }
    }

    @EventHandler
    public void onKOTHCap(KOTHCapturedEvent event) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(event.getPlayer().getName());
        String teamName = ChatColor.GOLD + "[" + ChatColor.YELLOW + "-" + ChatColor.GOLD + "]";

        if (team != null) {
            teamName = ChatColor.GOLD + "[" + ChatColor.YELLOW + team.getName() + ChatColor.GOLD + "]";
        }

        for (int i = 0; i < 6; i++) {
            FoxtrotPlugin.getInstance().getServer().broadcastMessage("");
        }

        boolean citadel = event.getKoth().getName().equalsIgnoreCase("Citadel");

        if (!citadel) {
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "[KingOfTheHill]" + ChatColor.BLUE + " " + event.getKoth().getName() + ChatColor.YELLOW + " has been controlled by " + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName() + ChatColor.YELLOW + "!");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "[KingOfTheHill]" + ChatColor.YELLOW + " Awarded" + ChatColor.BLUE + " Level " + event.getKoth().getLevel() + " Key" + ChatColor.YELLOW + " to " + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName() + ChatColor.YELLOW + ".");

            ItemStack rewardKey = InvUtils.generateKOTHRewardKey(event.getKoth().getName() + " KOTH", event.getKoth().getLevel());
            ItemStack kothSign = FoxtrotPlugin.getInstance().getServerHandler().generateKOTHSign(event.getKoth().getName(), team == null ? event.getPlayer().getName() : team.getName());

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
            boolean citadel = event.getKoth().getName().equals("Citadel");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + (citadel ? "[Citadel]" : "[KingOfTheHill]") + " Control of " + ChatColor.YELLOW + event.getKoth().getName() + ChatColor.GOLD + " lost.");
        }
    }

    @EventHandler
    public void onKOTHControlTick(KOTHControlTickEvent event) {
        if (event.getKoth().getRemainingCapTime() <= (event.getKoth().getCapTime() - 30)) {
            boolean citadel = event.getKoth().getName().equals("Citadel");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + (citadel ? "[Citadel]" : "[KingOfTheHill]") + " " + ChatColor.YELLOW + event.getKoth().getName() + ChatColor.GOLD + " is trying to be controlled.");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + " - Time left: " + ChatColor.BLUE + TimeUtils.getMMSS(event.getKoth().getRemainingCapTime()));
        }
    }

    @EventHandler
    public void onHour(HourEvent event) {
        // Don't start a KOTH if another one is active.
        for (KOTH koth : KOTHHandler.getKOTHs()) {
            if (koth.isActive()) {
                return;
            }
        }

        if (KOTHHandler.getKothSchedule().containsKey(event.getHour())) {
            KOTH koth = KOTHHandler.getKOTH(KOTHHandler.getKothSchedule().get(event.getHour()));

            if (koth == null) {
                FoxtrotPlugin.getInstance().getLogger().warning("The KOTH Scheduler has a schedule for a KOTH named " + KOTHHandler.getKothSchedule().get(event.getHour()) + ", but the KOTH does not exist.");
                return;
            }

            koth.activate();
        }
    }

}