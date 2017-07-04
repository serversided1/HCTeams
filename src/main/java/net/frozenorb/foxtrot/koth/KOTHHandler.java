package net.frozenorb.foxtrot.koth;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.basic.Basic;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.koth.listeners.KOTHListener;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.serialization.LocationSerializer;
import net.frozenorb.qlib.util.TimeUtils;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public class KOTHHandler {
    
    @Getter private Set<KOTH> KOTHs = new HashSet<>();
    @Getter private Map<KOTHScheduledTime, String> KOTHSchedule = new TreeMap<>();
    @Getter private Set<Location> KOTHSigns = new HashSet<>();

    @Getter @Setter private boolean scheduleEnabled;

    public KOTHHandler() {
        loadKOTHs();
        loadSchedules();
        loadSigns();

        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new KOTHListener(), Foxtrot.getInstance());
        FrozenCommandHandler.registerParameterType(KOTH.class, new KOTHType());

        new BukkitRunnable() {

            public void run() {
                if (Basic.getInstance().getServerManager().isFrozen()) {
                    return;
                }
                
                for (KOTH koth : KOTHs) {
                    if (koth.isActive()) {
                        koth.tick();
                    }
                }

                for (Location signLocation : KOTHSigns) {
                    if (!signLocation.getChunk().isLoaded()) {
                        continue;
                    }

                    if (signLocation.getBlock().getState() instanceof Sign) {
                        Sign sign = (Sign) signLocation.getBlock().getState();
                        KOTH updateFor = getKOTH(ChatColor.stripColor(sign.getLine(0)));

                        if (updateFor != null) {
                            sign.setLine(0, sign.getLine(0));
                            sign.setLine(1, updateFor.isActive() ? ChatColor.GREEN + TimeUtils.formatIntoMMSS(updateFor.getRemainingCapTime()) : ChatColor.DARK_RED + TimeUtils.formatIntoMMSS(updateFor.getCapTime()));
                            sign.setLine(2, "");
                            sign.setLine(3, ChatColor.AQUA.toString() + updateFor.getCapLocation().getBlockX() + ", " + updateFor.getCapLocation().getBlockZ());

                            sign.update();
                        }
                    }
                }
            }

        }.runTaskTimer(Foxtrot.getInstance(), 5L, 20L);
        // The initial delay of 5 ticks is to 'offset' us with the scoreboard handler.
    }

    public void loadKOTHs() {
        try {
            File kothsBase = new File(Foxtrot.getInstance().getDataFolder(), "KOTHs");

            if (!kothsBase.exists()) {
                kothsBase.mkdir();
            }

            for (File kothFile : kothsBase.listFiles()) {
                if (kothFile.getName().endsWith(".json")) {
                    KOTHs.add(qLib.GSON.fromJson(FileUtils.readFileToString(kothFile), KOTH.class));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bukkit.getScheduler().runTask(Foxtrot.getInstance(), () ->
            KOTHs.stream().filter(KOTH::isActive).forEach((koth) -> koth.activate(true))
        );
    }

    public void loadSchedules() {
        KOTHSchedule.clear();

        try {
            File kothSchedule = new File(Foxtrot.getInstance().getDataFolder(), "kothSchedule.json");

            if (!kothSchedule.exists()) {
                kothSchedule.createNewFile();
                BasicDBObject schedule = new BasicDBObject();
                int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
                List<String> allKOTHs = new ArrayList<>();

                for (KOTH koth : getKOTHs()) {
                    if (koth.isHidden() || koth.getName().equalsIgnoreCase("EOTW") || koth.getName().equalsIgnoreCase("Citadel")) {
                        continue;
                    }

                    allKOTHs.add(koth.getName());
                }

                for (int dayOffset = 0; dayOffset < 100; dayOffset++) {
                    int day = (currentDay + dayOffset) % 365;
                    KOTHScheduledTime[] times = new KOTHScheduledTime[] {

                            new KOTHScheduledTime(day, 1, 30), // 1:30am EST
                            new KOTHScheduledTime(day, 12, 30), // 12:30pm EST
                            new KOTHScheduledTime(day, 15, 30), // 03:30pm EST
                            new KOTHScheduledTime(day, 18, 30), // 6:30pm EST
                            new KOTHScheduledTime(day, 21, 30) // 9:30pm EST

                    };

                    Collections.shuffle(allKOTHs);

                    for (int kothTimeIndex = 0; kothTimeIndex < times.length; kothTimeIndex++) {
                        if (kothTimeIndex >= allKOTHs.size()) {
                            break;
                        }

                        KOTHScheduledTime kothTime = times[kothTimeIndex];
                        String kothName = allKOTHs.get(kothTimeIndex);

                        schedule.put(kothTime.toString(), kothName);
                    }
                }

                FileUtils.write(kothSchedule, qLib.GSON.toJson(new JsonParser().parse(schedule.toString())));
            }

            BasicDBObject dbo = (BasicDBObject) JSON.parse(FileUtils.readFileToString(kothSchedule));

            if (dbo != null) {
                for (Map.Entry<String, Object> entry : dbo.entrySet()) {
                    KOTHScheduledTime scheduledTime = KOTHScheduledTime.parse(entry.getKey());
                    this.KOTHSchedule.put(scheduledTime, String.valueOf(entry.getValue()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadSigns() {
        KOTHSigns.clear();

        try {
            File kothSigns = new File(Foxtrot.getInstance().getDataFolder(), "kothSigns.json");

            if (!kothSigns.exists()) {
                kothSigns.createNewFile();
                FileUtils.write(kothSigns, qLib.GSON.toJson(new JsonParser().parse(new BasicDBObject().toString())));
            }

            BasicDBObject dbo = (BasicDBObject) JSON.parse(FileUtils.readFileToString(kothSigns));

            if (dbo != null) {
                if (dbo.containsField("signs")) {
                    for (Object signObj : (BasicDBList) dbo.get("signs")) {
                        this.KOTHSigns.add(LocationSerializer.deserialize((BasicDBObject) signObj));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveKOTHs() {
        try {
            File kothsBase = new File(Foxtrot.getInstance().getDataFolder(), "KOTHs");

            if (!kothsBase.exists()) {
                kothsBase.mkdir();
            }

            for (File kothFile : kothsBase.listFiles()) {
                kothFile.delete();
            }

            for (KOTH koth : KOTHs) {
                File kothFile = new File(kothsBase, koth.getName() + ".json");
                FileUtils.write(kothFile, qLib.GSON.toJson(koth));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveSigns() {
        try {
            File kothSigns = new File(Foxtrot.getInstance().getDataFolder(), "kothSigns.json");
            BasicDBObject dbo = new BasicDBObject();
            BasicDBList signs = new BasicDBList();

            for (Location signLocation : KOTHSigns) {
                signs.add(LocationSerializer.serialize(signLocation));
            }

            dbo.put("signs", signs);
            kothSigns.delete();
            FileUtils.write(kothSigns, qLib.GSON.toJson(new JsonParser().parse(dbo.toString())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public KOTH getKOTH(String name) {
        for (KOTH koth : KOTHs) {
            if (koth.getName().equalsIgnoreCase(name)) {
                return (koth);
            }
        }

        return (null);
    }

}
