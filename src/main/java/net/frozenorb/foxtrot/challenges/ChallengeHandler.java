package net.frozenorb.foxtrot.challenges;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.frozenorb.qlib.redis.RedisCommand;
import net.frozenorb.qlib.util.ClassUtils;
import net.frozenorb.qlib.util.UUIDUtils;
import net.md_5.bungee.api.ChatColor;
import redis.clients.jedis.Jedis;

public class ChallengeHandler implements Listener {
    private static Map<String, Challenge> allChallenges = Maps.newHashMap();
    private static Map<String, KillBasedChallenge> killBasedChallenges = Maps.newHashMap();
    
    private Map<UUID, Map<Challenge, Integer>> challengeCounts = Maps.newConcurrentMap();
    
    private Challenge firstChallenge = null;
    private Challenge secondChallenge = null;
    
    private int lastDateAsInt = 0;
    
    private static UpdateOptions UPSERT = new UpdateOptions().upsert(true);
    
    private static ChallengeHandler instance;
    
    public ChallengeHandler() {
        instance = this;
        DBCollection mongoCollection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("challengeProgress");
        
        mongoCollection.createIndex(new BasicDBObject("user", 1));
        mongoCollection.createIndex(new BasicDBObject("date", 1));
        mongoCollection.createIndex(new BasicDBObject("lastUsername", 1));
        
        Bukkit.getLogger().info("Creating indexes done for challenges.");
        
        ClassUtils.getClassesInPackage(Foxtrot.getInstance(), "net.frozenorb.foxtrot.challenges.challenges").forEach(clazz -> {
            if (KillBasedChallenge.class.isAssignableFrom(clazz)) {
                try {
                    KillBasedChallenge challenge = (KillBasedChallenge) clazz.newInstance();
                    
                    killBasedChallenges.put(challenge.getName(), challenge);
                    allChallenges.put(challenge.getName(), challenge);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Challenge challenge = (Challenge) clazz.newInstance();
                    
                    allChallenges.put(challenge.getName(), challenge);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
        
        lastDateAsInt = qLib.getInstance().runRedisCommand(new RedisCommand<Integer>() {
            
            @Override
            public Integer execute(Jedis redis) {
                int currentDate = getDateAsInt();
                if (redis.exists("lastChallengeUpdateTime")) {
                    currentDate = Integer.valueOf(redis.get("lastChallengeUpdateTime"));
                }
                
                return currentDate;
            }
            
        });
        
        loadDailyChallenges();
        
        Bukkit.getScheduler().runTaskTimerAsynchronously(Foxtrot.getInstance(), () -> {
            int oldDate = lastDateAsInt;
            int newDate = getDateAsInt();
            
            if (oldDate != newDate) {
                challengeCounts.clear();
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&cChallenge progress has been reset! Check out the two new daily challenges."));
                
                List<Challenge> challenges = Lists.newArrayList(allChallenges.values());
                
                firstChallenge = challenges.get(qLib.RANDOM.nextInt(challenges.size()));
                secondChallenge = challenges.get(qLib.RANDOM.nextInt(challenges.size()));
                
                Bukkit.getLogger().info("Picked \"" + firstChallenge.getName() + "\" as first challenge.");
                Bukkit.getLogger().info("Picked \"" + secondChallenge.getName() + "\" as second challenge.");
                
                lastDateAsInt = newDate;
                saveLastDate();
                
                saveDailyChallenges();
            }
            
            long start = System.currentTimeMillis();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                saveProgress(onlinePlayer, false);
            }
            
            Bukkit.getLogger().info("Saved " + Bukkit.getOnlinePlayers().size() + " challenge progressions in " + (System.currentTimeMillis() - start) + "ms.");
        }, 30 * 20, 30 * 20);
        
        FrozenCommandHandler.registerAll(Foxtrot.getInstance());
        Bukkit.getPluginManager().registerEvents(this, Foxtrot.getInstance());
    }
    
    public void save() {
        saveDailyChallenges();
        saveLastDate();
    }
    
    private void loadDailyChallenges() {
        qLib.getInstance().runRedisCommand(new RedisCommand<Object>() {

            @Override
            public Object execute(Jedis redis) {
                if (redis.exists("firstDailyChallenge")) {
                    firstChallenge = allChallenges.get(redis.get("firstDailyChallenge"));
                    secondChallenge = allChallenges.get(redis.get("secondDailyChallenge"));
                    
                    Bukkit.getLogger().info("Loaded \"" + firstChallenge.getName() + "\" as first challenge.");
                    Bukkit.getLogger().info("Loaded \"" + secondChallenge.getName() + "\" as second challenge.");
                    
                    if (firstChallenge == secondChallenge) {
                        Bukkit.getLogger().info("Both challenges are the same. Picking new challenges.");
                        pickNewChallenges();
                    }
                } else {
                    pickNewChallenges();
                }
                
                return null;
            }
            
        });
    }
    
    private void pickNewChallenges() {
        List<Challenge> challenges = Lists.newArrayList(allChallenges.values());
        
        firstChallenge = challenges.get(qLib.RANDOM.nextInt(challenges.size()));
        
        challenges.remove(firstChallenge);
        
        secondChallenge = challenges.get(qLib.RANDOM.nextInt(challenges.size()));
        
        Bukkit.getLogger().info("Picked \"" + firstChallenge.getName() + "\" as first challenge.");
        Bukkit.getLogger().info("Picked \"" + secondChallenge.getName() + "\" as second challenge.");
    }
    
    private void saveDailyChallenges() {
        qLib.getInstance().runRedisCommand(new RedisCommand<Object>() {

            @Override
            public Object execute(Jedis redis) {
                redis.set("firstDailyChallenge", firstChallenge.getName());
                redis.set("secondDailyChallenge", secondChallenge.getName());
                return null;
            }
            
        });
    }
    
    @Command(names = {"challenge progress", "challenges"}, permission = "")
    public static void progress(Player sender) {
        sender.sendMessage(ChatColor.GREEN + "Today's daily challenges: " + instance.firstChallenge.getName() + " and " + instance.secondChallenge.getName());
        sender.sendMessage(ChatColor.GREEN + "Your progressions:");
        sender.sendMessage(ChatColor.RED + instance.firstChallenge.getName() + ": " + instance.getProgress(sender, instance.firstChallenge) + "/" + instance.firstChallenge.getCountToQualify());
        sender.sendMessage(ChatColor.RED + instance.secondChallenge.getName() + ": " + instance.getProgress(sender, instance.secondChallenge) + "/" + instance.secondChallenge.getCountToQualify());
    }
    
    @Command(names = {"challenge picknew"}, permission = "op")
    public static void newchallenges(CommandSender sender) {
        instance.pickNewChallenges();
        instance.saveDailyChallenges();
        
        sender.sendMessage(ChatColor.GREEN + "Picked \"" + instance.firstChallenge.getName() + "\" as first challenge.");
        sender.sendMessage(ChatColor.GREEN + "Picked \"" + instance.secondChallenge.getName() + "\" as second challenge.");
    }
        
    public void saveLastDate() {
        qLib.getInstance().runRedisCommand(new RedisCommand<Object>() {
            
            @Override
            public Object execute(Jedis redis) {
                redis.set("lastChallengeUpdateTime", Integer.toString(lastDateAsInt));
                return null;
            }
            
        });
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onLogin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(Foxtrot.getInstance(), () -> {
            loadProgress(event.getPlayer());
        });
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(Foxtrot.getInstance(), () -> {
            saveProgress(event.getPlayer(), true);
        });
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        Player died = event.getEntity();
        Player killer = died.getKiller();
        
        if (killer == null) {
            return;
        }
        
        killBasedChallenges.values().forEach(challenge -> {
            if (isActive(killer, challenge)) {
                if (challenge.counts(killer, died)) {
                    incrementChallenge(killer, challenge);
                }
            }
        });
    }
    
    public void incrementChallenge(Player player, Challenge challenge) {
        if (!challengeCounts.containsKey(player.getUniqueId())) {
            challengeCounts.put(player.getUniqueId(), Maps.newHashMap());
        }
        
        Map<Challenge, Integer> subMap = challengeCounts.get(player.getUniqueId());
        subMap.put(challenge, subMap.getOrDefault(challenge, 0) + 1);
        
        if (challenge.getCountToQualify() == subMap.get(challenge)) {
            completedChallenge(player, challenge);
        }
    }
    
    public static int getDateAsInt() {
        Calendar lCal = Calendar.getInstance();
        lCal.setTime(new Date());
        int lYear = lCal.get(Calendar.YEAR);
        int lMonth = lCal.get(Calendar.MONTH) + 1;
        int lDay = lCal.get(Calendar.DATE);
        int a = (14 - lMonth) / 12;
        int y = lYear + 4800 - a;
        int m = lMonth + 12 * a - 3;
        return lDay + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045;
    }
    
    private boolean isActive(Player player, Challenge challenge) {
        return (firstChallenge == challenge || secondChallenge == challenge) && getProgress(player, challenge) < challenge.getCountToQualify();
    }
    
    private int getProgress(Player player, Challenge challenge) {
        if (!challengeCounts.containsKey(player.getUniqueId()) || !challengeCounts.get(player.getUniqueId()).containsKey(challenge)) {
            return 0;
        }
        
        return challengeCounts.get(player.getUniqueId()).get(challenge);
    }
    
    private void loadProgress(Player player) {
        Document searchDocument = new Document("user", player.getUniqueId().toString());
        searchDocument.put("date", lastDateAsInt);
        
        Document locatedDocument = Foxtrot.getInstance().getMongoPool().getDatabase(Foxtrot.MONGO_DB_NAME).getCollection("challengeProgress").find(searchDocument).first();
        if (locatedDocument == null || locatedDocument.isEmpty()) {
            return;
        }
        
        Map<Challenge, Integer> progress = Maps.newHashMap();
        
        for (Challenge challenge : allChallenges.values()) {
            int challengeProgress = locatedDocument.getInteger(challenge.getMongoName(), 0);
            progress.put(challenge, challengeProgress);
        }
        
        challengeCounts.put(player.getUniqueId(), progress);
    }
    
    private void saveProgress(Player player, boolean remove) {
        Document updateDocument = new Document("user", player.getUniqueId().toString());
        updateDocument.put("date", lastDateAsInt);
        
        MongoDatabase database = Foxtrot.getInstance().getMongoPool().getDatabase(Foxtrot.MONGO_DB_NAME);
        
        Document toSet = new Document();
        
        for (Challenge challenge : allChallenges.values()) {
            toSet.put(challenge.getMongoName(), getProgress(player, challenge));
        }
        
        toSet.put("lastUsername", UUIDUtils.name(player.getUniqueId()));
        
        database.getCollection("challengeProgress").updateOne(updateDocument, new Document("$set", toSet), UPSERT);
        
        if (remove) {
            challengeCounts.remove(player.getUniqueId());
        }
    }

    private boolean hasCompletedBothChallenges(Player player) {
        return firstChallenge.getCountToQualify() == getProgress(player, firstChallenge) && secondChallenge.getCountToQualify() == getProgress(player, secondChallenge);
    }
    
    private void completedChallenge(Player player, Challenge challenge) {
        Bukkit.getLogger().info(player.getName() + " completed challenge " + challenge.getName());
        
        if (hasCompletedBothChallenges(player)) {
            Bukkit.getLogger().info("Player has completed both challenges. Awarding key");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " Challenge 1");
        }
    }
}
