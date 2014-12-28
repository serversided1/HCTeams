package net.frozenorb.foxtrot.ctf.commands.ctfadmin;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.ctf.CTFHandler;
import net.frozenorb.foxtrot.ctf.game.CTFFlag;
import net.frozenorb.foxtrot.ctf.game.CTFGame;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CTFAdminResetFlagCommand {

    @Command(names={ "ctfadmin resetflag" }, permissionNode="op")
    public static void ctfAdminResetFlag(Player sender, @Param(name="flag") String flagString) {
        CTFGame game = FoxtrotPlugin.getInstance().getCTFHandler().getGame();

        if (game == null) {
            sender.sendMessage(CTFHandler.PREFIX + " " + ChatColor.YELLOW + "There isn't an active CTF game!");
            return;
        }

        for (CTFFlag flag : game.getFlags().values()) {
            if (flag.getColor().getName().equalsIgnoreCase(flagString) || flagString.equalsIgnoreCase("all")) {
                flag.dropFlag(true);
            }
        }

        sender.sendMessage(CTFHandler.PREFIX + " " + ChatColor.YELLOW + "Reset the specified flag(s).");
    }

}