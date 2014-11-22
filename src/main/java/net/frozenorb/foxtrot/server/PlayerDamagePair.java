package net.frozenorb.foxtrot.server;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PlayerDamagePair {
    public static final int FALL_DAMAGE_ASSIST_SECONDS = 30;

    private UUID victimUUID, damagerUUID;

    public Player getVictim() {
        return Bukkit.getPlayer(victimUUID);
    }

    public Player getDamager() {
        return Bukkit.getPlayer(damagerUUID);
    }

}
