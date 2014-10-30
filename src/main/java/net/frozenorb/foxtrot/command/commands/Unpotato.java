package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.BaseCommand;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;

public class Unpotato extends BaseCommand {

    public Unpotato() {
        super("unpotato", "untater");
        setPermissionLevel("raven.potato", "§cYou are not allowed to do this!");
    }

    @Override
    public void syncExecute() {
        if (args.length > 0) {
            String pName = args[0].toLowerCase();

            if (!Potato.potatoSet.contains(pName)) {
                sender.sendMessage(ChatColor.RED + "That player is not potatoed.");
                return;
            }

            Potato.potatoSet.remove(pName);
            sender.sendMessage(ChatColor.GREEN + "You have unpotatoed " + args[0] + "!");

        } else {
            sender.sendMessage(ChatColor.RED + "/unpotato <playerName>");
        }
    }

    @Override
    public List<String> getTabCompletions() {
        return Arrays.asList(Potato.potatoSet.toArray(new String[]{}));
    }
}
