package net.frozenorb.foxtrot.team.commands.team.subclaim;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.team.claims.VisualSubclaimMap;
import org.bukkit.entity.Player;

public class TeamSubclaimMapCommand {

    @Command(names={ "team subclaim map", "t subclaim map", "f subclaim map", "faction subclaim map", "fac subclaim map", "team sub map", "t sub map", "f sub map", "faction sub map", "fac sub map" }, permissionNode="")
    public static void teamSubclaimMap(Player sender) {
        (new VisualSubclaimMap(sender)).draw(false);
    }

}