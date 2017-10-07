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
                "§9§l" + serverName + " Help §7- §eInformation on " + serverName,
                "§7§m-----------------------------------------------------",
                "§9Map Information:",
                "§eCurrent Map: §7" + Foxtrot.getInstance().getMapHandler().getMapStartedString(),
                "§eMap Border: §7" + BorderListener.BORDER_SIZE,
                "§eWarzone Until: §7" +  ServerHandler.WARZONE_RADIUS,
                "§eEnd Portals: §7" +  Foxtrot.getInstance().getMapHandler().getEndPortalLocation() + " in each quadrant",
                "§eEnchant Limits: §7" + prot + ", " + sharp + ", " + bow,
                "§eDeathban: §71 hour",

                "",
                "§9Helpful Commands:",
                "§e/report <player> <reason> §7- Report cheaters with this command!",
                "§e/request <message> §7- Request staff assistance.",

                "",
                "§9Chat Commands:",
                "§eGlobal Chat §7- Prefix your message with '§d!§7' or type '§d/gc§7' to set.",
                "§eTeam Chat §7- Prefix your message with '§d@§7' or type '§d/tc§7' to set.",
                "§eOfficer Chat §7- Prefix your message with '§d^§7' or type '§d/oc§7' to set.",
                "§eHide/Show Global Chat §7- Type '§d/tgc§7' to toggle chat visibility.",

                "",
                "§9Other Information:",
                "§eOfficial Teamspeak §7- §dts." + serverWebsite,
                "§e" + serverName + " Rules §7- §dwww." + serverWebsite + "/rules",
                "§eStore §7- §dwww." + serverWebsite + "/store",
                "§6§m-----------------------------------------------------",

        });
    }

}
