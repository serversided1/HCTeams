package net.frozenorb.foxtrot.team.commands.team.subclaim;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.team.claims.VisualClaim;
import net.frozenorb.foxtrot.team.claims.VisualClaimType;
import org.bukkit.entity.Player;

public class TeamSubclaimOpMapCommand {

    @Command(names={ "team subclaim opmap", "t subclaim opmap", "f subclaim opmap", "faction subclaim opmap", "fac subclaim opmap", "team sub opmap", "t sub opmap", "f sub opmap", "faction sub opmap", "fac sub opmap" }, permissionNode="op")
    public static void teamSubclaimOpMap(Player sender) {
        (new VisualClaim(sender, VisualClaimType.SUBCLAIM_MAP, true)).draw(false);
    }

}