package net.frozenorb.foxtrot.challenges;

import java.util.*;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.challenges.impl.KillBasedChallenge;
import net.frozenorb.foxtrot.challenges.impl.KillKitBasedChallenge;
import net.frozenorb.foxtrot.challenges.impl.util.KitBasedChallengeData;
import net.frozenorb.foxtrot.challenges.menu.ChallengesMenu;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
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
import net.frozenorb.qlib.util.ClassUtils;
import net.frozenorb.qlib.util.UUIDUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.ItemStack;

public class ChallengeHandler implements Listener {

    private static Map<String, Challenge> allChallenges = Maps.newHashMap();
    private static Map<String, KillBasedChallenge> killBasedChallenges = Maps.newHashMap();
    private static UpdateOptions UPSERT = new UpdateOptions().upsert(true);
    
    private Map<UUID, Map<Challenge, Integer>> challengeCounts = Maps.newConcurrentMap();
    private Map<UUID, Long> pendingTokens = Maps.newConcurrentMap();
    @Getter
    private List<Challenge> dailyChallenges = new ArrayList<>();
    private int lastDateAsInt;
    
    public ChallengeHandler() {
        DBCollection mongoCollection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("challengeProgress");

        mongoCollection.createIndex(new BasicDBObject("user", 1));
        mongoCollection.createIndex(new BasicDBObject("date", 1));
        mongoCollection.createIndex(new BasicDBObject("lastUsername", 1));
        
        Bukkit.getLogger().info("Creating indexes done for challenges.");

        // Dynamically load challenges
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

        // Programmatically load challenges with a repetitive objective
        final KitBasedChallengeData[] challengeDataArray = new KitBasedChallengeData[]{
                new KitBasedChallengeData("Diamond", new ItemStack[]{
                        new ItemStack(Material.DIAMOND),
                        new ItemStack(Material.DIAMOND_BOOTS),
                        new ItemStack(Material.DIAMOND_HELMET),
                        new ItemStack(Material.DIAMOND_CHESTPLATE),
                        new ItemStack(Material.DIAMOND_SWORD)
                }, new int[]{ 3, 5, 10, 25 }, new String[]{ "Killer", "Slayer", "Butcher", "Hunter" }),
                new KitBasedChallengeData("Archer", new ItemStack[]{
                        new ItemStack(Material.SUGAR),
                        new ItemStack(Material.LEATHER_BOOTS),
                        new ItemStack(Material.LEATHER_HELMET),
                        new ItemStack(Material.LEATHER_CHESTPLATE),
                        new ItemStack(Material.BOW)
                }, new int[]{ 3, 5, 10, 25 }, new String[]{ "Killer", "Slayer", "Butcher", "Hunter" }),
                new KitBasedChallengeData("Bard", new ItemStack[]{
                        new ItemStack(Material.GOLD_INGOT),
                        new ItemStack(Material.GOLD_BOOTS),
                        new ItemStack(Material.GOLD_HELMET),
                        new ItemStack(Material.GOLD_CHESTPLATE),
                        new ItemStack(Material.BLAZE_POWDER)
                }, new int[]{ 3, 5, 10, 25 }, new String[]{ "Killer", "Slayer", "Butcher", "Hunter" })
        };

        for (KitBasedChallengeData victim : challengeDataArray) {
            for (KitBasedChallengeData killer : challengeDataArray) {
                for (int i = 0; i < victim.getCounts().length; i++) {
                    registerKillBasedChallenge(new KillKitBasedChallenge(victim.getKitName() + " " + victim.getAggressionNames()[i], victim.getIcons()[i], victim.getCounts()[i], killer.getKitName(), victim.getKitName()));
                }
            }
        }

        lastDateAsInt = qLib.getInstance().runRedisCommand(redis -> {
            int currentDate = getDateAsInt();

            if (redis.exists("daily-challenges-updated")) {
                currentDate = Integer.valueOf(redis.get("daily-challenges-updated"));
            }

            return currentDate;
        });
        
        loadDailyChallenges();

        Bukkit.getScheduler().runTaskTimerAsynchronously(Foxtrot.getInstance(), () -> {
            List<UUID> toRemove = new ArrayList<>();
            List<UUID> toUpdate = new ArrayList<>();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!(pendingTokens.containsKey(player.getUniqueId()))) {
                    pendingTokens.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
                }
            }

            for (UUID uuid : pendingTokens.keySet()) {
                long time = pendingTokens.get(uuid);

                if (Bukkit.getPlayer(uuid) == null) {
                    toRemove.add(uuid);
                    continue;
                }

                if (System.currentTimeMillis() >= time) {
                    toUpdate.add(uuid);
                }
            }

            for (UUID uuid : toRemove) {
                pendingTokens.remove(uuid);
            }

            for (UUID uuid : toUpdate) {
                Player player = Bukkit.getPlayer(uuid);

                if (player != null) {
                    player.sendMessage(ChatColor.YELLOW + "You've received " + ChatColor.LIGHT_PURPLE + "1 token" + ChatColor.YELLOW + " for actively playing!");
                    player.sendMessage(ChatColor.YELLOW + "Trade your tokens at " + ChatColor.GOLD + "spawn" + ChatColor.YELLOW + " to receive a crate key!");
                    Foxtrot.getInstance().getTokensMap().setTokens(uuid, Foxtrot.getInstance().getTokensMap().getTokens(uuid)+1);
                    pendingTokens.put(uuid, System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
                }
            }
        },0, 20L);
        
        Bukkit.getScheduler().runTaskTimerAsynchronously(Foxtrot.getInstance(), () -> {
            int oldDate = lastDateAsInt;
            int newDate = getDateAsInt();
            
            if (oldDate != newDate) {
                challengeCounts.clear();
                pickNewChallenges();

                lastDateAsInt = newDate;

                saveLastDate();
                saveDailyChallenges();

                Bukkit.broadcastMessage(ChatColor.RED + "Challenge progress has been reset! Check out the two new daily challenges.");
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

    private void registerKillBasedChallenge(KillBasedChallenge challenge) {
        killBasedChallenges.put(challenge.getName(), challenge);
        allChallenges.put(challenge.getName(), challenge);
    }
    
    private void loadDailyChallenges() {
        qLib.getInstance().runRedisCommand(redis -> {
            for (int i = 0; i < 3; i++) {
                if (redis.exists("daily-challenges:" + i)) {
                    Challenge challenge = allChallenges.get(redis.get("daily-challenges:" + i));

                    if (challenge != null) {
                        dailyChallenges.add(challenge);
                    }
                }
            }

            if (dailyChallenges.size() < 3) {
                pickNewChallenges();
            }

            return null;
        });
    }
    
    private void pickNewChallenges() {
        dailyChallenges.clear();

        List<Challenge> selectedDailyChallenges = new ArrayList<>();
        List<Challenge> allChallenges = Lists.newArrayList(ChallengeHandler.allChallenges.values());

        // Get 3 daily challenges or add all if there are less than 3 challenges
        if (allChallenges.size() > 3) {
            while (selectedDailyChallenges.size() < 3) {
                Challenge random = allChallenges.get(qLib.RANDOM.nextInt(allChallenges.size()));

                if (!selectedDailyChallenges.contains(random)) {
                    selectedDailyChallenges.add(random);
                }
            }
        } else {
            selectedDailyChallenges.addAll(allChallenges);
        }

        dailyChallenges = selectedDailyChallenges;

        for (Challenge challenge : selectedDailyChallenges) {
            System.out.println("Selected challenge `" + challenge.getName() + "`");
        }
    }
    
    private void saveDailyChallenges() {
        qLib.getInstance().runRedisCommand(redis -> {
            for (int i = 0; i < 3; i++) {
                redis.set("daily-challenges:" + i, dailyChallenges.get(i).getName());
            }

            return null;
        });
    }

    @Command(names = {"tokens"}, permission = "")
    public static void tokens(Player sender) {
        sender.sendMessage(ChatColor.YELLOW + "You have " + ChatColor.GOLD + Foxtrot.getInstance().getTokensMap().getTokens(sender.getUniqueId()) + ChatColor.YELLOW + " tokens.");
    }

    @Command(names = {"challenge progress", "challenges"}, permission = "")
    public static void progress(Player sender) {
        new ChallengesMenu().openMenu(sender);
    }
    
    @Command(names = {"challenge picknew"}, permission = "op")
    public static void newchallenges(CommandSender sender) {
        Foxtrot.getInstance().getChallengeHandler().pickNewChallenges();
        Foxtrot.getInstance().getChallengeHandler().saveDailyChallenges();

        sender.sendMessage(ChatColor.GREEN + "The challenges have been refreshed!");
    }
        
    public void saveLastDate() {
        qLib.getInstance().runRedisCommand(redis -> {
            redis.set("daily-challenges-updated", Integer.toString(lastDateAsInt));
            return null;
        });
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onLogin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(Foxtrot.getInstance(), () -> {
            loadProgress(event.getPlayer());
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageEvent event) {
       if (event.getEntity() instanceof Villager) {
           Villager villager = (Villager) event.getEntity();

           if (villager.getCustomName() != null && villager.getCustomName().contains(ChatColor.COLOR_CHAR + "") && villager.getCustomName().contains("Token")) {
               event.setCancelled(true);
           }

       }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Villager) {
            Villager villager = (Villager) event.getRightClicked();

            if (villager.getCustomName() != null && villager.getCustomName().contains(ChatColor.COLOR_CHAR + "") && villager.getCustomName().contains("Token")) {
                event.setCancelled(true);
                int tokens = Foxtrot.getInstance().getTokensMap().getTokens(event.getPlayer().getUniqueId());

                if (tokens <= 0) {
                    event.getPlayer().sendMessage(ChatColor.RED + "You don't have any tokens to claim.");
                    return;
                }

                if (tokens < 3) {
                    event.getPlayer().sendMessage(ChatColor.RED + "You need at least 3 tokens to exchange them for a key.");
                    return;
                }

                int keys = 0;
                while (tokens >= 3) {
                    tokens -=3;
                    keys++;
                }

                Foxtrot.getInstance().getTokensMap().setTokens(event.getPlayer().getUniqueId(), Foxtrot.getInstance().getTokensMap().getTokens(event.getPlayer().getUniqueId()) % 3);
                event.getPlayer().sendMessage(ChatColor.GREEN + "You've exchanged your tokens for " + ChatColor.DARK_GREEN + keys + ChatColor.GREEN + " crate keys.");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + event.getPlayer().getName() + " token " + keys);
            }
        }
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
        return dailyChallenges.contains(challenge) && getProgress(player, challenge) < challenge.getCountToQualify();
    }
    
    public int getProgress(Player player, Challenge challenge) {
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

    private boolean hasCompletedAllChallenges(Player player) {
        for (Challenge challenge : dailyChallenges) {
            if (challenge.getCountToQualify() != getProgress(player, challenge)) {
                return false;
            }
        }

        return true;
    }
    
    private void completedChallenge(Player player, Challenge challenge) {
        FancyMessage message = new FancyMessage("Challenge Completed: ").color(org.bukkit.ChatColor.GREEN).then();
        message.text(challenge.getName()).color(org.bukkit.ChatColor.GRAY);
        message.command("challenges");
        message.send(player);

        Bukkit.getLogger().info(player.getName() + " completed challenge " + challenge.getName());
        
        if (hasCompletedAllChallenges(player)) {
            Bukkit.getLogger().info("Player has completed both challenges. Awarding key");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " Challenge 1");
        }
    }

}
