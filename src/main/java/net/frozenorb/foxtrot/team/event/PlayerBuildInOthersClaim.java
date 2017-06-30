package net.frozenorb.foxtrot.team.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerBuildInOthersClaim extends PlayerEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    @Getter @Setter private boolean willIgnore;

    public PlayerBuildInOthersClaim(Player who) {
        super(who);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
