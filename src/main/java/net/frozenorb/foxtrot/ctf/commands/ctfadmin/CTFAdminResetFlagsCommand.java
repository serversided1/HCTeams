package net.frozenorb.foxtrot.ctf.commands.ctfadmin;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.ctf.CTFHandler;
import net.frozenorb.foxtrot.ctf.game.CTFFlag;
import net.frozenorb.foxtrot.ctf.game.CTFGame;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CTFAdminResetFlagsCommand {

    @Command(names={ "ctfadmin resetflags" }, permissionNode="op")
    public static void ctfAdminResetFlags(Player sender) {
        CTFGame game = FoxtrotPlugin.getInstance().getCTFHandler().getGame();

        if (game == null) {
            sender.sendMessage(CTFHandler.PREFIX + " " + ChatColor.RED + "There isn't an active CTF game!");
            return;
        }

        for (CTFFlag flag : game.getFlags().values()) {
            flag.dropFlag(true);
        }

        sender.sendMessage(CTFHandler.PREFIX + " " + ChatColor.YELLOW + "Reset all CTF flags.");
    }

}