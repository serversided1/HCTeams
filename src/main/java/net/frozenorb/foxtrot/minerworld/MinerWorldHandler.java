package net.frozenorb.foxtrot.minerworld;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.qLib;
import net.minecraft.util.com.google.common.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class MinerWorldHandler {

    @Getter private final World world;

    @Getter @Setter private Location portalLocation;
    @Getter @Setter private int portalRadius;

    @Getter @Setter private int maxFactionAmount;
    private Set<UUID> players;

    public MinerWorldHandler() {
        world = Bukkit.createWorld(new WorldCreator("world_miner"));
        qLib.getInstance().runRedisCommand((redis) -> {
            portalLocation = qLib.PLAIN_GSON.fromJson(redis.get("minerWorld:portalLocation"), Location.class);
            portalRadius = Integer.valueOf(redis.get("minerWorld:portalRadius"));

            maxFactionAmount = Integer.valueOf(redis.get("minerWorld:maxFactionAmount"));
            players = qLib.PLAIN_GSON.fromJson(redis.get("minerWorld:players"), new TypeToken<Set<UUID>>() {}.getType());
            return null;
        });
    }

    public void save() {
        qLib.getInstance().runRedisCommand((redis) -> {
            redis.set("minerWorld:maxFactionAmount", String.valueOf(maxFactionAmount));
            redis.set("minerWorld:players", qLib.PLAIN_GSON.toJson(players));
            return null;
        });
    }

    public boolean canEnter(Player player) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (team == null) {
            return true;
        }

        int factionAmount = 0;
        for (UUID uuid : players) {
            if (team.isMember(uuid)) {
                factionAmount++;
            }
        }

        return factionAmount < 2;
    }

    public void enter(Player player) {
        player.teleport(world.getSpawnLocation());
        players.add(player.getUniqueId());
    }

    public void leave(Player player) {
        players.remove(player.getUniqueId());
    }

}
