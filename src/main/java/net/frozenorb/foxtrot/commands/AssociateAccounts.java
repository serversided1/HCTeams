package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * ---------- hcteams ----------
 * Created by Fraser.Cumming on 29/03/2016.
 * © 2016 Fraser Cumming All Rights Reserved
 */
public class AssociateAccounts {


    @Command(names={ "ass", "associate" }, permissionNode="op")
    public static void associate(Player sender, @Parameter(name="player") UUID player, @Parameter(name = "associate") UUID playertwo) {
        if(Foxtrot.getInstance().getWhitelistedIPMap().contains(player)) {
            UUID other = Foxtrot.getInstance().getWhitelistedIPMap().get(player);
            Foxtrot.getInstance().getWhitelistedIPMap().add(playertwo, other);
            //ASS 1 to 2
        } else if( Foxtrot.getInstance().getWhitelistedIPMap().contains(playertwo)) {
            UUID other = Foxtrot.getInstance().getWhitelistedIPMap().get(playertwo);
        } else {
            if( Foxtrot.getInstance().getWhitelistedIPMap().containsValue(player)) {
                Foxtrot.getInstance().getWhitelistedIPMap().add(playertwo, player);
            } else if( Foxtrot.getInstance().getWhitelistedIPMap().containsValue(playertwo)) {
                Foxtrot.getInstance().getWhitelistedIPMap().add(player, playertwo);
            } else {
                Foxtrot.getInstance().getWhitelistedIPMap().add(playertwo, player);
            }
        }
    }

}
