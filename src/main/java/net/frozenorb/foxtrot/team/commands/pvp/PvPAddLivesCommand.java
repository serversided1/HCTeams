package net.frozenorb.foxtrot.team.commands.pvp;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.util.UUIDUtils;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class PvPAddLivesCommand {

    @Command(names={ "pvptimer addlives", "timer addlives", "pvp addlives", "pvptimer addlives", "timer addlives", "pvp addlives" }, permissionNode="op")
    public static void pvpSetLives(CommandSender sender, @Parameter(name="player") UUID target, @Parameter(name="Life Type") String lifeType, @Parameter(name="Amount") int amount) {
        if (lifeType.equalsIgnoreCase("soulbound")) {
            FoxtrotPlugin.getInstance().getSoulboundLivesMap().setLives(target, FoxtrotPlugin.getInstance().getSoulboundLivesMap().getLives(target) + amount);
            sender.sendMessage(ChatColor.YELLOW + "Gave " + ChatColor.GREEN + UUIDUtils.name(target) + ChatColor.YELLOW + " " + amount + " soulbound lives.");
        } else if (lifeType.equalsIgnoreCase("friend")) {
            FoxtrotPlugin.getInstance().getFriendLivesMap().setLives(target, FoxtrotPlugin.getInstance().getFriendLivesMap().getLives(target) + amount);
            sender.sendMessage(ChatColor.YELLOW + "Gave " + ChatColor.GREEN + UUIDUtils.name(target) + ChatColor.YELLOW + " " + amount + " friend lives.");
        } else if (lifeType.equalsIgnoreCase("transferable")) {
            FoxtrotPlugin.getInstance().getTransferableLivesMap().setLives(target, FoxtrotPlugin.getInstance().getTransferableLivesMap().getLives(target) + amount);
            sender.sendMessage(ChatColor.YELLOW + "Gave " + ChatColor.GREEN + UUIDUtils.name(target) + ChatColor.YELLOW + " " + amount + " transferable lives.");
        } else {
            sender.sendMessage(ChatColor.RED + "Not a valid life type: Options are soulbound, friend, or transferable.");
        }
    }

}