package net.frozenorb.foxtrot.ctf.commands.ctfadmin;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.ctf.CTFHandler;
import net.frozenorb.foxtrot.ctf.game.CTFFlag;
import net.frozenorb.foxtrot.ctf.game.CTFGame;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CTFAdminTPCommand {

    @Command(names={ "ctfadmin tp" }, permissionNode="op")
    public static void ctfAdminTP(Player sender, @Param(name="location") String location) {
        CTFGame game = FoxtrotPlugin.getInstance().getCTFHandler().getGame();

        if (game == null) {
            sender.sendMessage(CTFHandler.PREFIX + " " + ChatColor.YELLOW + "There isn't an active CTF game!");
            return;
        }

        for (CTFFlag flag : game.getFlags().values()) {
            if (flag.getColor().getName().equalsIgnoreCase(location)) {
                sender.teleport(flag.getLocation());
                sender.sendMessage(CTFHandler.PREFIX + " " + ChatColor.YELLOW + "Teleported to the " + flag.getColor().getChatColor() + flag.getColor().getName() + " Flag" + ChatColor.YELLOW + ".");
                return;
            }

            if ((flag.getColor().getName() + "_cap").equalsIgnoreCase(location)) {
                sender.teleport(flag.getCaptureLocation());
                sender.sendMessage(CTFHandler.PREFIX + " " + ChatColor.YELLOW + "Teleported to the " + flag.getColor().getChatColor() + flag.getColor().getName() + " Flag" + ChatColor.YELLOW + " capture location.");
                return;
            }

            if ((flag.getColor().getName() + "_spawn").equalsIgnoreCase(location)) {
                sender.teleport(flag.getSpawnLocation());
                sender.sendMessage(CTFHandler.PREFIX + " " + ChatColor.YELLOW + "Teleported to the " + flag.getColor().getChatColor() + flag.getColor().getName() + " Flag" + ChatColor.YELLOW + " spawn location.");
                return;
            }
        }

        sender.sendMessage(CTFHandler.PREFIX + " " + ChatColor.LIGHT_PURPLE + location + ChatColor.YELLOW + " is not a valid location.");
    }

}