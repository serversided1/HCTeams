package net.frozenorb.foxtrot.commands;

import com.google.common.io.Files;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.qlib.command.annotations.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReviveCommand {

    @Command(names={ "Revive" }, permissionNode="op")
    public static void revive(CommandSender sender, @Parameter(name="Target") String target, @Parameter(name="Reason", wildcard=true) String reason) {
        if (reason.equals(".")) {
            sender.sendMessage(ChatColor.RED + ". is not a good reason...");
            return;
        }

        if (FoxtrotPlugin.getInstance().getDeathbanMap().isDeathbanned(target)) {
            File logTo = new File(new File("foxlogs"), "adminrevives.log");

            try {
                logTo.createNewFile();
                Files.append("[" + SimpleDateFormat.getDateTimeInstance().format(new Date()) + "] " + sender.getName() + " revived " + target + " for " + reason + "\n", logTo, Charset.defaultCharset());
            } catch (Exception e) {
                FoxtrotPlugin.getInstance().getBugSnag().notify(e);
                e.printStackTrace();
            }

            FoxtrotPlugin.getInstance().getDeathbanMap().revive(target);
            sender.sendMessage(ChatColor.GREEN + "Revived " + target + "!");
        } else {
            sender.sendMessage(ChatColor.RED + "That player is not deathbanned!");
        }
    }

}