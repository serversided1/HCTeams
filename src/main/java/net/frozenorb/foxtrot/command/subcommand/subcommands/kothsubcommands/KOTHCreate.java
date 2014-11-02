package net.frozenorb.foxtrot.command.subcommand.subcommands.kothsubcommands;

import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.koth.KOTH;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 10/31/2014.
 */
public class KOTHCreate extends Subcommand {

    public KOTHCreate(String name, String errorMessage, String... aliases) {
        super(name, errorMessage, aliases);
    }

    @Override
    public void syncExecute() {
        if (sender.hasPermission("foxtrot.koth.admin")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Error: " + getErrorMessage());
                return;
            }

            new KOTH(args[1], ((Player) sender).getLocation().toVector().toBlockVector());
            sender.sendMessage(ChatColor.GRAY + "Created a KOTH named " + args[1] + ".");
        }
    }

}