package net.frozenorb.foxtrot.map.kit.stats.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.map.kit.stats.StatsEntry;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.qlib.util.UUIDUtils;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class StatsCommand {

    @Command(names = {"stats"}, permissionNode = "")
    public static void stats(CommandSender sender, @Parameter(name = "player", defaultValue = "self") UUID uuid) {
        StatsEntry stats = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(uuid);

        if (stats == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return;
        }

        sender.sendMessage(ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));
        sender.sendMessage(ChatColor.YELLOW + UUIDUtils.name(uuid));
        sender.sendMessage(ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));

        sender.sendMessage(ChatColor.YELLOW + "Kills: " + ChatColor.RED + stats.getKills());
        sender.sendMessage(ChatColor.YELLOW + "Deaths: " + ChatColor.RED + stats.getDeaths());
        sender.sendMessage(ChatColor.YELLOW + "Killstreak: " + ChatColor.RED + stats.getKillstreak());
        sender.sendMessage(ChatColor.YELLOW + "Highest Killstreak: " + ChatColor.RED + stats.getHighestKillstreak());

        sender.sendMessage(ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));
    }

}
