package net.frozenorb.foxtrot.command.commands.subcommands.pvpsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/6/2014.
 */
public class PvPReviveCommand {

    @Command(names={ "pvptimer revive", "timer revive", "pvp revive", "pvptimer revive", "timer revive", "pvp revive", "f revive" }, permissionNode="")
    public static void pvpRevive(Player sender, @Param(name="player") OfflinePlayer target) {
        int friendLives = FoxtrotPlugin.getInstance().getFriendLivesMap().getLives(sender.getName());
        int transferableLives = FoxtrotPlugin.getInstance().getTransferableLivesMap().getLives(sender.getName());

        if (FoxtrotPlugin.getInstance().getServerHandler().isEOTW()) {
            sender.sendMessage(ChatColor.RED + "The server is in EOTW Mode: Lives cannot be used.");
            return;
        }

        if (friendLives == 0 && transferableLives == 0) {
            sender.sendMessage(ChatColor.RED + "You have no lives which can be used to revive other players!");
            return;
        }

        if (!FoxtrotPlugin.getInstance().getDeathbanMap().isDeathbanned(target.getName())) {
            sender.sendMessage(ChatColor.RED + "That player is not deathbanned!");
            return;
        }

        FoxtrotPlugin.getInstance().getDeathbanMap().updateValue(target.getName(), 0L);

        if (friendLives == 0) {
            // Use a transferable life.
            FoxtrotPlugin.getInstance().getTransferableLivesMap().updateValue(sender.getName(), transferableLives - 1);
            sender.sendMessage(ChatColor.YELLOW + "You have revived " + ChatColor.GREEN + target.getName() + ChatColor.YELLOW + " with a transferable life!");
        } else {
            // Use a friend life.
            FoxtrotPlugin.getInstance().getFriendLivesMap().updateValue(sender.getName(), friendLives - 1);
            sender.sendMessage(ChatColor.YELLOW + "You have revived " + ChatColor.GREEN + target.getName() + ChatColor.YELLOW + " with a friend life!");
        }
    }

}