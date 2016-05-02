package net.frozenorb.foxtrot.map.kit.stats.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.map.kit.killstreaks.Killstreak;
import net.frozenorb.qlib.command.Command;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class KillstreaksCommand {

    @Command(names = {"killstreaks", "ks", "killstreak"}, permissionNode = "")
    public static void killstreaks(CommandSender sender) {

        sender.sendMessage(ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));

        for (Killstreak killstreak : Foxtrot.getInstance().getMapHandler().getKillstreakHandler().getKillstreaks()) {
            sender.sendMessage(ChatColor.YELLOW + killstreak.getName() + ": " + ChatColor.RED + killstreak.getKills()[0]);
        }

        sender.sendMessage(ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));
    }

}
