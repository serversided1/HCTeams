package net.frozenorb.foxtrot.game.games.koth;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.minecraft.util.com.google.gson.Gson;
import net.minecraft.util.org.apache.commons.io.IOUtils;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by macguy8 on 10/31/2014.
 */
public class KOTHs {

    private static Set<KOTH> KOTHs = new HashSet<KOTH>();

    public static void init() {
        loadKOTHs();

        new BukkitRunnable() {

            public void run() {
                for (KOTH koth : KOTHs) {
                    if (koth.isActive()) {
                        koth.tick();
                    }
                }
            }

        }.runTaskTimer(FoxtrotPlugin.getInstance(), 1L, 1L);
    }

    public static void loadKOTHs() {
        try {
            File kothsBase = new File("KOTHs");

            if (!kothsBase.exists()) {
                kothsBase.mkdir();
            }

            for (File kothFile : kothsBase.listFiles()) {
                if (kothFile.getName().endsWith(".json")) {
                    BufferedInputStream e = new BufferedInputStream(new FileInputStream(kothFile));

                    StringWriter writer = new StringWriter();
                    IOUtils.copy(e, writer, "utf-8");

                    KOTHs.add((new Gson()).fromJson(writer.toString(), KOTH.class));

                    e.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveKOTHs() {
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
                FileWriter e = new FileWriter(kothFile);

                e.write((new Gson()).toJson(koth));

                e.flush();
                e.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Set<KOTH> getKOTHs() {
        return (KOTHs);
    }

    public static KOTH getKOTH(String name) {
        for (KOTH koth : KOTHs) {
            if (koth.getName().equalsIgnoreCase(name)) {
                return (koth);
            }
        }

        return (null);
    }

}