package net.frozenorb.foxtrot.citadel.commands.citadel;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/25/2014.
 */
public class CitadelRespawnChestsCommand {

    @Command(names={"citadel respawnchests"}, permissionNode="op")
    public static void citadelRespawnChests(Player sender) {
        FoxtrotPlugin.getInstance().getCitadelHandler().respawnCitadelChests();
    }

}