package net.frozenorb.foxtrot.map.kit.stats.command;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.google.common.collect.Lists;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.map.kit.killstreaks.Killstreak;
import net.frozenorb.foxtrot.map.kit.killstreaks.PersistentKillstreak;
import net.frozenorb.qlib.command.Command;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;

public class KillstreaksCommand {

    @Command(names = {"killstreaks", "ks", "killstreak"}, permission = "")
    public static void killstreaks(CommandSender sender) {
    	if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
    		sender.sendMessage(ChatColor.RED + "You cannot perform this command on this server.");
    		return;
	    }

        sender.sendMessage(ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));

        List<Object> streaks = Lists.newArrayList(Foxtrot.getInstance().getMapHandler().getKillstreakHandler().getKillstreaks());
        streaks.addAll(Foxtrot.getInstance().getMapHandler().getKillstreakHandler().getPersistentKillstreaks());

        streaks.sort((first, second) -> {

            int firstNumber = first instanceof Killstreak ? ((Killstreak) first).getKills()[0] : ((PersistentKillstreak) first).getKillsRequired();
            int secondNumber = second instanceof Killstreak ? ((Killstreak) second).getKills()[0] : ((PersistentKillstreak) second).getKillsRequired();

            if (firstNumber < secondNumber) {
                return -1;
            }
            return 1;

        });

        for (Object ks : streaks) {
            String name = ks instanceof Killstreak ? ((Killstreak) ks).getName() : ((PersistentKillstreak) ks).getName();
            int kills = ks instanceof Killstreak ? ((Killstreak) ks).getKills()[0] : ((PersistentKillstreak) ks).getKillsRequired();


            sender.sendMessage(ChatColor.YELLOW + name + ": " + ChatColor.RED + kills);
        }

        sender.sendMessage(ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));
    }

}
