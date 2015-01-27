package net.frozenorb.foxtrot.citadel.events;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CitadelCapturedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter private ObjectId capper;
    @Getter private int level;

    public CitadelCapturedEvent(ObjectId capper) {
        this.capper = capper;
        this.level = level;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}