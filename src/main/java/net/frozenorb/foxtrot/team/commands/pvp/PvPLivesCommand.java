package net.frozenorb.foxtrot.team.commands.pvp;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class PvPLivesCommand {

    @Command(names={ "pvptimer lives", "timer lives", "pvp lives" }, permissionNode="")
    public static void pvpLives(CommandSender sender, @Parameter(name="player", defaultValue="self") UUID player) {
        String name = UUIDUtils.name(player);

        sender.sendMessage(ChatColor.GOLD + name + "'s Soulbound Lives: " + ChatColor.WHITE + Foxtrot.getInstance().getSoulboundLivesMap().getLives(player));
        sender.sendMessage(ChatColor.GOLD + name + "'s Friend Lives: " + ChatColor.WHITE + Foxtrot.getInstance().getFriendLivesMap().getLives(player));
        sender.sendMessage(ChatColor.GOLD + name + "'s Transferable Lives: " + ChatColor.WHITE + Foxtrot.getInstance().getTransferableLivesMap().getLives(player));
    }

}