package net.frozenorb.foxtrot.deathtracker;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.deathmessage.DeathMessageHandler;
import net.frozenorb.foxtrot.deathmessage.objects.Damage;
import net.frozenorb.foxtrot.deathmessage.objects.MobDamage;
import net.frozenorb.foxtrot.deathmessage.objects.PlayerDamage;
import net.frozenorb.foxtrot.deathmessage.trackers.ArrowTracker;
import net.frozenorb.foxtrot.listener.EnderpearlListener;
import net.frozenorb.foxtrot.pvpclasses.PvPClassHandler;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.util.TimeUtils;
import net.frozenorb.mBasic.Basic;
import net.frozenorb.mBasic.Utilities.Lag;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by macguy8 on 11/18/2014.
 */
public class DeathTracker {

    public static void logDeath(Player player, Player killer) {
        File logToFolder = new File("deathtracker" + File.separator + player.getName());
        File logTo = new File(logToFolder, player.getName() + "-" + (killer == null ? "Environment" : killer.getName()) + "-" + (new Date().toString()) + ".log");

        try {
            logTo.getParentFile().mkdirs();
            logTo.createNewFile();

            FileUtils.write(logTo, new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(generateDeathData(player, killer).toString())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static BasicDBObject generateDeathData(Player player, Player killer) {
        BasicDBObject deathData = new BasicDBObject();

        deathData.put("Date", (new Date()).toString());
        deathData.put("Killer", killer == null ? "N/A" : generatePlayerData(killer, false));
        deathData.put("Player", generatePlayerData(player, true));
        deathData.put("Server", generateServerData());

        return (deathData);
    }

    public static BasicDBObject generatePlayerData(Player player, boolean includeDamage) {
        BasicDBObject playerData = new BasicDBObject();
        BasicDBList potionEffects = new BasicDBList();

        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            BasicDBObject potionEffectDBObject = new BasicDBObject();

            potionEffectDBObject.put("Type", potionEffect.getType().getName());
            potionEffectDBObject.put("Tier", potionEffect.getAmplifier() + 1);
            potionEffectDBObject.put("Duration", potionEffect.getAmplifier() > 1_000_000L ? "Infinite" : TimeUtils.getMMSS(potionEffect.getAmplifier() / 20));

            potionEffects.add(potionEffectDBObject);
        }

        BasicDBObject locationData = new BasicDBObject();

        locationData.put("World", player.getLocation().getWorld().getName());
        locationData.put("X", player.getLocation().getX());
        locationData.put("Y", player.getLocation().getY());
        locationData.put("Z", player.getLocation().getZ());

        Team claimOwner = LandBoard.getInstance().getTeam(player.getLocation());

        if (claimOwner == null) {
            locationData.put("Claim", "N/A");
        } else {
            locationData.put("Claim", claimOwner.getName());
        }

        BasicDBObject teamData = null;

        Team playerTeam = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());

        if (playerTeam != null) {
            teamData = new BasicDBObject();

            teamData.put("RegenTime", TimeUtils.getConvertedTime(((int) (playerTeam.getDTRCooldown() - System.currentTimeMillis())) / 1000).trim());
            teamData.put("Name", playerTeam.getName());
            teamData.put("DTR", playerTeam.getDTR());
            teamData.put("MembersOnline", playerTeam.getOnlineMemberAmount());
        }

        if (EnderpearlListener.getEnderpearlCooldown().containsKey(player.getName()) && EnderpearlListener.getEnderpearlCooldown().get(player.getName()) >= System.currentTimeMillis()) {
            playerData.put("EnderpearlCooldown", ((float) (EnderpearlListener.getEnderpearlCooldown().get(player.getName()) - System.currentTimeMillis())) / 1000F);
        } else {
            playerData.put("EnderpearlCooldown", 0);
        }

        if (SpawnTagHandler.isTagged(player)) {
            playerData.put("SpawnTag", ((float) SpawnTagHandler.getTag(player)) / 1000F);
        } else {
            playerData.put("SpawnTag", 0);
        }

        if (includeDamage) {
            BasicDBList damageRecord = new BasicDBList();
            List<Damage> records = DeathMessageHandler.getDamage(player);

            for (Damage record : records) {
                if (System.currentTimeMillis() - record.getTime() > 30_000L) {
                    continue;
                }

                BasicDBObject recordDBObject = new BasicDBObject();

                recordDBObject.put("Class", record.getClass().getSimpleName());
                recordDBObject.put("TimeBeforeDeath", ((float) (System.currentTimeMillis() - record.getTime())) / 1000F);
                recordDBObject.put("Damage", record.getDamage());
                recordDBObject.put("Description", record.getDescription());
                recordDBObject.put("Health", record.getHealthAfter());

                if (record instanceof PlayerDamage) {
                    recordDBObject.put("Damager", ((PlayerDamage) record).getDamager());
                } else if (record instanceof MobDamage) {
                    recordDBObject.put("EntityType", ((MobDamage) record).getMobType().name());
                }

                if (record instanceof ArrowTracker.ArrowDamageByPlayer) {
                    ArrowTracker.ArrowDamageByPlayer damage = (ArrowTracker.ArrowDamageByPlayer) record;
                    BasicDBObject locationData2 = new BasicDBObject();

                    locationData2.put("World", damage.getShotFrom().getWorld().getName());
                    locationData2.put("X", damage.getShotFrom().getX());
                    locationData2.put("Y", damage.getShotFrom().getY());
                    locationData2.put("Z", damage.getShotFrom().getZ());

                    recordDBObject.put("Distance", damage.getDistance());
                    recordDBObject.put("ShotFrom", locationData2);
                }

                damageRecord.add(recordDBObject);
            }

            playerData.put("DamageRecord", damageRecord);
        }

        playerData.put("Team", teamData == null ? "N/A" : teamData);
        playerData.put("PotionEffects", potionEffects);
        playerData.put("Balance", Basic.get().getEconomyManager().getBalance(player.getName()));
        playerData.put("GoppleCooldown", FoxtrotPlugin.getInstance().getOppleMap().getCooldown(player.getName()) > System.currentTimeMillis());
        playerData.put("PvPClass", PvPClassHandler.getEquippedKits().containsKey(player.getName()) ? PvPClassHandler.getEquippedKits().get(player.getName()).getName() : "N/A");
        playerData.put("HeldItem", player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR ? "N/A" : player.getItemInHand().getType().name());
        playerData.put("Location", locationData);
        playerData.put("Health", player.getHealth());
        playerData.put("Name", player.getName());
        playerData.put("UUID", player.getUniqueId().toString());
        playerData.put("Ping", ((CraftPlayer) player).getHandle().ping);

        return (playerData);
    }

    public static BasicDBObject generateServerData() {
        BasicDBObject serverData = new BasicDBObject();

        serverData.put("Playercount", FoxtrotPlugin.getInstance().getServer().getOnlinePlayers().length);
        serverData.put("TPS", Lag.getTPS());

        return (serverData);
    }

}