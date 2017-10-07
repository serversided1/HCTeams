package net.frozenorb.foxtrot.commands;

import java.io.File;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.google.common.io.Files;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.UUIDUtils;

public class ReviveCommand {

    @Command(names={ "Revive" }, permission="foxtrot.revive")
    public static void revive(CommandSender sender, @Param(name="player") UUID player, @Param(name="reason", wildcard=true) String reason) {
        if (reason.equals(".")) {
            sender.sendMessage(ChatColor.RED + ". is not a good reason...");
            return;
        }

        if (Foxtrot.getInstance().getDeathbanMap().isDeathbanned(player)) {
            File logTo = new File(new File("foxlogs"), "adminrevives.log");

            try {
                logTo.createNewFile();
                Files.append("[" + SimpleDateFormat.getDateTimeInstance().format(new Date()) + "] " + sender.getName() + " revived " + UUIDUtils.name(player) + " for " + reason + "\n", logTo, Charset.defaultCharset());
            } catch (Exception e) {
                e.printStackTrace();
            }

            Foxtrot.getInstance().getDeathbanMap().revive(player);
            sender.sendMessage(ChatColor.GREEN + "Revived " + UUIDUtils.name(player) + "!");
        } else {
            sender.sendMessage(ChatColor.RED + "That player is not deathbanned!");
        }
    }

}
