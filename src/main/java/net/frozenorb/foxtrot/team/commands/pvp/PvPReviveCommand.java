package net.frozenorb.foxtrot.team.commands.pvp;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PvPReviveCommand {

    @Command(names={ "pvptimer revive", "timer revive", "pvp revive", "pvptimer revive", "timer revive", "pvp revive", "f revive" }, permissionNode="")
    public static void pvpRevive(Player sender, @Parameter(name="player") UUID target) {
        int friendLives = Foxtrot.getInstance().getFriendLivesMap().getLives(sender.getUniqueId());
        int transferableLives = Foxtrot.getInstance().getTransferableLivesMap().getLives(sender.getUniqueId());

        if (Foxtrot.getInstance().getServerHandler().isPreEOTW()) {
            sender.sendMessage(ChatColor.RED + "The server is in EOTW Mode: Lives cannot be used.");
            return;
        }

        if (friendLives == 0 && transferableLives == 0) {
            sender.sendMessage(ChatColor.RED + "You have no lives which can be used to revive other players!");
            return;
        }

        if (!Foxtrot.getInstance().getDeathbanMap().isDeathbanned(target)) {
            sender.sendMessage(ChatColor.RED + "That player is not deathbanned!");
            return;
        }

        if (friendLives == 0) {
            // Use a transferable life.
            Foxtrot.getInstance().getTransferableLivesMap().setLives(sender.getUniqueId(), transferableLives - 1);
            sender.sendMessage(ChatColor.YELLOW + "You have revived " + ChatColor.GREEN + UUIDUtils.name(target) + ChatColor.YELLOW + " with a transferable life!");
        } else {
            // Use a friend life.
            Foxtrot.getInstance().getFriendLivesMap().setLives(sender.getUniqueId(), friendLives - 1);
            sender.sendMessage(ChatColor.YELLOW + "You have revived " + ChatColor.GREEN + UUIDUtils.name(target) + ChatColor.YELLOW + " with a friend life!");
        }

        Foxtrot.getInstance().getDeathbanMap().revive(target);
    }

}