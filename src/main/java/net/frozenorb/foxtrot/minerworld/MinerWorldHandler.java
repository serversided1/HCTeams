package net.frozenorb.foxtrot.minerworld;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.minerworld.blockregen.BlockRegenHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.util.ClassUtils;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

    private File minerWorldInfoFile;
    private JsonObject config;

    public MinerWorldHandler() {
        world = Bukkit.createWorld(new WorldCreator("world_miner"));

        try {
            minerWorldInfoFile = new File(Foxtrot.getInstance().getDataFolder(), "minerWorldInfo.json");
            if (!minerWorldInfoFile.exists()) {
                minerWorldInfoFile.createNewFile();
                config = getDefaults();
                FileUtils.writeStringToFile(minerWorldInfoFile, config.toString());
            } else {
                config = qLib.PLAIN_GSON.fromJson(new FileReader(minerWorldInfoFile), JsonObject.class);
                if (config.has("enabled")) enabled = config.get("enabled").getAsBoolean();
                if (config.has("portalLocation")) portalLocation = qLib.PLAIN_GSON.fromJson(config.get("portalLocation"), Location.class);
                if (config.has("portalRadius")) portalRadius = config.get("portalRadius").getAsInt();
                if (config.has("maxFactionAmount")) maxFactionAmount = config.get("maxFactionAmount").getAsInt();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        blockRegenHandler = new BlockRegenHandler(config);
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
        config.addProperty("enabled", enabled);
        config.add("portalLocation", qLib.PLAIN_GSON.toJsonTree(portalLocation));
        config.addProperty("portalRadius", portalRadius);
        config.addProperty("maxFactionAmount", maxFactionAmount);

        try {
            FileUtils.writeStringToFile(minerWorldInfoFile, config.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JsonObject getDefaults() {
        JsonObject object = new JsonObject();
        object.addProperty("enabled", false);
        object.addProperty("maxFactionAmount", 2);
        return object;
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

        return factionAmount < maxFactionAmount;
    }

    public void enter(Player player) {
        player.teleport(world.getSpawnLocation());
        players.add(player.getUniqueId());
    }

    public void leave(Player player) {
        players.remove(player.getUniqueId());
    }

}
