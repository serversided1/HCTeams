package net.frozenorb.foxtrot.map.kit.stats.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.map.kit.stats.StatsEntry;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.qlib.util.UUIDUtils;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

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
        sender.sendMessage(ChatColor.YELLOW + "KD: " + ChatColor.RED + (stats.getDeaths() == 0 ? "Infinity" : Team.DTR_FORMAT.format((double) stats.getKills() / (double) stats.getDeaths())));

        sender.sendMessage(ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));
    }

    @Command(names = {"clearallstats"}, permissionNode = "op")
    public static void clearallstats(Player sender) {
        ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            public String getPromptText(ConversationContext context) {
                return "§aAre you sure you want to clear all stats? Type §byes§a to confirm or §cno§a to quit.";
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String s) {
                if (s.equalsIgnoreCase("yes")) {
                    Foxtrot.getInstance().getMapHandler().getStatsHandler().clearAll();
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "All stats cleared!");
                    return Prompt.END_OF_CONVERSATION;
                }

                if (s.equalsIgnoreCase("no")) {
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Cancelled.");
                    return Prompt.END_OF_CONVERSATION;
                }

                cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Unrecognized response. Type §b/yes§a to confirm or §c/no§a to quit.");
                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false).withEscapeSequence("/no").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");

        Conversation con = factory.buildConversation(sender);
        sender.beginConversation(con);
    }

}
