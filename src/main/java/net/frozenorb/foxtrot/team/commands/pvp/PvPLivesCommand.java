package net.frozenorb.foxtrot.team.commands.pvp;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class PvPLivesCommand {

    @Command(names={ "pvptimer lives", "timer lives", "pvp lives" }, permissionNode="")
    public static void pvpLives(CommandSender sender, @Param(name="Player", defaultValue="self") OfflinePlayer target) {
        sender.sendMessage(ChatColor.GOLD + target.getName() + "'s Soulbound Lives: " + ChatColor.WHITE + FoxtrotPlugin.getInstance().getSoulboundLivesMap().getLives(target.getName()));
        sender.sendMessage(ChatColor.GOLD + target.getName() + "'s Friend Lives: " + ChatColor.WHITE + FoxtrotPlugin.getInstance().getFriendLivesMap().getLives(target.getName()));
        sender.sendMessage(ChatColor.GOLD + target.getName() + "'s Transferable Lives: " + ChatColor.WHITE + FoxtrotPlugin.getInstance().getTransferableLivesMap().getLives(target.getName()));
    }

}