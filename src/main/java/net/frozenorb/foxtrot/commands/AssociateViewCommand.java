package net.frozenorb.foxtrot.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
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
public class AssociateViewCommand {

    @Command(names={ "assview", "associateview" }, permission="op")
    public static void associate(Player sender, @Param(name="player") UUID player) {
        if( Foxtrot.getInstance().getWhitelistedIPMap().contains(player)) {
            player = Foxtrot.getInstance().getWhitelistedIPMap().get(player);
        }

        Map<UUID, UUID> map = Foxtrot.getInstance().getWhitelistedIPMap().getMap();
        List<String> list = new ArrayList<String>();
        for( UUID id : map.keySet() ) {
            UUID found = map.get(id);
            if( found.equals(player) ) {
                sender.sendMessage( ChatColor.RED + Bukkit.getOfflinePlayer(id).getName() + ChatColor.YELLOW + " is associated!");
            }
        }
    }
}
