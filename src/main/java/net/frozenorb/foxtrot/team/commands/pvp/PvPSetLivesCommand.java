package net.frozenorb.foxtrot.team.commands.pvp;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.util.UUIDUtils;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PvPSetLivesCommand {

    @Command(names={ "pvptimer setlives", "timer setlives", "pvp setlives", "pvptimer setlives", "timer setlives", "pvp setlives" }, permissionNode="op")
    public static void pvpSetLives(Player sender, @Parameter(name="player") UUID target, @Parameter(name="Life Type") String lifeType, @Parameter(name="Amount") int amount) {
        if (lifeType.equalsIgnoreCase("soulbound")) {
            FoxtrotPlugin.getInstance().getSoulboundLivesMap().setLives(target, amount);
            sender.sendMessage(ChatColor.YELLOW + "Set " + ChatColor.GREEN + UUIDUtils.name(target) + ChatColor.YELLOW + "'s soulbound life count to " + amount + ".");
        } else if (lifeType.equalsIgnoreCase("friend")) {
            FoxtrotPlugin.getInstance().getFriendLivesMap().setLives(target, amount);
            sender.sendMessage(ChatColor.YELLOW + "Set " + ChatColor.GREEN + UUIDUtils.name(target) + ChatColor.YELLOW + "'s friend life count to " + amount + ".");
        } else if (lifeType.equalsIgnoreCase("transferable")) {
            FoxtrotPlugin.getInstance().getTransferableLivesMap().setLives(target, amount);
            sender.sendMessage(ChatColor.YELLOW + "Set " + ChatColor.GREEN + UUIDUtils.name(target) + ChatColor.YELLOW + "'s transferable life count to " + amount + ".");
        } else {
            sender.sendMessage(ChatColor.RED + "Not a valid life type: Options are soulbound, friend, or transferable.");
        }
    }

}