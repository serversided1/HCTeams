package net.frozenorb.foxtrot.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by macguy8 on 12/2/2014.
 */
public class HourEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    int hour;

    public HourEvent(int hour) {
        this.hour = hour;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}