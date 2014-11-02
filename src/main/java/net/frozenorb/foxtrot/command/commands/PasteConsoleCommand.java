package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.util.paste.GistFile;
import net.frozenorb.foxtrot.util.paste.GistPaste;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.parser.ParseException;

/**
 * Created by chasechocolate.
 */
public class PasteConsoleCommand {

    @Command(names = {"PasteConsole"}, permissionNode = "op")
    public static void pasteConsole(Player sender) {
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