package net.frozenorb.foxtrot.command.commands.subcommands.pvpsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/6/2014.
 */
public class PvPSetLivesCommand {

    @Command(names={ "pvptimer setlives", "timer setlives", "pvp setlives", "pvptimer setlives", "timer setlives", "pvp setlives" }, permissionNode="op")
    public static void pvpSetLives(Player sender, @Param(name="player") OfflinePlayer target, @Param(name="Life Type") String lifeType, @Param(name="Amount") int amount) {
        if (lifeType.equalsIgnoreCase("soulbound")) {
            FoxtrotPlugin.getInstance().getSoulboundLivesMap().setLives(target.getName(), amount);
            sender.sendMessage(ChatColor.YELLOW + "Set " + ChatColor.GREEN + target.getName() + ChatColor.YELLOW + "'s soulbound life count to " + amount + ".");
        } else if (lifeType.equalsIgnoreCase("friend")) {
            FoxtrotPlugin.getInstance().getFriendLivesMap().setLives(target.getName(), amount);
            sender.sendMessage(ChatColor.YELLOW + "Set " + ChatColor.GREEN + target.getName() + ChatColor.YELLOW + "'s friend life count to " + amount + ".");
        } else if (lifeType.equalsIgnoreCase("transferable")) {
            FoxtrotPlugin.getInstance().getTransferableLivesMap().setLives(target.getName(), amount);
            sender.sendMessage(ChatColor.YELLOW + "Set " + ChatColor.GREEN + target.getName() + ChatColor.YELLOW + "'s transferable life count to " + amount + ".");
        } else {
            sender.sendMessage(ChatColor.RED + "Not a valid life type: Options are soulbound, friend, or transferable.");
        }
    }

}