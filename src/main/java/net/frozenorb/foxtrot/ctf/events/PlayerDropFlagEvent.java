package net.frozenorb.foxtrot.ctf.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.foxtrot.ctf.game.CTFFlag;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
public class PlayerDropFlagEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter private Player player;
    @Getter private CTFFlag flag;

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}