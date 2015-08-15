package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.EnderpearlListener;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.mBasic.CommandSystem.Commands.Freeze;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class TeamHQCommand {

    public static final int WARP_PRICE = 50;

    @Command(names={ "team hq", "t hq", "f hq", "faction hq", "fac hq", "team home", "t home", "f home", "faction home", "fac home", "home", "hq" }, permissionNode="")
    public static void teamHQ(Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.DARK_AQUA + "You are not on a team!");
            return;
        }

        if (team.getHQ() == null) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to your team headquarters as it is not set.");
            return;
        }

        if (sender.getWorld().getEnvironment() == World.Environment.THE_END) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to your team headquarters while in The End.");
            return;
        }

        if (sender.getWorld().getEnvironment() == World.Environment.NETHER) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to your team headquarters while in the nether.");
            return;
        }

        if (Foxtrot.getInstance().getServerHandler().isEOTW()) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to your team headquarters during the End of the World.");
            return;
        }

        if (Freeze.isFrozen(sender)) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to your team headquarters while you're frozen.");
            return;
        }

        boolean enemyCheckBypass = sender.getGameMode() == GameMode.CREATIVE || sender.hasMetadata("modmode") || DTRBitmask.SAFE_ZONE.appliesAt(sender.getLocation());
        Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
        double bal = playerTeam.getBalance();

        if (bal < WARP_PRICE) {
            sender.sendMessage(ChatColor.RED + "This costs $" + WARP_PRICE + " while your team has only $" + bal + "!");
            return;
        }

        if (!enemyCheckBypass) {
            // Disallow warping while on enderpearl cooldown.
            if (EnderpearlListener.getEnderpearlCooldown().containsKey(sender.getName()) && EnderpearlListener.getEnderpearlCooldown().get(sender.getName()) > System.currentTimeMillis()) {
                sender.sendMessage(ChatColor.RED + "You cannot teleport to your team headquarters while you are on enderpearl cooldown.");
                return;
            }

            boolean enemyWithinRange = false;

            for (Entity e : sender.getNearbyEntities(30, 256, 30)) {
                if (e instanceof Player) {
                    Player other = (Player) e;

                    if (other.hasMetadata("invisible") || Foxtrot.getInstance().getPvPTimerMap().hasTimer(other.getUniqueId())) {
                        continue;
                    }

                    if (!playerTeam.isMember(other.getUniqueId()) && !playerTeam.isAlly(other.getUniqueId())) {
                        enemyWithinRange = true;
                        break;
                    }
                }
            }

            if (enemyWithinRange) {
                sender.sendMessage(ChatColor.RED + "You cannot teleport to your team headquarters because an enemy is nearby.");
                return;
            }

            if (sender.getHealth() <= sender.getMaxHealth() - 1D) {
                sender.sendMessage(ChatColor.RED + "You cannot teleport to your team headquarters because you do not have full health.");
                return;
            }

            if (sender.getFoodLevel() != 20) {
                sender.sendMessage(ChatColor.RED + "You cannot teleport to your team headquarters because you do not have full hunger.");
                return;
            }

            Team inClaim = LandBoard.getInstance().getTeam(sender.getLocation());

            if (inClaim != null) {
                if (inClaim.getOwner() != null && !inClaim.isMember(sender.getUniqueId())) {
                    sender.sendMessage(ChatColor.RED + "You cannot teleport to your team headquarters from an enemy's claim!");
                    return;
                }

                if (inClaim.getOwner() == null && (inClaim.hasDTRBitmask(DTRBitmask.KOTH) || inClaim.hasDTRBitmask(DTRBitmask.CITADEL))) {
                    sender.sendMessage(ChatColor.RED + "You cannot teleport to your team headquarters from inside of events!");
                    return;
                }
            }
        }

        // Remove their PvP timer.
        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(sender.getUniqueId())) {
            Foxtrot.getInstance().getPvPTimerMap().removeTimer(sender.getUniqueId());
        }

        sender.sendMessage(ChatColor.LIGHT_PURPLE + "$" + WARP_PRICE + ChatColor.YELLOW + " has been deducted from your team balance.");
        playerTeam.setBalance(playerTeam.getBalance() - WARP_PRICE);

        sender.teleport(team.getHQ());
    }

}