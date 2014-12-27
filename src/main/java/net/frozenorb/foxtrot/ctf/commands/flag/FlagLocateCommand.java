package net.frozenorb.foxtrot.ctf.commands.flag;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.ctf.CTFHandler;
import net.frozenorb.foxtrot.ctf.game.CTFFlag;
import net.frozenorb.foxtrot.ctf.game.CTFGame;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FlagLocateCommand {

    @Command(names={ "flag locate" }, permissionNode="")
    public static void flagLocate(Player sender, @Param(name="flag") String flagColor) {
        CTFGame game = FoxtrotPlugin.getInstance().getCTFHandler().getGame();

        if (game == null) {
            sender.sendMessage(CTFHandler.PREFIX + " " + ChatColor.YELLOW + "There isn't an active CTF game!");
            return;
        }

        for (CTFFlag flag : game.getFlags().values()) {
            if (flag.getColor().getName().equalsIgnoreCase(flagColor)) {
                return;
            }
        }

        sender.sendMessage(ChatColor.LIGHT_PURPLE + flagColor + ChatColor.YELLOW + " isn't a flag color!");
    }

}