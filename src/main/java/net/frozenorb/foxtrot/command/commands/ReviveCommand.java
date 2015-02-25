package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;

public class ReviveCommand {

    @Command(names={ "Revive" }, permissionNode="op")
    public static void playSound(CommandSender sender, @Param(name="Target") String target, @Param(name="Reason", wildcard=true) String reason) {
        if (reason.equals(".")) {
            sender.sendMessage(ChatColor.RED + ". is not a good reason...");
            return;
        }

        if (FoxtrotPlugin.getInstance().getDeathbanMap().isDeathbanned(target)) {
            File logTo = new File(new File("foxlogs"), "adminrevives.log");

            try {
                logTo.createNewFile();

                BufferedWriter output = new BufferedWriter(new FileWriter(logTo, true));
                output.append("[").append(new Date().toString()).append("] ").append(sender.getName()).append(" revived ").append(target).append(" for ").append(reason).append("\n");
                output.close();
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