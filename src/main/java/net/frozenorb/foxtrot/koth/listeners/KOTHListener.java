package net.frozenorb.foxtrot.koth.listeners;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.events.HourEvent;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.foxtrot.koth.events.KOTHActivatedEvent;
import net.frozenorb.foxtrot.koth.events.KOTHCapturedEvent;
import net.frozenorb.foxtrot.koth.events.KOTHControlLostEvent;
import net.frozenorb.foxtrot.koth.events.KOTHControlTickEvent;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.InvUtils;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by macguy8 on 12/2/2014.
 */
public class KOTHListener implements Listener {

    @EventHandler
    public void onKOTHActivated(KOTHActivatedEvent event) {
        if (event.getKOTH().isHidden()) {
            return;
        }

        if (event.getKOTH().getName().equals("EOTW")) {
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
        } else if (event.getKOTH().getName().equals("Citadel")) {
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "███████");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "██" + ChatColor.DARK_PURPLE + "████" + ChatColor.GRAY + "█");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.GOLD + "[Citadel]");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.DARK_PURPLE + event.getKOTH().getName());
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.GOLD + "can be contested now.");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "██" + ChatColor.DARK_PURPLE + "████" + ChatColor.GRAY + "█");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "███████");
        } else {
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "███████");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "███" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "█");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██" + " " + ChatColor.GOLD + "[KingOfTheHill]");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "███" + ChatColor.GRAY + "███" + " " + ChatColor.YELLOW + event.getKOTH().getName() + " KOTH");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██" + " " + ChatColor.GOLD + "can be contested now.");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "███" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "█");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "███" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "█");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "███████");
        }
    }

    @EventHandler
    public void onKOTHCaptured(KOTHCapturedEvent event) {
        if (event.getKOTH().isHidden()) {
            return;
        }

        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(event.getPlayer().getName());
        String teamName = ChatColor.GOLD + "[" + ChatColor.YELLOW + "-" + ChatColor.GOLD + "]";

        if (team != null) {
            teamName = ChatColor.GOLD + "[" + ChatColor.YELLOW + team.getName() + ChatColor.GOLD + "]";
        }

        for (int i = 0; i < 6; i++) {
            FoxtrotPlugin.getInstance().getServer().broadcastMessage("");
        }

        if (!event.getKOTH().getName().equalsIgnoreCase("Citadel")) {
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "[KingOfTheHill]" + ChatColor.BLUE + " " + event.getKOTH().getName() + ChatColor.YELLOW + " has been controlled by " + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName() + ChatColor.YELLOW + "!");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "[KingOfTheHill]" + ChatColor.YELLOW + " Awarded" + ChatColor.BLUE + " Level " + event.getKOTH().getLevel() + " Key" + ChatColor.YELLOW + " to " + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName() + ChatColor.YELLOW + ".");

            ItemStack rewardKey = InvUtils.generateKOTHRewardKey(event.getKOTH().getName() + " KOTH", event.getKOTH().getLevel());
            ItemStack kothSign = FoxtrotPlugin.getInstance().getServerHandler().generateKOTHSign(event.getKOTH().getName(), team == null ? event.getPlayer().getName() : team.getName());

            event.getPlayer().getInventory().addItem(rewardKey);
            event.getPlayer().getInventory().addItem(kothSign);

            if (!event.getPlayer().getInventory().contains(rewardKey)) {
                event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), rewardKey);
            }

            if (!event.getPlayer().getInventory().contains(kothSign)) {
                event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), kothSign);
            }
        } else {
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "███████");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "██" + ChatColor.DARK_PURPLE + "████" + ChatColor.GRAY + "█");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.GOLD + "[Citadel]");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.YELLOW + "controlled by");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName());
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "██" + ChatColor.DARK_PURPLE + "████" + ChatColor.GRAY + "█");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "███████");
        }
    }

    @EventHandler
    public void onKOTHControlList(KOTHControlLostEvent event) {
        if (event.getKOTH().getRemainingCapTime() <= (event.getKOTH().getCapTime() - 30)) {
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "[KingOfTheHill] Control of " + ChatColor.YELLOW + event.getKOTH().getName() + ChatColor.GOLD + " lost.");
        }
    }

    @EventHandler
    public void onKOTHControlTick(KOTHControlTickEvent event) {
        if (event.getKOTH().getRemainingCapTime() % 180 == 0 && event.getKOTH().getRemainingCapTime() <= (event.getKOTH().getCapTime() - 30)) {
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + event.getKOTH().getName() + ChatColor.GOLD + " is trying to be controlled.");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + " - Time left: " + ChatColor.BLUE + TimeUtils.getMMSS(event.getKOTH().getRemainingCapTime()));
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (!event.getPlayer().isOp() || !event.getLine(0).equalsIgnoreCase("[KOTH]")) {
            return;
        }

        event.setLine(0, ChatColor.translateAlternateColorCodes('&', event.getLine(1)));
        event.setLine(1, "");

        FoxtrotPlugin.getInstance().getKOTHHandler().getKOTHSigns().add(event.getBlock().getLocation());
        FoxtrotPlugin.getInstance().getKOTHHandler().saveSigns();

        event.getPlayer().sendMessage(ChatColor.GREEN + "Created a KOTH sign!");
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled() || !(event.getBlock().getState() instanceof Sign)) {
            return;
        }

        if (FoxtrotPlugin.getInstance().getKOTHHandler().getKOTHSigns().contains(event.getBlock().getLocation())) {
            FoxtrotPlugin.getInstance().getKOTHHandler().getKOTHSigns().remove(event.getBlock().getLocation());
            FoxtrotPlugin.getInstance().getKOTHHandler().saveSigns();

            event.getPlayer().sendMessage(ChatColor.GREEN + "Removed a KOTH sign!");
        }
    }

    @EventHandler
    public void onHour(HourEvent event) {
        // Don't start a KOTH if another one is active.
        for (KOTH koth : FoxtrotPlugin.getInstance().getKOTHHandler().getKOTHs()) {
            if (koth.isHidden()) {
                continue;
            }

            if (koth.isActive()) {
                return;
            }
        }

        if (FoxtrotPlugin.getInstance().getKOTHHandler().getKOTHSchedule().containsKey(event.getHour())) {
            String resolvedName = FoxtrotPlugin.getInstance().getKOTHHandler().getKOTHSchedule().get(event.getHour());
            KOTH resolved = FoxtrotPlugin.getInstance().getKOTHHandler().getKOTH(resolvedName);

            if (resolved == null) {
                FoxtrotPlugin.getInstance().getLogger().warning("The KOTH Scheduler has a schedule for a KOTH named " + resolvedName + ", but the KOTH does not exist.");
                return;
            }

            resolved.activate();
        }
    }

}