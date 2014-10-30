package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.BaseCommand;
import net.frozenorb.foxtrot.util.paste.GistFile;
import net.frozenorb.foxtrot.util.paste.GistPaste;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.parser.ParseException;

/**
 * Created by chasechocolate.
 */
public class PasteConsole extends BaseCommand {
    public PasteConsole() {
        super("pasteconsole");
    }

    @Override
    public void syncExecute() {
        if (!(sender.isOp())) {
            return;
        }

        sender.sendMessage(ChatColor.GRAY + "Pasting console...");

        new BukkitRunnable() {
            public void run() {
                String log = StringUtils.join(FoxtrotPlugin.getInstance().getConsoleLog(), "\n");
                GistPaste paste = new GistPaste("Console log");

                paste.addFile(new GistFile("console.log", log));

                String url = null;
                try {
                    url = paste.paste();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                sender.sendMessage(ChatColor.GREEN + "Console pasted! " + url);
            }
        }.runTaskAsynchronously(FoxtrotPlugin.getInstance());
    }
}