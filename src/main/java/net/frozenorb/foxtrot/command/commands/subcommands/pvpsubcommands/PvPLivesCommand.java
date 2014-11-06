package net.frozenorb.foxtrot.command.commands.subcommands.pvpsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/6/2014.
 */
public class PvPLivesCommand {

    @Command(names={ "pvptimer lives", "timer lives", "pvp lives" }, permissionNode="")
    public static void pvpLives(Player sender, @Param(name="Player", defaultValue="self") String target) {
        if (target.equals("self")) {
            target = sender.getName();
        }

        sender.sendMessage(ChatColor.GOLD + "Soulbound Lives: " + FoxtrotPlugin.getInstance().getSoulboundLivesMap().getLives(target));
        sender.sendMessage(ChatColor.GOLD + "Friend Lives: " + FoxtrotPlugin.getInstance().getFriendLivesMap().getLives(target));
        sender.sendMessage(ChatColor.GOLD + "Transferable Lives: " + FoxtrotPlugin.getInstance().getTransferableLivesMap().getLives(target));
    }

}