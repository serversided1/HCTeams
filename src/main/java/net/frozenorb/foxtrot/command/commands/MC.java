package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class MC extends BaseCommand {

    public MC() {
        super("mc");
    }

    @Override
    public void syncExecute() {
        if (args.length > 0) {
            Player p = (Player) sender;
            switch (args[0]) {
                case "fc":
                    if (!p.hasMetadata("teamchat")) {
                        p.setMetadata("teamChat", new FixedMetadataValue(FoxtrotPlugin.getInstance(), true));
                    }
                    p.sendMessage(ChatColor.DARK_AQUA + "You are now in team chat only mode.");
                    break;
                case "gc":
                    if (p.hasMetadata("teamChat")) {
                        p.removeMetadata("teamChat", FoxtrotPlugin.getInstance());
                    }
                    p.sendMessage(ChatColor.DARK_AQUA + "You are now in public chat.");
                    break;

                default:
                    sender.sendMessage(ChatColor.RED + "/mc <fc|gc>");
                    break;
            }

        } else {
            sender.sendMessage(ChatColor.RED + "/mc <fc|gc>");
        }

    }

}
