package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.BaseCommand;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.ChatColor;

public class Gopple extends BaseCommand {

    public Gopple() {
        super("gopple", "opple", "goppletime", "oppletime", "goppletimer", "oppletimer");
    }

    @Override
    public void syncExecute() {
        String name = sender.getName();
        if (sender.isOp() && args.length > 0) {

            name = args[0];
        }

        if (FoxtrotPlugin.getInstance().getOppleMap().contains(name)) {

            Long i = FoxtrotPlugin.getInstance().getOppleMap().getValue(name);

            if (i != null && i > System.currentTimeMillis()) {
                long millisLeft = i - System.currentTimeMillis();

                String msg = TimeUtils.getDurationBreakdown(millisLeft);

                if (sender.getName().equals(name)) {
                    sender.sendMessage(ChatColor.GOLD + "Gopple cooldown§f: " + msg);

                } else {
                    sender.sendMessage(ChatColor.GOLD + name + "'s gopple cooldown§f: " + msg);
                }

            } else {
                sender.sendMessage(ChatColor.RED + "No current gopple cooldown!");
            }

        } else {
            sender.sendMessage(ChatColor.RED + "No current gopple cooldown!");
        }

    }
}
