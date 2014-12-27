package net.frozenorb.foxtrot.ctf.commands.flag;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.ctf.CTFHandler;
import net.frozenorb.foxtrot.ctf.enums.CTFFlagState;
import net.frozenorb.foxtrot.ctf.game.CTFFlag;
import net.frozenorb.foxtrot.ctf.game.CTFGame;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FlagsCommand {

    @Command(names={ "flags" }, permissionNode="")
    public static void flags(Player sender) {
        CTFGame game = FoxtrotPlugin.getInstance().getCTFHandler().getGame();

        if (game == null) {
            sender.sendMessage(CTFHandler.PREFIX + " " + ChatColor.YELLOW + "There isn't an active CTF game!");
            return;
        }

        for (CTFFlag flag : game.getFlags().values()) {
            Location flagLocation = flag.getLocation();
            String locationString;

            if (flag.getState() == CTFFlagState.CAP_POINT) {
                locationString = "At cap point";
            } else {
                locationString = "Held by " + flag.getFlagHolder().getName();
            }

            sender.sendMessage(flag.getColor().getChatColor() + flag.getColor().getName() + " Flag: " + ChatColor.WHITE + locationString + ChatColor.DARK_AQUA + " (" + flagLocation.getBlockX() + ", " + flagLocation.getBlockY() + ", " + flagLocation.getBlockZ() + ")");
        }

        sender.sendMessage(ChatColor.YELLOW + "Use " + ChatColor.LIGHT_PURPLE + "/flag locate <flag>" + ChatColor.YELLOW + " for more specific information.");
    }

}