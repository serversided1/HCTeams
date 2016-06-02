package net.frozenorb.foxtrot.minerworld;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.minerworld.blockregen.BlockRegenHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.util.ClassUtils;
import net.minecraft.util.com.google.common.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MinerWorldHandler {

    @Getter private final World world;

    @Getter @Setter private boolean enabled;

    @Getter private final BlockRegenHandler blockRegenHandler;

    @Getter @Setter private Location portalLocation;
    @Getter @Setter private int portalRadius;

    @Getter @Setter private int maxFactionAmount;
    @Getter private Set<UUID> players = new HashSet<>();

    public MinerWorldHandler() {
        world = Bukkit.createWorld(new WorldCreator("world_miner"));

        blockRegenHandler = new BlockRegenHandler();

        qLib.getInstance().runRedisCommand((redis) -> {
            if (redis.exists("minerWorld:enabled")) enabled = Boolean.valueOf(redis.get("minerWorld:enabled"));

            if (redis.exists("minerWorld:portalLocation")) portalLocation = qLib.PLAIN_GSON.fromJson(redis.get("minerWorld:portalLocation"), Location.class);
            if (redis.exists("minerWorld:portalRadius")) portalRadius = Integer.valueOf(redis.get("minerWorld:portalRadius"));

            if (redis.exists("minerWorld:maxFactionAmount")) maxFactionAmount = Integer.valueOf(redis.get("minerWorld:maxFactionAmount"));
            if (redis.exists("minerWorld:players")) players = qLib.PLAIN_GSON.fromJson(redis.get("minerWorld:players"), new TypeToken<Set<UUID>>() {}.getType());
            return null;
        });

        FrozenCommandHandler.registerPackage(Foxtrot.getInstance(), "net.frozenorb.foxtrot.minerworld.commands");

        // register listeners
        ClassUtils.getClassesInPackage(Foxtrot.getInstance(), "net.frozenorb.foxtrot.minerworld.listener").stream().filter(Listener.class::isAssignableFrom).forEach(clazz -> {
            try {
                Bukkit.getPluginManager().registerEvents((Listener) clazz.newInstance(), Foxtrot.getInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    public void save() {
        qLib.getInstance().runRedisCommand((redis) -> {
            redis.set("minerWorld:enabled", String.valueOf(enabled));

            redis.set("minerWorld:portalLocation", qLib.PLAIN_GSON.toJson(portalLocation));
            redis.set("minerWorld:portalRadius", String.valueOf(portalRadius));

            redis.set("minerWorld:maxFactionAmount", String.valueOf(maxFactionAmount));
            redis.set("minerWorld:players", qLib.PLAIN_GSON.toJson(players));
            return null;
        });
    }

    public boolean canEnter(UUID player) {
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
