package net.frozenorb.foxtrot.koth.commands.koth;

import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.foxtrot.koth.KOTHScheduledTime;
import net.frozenorb.qlib.command.Command;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.Map;

import static org.bukkit.ChatColor.*;

public class KOTHCommand {

    // Make this pretty.
    @Command(names={ "KOTH", "KOTH Next", "KOTH Info", "KOTH" }, permissionNode="")
    public static void koth(Player sender) {
        for (KOTH koth : Foxtrot.getInstance().getKOTHHandler().getKOTHs()) {
            if (!koth.isHidden() && koth.isActive()) {
                new FancyMessage("[KingOfTheHill] ")
                        .color(GOLD)
                        .then(koth.getName())
                            .color(YELLOW) // koth name should be yellow
                            .tooltip(YELLOW.toString() + koth.getCapLocation().getBlockX() + ", " + koth.getCapLocation().getBlockZ())
                            .color(YELLOW) // should color KOTH coords gray
                        .then(" can be contested now.")
                            .color(GOLD)
                        .send(sender);
                return;
            }
        }

        Date now = new Date();

        for (Map.Entry<KOTHScheduledTime, String> entry : Foxtrot.getInstance().getKOTHHandler().getKOTHSchedule().entrySet()) {
            if (entry.getKey().toDate().after(now)) {
                sender.sendMessage(GOLD + "[KingOfTheHill] " + YELLOW + entry.getValue() + GOLD + " can be captured at " + BLUE + KOTHScheduleCommand.KOTH_DATE_FORMAT.format(entry.getKey().toDate()) + GOLD + ".");
                sender.sendMessage(GOLD + "[KingOfTheHill] " + YELLOW + "It is currently " + BLUE + KOTHScheduleCommand.KOTH_DATE_FORMAT.format(now) + GOLD + ".");
                sender.sendMessage(YELLOW + "Type '/koth schedule' to see more upcoming KOTHs.");
                return;
            }
        }

        sender.sendMessage(GOLD + "[KingOfTheHill] " + RED + "Next KOTH: " + YELLOW + "Undefined");
    }

}