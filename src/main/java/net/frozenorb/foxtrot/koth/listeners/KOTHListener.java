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

public class KOTHListener implements Listener {

    @EventHandler
    public void onKOTHActivated(KOTHActivatedEvent event) {
        if (event.getKOTH().isHidden()) {
            return;
        }

        String[] messages;

        switch (event.getKOTH().getName()) {
            case "EOTW":
                messages = new String[]{
                        ChatColor.RED + "███████",
                        ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█" + " " + ChatColor.DARK_RED + "[EOTW]",
                        ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "The cap point at spawn",
                        ChatColor.RED + "█" + ChatColor.DARK_RED + "████" + ChatColor.RED + "██" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "is now active.",
                        ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.DARK_RED + "EOTW " + ChatColor.GOLD + "can be contested now.",
                        ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█",
                        ChatColor.RED + "███████"
                };

                for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                    player.playSound(player.getLocation(), Sound.WITHER_SPAWN, 1F, 1F);
                }

                break;
            case "Citadel":
                messages = new String[]{
                        ChatColor.GRAY + "███████",
                        ChatColor.GRAY + "██" + ChatColor.DARK_PURPLE + "████" + ChatColor.GRAY + "█",
                        ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.GOLD + "[Citadel]",
                        ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.DARK_PURPLE + event.getKOTH().getName(),
                        ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.GOLD + "can be contested now.",
                        ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████",
                        ChatColor.GRAY + "██" + ChatColor.DARK_PURPLE + "████" + ChatColor.GRAY + "█",
                        ChatColor.GRAY + "███████"
                };

                break;
            default:
                messages = new String[]{
                        ChatColor.GRAY + "███████",
                        ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "███" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "█",
                        ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██" + " " + ChatColor.GOLD + "[KingOfTheHill]",
                        ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "███" + ChatColor.GRAY + "███" + " " + ChatColor.YELLOW + event.getKOTH().getName() + " KOTH",
                        ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██" + " " + ChatColor.GOLD + "can be contested now.",
                        ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "███" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "█",
                        ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "███" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "█",
                        ChatColor.GRAY + "███████"
                };

                break;
        }

        for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
            player.sendMessage(messages);
        }

        // Can't forget console now can we
        for (String message : messages) {
            FoxtrotPlugin.getInstance().getLogger().info(message);
        }
    }

    @EventHandler
    public void onKOTHCaptured(KOTHCapturedEvent event) {
        if (event.getKOTH().isHidden()) {
            return;
        }

        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getTeam(event.getPlayer());
        String teamName = ChatColor.GOLD + "[" + ChatColor.YELLOW + "-" + ChatColor.GOLD + "]";

        if (team != null) {
            teamName = ChatColor.GOLD + "[" + ChatColor.YELLOW + team.getName() + ChatColor.GOLD + "]";
        }

        String[] filler = { "", "", "", "", "", "" };
        String[] messages;

        if (event.getKOTH().getName().equalsIgnoreCase("Citadel")) {
            messages = new String[] {
                    ChatColor.GRAY + "███████",
                    ChatColor.GRAY + "██" + ChatColor.DARK_PURPLE + "████" + ChatColor.GRAY + "█",
                    ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.GOLD + "[Citadel]",
                    ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.YELLOW + "controlled by",
                    ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName(),
                    ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████",
                    ChatColor.GRAY + "██" + ChatColor.DARK_PURPLE + "████" + ChatColor.GRAY + "█",
                    ChatColor.GRAY + "███████"
            };
        } else {
            messages = new String[] {
                    ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.BLUE + event.getKOTH().getName() + ChatColor.YELLOW + " has been controlled by " + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName() + ChatColor.YELLOW + "!",
                    ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + "Awarded" + ChatColor.BLUE + " Level " + event.getKOTH().getLevel() + " Key" + ChatColor.YELLOW + " to " + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName() + ChatColor.YELLOW + "."
            };

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
        }

        for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
            player.sendMessage(filler);
            player.sendMessage(messages);
        }

        // Can't forget console now can we
        // but we don't want to give console the filler.
        for (String message : messages) {
            FoxtrotPlugin.getInstance().getLogger().info(message);
        }
    }

    @EventHandler
    public void onKOTHControlLost(KOTHControlLostEvent event) {
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