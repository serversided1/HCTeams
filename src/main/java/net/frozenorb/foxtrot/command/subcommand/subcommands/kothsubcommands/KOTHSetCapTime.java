package net.frozenorb.foxtrot.command.subcommand.subcommands.kothsubcommands;

import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.foxtrot.koth.KOTHs;
import org.bukkit.ChatColor;

/**
 * Created by macguy8 on 10/31/2014.
 */
public class KOTHSetCapTime extends Subcommand {

    public KOTHSetCapTime(String name, String errorMessage, String... aliases) {
        super(name, errorMessage, aliases);
    }

    @Override
    public void syncExecute() {
        if (sender.hasPermission("foxtrot.koth.admin")) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Error: " + getErrorMessage());
                return;
            }

            KOTH koth = KOTHs.getKOTH(args[1]);

            if (koth == null) {
                sender.sendMessage(ChatColor.RED + "No KOTH named " + args[1] + " found.");
                return;
            }

            int time = 3;

            try {
                time = Integer.valueOf(args[2]);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + args[2] + " is not an integer.");
                return;
            }

            koth.setCapTime(time);
            sender.sendMessage(ChatColor.GRAY + "Set cap time for the " + koth.getName() + " KOTH.");
        }
    }

}