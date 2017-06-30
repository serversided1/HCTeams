package net.frozenorb.foxtrot.team.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerBuildInOthersClaim extends PlayerEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    @Getter @Setter private boolean willIgnore;
    @Getter private final Block block;

    public PlayerBuildInOthersClaim(Player who, Block block) {
        super(who);
        this.block = block;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
