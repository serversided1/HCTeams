package net.frozenorb.foxtrot.ctf.commands.ctfadmin;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.ctf.CTFHandler;
import net.frozenorb.foxtrot.ctf.enums.CTFFlagColor;
import net.frozenorb.foxtrot.ctf.game.CTFFlag;
import net.frozenorb.foxtrot.ctf.game.CTFGame;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CTFAdminStartGameCommand {

    @Command(names={ "ctfadmin startgame" }, permissionNode="op")
    public static void ctfAdminStartGame(Player sender) {
        CTFGame game = FoxtrotPlugin.getInstance().getCTFHandler().getGame();

        if (game != null) {
            sender.sendMessage(CTFHandler.PREFIX + " " + ChatColor.RED + "There is already an active CTF game!");
            return;
        }

        new CTFGame(

                new CTFFlag(sender.getLocation().add(20, 0, 0), sender.getLocation(), CTFFlagColor.BLUE),
                new CTFFlag(sender.getLocation().add(-20, 0, 0), sender.getLocation(), CTFFlagColor.GREEN),
                new CTFFlag(sender.getLocation().add(0, 0, 20), sender.getLocation(), CTFFlagColor.RED),
                new CTFFlag(sender.getLocation().add(0, 0, -20), sender.getLocation(), CTFFlagColor.YELLOW)

        );
    }

}