package net.frozenorb.foxtrot.command.subcommand.subcommands.kothsubcommands;

import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.foxtrot.koth.KOTHHandler;
import org.bukkit.ChatColor;

/**
 * Created by macguy8 on 10/31/2014.
 */
public class KOTHList extends Subcommand {

    public KOTHList(String name, String errorMessage, String... aliases) {
        super(name, errorMessage, aliases);
    }

    @Override
    public void syncExecute() {
        if (sender.hasPermission("foxtrot.koth")) {
            for (KOTH koth : KOTHHandler.getKOTHs()) {
                sender.sendMessage((koth.isActive() ? ChatColor.GREEN : ChatColor.RED) + koth.getName() + " KOTH " + ChatColor.WHITE + "- " + ChatColor.GRAY + koth.getRemainingCapTime() + ChatColor.DARK_GRAY + "/" + ChatColor.GRAY + koth.getCapTime() + " " + ChatColor.WHITE + "- " + ChatColor.GRAY + (koth.getCurrentCapper() == null ? "None" : koth.getCurrentCapper()));
            }
        }
    }

}