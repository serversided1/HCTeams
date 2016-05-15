package net.frozenorb.foxtrot.team.commands.pvp;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PvPAddLivesCommand {

    @Command(names = {"pvptimer addlives", "timer addlives", "pvp addlives", "pvptimer addlives", "timer addlives", "pvp addlives"}, permission = "worldedit.*")
    public static void pvpSetLives(CommandSender sender, @Param(name = "player") UUID player, @Param(name = "life type") String lifeType, @Param(name = "amount") int amount) {
        if (lifeType.equalsIgnoreCase("soulbound")) {
            Foxtrot.getInstance().getSoulboundLivesMap().setLives(player, Foxtrot.getInstance().getSoulboundLivesMap().getLives(player) + amount);
            sender.sendMessage(ChatColor.YELLOW + "Gave " + ChatColor.GREEN + UUIDUtils.name(player) + ChatColor.YELLOW + " " + amount + " soulbound lives.");

            Player bukkitPlayer = Bukkit.getPlayer(player);
            if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
                String suffix = sender instanceof Player ? " from " + sender.getName() : "";
                bukkitPlayer.sendMessage(ChatColor.GREEN + "You have received " + amount + " lives" + suffix);
            }

        } else if (lifeType.equalsIgnoreCase("friend")) {
            Foxtrot.getInstance().getFriendLivesMap().setLives(player, Foxtrot.getInstance().getFriendLivesMap().getLives(player) + amount);
            sender.sendMessage(ChatColor.YELLOW + "Gave " + ChatColor.GREEN + UUIDUtils.name(player) + ChatColor.YELLOW + " " + amount + " friend lives.");

            Player bukkitPlayer = Bukkit.getPlayer(player);
            if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
                String suffix = sender instanceof Player ? " from " + sender.getName() : "";
                bukkitPlayer.sendMessage(ChatColor.GREEN + "You have received " + amount + " lives" + suffix);
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Not a valid life type: Options are soulbound or friend");
        }
    }

}