package net.frozenorb.foxtrot.ctf.commands.ctfadmin;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.ctf.CTFHandler;
import net.frozenorb.foxtrot.ctf.enums.CTFFlagState;
import net.frozenorb.foxtrot.ctf.game.CTFFlag;
import net.frozenorb.foxtrot.ctf.game.CTFGame;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CTFAdminGiveFlagCommand {

    @Command(names={ "ctfadmin giveflag" }, permissionNode="op")
    public static void ctfAdminGiveFlag(Player sender, @Param(name="target") Player target, @Param(name="color") String flagColor) {
        CTFGame game = FoxtrotPlugin.getInstance().getCTFHandler().getGame();

        if (game == null) {
            sender.sendMessage(CTFHandler.PREFIX + " " + ChatColor.YELLOW + "There isn't an active CTF game!");
            return;
        }

        for (CTFFlag flag : game.getFlags().values()) {
            if (flag.getColor().getName().equalsIgnoreCase(flagColor)) {
                if (flag.getState() != CTFFlagState.CAP_POINT) {
                    sender.sendMessage(CTFHandler.PREFIX + " " + ChatColor.YELLOW + "That flag is not at the cap point.");
                }

                flag.pickupFlag(target, false);
                return;
            }
        }

        sender.sendMessage(CTFHandler.PREFIX + " " + ChatColor.LIGHT_PURPLE + flagColor + ChatColor.YELLOW + " isn't a valid flag color.");
    }

}