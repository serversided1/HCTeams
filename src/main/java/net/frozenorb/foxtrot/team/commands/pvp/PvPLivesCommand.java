package net.frozenorb.foxtrot.team.commands.pvp;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.util.UUIDUtils;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class PvPLivesCommand {

    @Command(names={ "pvptimer lives", "timer lives", "pvp lives" }, permissionNode="")
    public static void pvpLives(CommandSender sender, @Parameter(name="Player", defaultValue="self") UUID target) {
        String name = UUIDUtils.name(target);

        sender.sendMessage(ChatColor.GOLD + name + "'s Soulbound Lives: " + ChatColor.WHITE + FoxtrotPlugin.getInstance().getSoulboundLivesMap().getLives(target));
        sender.sendMessage(ChatColor.GOLD + name + "'s Friend Lives: " + ChatColor.WHITE + FoxtrotPlugin.getInstance().getFriendLivesMap().getLives(target));
        sender.sendMessage(ChatColor.GOLD + name + "'s Transferable Lives: " + ChatColor.WHITE + FoxtrotPlugin.getInstance().getTransferableLivesMap().getLives(target));
    }

}