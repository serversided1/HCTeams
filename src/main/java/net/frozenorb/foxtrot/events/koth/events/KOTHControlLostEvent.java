package net.frozenorb.foxtrot.events.koth.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.foxtrot.events.koth.KOTH;

@AllArgsConstructor
public class KOTHControlLostEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter private KOTH KOTH;

    public HandlerList getHandlers() {
        return (handlers);
    }

    public static HandlerList getHandlerList() {
        return (handlers);
    }

}