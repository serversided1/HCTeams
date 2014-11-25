package net.frozenorb.foxtrot.team.commands.pvp;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

/**
 * Created by macguy8 on 11/6/2014.
 */
public class PvPReviveCommand {

    @Command(names={ "pvptimer revive", "timer revive", "pvp revive", "pvptimer revive", "timer revive", "pvp revive", "f revive" }, permissionNode="")
    public static void pvpRevive(Player sender, @Param(name="player") OfflinePlayer target) {
        int friendLives = FoxtrotPlugin.getInstance().getFriendLivesMap().getLives(sender.getName());
        int transferableLives = FoxtrotPlugin.getInstance().getTransferableLivesMap().getLives(sender.getName());

        if (FoxtrotPlugin.getInstance().getServerHandler().isPreEOTW()) {
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

        if (friendLives == 0) {
            // Use a transferable life.
            FoxtrotPlugin.getInstance().getTransferableLivesMap().setLives(sender.getName(), transferableLives - 1);
            sender.sendMessage(ChatColor.YELLOW + "You have revived " + ChatColor.GREEN + target.getName() + ChatColor.YELLOW + " with a transferable life!");
        } else {
            // Use a friend life.
            FoxtrotPlugin.getInstance().getFriendLivesMap().setLives(sender.getName(), friendLives - 1);
            sender.sendMessage(ChatColor.YELLOW + "You have revived " + ChatColor.GREEN + target.getName() + ChatColor.YELLOW + " with a friend life!");
        }

        FoxtrotPlugin.getInstance().getDeathbanMap().revive(target.getName());
    }

}