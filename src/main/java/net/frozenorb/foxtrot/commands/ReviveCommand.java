package net.frozenorb.foxtrot.commands;

import com.google.common.io.Files;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.util.UUIDUtils;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class ReviveCommand {

    @Command(names={ "Revive" }, permissionNode="op")
    public static void revive(CommandSender sender, @Parameter(name="Target") UUID target, @Parameter(name="Reason", wildcard=true) String reason) {
        if (reason.equals(".")) {
            sender.sendMessage(ChatColor.RED + ". is not a good reason...");
            return;
        }

        if (FoxtrotPlugin.getInstance().getDeathbanMap().isDeathbanned(target)) {
            File logTo = new File(new File("foxlogs"), "adminrevives.log");

            try {
                logTo.createNewFile();
                Files.append("[" + SimpleDateFormat.getDateTimeInstance().format(new Date()) + "] " + sender.getName() + " revived " + UUIDUtils.name(target) + " for " + reason + "\n", logTo, Charset.defaultCharset());
            } catch (Exception e) {
                e.printStackTrace();
            }

            FoxtrotPlugin.getInstance().getDeathbanMap().revive(target);
            sender.sendMessage(ChatColor.GREEN + "Revived " + UUIDUtils.name(target) + "!");
        } else {
            sender.sendMessage(ChatColor.RED + "That player is not deathbanned!");
        }
    }

}