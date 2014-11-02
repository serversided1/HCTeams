package net.frozenorb.foxtrot.damage.event;

import net.frozenorb.foxtrot.damage.objects.Damage;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Created by macguy8 on 10/3/2014.
 */
public class CustomPlayerDamageEvent extends Event {

    //***************************//

    private static HandlerList handlerList = new HandlerList();

    public HandlerList getHandlers() {
        return (handlerList);
    }

    public static HandlerList getHandlerList() {
        return (handlerList);
    }

    //***************************//

    private EntityDamageEvent cause;
    private Damage trackerDamage;
    private boolean cancelled = false;

    //***************************//

    public CustomPlayerDamageEvent(EntityDamageEvent cause) {
        this.cause = cause;
    }

    //***************************//

    public EntityDamageEvent getCause() {
        return (cause);
    }

    public Player getPlayer() {
        return ((Player) cause.getEntity());
    }

    public double getDamage() {
        return (cause.getDamage());
    }

    public Damage getTrackerDamage() {
        return (trackerDamage);
    }

    public void setTrackerDamage(Damage trackerDamage) {
        this.trackerDamage = trackerDamage;
    }

    //***************************//

    public boolean isCancelled() {
        return (cancelled);
    }

    public void setCancelled(boolean b) {
        cancelled = b;
    }

    //***************************//

}