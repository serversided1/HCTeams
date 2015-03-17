package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import org.bukkit.entity.Player;

public class LogoutCommand {

    @Command(names={ "Logout" }, permissionNode="")
    public static void logout(Player sender) {
        Foxtrot.getInstance().getServerHandler().startLogoutSequence(sender);
    }

}