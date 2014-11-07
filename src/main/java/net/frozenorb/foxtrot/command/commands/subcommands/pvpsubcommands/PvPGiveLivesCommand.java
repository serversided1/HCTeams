package net.frozenorb.foxtrot.command.commands.subcommands.pvpsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/6/2014.
 */
public class PvPGiveLivesCommand {

    @Command(names={ "pvptimer givelives", "timer givelives", "pvp givelives", "pvptimer givelife", "timer givelife", "pvp givelife" }, permissionNode="")
    public static void pvpGiveLives(Player sender, @Param(name="Player") Player target, @Param(name="Amount") int amount) {
        int transferableLives = FoxtrotPlugin.getInstance().getTransferableLivesMap().getLives(sender.getName());

        if (transferableLives < amount) {
            sender.sendMessage(ChatColor.RED + "You do not have that many lives which can be given to other players!");
            return;
        }

        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "You can only give a positive number of lives!");
            return;
        }

        FoxtrotPlugin.getInstance().getTransferableLivesMap().updateValue(sender.getName(), transferableLives - amount);
        FoxtrotPlugin.getInstance().getTransferableLivesMap().updateValue(target.getName(), FoxtrotPlugin.getInstance().getTransferableLivesMap().getValue(target.getName()) + amount);

        sender.sendMessage(ChatColor.YELLOW + "Gave " + amount + " transferable " + (amount == 1 ? "life" : "lives") + " to " + ChatColor.BLUE + target.getName() + ChatColor.YELLOW + ".");
        target.sendMessage(ChatColor.YELLOW + "Received " + amount + " transferable " + (amount == 1 ? "life" : "lives") + " from " + ChatColor.BLUE + sender.getName() + ChatColor.YELLOW + ".");
    }

}