package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.BaseCommand;
import net.frozenorb.mBasic.Basic;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.NumberFormat;

/**
 * @author Connor Hollasch
 * @since 10/10/14
 */
public class Pay extends BaseCommand {

    public Pay() {
        super("pay", "p2p");
    }

    @Override
    public void syncExecute() {
        Player player = (Player) sender;

        double balance = Basic.get().getEconomyManager().getBalance(sender.getName());

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Format: /" + label + " <player> <balance>");
            return;
        }

        String target = args[0];

        if(!(FoxtrotPlugin.getInstance().getPlaytimeMap().contains(target))){
            player.sendMessage(ChatColor.RED + target + " has never played before!");
            return;
        }

        //Format online players name
        Player pTarget = Bukkit.getPlayer(target);

        if(pTarget != null){
            target = pTarget.getName();
        }

        if(target.equalsIgnoreCase(sender.getName())){
            sender.sendMessage(ChatColor.RED + "You cannot send money to yourself!");
            return;
        }

        /*
        if (!FoxtrotPlugin.getInstance().getServerManager().isGlobalSpawn(player.getLocation()) && !FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(player.getName()).ownsLocation(player.getLocation())) {
            sender.sendMessage(ChatColor.RED+"You can only do this in spawn or in your own territory!");
            return;
        }
        */

        double pay = 0.0;

        try {
            pay = Double.parseDouble(args[1]);
            if (pay < 50) {
                sender.sendMessage(ChatColor.RED + "You must send at least $50!");
                return;
            }

            if (balance < pay) {
                sender.sendMessage(ChatColor.RED + "You do not have $" + pay + "!");
                return;
            }

            Basic.get().getEconomyManager().depositPlayer(target, pay);
            Basic.get().getEconomyManager().withdrawPlayer(sender.getName(), pay);

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou sent &d" + NumberFormat.getCurrencyInstance().format(pay) + "&e to &d" + target + "&e!"));
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Number couldn't be parsed!");
            return;
        }
    }
}
