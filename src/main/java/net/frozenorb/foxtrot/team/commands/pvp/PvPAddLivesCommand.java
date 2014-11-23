package net.frozenorb.foxtrot.team.commands.pvp;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

/**
 * Created by macguy8 on 11/23/2014.
 */
public class PvPAddLivesCommand {

    @Command(names={ "pvptimer addlives", "timer addlives", "pvp addlives", "pvptimer addlives", "timer addlives", "pvp addlives" }, permissionNode="op")
    public static void pvpSetLives(CommandSender sender, @Param(name="player") OfflinePlayer target, @Param(name="Life Type") String lifeType, @Param(name="Amount") int amount) {
        if (lifeType.equalsIgnoreCase("soulbound")) {
            FoxtrotPlugin.getInstance().getSoulboundLivesMap().setLives(target.getName(), FoxtrotPlugin.getInstance().getSoulboundLivesMap().getLives(target.getName()) + amount);
            sender.sendMessage(ChatColor.YELLOW + "Gave " + ChatColor.GREEN + target.getName() + ChatColor.YELLOW + " " + amount + " soulbound lives.");
        } else if (lifeType.equalsIgnoreCase("friend")) {
            FoxtrotPlugin.getInstance().getFriendLivesMap().setLives(target.getName(), FoxtrotPlugin.getInstance().getFriendLivesMap().getLives(target.getName()) + amount);
            sender.sendMessage(ChatColor.YELLOW + "Gave " + ChatColor.GREEN + target.getName() + ChatColor.YELLOW + " " + amount + " friend lives.");
        } else if (lifeType.equalsIgnoreCase("transferable")) {
            FoxtrotPlugin.getInstance().getTransferableLivesMap().setLives(target.getName(), FoxtrotPlugin.getInstance().getTransferableLivesMap().getLives(target.getName()) + amount);
            sender.sendMessage(ChatColor.YELLOW + "Gave " + ChatColor.GREEN + target.getName() + ChatColor.YELLOW + " " + amount + " transferable lives.");
        } else {
            sender.sendMessage(ChatColor.RED + "Not a valid life type: Options are soulbound, friend, or transferable.");
        }
    }

}