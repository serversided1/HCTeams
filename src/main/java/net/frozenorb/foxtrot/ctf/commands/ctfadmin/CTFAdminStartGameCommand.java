package net.frozenorb.foxtrot.ctf.commands.ctfadmin;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.ctf.CTFHandler;
import net.frozenorb.foxtrot.ctf.enums.CTFFlagColor;
import net.frozenorb.foxtrot.ctf.game.CTFFlag;
import net.frozenorb.foxtrot.ctf.game.CTFGame;
import net.frozenorb.foxtrot.serialization.serializers.LocationSerializer;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CTFAdminStartGameCommand {

    @Command(names={ "ctfadmin startgame" }, permissionNode="op")
    public static void ctfAdminStartGame(Player sender, @Param(name="config") String config) {
        CTFGame game = FoxtrotPlugin.getInstance().getCTFHandler().getGame();

        if (game != null) {
            sender.sendMessage(CTFHandler.PREFIX + " " + ChatColor.RED + "There is already an active CTF game!");
            return;
        }

        File configFile = new File(config);

        if (!configFile.exists()) {
            sender.sendMessage(CTFHandler.PREFIX + " " + ChatColor.YELLOW + "The CTF config " + ChatColor.LIGHT_PURPLE + config + ChatColor.YELLOW + " does not exist.");
            return;
        }

        try {
            LocationSerializer locationSerializer = new LocationSerializer();
            BasicDBObject dbo = (BasicDBObject) JSON.parse(FileUtils.readFileToString(configFile));
            List<CTFFlag> flags = new ArrayList<CTFFlag>();

            for (CTFFlagColor flagColor : CTFFlagColor.values()) {
                BasicDBObject flagDBObject = (BasicDBObject) dbo.get(flagColor.getName());

                Location captureLocation = locationSerializer.deserialize((BasicDBObject) flagDBObject.get("CaptureLocation"));
                Location spawnLocation = locationSerializer.deserialize((BasicDBObject) flagDBObject.get("SpawnLocation"));

                flags.add(new CTFFlag(spawnLocation, captureLocation, flagColor));
            }

            new CTFGame(flags.toArray(new CTFFlag[flags.size()]));
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(CTFHandler.PREFIX + " " + ChatColor.YELLOW + "The CTF config " + ChatColor.LIGHT_PURPLE + configFile.getName() + ChatColor.YELLOW + " is invalid.");
        }
    }

}