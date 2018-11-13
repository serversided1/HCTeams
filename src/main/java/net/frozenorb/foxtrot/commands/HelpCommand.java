package net.frozenorb.foxtrot.commands;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.BorderListener;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.frozenorb.qlib.command.Command;

public class HelpCommand {

    @Command(names={ "Help" }, permission="")
    public static void help(Player sender) {
        String sharp = "Sharpness " + Enchantment.DAMAGE_ALL.getMaxLevel();
        String prot = "Protection " + Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel();
        String bow = "Power " + Enchantment.ARROW_DAMAGE.getMaxLevel();

        String serverName = Foxtrot.getInstance().getServerHandler().getServerName();
        String serverWebsite = Foxtrot.getInstance().getServerHandler().getNetworkWebsite();

        sender.sendMessage(new String[] {

                "§6§m-----------------------------------------------------",
                "§9Helpful Commands:",
                "§e/report <player> <reason> §7- Report cheaters!",
                "§e/request <message> §7- Request staff assistance.",
                "§e/tgc §7- Toggle chat visibility.",

                "",
                "§9Other Information:",
                "§eOfficial Teamspeak §7- §dts." + serverWebsite,
                "§e" + serverName + " Rules §7- §dwww." + serverWebsite + "/rules",
                "§eStore §7- §dwww." + serverWebsite + "/store",
                "§6§m-----------------------------------------------------",

        });
    }

}
