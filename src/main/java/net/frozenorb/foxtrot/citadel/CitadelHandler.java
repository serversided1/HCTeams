package net.frozenorb.foxtrot.citadel;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.citadel.listeners.CitadelListener;
import net.frozenorb.foxtrot.team.Team;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by macguy8 on 11/14/2014.
 */
public class CitadelHandler {

    public static final String PREFIX = ChatColor.DARK_PURPLE + "[Citadel]";

    @Getter private ObjectId capper;
    @Getter private int level;
    @Getter private Date townLootable;
    @Getter private Date courtyardLootable;

    public CitadelHandler() {
        loadCitadelInfo();
        FoxtrotPlugin.getInstance().getServer().getPluginManager().registerEvents(new CitadelListener(), FoxtrotPlugin.getInstance());
    }

    public void reloadCitadelInfo() {
        loadCitadelInfo();
    }

    private void loadCitadelInfo() {
        try {
            File citadelInfo = new File("citadelInfo.json");

            if (!citadelInfo.exists()) {
                citadelInfo.createNewFile();
                BasicDBObject dbo = new BasicDBObject();

                dbo.put("capper", null);
                dbo.put("level", 0);
                dbo.put("townLootable", getTownLootable());
                dbo.put("courtyardLootable", getCourtyardLootable());

                FileUtils.write(citadelInfo, new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(dbo.toString())));
            }

            BasicDBObject dbo = (BasicDBObject) JSON.parse(FileUtils.readFileToString(citadelInfo));

            if (dbo != null) {
                this.capper = dbo.getObjectId("capper");
                this.level = dbo.getInt("level");
                this.townLootable = dbo.getDate("townLootable");
                this.courtyardLootable = dbo.getDate("courtyardLootable");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveCitadelInfo() {
        try {
            File citadelInfo = new File("citadelInfo.json");
            BasicDBObject dbo = new BasicDBObject();

            dbo.put("capper", capper);
            dbo.put("level", level);
            dbo.put("townLootable", townLootable);
            dbo.put("courtyardLootable", courtyardLootable);

            citadelInfo.delete();
            FileUtils.write(citadelInfo, new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(dbo.toString())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCapper(ObjectId capper, int level) {
        this.capper = capper;
        this.level = level;
        this.townLootable = generateTownLootableDate();
        this.courtyardLootable = generateCourtyardLootableDate();

        saveCitadelInfo();
    }

    public boolean canLootCitadelTown(Player player) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());
        return ((team != null && team.getUniqueId() == capper) || System.currentTimeMillis() > townLootable.getTime());
    }

    public boolean canLootCitadelCourtyard(Player player) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());
        return ((team != null && team.getUniqueId() == capper) || System.currentTimeMillis() > courtyardLootable.getTime());
    }

    public boolean canLootCitadelKeep(Player player) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());
        return (team != null && team.getUniqueId() == capper);
    }

    // Credit to http://stackoverflow.com/a/3465656 on StackOverflow.
    private Date generateTownLootableDate() {
        Calendar date = Calendar.getInstance();
        int diff = Calendar.TUESDAY  - date.get(Calendar.DAY_OF_WEEK);

        if (diff <= 0) {
            diff += 7;
        }

        date.add(Calendar.DAY_OF_MONTH, diff);
        date.set(Calendar.HOUR_OF_DAY, 17); // 5 PM server time
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        return (date.getTime());
    }

    // Credit to http://stackoverflow.com/a/3465656 on StackOverflow.
    private Date generateCourtyardLootableDate() {
        Calendar date = Calendar.getInstance();
        int diff = Calendar.THURSDAY  - date.get(Calendar.DAY_OF_WEEK);

        if (diff <= 0) {
            diff += 7;
        }

        date.add(Calendar.DAY_OF_MONTH, diff);
        date.set(Calendar.HOUR_OF_DAY, 17); // 5 PM server time
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        return (date.getTime());
    }

}