package net.frozenorb.foxtrot.map.kit.stats;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.map.kit.stats.command.StatsTopCommand;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.frozenorb.qlib.command.ParameterType;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.serialization.LocationSerializer;
import net.frozenorb.qlib.util.UUIDUtils;
import net.minecraft.util.com.google.common.collect.Iterables;
import net.minecraft.util.com.google.common.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.*;
import java.util.stream.Collectors;

public class StatsHandler implements Listener {

    private Map<UUID, StatsEntry> stats = Maps.newConcurrentMap();

    @Getter private Map<Location, Integer> leaderboardSigns = Maps.newHashMap();
    @Getter private Map<Location, Integer> leaderboardHeads = Maps.newHashMap();

    public StatsHandler() {
        qLib.getInstance().runRedisCommand(redis -> {
            for (String key : redis.keys("stats.*")) {
                UUID uuid = UUID.fromString(key.split(".")[1]);
                StatsEntry entry = qLib.PLAIN_GSON.fromJson(redis.get(key), StatsEntry.class);

                stats.put(uuid, entry);
            }

            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Kit Map] Loaded " + stats.size() + " stats.");

            if (redis.exists("leaderboardSigns")) {
                List<String> serializedSigns = qLib.PLAIN_GSON.fromJson(redis.get("leaderboardSigns"), new TypeToken<List<String>>() {}.getType());

                for (String sign : serializedSigns) {
                    Location location = LocationSerializer.deserialize((BasicDBObject) JSON.parse(sign.split("----")[0]));
                    int place = Integer.parseInt(sign.split("----")[1]);

                    leaderboardSigns.put(location, place);
                }

                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Kit Map] Loaded " + leaderboardSigns.size() + " leaderboard signs.");
            }

            if (redis.exists("leaderboardHeads")) {
                List<String> serializedHeads = qLib.PLAIN_GSON.fromJson(redis.get("leaderboardHeads"), new TypeToken<List<String>>() {}.getType());

                for (String sign : serializedHeads) {
                    Location location = LocationSerializer.deserialize((BasicDBObject) JSON.parse(sign.split("----")[0]));
                    int place = Integer.parseInt(sign.split("----")[1]);

                    leaderboardHeads.put(location, place);
                }

                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Kit Map] Loaded " + leaderboardHeads.size() + " leaderboard heads.");
            }

            return null;
        });

        Bukkit.getPluginManager().registerEvents(this, Foxtrot.getInstance());

        FrozenCommandHandler.registerPackage(Foxtrot.getInstance(), "net.frozenorb.foxtrot.map.kit.stats.command");

        FrozenCommandHandler.registerParameterType(StatsTopCommand.StatsObjective.class, new ParameterType<StatsTopCommand.StatsObjective>() {

            @Override
            public StatsTopCommand.StatsObjective transform(CommandSender sender, String source) {
                for (StatsTopCommand.StatsObjective objective : StatsTopCommand.StatsObjective.values()) {
                    if (source.equalsIgnoreCase(objective.getName())) {
                        return objective;
                    }

                    for (String alias : objective.getAliases()) {
                        if (source.equalsIgnoreCase(alias)) {
                            return objective;
                        }
                    }
                }

                sender.sendMessage(ChatColor.RED + "Objective '" + source + "' not found.");
                return null;
            }

            @Override
            public List<String> tabComplete(Player sender, Set<String> flags, String source) {
                List<String> completions = Lists.newArrayList();

                obj:
                for (StatsTopCommand.StatsObjective objective : StatsTopCommand.StatsObjective.values()) {
                    if (StringUtils.startsWithIgnoreCase(objective.getName().replace(" ", ""), source)) {
                        completions.add(objective.getName().replace(" ", ""));
                        continue;
                    }

                    for (String alias : objective.getAliases()) {
                        if (StringUtils.startsWithIgnoreCase(alias, source)) {
                            completions.add(alias);
                            continue obj;
                        }
                    }
                }

                return completions;
            }

        });

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(Foxtrot.getInstance(), this::save, 30 * 20L, 30 * 20L);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Foxtrot.getInstance(), this::updatePhysicalLeaderboards, 60 * 20L, 60 * 20L);
    }

    public void save() {
        qLib.getInstance().runRedisCommand(redis -> {
            List<String> serializedSigns = leaderboardSigns.entrySet().stream().map(entry -> LocationSerializer.serialize(entry.getKey()).toString() + "----" + entry.getValue()).collect(Collectors.toList());
            List<String> serializedHeads = leaderboardHeads.entrySet().stream().map(entry -> LocationSerializer.serialize(entry.getKey()).toString() + "----" + entry.getValue()).collect(Collectors.toList());

            redis.set("leaderboardSigns", qLib.PLAIN_GSON.toJson(serializedSigns));
            redis.set("leaderboardHeads", qLib.PLAIN_GSON.toJson(serializedHeads));

            // stats
            for (StatsEntry entry : stats.values()) {
                redis.set("stats." + entry.getOwner().toString(), qLib.PLAIN_GSON.toJson(entry));
            }
            return null;
        });
    }

    public StatsEntry getStats(Player player) {
        return getStats(player.getUniqueId());
    }

    public StatsEntry getStats(String name) {
        return getStats(UUIDUtils.uuid(name));
    }

    public StatsEntry getStats(UUID uuid) {
        stats.putIfAbsent(uuid, new StatsEntry(uuid));
        return stats.get(uuid);
    }

    public void updatePhysicalLeaderboards() {
        Iterator<Map.Entry<Location, Integer>> iterator = leaderboardSigns.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Location, Integer> entry = iterator.next();

            StatsEntry stats = get(entry.getValue());

            if (stats == null) {
                continue;
            }

            if (!(entry.getKey().getBlock().getState() instanceof Sign)) {
                iterator.remove();
                continue;
            }

            Sign sign = (Sign) entry.getKey().getBlock().getState();

            sign.setLine(0, trim(UUIDUtils.name(stats.getOwner())));
            sign.setLine(1, ChatColor.DARK_GRAY + "#" + entry.getValue());
            sign.setLine(2, ChatColor.GREEN + "K " + ChatColor.BLACK + stats.getKills());
            sign.setLine(3, ChatColor.RED + "D " + ChatColor.BLACK + stats.getDeaths());

            sign.update();
        }

        Iterator<Map.Entry<Location, Integer>> headIterator = leaderboardHeads.entrySet().iterator();

        while (headIterator.hasNext()) {
            Map.Entry<Location, Integer> entry = headIterator.next();

            StatsEntry stats = get(entry.getValue());

            if (stats == null) {
                continue;
            }

            if (!(entry.getKey().getBlock().getState() instanceof Skull)) {
                headIterator.remove();
                continue;
            }

            Skull skull = (Skull) entry.getKey().getBlock().getState();

            skull.setOwner(UUIDUtils.name(stats.getOwner()));
            skull.update();
        }
    }

    private String trim(String name) {
        return name.length() <= 15 ? name : name.substring(0, 15);
    }

    private StatsEntry get(int place) {
        Map<StatsEntry, Integer> base = Maps.newHashMap();

        for (StatsEntry entry : stats.values()) {
            base.put(entry, entry.getKills());
        }

        TreeMap<StatsEntry, Integer> ordered = new TreeMap<>((Comparator<StatsEntry>) (first, second) -> {
            if (first.getKills() >= second.getKills()) {
                return -1;
            }
            return 1;
        });

        ordered.putAll(base);

        Map<StatsEntry, String> leaderboards = Maps.newLinkedHashMap();

        int index = 0;
        for (Map.Entry<StatsEntry, Integer> entry : ordered.entrySet()) {
            leaderboards.put(entry.getKey(), entry.getValue() + "");

            index++;

            if (index == place + 1) {
                break;
            }
        }

        try {
            return Iterables.get(leaderboards.keySet(), place - 1);
        } catch (Exception e) {
            return null;
        }
    }

    public void clearAll() {
        stats.clear();
        Bukkit.getScheduler().scheduleAsyncDelayedTask(Foxtrot.getInstance(), this::save);
    }

    public void clearLeaderboards() {
        leaderboardHeads.clear();
        leaderboardSigns.clear();

        Bukkit.getScheduler().scheduleAsyncDelayedTask(Foxtrot.getInstance(), this::save);
    }

    public Map<StatsEntry, String> getLeaderboards(StatsTopCommand.StatsObjective objective, int range) {
        if (objective != StatsTopCommand.StatsObjective.KD) {
            Map<StatsEntry, Integer> base = Maps.newHashMap();

            for (StatsEntry entry : stats.values()) {
                base.put(entry, entry.get(objective));
            }

            TreeMap<StatsEntry, Integer> ordered = new TreeMap<>((Comparator<StatsEntry>) (first, second) -> {
                if (first.get(objective) >= second.get(objective)) {
                    return -1;
                }

                return 1;
            });
            ordered.putAll(base);

            Map<StatsEntry, String> leaderboards = Maps.newLinkedHashMap();

            int index = 0;
            for (Map.Entry<StatsEntry, Integer> entry : ordered.entrySet()) {
                leaderboards.put(entry.getKey(), entry.getValue() + "");

                index++;

                if (index == range) {
                    break;
                }
            }

            return leaderboards;
        } else {
            Map<StatsEntry, Double> base = Maps.newHashMap();

            for (StatsEntry entry : stats.values()) {
                base.put(entry, entry.getKD());
            }

            TreeMap<StatsEntry, Double> ordered = new TreeMap<>((Comparator<StatsEntry>) (first, second) -> {
                if (first.getKD() > second.getKD()) {
                    return -1;
                }

                return 1;
            });
            ordered.putAll(base);

            Map<StatsEntry, String> leaderboards = Maps.newHashMap();

            int index = 0;
            for (Map.Entry<StatsEntry, Double> entry : ordered.entrySet()) {
                if (entry.getKey().getDeaths() < 10) {
                    continue;
                }

                String kd = Team.DTR_FORMAT.format((double) entry.getKey().getKills() / (double) entry.getKey().getDeaths());

                leaderboards.put(entry.getKey(), kd);

                index++;

                if (index == range) {
                    break;
                }
            }

            return leaderboards;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (leaderboardHeads.containsKey(event.getBlock().getLocation())) {
            leaderboardHeads.remove(event.getBlock().getLocation());
            player.sendMessage(ChatColor.YELLOW + "Removed this skull from leaderboards.");

            Bukkit.getScheduler().scheduleAsyncDelayedTask(Foxtrot.getInstance(), this::save);
        }

        if (leaderboardSigns.containsKey(event.getBlock().getLocation())) {
            leaderboardSigns.remove(event.getBlock().getLocation());
            player.sendMessage(ChatColor.YELLOW + "Removed this sign from leaderboards.");

            Bukkit.getScheduler().scheduleAsyncDelayedTask(Foxtrot.getInstance(), this::save);
        }
    }

}
