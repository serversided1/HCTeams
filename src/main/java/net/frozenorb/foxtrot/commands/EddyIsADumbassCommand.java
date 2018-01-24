package net.frozenorb.foxtrot.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;

public class EddyIsADumbassCommand {

    @Command(names={ "eddyIsADumbass_longCommandSoNobodyRunsItAccidentally" }, permission="op", async = true)
    public static void eddyIsADumbass_longCommandSoNobodyRunsItAccidentally(Player sender) {
        DBCollection coll = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("TeamActions");
        DBCursor cursor = coll.find();
        Map<String, List<BasicDBObject>> data = new HashMap<>();

        sender.sendMessage("starting database pull");

        while (cursor.hasNext()) {
            BasicDBObject obj = (BasicDBObject) cursor.next();
            data.computeIfAbsent(obj.getString("teamId"), i -> new ArrayList<>());
            data.get(obj.getString("teamId")).add(obj);
        }

        sender.sendMessage("Collected data for " + data.size() + " unique teams... starting to sort");

        data.values().forEach(objs -> {
            objs.sort((a, b) -> {
                return a.getDate("time").compareTo(b.getDate("time"));
            });
        });

        sender.sendMessage("sorted teams, starting to remove old");

        data.values().removeIf(e -> {
            sender.sendMessage(e.get(0) + " -> " + e.get(e.size() - 1));
            return e.get(e.size() - 1).getString("type").equals("playerDisbandTeam");
        });

        sender.sendMessage("removed old teams, currently " + data.size() + " are left");

        data.forEach((key, e) -> {
            BasicDBObject latest = e.get(e.size() - 1);
            Team team = new Team(latest.getString("teamName"));
            team.load((BasicDBObject) latest.get("teamAfterAction"));

            //Foxtrot.getInstance().getTeamHandler().setupTeam(team);
            sender.sendMessage(ChatColor.GREEN + "would have Reinstated team " + team.getName() + " with " + team.getClaims().size() + " claims and " + team.getMembers().size() + " members");
        });

        sender.sendMessage("reinstated all teams");

    }

    @Command(names = {"fixkitmap_fuck"}, permission = "op")
    public static void fixKitmap(CommandSender sender) {
        DBCollection coll = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("Teams");
        DBCursor cursor = coll.find();

        cursor.forEach(document -> {
            if (!document.containsKey("Name")) return;

            Team team = new Team((String) document.get("Name"));
            team.load((BasicDBObject) document);

            Foxtrot.getInstance().getTeamHandler().setupTeam(team);
        });
    }

}