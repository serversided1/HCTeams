package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.entity.Player;

public class LogoutCommand {

    @Command(names={ "Logout" }, permissionNode="")
    public static void logout(Player sender) {
        FoxtrotPlugin.getInstance().getServerHandler().startLogoutSequence(sender);
    }

}