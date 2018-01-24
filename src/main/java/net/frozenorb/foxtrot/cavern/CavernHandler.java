package net.frozenorb.foxtrot.cavern;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.qlib.qLib;
import net.minecraft.util.org.apache.commons.io.FileUtils;

public class CavernHandler {

    private static File file;
    @Getter private final static String cavernTeamName = "Cavern";
    @Getter @Setter private Cavern cavern;

    public CavernHandler() {
        if (Foxtrot.getInstance().getServerHandler().isVelt()) return;
        try {
            file = new File(Foxtrot.getInstance().getDataFolder(), "cavern.json");

            if (!file.exists()) {
                cavern = null;

                if (file.createNewFile()) {
                    Foxtrot.getInstance().getLogger().warning("Created a new Cavern json file.");
                }
            } else {
                cavern = qLib.GSON.fromJson(FileUtils.readFileToString(file), Cavern.class);
                Foxtrot.getInstance().getLogger().info("Successfully loaded the Cavern from file");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Foxtrot.getInstance().getServer().getScheduler().runTaskTimer(Foxtrot.getInstance(), () -> {
            if (Foxtrot.getInstance().getServerHandler().isVelt()) return;
            getCavern().reset();
            // Broadcast the reset
            Bukkit.broadcastMessage(ChatColor.AQUA + "[Cavern]" + ChatColor.GREEN + " All ores have been reset!");
        }, 5 * 20 * 60, 5 * 20 * 60);
    }

    public void save() {
        try {
            FileUtils.write(file, qLib.GSON.toJson(cavern));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasCavern() {
        return cavern != null;
    }

    public static Claim getClaim() {
        return Foxtrot.getInstance().getTeamHandler().getTeam(cavernTeamName).getClaims().get(0); // null if no glowmtn is set!
    }
}