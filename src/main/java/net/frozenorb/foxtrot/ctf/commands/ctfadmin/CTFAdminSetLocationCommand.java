package net.frozenorb.foxtrot.ctf.commands.ctfadmin;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.ctf.CTFHandler;
import net.frozenorb.foxtrot.ctf.enums.CTFFlagColor;
import net.frozenorb.foxtrot.serialization.serializers.LocationSerializer;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;
import org.bukkit.entity.Player;

import java.io.File;

/**
 * Created by Colin on 12/25/2014.
 */
public class CTFAdminSetLocationCommand {

    @Command(names={ "ctfadmin setlocation" }, permissionNode="op")
    public static void ctfAdminSetLocation(Player sender, @Param(name="config") String config, @Param(name="location") String location) {
        File configFile = new File(config);

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                sender.sendMessage(CTFHandler.PREFIX + " " + ChatColor.YELLOW + "Created the CTF config file " + ChatColor.LIGHT_PURPLE + config + ChatColor.YELLOW + ".");
            } catch (Exception e) {
                sender.sendMessage(CTFHandler.PREFIX + " " + ChatColor.YELLOW + "An error occurred while generating a CTF config file.");
            }
        }

        try {
            LocationSerializer locationSerializer = new LocationSerializer();
            BasicDBObject dbo = (BasicDBObject) JSON.parse(FileUtils.readFileToString(configFile));

            // If we just created the file this is possible.
            if (dbo == null) {
                dbo = new BasicDBObject();
            }

            for (CTFFlagColor flagColor : CTFFlagColor.values()) {
                if (location.toLowerCase().startsWith(flagColor.getName().toLowerCase())) {
                    BasicDBObject flagDBObject = new BasicDBObject();

                    if (dbo.containsField(flagColor.getName())) {
                        flagDBObject = (BasicDBObject) dbo.get(flagColor.getName());
                    }

                    if (location.toLowerCase().endsWith("_cap")) {
                        flagDBObject.put("CaptureLocation", locationSerializer.serialize(sender.getLocation()));
                    } else if (location.toLowerCase().endsWith("_spawn")) {
                        flagDBObject.put("SpawnLocation", locationSerializer.serialize(sender.getLocation()));
                    } else {
                        continue; // If we 'continue' past our one match, we'll end up sending the user the 'not a valid location' message.
                    }

                    dbo.put(flagColor.getName(), flagDBObject);
                    FileUtils.write(configFile, new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(dbo.toString())));
                    sender.sendMessage(CTFHandler.PREFIX + " " + ChatColor.YELLOW + "Updated CTF location.");
                    return;
                }
            }

            sender.sendMessage(CTFHandler.PREFIX + " " + ChatColor.LIGHT_PURPLE + location + ChatColor.YELLOW + " isn't a valid location to set.");
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(CTFHandler.PREFIX + " " + ChatColor.YELLOW + "An error occurred while reading/writing the config file to disk.");
        }
    }

}