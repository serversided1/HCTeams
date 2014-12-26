package net.frozenorb.foxtrot.ctf.commands.ctf;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.ctf.CTFHandler;
import net.frozenorb.foxtrot.ctf.game.CTFFlag;
import net.frozenorb.foxtrot.ctf.game.CTFGame;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CTFDropCommand {

    @Command(names={ "ctf drop", "ctf dropflag", "drop" }, permissionNode="")
    public static void ctfDrop(Player sender) {
        CTFGame game = FoxtrotPlugin.getInstance().getCTFHandler().getGame();

        if (game == null) {
            sender.sendMessage(CTFHandler.PREFIX + " " + ChatColor.YELLOW + "There isn't an active CTF game!");
            return;
        }

        for (CTFFlag flag : game.getFlags().values()) {
            if (flag.getFlagHolder() != null && flag.getFlagHolder() == sender) {
                flag.dropFlag(false);
                FoxtrotPlugin.getInstance().getServer().broadcastMessage(CTFHandler.PREFIX + " " + ChatColor.AQUA + sender.getName() + ChatColor.YELLOW + " has dropped the " + flag.getColor().getChatColor() + flag.getColor().getName() + " Flag" + ChatColor.YELLOW + "!");
                return;
            }
        }

        sender.sendMessage(CTFHandler.PREFIX + " " + ChatColor.YELLOW + "You don't have a flag you can drop!");
    }

}