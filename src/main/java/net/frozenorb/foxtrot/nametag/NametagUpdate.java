package net.frozenorb.foxtrot.nametag;

import lombok.Getter;
import org.bukkit.entity.Player;

public class NametagUpdate {

    @Getter private String toRefresh;
    @Getter private String refreshFor;

    public NametagUpdate(Player toRefresh) {
        this.toRefresh = toRefresh.getName();
    }

    public NametagUpdate(Player toRefresh, Player refreshFor) {
        this.toRefresh = toRefresh.getName();
        this.refreshFor = refreshFor.getName();
    }

}