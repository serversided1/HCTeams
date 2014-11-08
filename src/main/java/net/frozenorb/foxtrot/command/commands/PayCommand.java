package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.mBasic.Basic;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.NumberFormat;

/**
 * @author Connor Hollasch
 * @since 10/10/14
 */
public class PayCommand {

    @Command(names={ "Pay", "P2P" }, permissionNode="")
    public static void mc(Player sender, @Param(name="Target") String target, @Param(name="Amount") float value) {
        double balance = Basic.get().getEconomyManager().getBalance(sender.getName());

        if(!(FoxtrotPlugin.getInstance().getPlaytimeMap().contains(target))){
            sender.sendMessage(ChatColor.RED + target + " has never played before!");
            return;
        }

        //Format online players name
        Player pTarget = Bukkit.getPlayer(target);

        if (pTarget != null){
            target = pTarget.getName();
        }

        if (target.equalsIgnoreCase(sender.getName())) {
            sender.sendMessage(ChatColor.RED + "You cannot send money to yourself!");
            return;
        }

        /*
        if (!FoxtrotPlugin.getInstance().getServerHandler().isGlobalSpawn(player.getLocation()) && !FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName()).ownsLocation(player.getLocation())) {
            sender.sendMessage(ChatColor.RED+"You can only do this in spawn or in your own territory!");
            return;
        }
        */

        if (value < 50) {
            sender.sendMessage(ChatColor.RED + "You must send at least $50!");
            return;
        }

        if (balance < value) {
            sender.sendMessage(ChatColor.RED + "You do not have $" + value + "!");
            return;
        }

        Basic.get().getEconomyManager().depositPlayer(target, value);
        Basic.get().getEconomyManager().withdrawPlayer(sender.getName(), value);

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou sent &d" + NumberFormat.getCurrencyInstance().format(value) + "&e to &d" + target + "&e!"));
    }
}
