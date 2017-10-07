package net.frozenorb.foxtrot.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

/**
 * ---------- hcteams ----------
 * Created by Fraser.Cumming on 29/03/2016.
 * © 2016 Fraser Cumming All Rights Reserved
 */
public class AssociateAccountsCommand {


    @Command(names={ "ass", "associate" }, permission="op")
    public static void associate(Player sender, @Param(name="player") UUID player, @Param(name = "associate") UUID playertwo) {
        if(Foxtrot.getInstance().getWhitelistedIPMap().contains(player)) {
            UUID other = Foxtrot.getInstance().getWhitelistedIPMap().get(player);
            Foxtrot.getInstance().getWhitelistedIPMap().add(playertwo, other);
        } else if( Foxtrot.getInstance().getWhitelistedIPMap().contains(playertwo)) {
            UUID other = Foxtrot.getInstance().getWhitelistedIPMap().get(playertwo);
            Foxtrot.getInstance().getWhitelistedIPMap().add(player, other);
        } else {
            if( Foxtrot.getInstance().getWhitelistedIPMap().containsValue(player)) {
                Foxtrot.getInstance().getWhitelistedIPMap().add(playertwo, player);
            } else if( Foxtrot.getInstance().getWhitelistedIPMap().containsValue(playertwo)) {
                Foxtrot.getInstance().getWhitelistedIPMap().add(player, playertwo);
            } else {
                Foxtrot.getInstance().getWhitelistedIPMap().add(playertwo, player);
            }
        }
        sender.sendMessage(ChatColor.GREEN + "You have successfully associated these accounts");
    }



}
