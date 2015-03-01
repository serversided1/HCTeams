package net.frozenorb.foxtrot.koth;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.koth.listeners.KOTHListener;
import net.frozenorb.foxtrot.serialization.LocationSerializer;
import net.frozenorb.foxtrot.util.TimeUtils;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public class KOTHHandler {
    
    @Getter private Set<KOTH> KOTHs = new HashSet<>();
    @Getter private Map<Integer, String> KOTHSchedule = new TreeMap<>();
    @Getter private Set<Location> KOTHSigns = new HashSet<>();

    public KOTHHandler() {
        loadKOTHs();
        loadSchedules();
        loadSigns();

        FoxtrotPlugin.getInstance().getServer().getPluginManager().registerEvents(new KOTHListener(), FoxtrotPlugin.getInstance());
        FrozenCommandHandler.registerParameterType(KOTH.class, new KOTHType());

        new BukkitRunnable() {

            public void run() {
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
                            sign.setLine(1, updateFor.isActive() ? ChatColor.GREEN + TimeUtils.getMMSS(updateFor.getRemainingCapTime()) : ChatColor.DARK_RED + TimeUtils.getMMSS(updateFor.getCapTime()));
                            sign.setLine(2, "");
                            sign.setLine(3, ChatColor.AQUA.toString() + updateFor.getCapLocation().getBlockX() + ", " + updateFor.getCapLocation().getBlockZ());

                            sign.update();
                        }
                    }
                }
            }

        }.runTaskTimer(FoxtrotPlugin.getInstance(), 5L, 20L);
        // The initial delay of 5 ticks is to 'offset' us with the scoreboard handler.
    }

    public void loadKOTHs() {
        try {
            File kothsBase = new File("KOTHs");

            if (!kothsBase.exists()) {
                kothsBase.mkdir();
            }

            for (File kothFile : kothsBase.listFiles()) {
                if (kothFile.getName().endsWith(".json")) {
                    KOTHs.add(FoxtrotPlugin.GSON.fromJson(FileUtils.readFileToString(kothFile), KOTH.class));
                }
            }
        } catch (Exception e) {
            FoxtrotPlugin.getInstance().getBugSnag().notify(e);
            e.printStackTrace();
        }
    }

    public void loadSchedules() {
        KOTHSchedule.clear();

        try {
            File kothSchedule = new File("kothSchedule.json");

            if (!kothSchedule.exists()) {
                kothSchedule.createNewFile();
                FileUtils.write(kothSchedule, FoxtrotPlugin.GSON.toJson(new JsonParser().parse(new BasicDBObject().toString())));
            }

            BasicDBObject dbo = (BasicDBObject) JSON.parse(FileUtils.readFileToString(kothSchedule));

            if (dbo != null) {
                for (Map.Entry<String, Object> entry : dbo.entrySet()) {
                    this.KOTHSchedule.put(Integer.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
                }
            }
        } catch (Exception e) {
            FoxtrotPlugin.getInstance().getBugSnag().notify(e);
            e.printStackTrace();
        }
    }

    public void loadSigns() {
        KOTHSigns.clear();

        try {
            File kothSigns = new File("kothSigns.json");

            if (!kothSigns.exists()) {
                kothSigns.createNewFile();
                FileUtils.write(kothSigns, FoxtrotPlugin.GSON.toJson(new JsonParser().parse(new BasicDBObject().toString())));
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
            FoxtrotPlugin.getInstance().getBugSnag().notify(e);
            e.printStackTrace();
        }
    }

    public void saveKOTHs() {
        try {
            File kothsBase = new File("KOTHs");

            if (!kothsBase.exists()) {
                kothsBase.mkdir();
            }

            for (File kothFile : kothsBase.listFiles()) {
                kothFile.delete();
            }

            for (KOTH koth : KOTHs) {
                File kothFile = new File(kothsBase, koth.getName() + ".json");
                FileUtils.write(kothFile, FoxtrotPlugin.GSON.toJson(koth));
            }
        } catch (Exception e) {
            FoxtrotPlugin.getInstance().getBugSnag().notify(e);
            e.printStackTrace();
        }
    }

    public void saveSigns() {
        try {
            File kothSigns = new File("kothSigns.json");
            BasicDBObject dbo = new BasicDBObject();
            BasicDBList signs = new BasicDBList();

            for (Location signLocation : KOTHSigns) {
                signs.add(LocationSerializer.serialize(signLocation));
            }

            dbo.put("signs", signs);
            kothSigns.delete();
            FileUtils.write(kothSigns, FoxtrotPlugin.GSON.toJson(new JsonParser().parse(dbo.toString())));
        } catch (Exception e) {
            FoxtrotPlugin.getInstance().getBugSnag().notify(e);
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