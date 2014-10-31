package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.BaseCommand;
import net.frozenorb.foxtrot.command.subcommand.subcommands.kothsubcommands.*;
import org.bukkit.ChatColor;

/**
 * Created by macguy8 on 10/31/2014.
 */
public class KOTH extends BaseCommand {

    public KOTH() {
        super("koth");
        registerSubcommand(new KOTHList("list", "§c/koth list"));
        registerSubcommand(new KOTHActivate("activate", "§c/koth activate"));
        registerSubcommand(new KOTHDeactivate("deactivate", "§c/koth deactivate"));
        registerSubcommand(new KOTHSetCapLocation("setcaplocation", "§c/koth setcaplocation"));
        registerSubcommand(new KOTHSetCapTime("setcaptime", "§c/koth setcaptime"));
        registerSubcommand(new KOTHSetCapDistance("setcapdistance", "§c/koth setcapdistance"));
        registerSubcommand(new KOTHCreate("create", "§c/koth create"));
        registerSubcommand(new KOTHDelete("delete", "§c/koth delete"));
    }

    public void syncExecute() {
        if (sender.hasPermission("foxtrot.koth")) {
            sender.sendMessage(ChatColor.GRAY + "/koth list - Lists KOTHs");
            sender.sendMessage(ChatColor.GRAY + "/koth activate <name> - Activates a KOTH");
            sender.sendMessage(ChatColor.GRAY + "/koth deactivate <name> - Deactivates a KOTH");
            sender.sendMessage(ChatColor.GRAY + "/koth setcaplocation <name> - Set a KOTH's cap location");
            sender.sendMessage(ChatColor.GRAY + "/koth setcaptime <name> <time> - Sets a KOTH's cap time");
            sender.sendMessage(ChatColor.GRAY + "/koth setcapdistance <name> <distance> - Sets a KOTH's cap distance");
            sender.sendMessage(ChatColor.GRAY + "/koth create <name> - Creates a KOTH");
            sender.sendMessage(ChatColor.GRAY + "/koth delete <name> - Deletes a KOTH");
        }
    }

}