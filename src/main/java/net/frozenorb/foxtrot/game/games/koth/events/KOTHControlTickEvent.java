package net.frozenorb.foxtrot.game.games.koth.events;

import lombok.Getter;
import net.frozenorb.foxtrot.game.games.koth.KOTH;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by macguy8 on 10/31/2014.
 */
public class KOTHControlTickEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    private KOTH koth;

    public KOTHControlTickEvent(KOTH koth) {
        this.koth = koth;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}