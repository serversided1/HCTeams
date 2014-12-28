package net.frozenorb.foxtrot.ctf.commands.flag;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.ctf.CTFHandler;
import net.frozenorb.foxtrot.ctf.enums.CTFFlagState;
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
                sender.sendMessage(flag.getColor().getChatColor() + flag.getColor().getName() + " Flag:");

                String locationString;

                if (flag.getState() == CTFFlagState.CAP_POINT) {
                    locationString = "At cap point";
                } else {
                    locationString = "Held by " + flag.getFlagHolder().getName();
                }

                sender.sendMessage(ChatColor.DARK_AQUA + "Location: " + ChatColor.WHITE + locationString);
                sender.sendMessage(ChatColor.DARK_AQUA + "Return location: " + ChatColor.WHITE + flag.getCaptureLocation().getBlockX() + ", " + flag.getCaptureLocation().getBlockY() + ", " + flag.getCaptureLocation().getBlockZ());
                sender.sendMessage(ChatColor.DARK_AQUA + "Spawn location: " + ChatColor.WHITE + flag.getSpawnLocation().getBlockX() + ", " + flag.getSpawnLocation().getBlockY() + ", " + flag.getSpawnLocation().getBlockZ());
                sender.sendMessage(ChatColor.DARK_AQUA + "Current location: " + ChatColor.WHITE + flag.getLocation().getBlockX() + ", " + flag.getLocation().getBlockY() + ", " + flag.getLocation().getBlockZ());

                return;
            }
        }

        sender.sendMessage(ChatColor.LIGHT_PURPLE + flagColor + ChatColor.YELLOW + " isn't a flag color!");
    }

}