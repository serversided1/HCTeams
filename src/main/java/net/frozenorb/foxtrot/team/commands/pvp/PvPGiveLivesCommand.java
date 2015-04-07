package net.frozenorb.foxtrot.team.commands.pvp;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PvPGiveLivesCommand {

    @Command(names={ "pvptimer givelives", "timer givelives", "pvp givelives", "pvptimer givelife", "timer givelife", "pvp givelife" }, permissionNode="")
    public static void pvpGiveLives(Player sender, @Parameter(name="player") Player player, @Parameter(name="amount") int amount) {
        int transferableLives = Foxtrot.getInstance().getTransferableLivesMap().getLives(sender.getUniqueId());

        if (transferableLives < amount) {
            sender.sendMessage(ChatColor.RED + "You do not have that many lives which can be given to other players!");
            return;
        }

        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "You can only give a positive number of lives!");
            return;
        }

        Foxtrot.getInstance().getTransferableLivesMap().setLives(sender.getUniqueId(), transferableLives - amount);
        Foxtrot.getInstance().getTransferableLivesMap().setLives(player.getUniqueId(), Foxtrot.getInstance().getTransferableLivesMap().getLives(player.getUniqueId()) + amount);

        sender.sendMessage(ChatColor.YELLOW + "Gave " + amount + " transferable " + (amount == 1 ? "life" : "lives") + " to " + ChatColor.BLUE + player.getName() + ChatColor.YELLOW + ".");
        player.sendMessage(ChatColor.YELLOW + "Received " + amount + " transferable " + (amount == 1 ? "life" : "lives") + " from " + ChatColor.BLUE + sender.getName() + ChatColor.YELLOW + ".");
    }

}