package net.frozenorb.foxtrot.koth.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.foxtrot.koth.KOTH;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
public class KOTHActivatedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter private KOTH KOTH;

    public HandlerList getHandlers() {
        return (handlers);
    }

    public static HandlerList getHandlerList() {
        return (handlers);
    }

}