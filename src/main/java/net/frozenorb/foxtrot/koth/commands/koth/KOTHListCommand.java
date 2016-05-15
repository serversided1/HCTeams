package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.entity.Player;

import java.util.Set;

import static org.bukkit.ChatColor.*;

public class KOTHListCommand {

    @Command(names={ "KOTH List" }, permission="foxtrot.koth")
    public static void kothList(Player sender) {
        if(Foxtrot.getInstance().getKOTHHandler().getKOTHs().isEmpty()) {
            sender.sendMessage(RED + "There aren't any KOTHs set.");
            return;
        }

        for (KOTH koth : Foxtrot.getInstance().getKOTHHandler().getKOTHs()) {
            sender.sendMessage((koth.isHidden() ? DARK_GRAY + "[H] " : "") + (koth.isActive() ? GREEN : RED) +
                    koth.getName() + WHITE + " - " + GRAY + TimeUtils.formatIntoMMSS(koth.getRemainingCapTime())
                    + DARK_GRAY + "/" + GRAY + TimeUtils.formatIntoMMSS(koth.getCapTime()) + " " + WHITE + "- "
                    + GRAY + (koth.getCurrentCapper() == null ? "None" : koth.getCurrentCapper()));
        }
    }

}