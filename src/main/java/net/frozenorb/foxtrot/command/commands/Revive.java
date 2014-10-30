package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.BaseCommand;
import org.bukkit.ChatColor;

import java.util.List;

public class Revive extends BaseCommand {

    public Revive() {
        super("revive", "removedeathban");
    }

    @Override
    public void syncExecute() {
        if (sender.isOp()) {
            if (args.length > 0) {
                String name = args[0];

                if (FoxtrotPlugin.getInstance().getDeathbanMap().isDeathbanned(name)) {
                    FoxtrotPlugin.getInstance().getDeathbanMap().updateValue(name, 0L);
                    sender.sendMessage(ChatColor.GREEN + "Revived " + name + "!");
                } else {
                    sender.sendMessage(ChatColor.RED + "That player is not deathbanned!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "/revive <player>");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You are not allowed to do this! Did you mean §e/pvp revive§c?");
        }
    }

    @Override
    public List<String> getTabCompletions() {
        return FoxtrotPlugin.getInstance().getDeathbanMap().keyList();
    }
}
