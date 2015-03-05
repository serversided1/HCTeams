package net.frozenorb.foxtrot.team.commands.pvp;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.qlib.command.annotations.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PvPReviveCommand {

    @Command(names={ "pvptimer revive", "timer revive", "pvp revive", "pvptimer revive", "timer revive", "pvp revive", "f revive" }, permissionNode="")
    public static void pvpRevive(Player sender, @Parameter(name="player") OfflinePlayer target) {
        int friendLives = FoxtrotPlugin.getInstance().getFriendLivesMap().getLives(sender.getUniqueId());
        int transferableLives = FoxtrotPlugin.getInstance().getTransferableLivesMap().getLives(sender.getUniqueId());

        if (FoxtrotPlugin.getInstance().getServerHandler().isPreEOTW()) {
            sender.sendMessage(ChatColor.RED + "The server is in EOTW Mode: Lives cannot be used.");
            return;
        }

        if (friendLives == 0 && transferableLives == 0) {
            sender.sendMessage(ChatColor.RED + "You have no lives which can be used to revive other players!");
            return;
        }

        if (!FoxtrotPlugin.getInstance().getDeathbanMap().isDeathbanned(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "That player is not deathbanned!");
            return;
        }

        if (friendLives == 0) {
            // Use a transferable life.
            FoxtrotPlugin.getInstance().getTransferableLivesMap().setLives(sender.getUniqueId(), transferableLives - 1);
            sender.sendMessage(ChatColor.YELLOW + "You have revived " + ChatColor.GREEN + target.getName() + ChatColor.YELLOW + " with a transferable life!");
        } else {
            // Use a friend life.
            FoxtrotPlugin.getInstance().getFriendLivesMap().setLives(sender.getUniqueId(), friendLives - 1);
            sender.sendMessage(ChatColor.YELLOW + "You have revived " + ChatColor.GREEN + target.getName() + ChatColor.YELLOW + " with a friend life!");
        }

        FoxtrotPlugin.getInstance().getDeathbanMap().revive(target.getUniqueId());
    }

}