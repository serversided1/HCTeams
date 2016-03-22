package net.frozenorb.foxtrot.team.commands.pvp;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class PvPAddLivesCommand {

    @Command(names={ "pvptimer addlives", "timer addlives", "pvp addlives", "pvptimer addlives", "timer addlives", "pvp addlives" }, permissionNode="worldedit.*")
    public static void pvpSetLives(CommandSender sender, @Parameter(name="player") UUID player, @Parameter(name="life type") String lifeType, @Parameter(name="amount") int amount) {
        if (lifeType.equalsIgnoreCase("soulbound")) {
            Foxtrot.getInstance().getSoulboundLivesMap().setLives(player, Foxtrot.getInstance().getSoulboundLivesMap().getLives(player) + amount);
            sender.sendMessage(ChatColor.YELLOW + "Gave " + ChatColor.GREEN + UUIDUtils.name(player) + ChatColor.YELLOW + " " + amount + " soulbound lives.");
        } else if (lifeType.equalsIgnoreCase("friend")) {
            Foxtrot.getInstance().getFriendLivesMap().setLives(player, Foxtrot.getInstance().getFriendLivesMap().getLives(player) + amount);
            sender.sendMessage(ChatColor.YELLOW + "Gave " + ChatColor.GREEN + UUIDUtils.name(player) + ChatColor.YELLOW + " " + amount + " friend lives.");
        }  else {
            sender.sendMessage(ChatColor.RED + "Not a valid life type: Options are soulbound or friend");
        }
    }

}