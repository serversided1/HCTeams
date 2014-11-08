package net.frozenorb.foxtrot.koth.events;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.koth.KOTH;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Created by macguy8 on 10/31/2014.
 */
public class KOTHCapturedEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    private KOTH koth;
    @Getter @Setter
    private boolean cancelled;

    public KOTHCapturedEvent(KOTH koth, Player capper) {
        super(capper);

        this.koth = koth;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}