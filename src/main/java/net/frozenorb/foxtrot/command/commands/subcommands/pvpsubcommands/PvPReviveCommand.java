package net.frozenorb.foxtrot.command.commands.subcommands.pvpsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/6/2014.
 */
public class PvPReviveCommand {

    @Command(names={ "pvptimer revive", "timer revive", "pvp revive", "pvptimer revive", "timer revive", "pvp revive", "f revive" }, permissionNode="")
    public static void pvpRevive(Player sender, @Param(name="player") String name) {
        int friendLives = FoxtrotPlugin.getInstance().getFriendLivesMap().getLives(sender.getName());
        int transferableLives = FoxtrotPlugin.getInstance().getTransferableLivesMap().getLives(sender.getName());

        if (friendLives == 0 && transferableLives == 0) {
            sender.sendMessage(ChatColor.RED + "You have no lives which can be used to revive other players!");
            return;
        }

        if (!FoxtrotPlugin.getInstance().getDeathbanMap().isDeathbanned(name)) {
            sender.sendMessage(ChatColor.RED + "That player is not deathbanned!");
            return;
        }

        FoxtrotPlugin.getInstance().getDeathbanMap().updateValue(name, 0L);
        FoxtrotPlugin.getInstance().getSoulboundLivesMap().updateValue(sender.getName(), lives - 1);

        sender.sendMessage(ChatColor.YELLOW + "You have revived " + ChatColor.GREEN + name + ChatColor.YELLOW + "!");
    }

}