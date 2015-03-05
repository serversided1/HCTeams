package net.frozenorb.foxtrot.server;

import com.google.common.collect.ImmutableSet;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.persist.maps.PvPTimerMap;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.InvUtils;
import net.frozenorb.foxtrot.util.UUIDUtils;
import net.frozenorb.mBasic.Basic;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("deprecation")
public class ServerHandler {

    public static int WARZONE_RADIUS = 1000;

    // NEXT MAP //
    // http://minecraft.gamepedia.com/Potion#Data_value_table
    public static final Set<Integer> DISALLOWED_POTIONS = ImmutableSet.of(
            8193, 8225, 8257, 16385, 16417, 16449, // Regeneration Potions
            8200, 8232, 8264, 16392, 16424, 16456, // Weakness Potions
            8201, 8233, 8265, 16393, 16425, 16457, // Strength Potions
            8204, 8236, 8268, 16396, 16428, 16460, // Harming Potions
            8238, 8270, 16430, 16398, 8238, // Invisibility Potions
            8228, 8260, 16420, 16452, // Poison Potions
            8234, 8266, 16426, 16458 // Slowness Potions
    );

    @Getter private static Map<String, Integer> tasks = new HashMap<>();

    @Getter private Map<String, String> customPrefixes = new HashMap<>();
    @Getter private Set<String> highRollers = new HashSet<>();

    @Getter @Setter private boolean EOTW = false;
    @Getter @Setter private boolean PreEOTW = false;

    public ServerHandler() {
        loadPrefixes();

        try {
            File f = new File("highRollers.json");

            if (!f.exists()) {
                f.createNewFile();
            }

            BasicDBObject dbo = (BasicDBObject) JSON.parse(FileUtils.readFileToString(f));

            if (dbo != null) {
                for (Object o : (BasicDBList) dbo.get("names")) {
                    highRollers.add((String) o);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        new BukkitRunnable() {

            public void run() {
                StringBuilder messageBuilder = new StringBuilder();

                for (String highRoller : highRollers) {
                    messageBuilder.append(ChatColor.DARK_PURPLE).append(highRoller).append(ChatColor.GOLD).append(", ");
                }

                if (messageBuilder.length() > 2) {
                    messageBuilder.setLength(messageBuilder.length() - 2);
                    FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "HCTeams HighRollers: " + messageBuilder.toString());
                }
            }

        }.runTaskTimerAsynchronously(FoxtrotPlugin.getInstance(), 3000L, 6000L);
    }

    public void loadPrefixes() {
        try {
            File f = new File("customPrefixes.json");

            if (!f.exists()) {
                f.createNewFile();
            }

            BasicDBObject dbo = (BasicDBObject) JSON.parse(FileUtils.readFileToString(f));

            if (dbo != null) {
                customPrefixes.clear();

                for (Map.Entry<String, Object> o : ((BasicDBObject) dbo.get("prefixes")).entrySet()) {
                    customPrefixes.put(o.getKey(), ChatColor.translateAlternateColorCodes('&', o.getValue().toString()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            File f = new File("highRollers.json");

            if (!f.exists()) {
                f.createNewFile();
            }

            BasicDBObject dbo = new BasicDBObject();
            BasicDBList list = new BasicDBList();

            for (String n : highRollers) {
                list.add(n);
            }

            dbo.put("names", list);
            FileUtils.write(f, new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(dbo.toString())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            File f = new File("customPrefixes.json");

            if (!f.exists()) {
                f.createNewFile();
            }

            BasicDBObject dbo = new BasicDBObject();
            dbo.put("prefixes", customPrefixes);
            FileUtils.write(f, new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(dbo.toString())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isWarzone(Location loc) {
        if (loc.getWorld().getEnvironment() != Environment.NORMAL) {
            return (false);
        }

        return (Math.abs(loc.getBlockX()) <= WARZONE_RADIUS && Math.abs(loc.getBlockZ()) <= WARZONE_RADIUS);
    }

    public void startLogoutSequence(final Player player) {
        player.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Logging out... " +ChatColor.YELLOW + "Please wait" + ChatColor.RED+ " 30" + ChatColor.YELLOW + " seconds.");

        BukkitTask taskid = new BukkitRunnable() {

            int seconds = 30;

            @Override
            public void run() {
                seconds--;
                player.sendMessage(ChatColor.RED + "" + seconds + "§e seconds...");

                if (seconds == 0) {
                    if (tasks.containsKey(player.getName())) {
                        tasks.remove(player.getName());
                        player.setMetadata("loggedout", new FixedMetadataValue(FoxtrotPlugin.getInstance(), true));
                        player.kickPlayer("§cYou have been safely logged out of the server!");
                        cancel();
                    }
                }

            }
        }.runTaskTimer(FoxtrotPlugin.getInstance(), 20L, 20L);

        if (tasks.containsKey(player.getName())) {
            FoxtrotPlugin.getInstance().getServer().getScheduler().cancelTask(tasks.remove(player.getName()));
        }

        tasks.put(player.getName(), taskid.getTaskId());
    }

    public RegionData getRegion(Team ownerTo, Location location) {
        if (ownerTo != null && ownerTo.getOwner() == null) {
            if (ownerTo.hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
                return (new RegionData(RegionType.SPAWN, ownerTo));
            } else if (ownerTo.hasDTRBitmask(DTRBitmask.KOTH)) {
                return (new RegionData(RegionType.KOTH, ownerTo));
            } else if (ownerTo.hasDTRBitmask(DTRBitmask.CITADEL)) {
                return (new RegionData(RegionType.CITADEL, ownerTo));
            }
        }

        if (ownerTo != null) {
            return (new RegionData(RegionType.CLAIMED_LAND, ownerTo));
        } else if (isWarzone(location)) {
            return (new RegionData(RegionType.WARZONE, null));
        }

        return (new RegionData(RegionType.WILDNERNESS, null));
    }

    public void beginHQWarp(final Player player, final Team team, final int warmup) {
        Team inClaim = LandBoard.getInstance().getTeam(player.getLocation());

        if (inClaim != null) {
            if (inClaim.getOwner() != null && !inClaim.isMember(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You may not go to your team headquarters from an enemy's claim! Use '/team stuck' first.");
                return;
            }

            if (inClaim.getOwner() == null && (inClaim.hasDTRBitmask(DTRBitmask.KOTH) || inClaim.hasDTRBitmask(DTRBitmask.CITADEL))) {
                player.sendMessage(ChatColor.RED + "You may not go to your team headquarters from inside of events!");
                return;
            }
        }

        if (SpawnTagHandler.isTagged(player)) {
            player.sendMessage(ChatColor.RED + "You may not go to your team headquarters while spawn tagged!");
            return;
        }

        player.sendMessage(ChatColor.YELLOW + "Teleporting to your team's HQ in " + ChatColor.LIGHT_PURPLE + warmup + " seconds" + ChatColor.YELLOW + "... Stay still and do not take damage.");

        new BukkitRunnable() {

            int time = warmup;
            Location startLocation = player.getLocation();
            double startHealth = player.getHealth();

            @Override
            public void run() {
                time--;

                if (!player.getLocation().getWorld().equals(startLocation.getWorld()) || player.getLocation().distanceSquared(startLocation) >= 0.1 || player.getHealth() < startHealth) {
                    player.sendMessage(ChatColor.YELLOW + "Teleport cancelled.");
                    cancel();
                    return;
                }

                // Reset their previous health, so players can't start on 1/2 a heart, splash, and then be able to take damage before warping.
                startHealth = player.getHealth();

                if (time == 0) {
                    // Remove their PvP timer.
                    if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId()) || FoxtrotPlugin.getInstance().getPvPTimerMap().getTimer(player.getUniqueId()) == PvPTimerMap.PENDING_USE) {
                        FoxtrotPlugin.getInstance().getPvPTimerMap().removeTimer(player.getUniqueId());
                    }

                    player.sendMessage(ChatColor.YELLOW + "Warping to " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + "'s HQ.");
                    player.teleport(team.getHQ());
                    cancel();
                }
            }

        }.runTaskTimer(FoxtrotPlugin.getInstance(), 20L, 20L);
    }

    public boolean isUnclaimed(Location loc) {
        return (LandBoard.getInstance().getClaim(loc) == null && !isWarzone(loc));
    }

    public boolean isAdminOverride(Player player) {
        return (player.getGameMode() == GameMode.CREATIVE);
    }

    public Location getSpawnLocation() {
        return (FoxtrotPlugin.getInstance().getServer().getWorld("world").getSpawnLocation().add(new Vector(0.5, 1, 0.5)));
    }

    public boolean isUnclaimedOrRaidable(Location loc) {
        Team owner = LandBoard.getInstance().getTeam(loc);
        return (owner == null || owner.isRaidable());
    }

    public double getDTRLoss(Player player) {
        return (getDTRLoss(player.getLocation()));
    }

    public double getDTRLoss(Location location) {
        double dtrLoss = 1.00D;

        if (FoxtrotPlugin.getInstance().getMapHandler().isKitMap()) {
            dtrLoss = Math.min(dtrLoss, 0.01D);
        }

        if (FoxtrotPlugin.getInstance().getConquestHandler().getGame() != null) {
            dtrLoss = Math.min(dtrLoss, 0.50D);
        }

        Team ownerTo = LandBoard.getInstance().getTeam(location);

        if (ownerTo != null) {
            if (ownerTo.hasDTRBitmask(DTRBitmask.QUARTER_DTR_LOSS)) {
                dtrLoss = Math.min(dtrLoss, 0.25D);
            } else if (ownerTo.hasDTRBitmask(DTRBitmask.REDUCED_DTR_LOSS)) {
                dtrLoss = Math.min(dtrLoss, 0.75D);
            }
        }

        return (dtrLoss);
    }

    public long getDeathban(Player player) {
        return (getDeathban(player.getName(), player.getLocation()));
    }

    public long getDeathban(String playerName, Location location) {
        // Things we already know and can easily eliminate.
        if (isPreEOTW()) {
            return (TimeUnit.DAYS.toSeconds(1000));
        } else if (FoxtrotPlugin.getInstance().getMapHandler().isKitMap()) {
            return (TimeUnit.SECONDS.toSeconds(10));
        }

        Team ownerTo = LandBoard.getInstance().getTeam(location);
        Player player = FoxtrotPlugin.getInstance().getServer().getPlayerExact(playerName); // Used in various checks down below.

        // Check DTR flags, which will also take priority over playtime.
        if (ownerTo != null && ownerTo.getOwner() == null) {
            if (ownerTo.hasDTRBitmask(DTRBitmask.FIVE_MINUTE_DEATHBAN)) {
                return (TimeUnit.MINUTES.toSeconds(5));
            } else if (ownerTo.hasDTRBitmask(DTRBitmask.FIFTEEN_MINUTE_DEATHBAN)) {
                return (TimeUnit.MINUTES.toSeconds(15));
            }
        }

        // The default max.
        long max = TimeUnit.HOURS.toSeconds(3);

        // 1 to 2 hours based on the player's rank.
        if (player != null) {
            if (player.hasPermission("PRO")) {
                max = TimeUnit.HOURS.toSeconds(1);
            } else if (player.hasPermission("VIP")) {
                max = TimeUnit.HOURS.toSeconds(2);
            }
        }

        // Actually calculate their ban.
        UUID playerUUID = UUIDUtils.uuid(playerName);
        long ban = FoxtrotPlugin.getInstance().getPlaytimeMap().getPlaytime(playerUUID);

        if (player != null && FoxtrotPlugin.getInstance().getPlaytimeMap().hasPlayed(playerUUID)) {
            ban += FoxtrotPlugin.getInstance().getPlaytimeMap().getCurrentSession(playerUUID) / 1000L;
        }

        return (Math.min(max, ban));
    }

    public void disablePlayerAttacking(final Player player, int seconds) {
        if (seconds == 10) {
            player.sendMessage(ChatColor.GRAY + "You cannot attack for " + seconds + " seconds.");
        }

        final Listener listener = new Listener() {

            @EventHandler
            public void onPlayerDamage(EntityDamageByEntityEvent event) {
                if (event.getDamager() instanceof Player) {
                    if (((Player) event.getDamager()).getName().equals(player.getName())) {
                        event.setCancelled(true);
                    }
                }
            }

        };

        FoxtrotPlugin.getInstance().getServer().getPluginManager().registerEvents(listener, FoxtrotPlugin.getInstance());
        FoxtrotPlugin.getInstance().getServer().getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), () -> HandlerList.unregisterAll(listener), seconds * 20L);
    }

    public boolean isSpawnBufferZone(Location loc) {
        if (loc.getWorld().getEnvironment() != Environment.NORMAL){
            return (false);
        }

        int radius = 300;
        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        return ((x < radius && x > -radius) && (z < radius && z > -radius));
    }

    public boolean isNetherBufferZone(Location loc) {
        if (loc.getWorld().getEnvironment() != Environment.NETHER){
            return (false);
        }

        int radius = 75;
        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        return ((x < radius && x > -radius) && (z < radius && z > -radius));
    }

    public void handleShopSign(Sign sign, Player player) {
        ItemStack itemStack = (sign.getLine(2).contains("Crowbar") ? InvUtils.CROWBAR : Basic.get().getItemDb().get(sign.getLine(2).toLowerCase().replace(" ", "")));

        if (itemStack == null) {
            System.err.println(sign.getLine(2).toLowerCase().replace(" ", ""));
            return;
        }

        if (sign.getLine(0).toLowerCase().contains("buy")) {
            int price;
            int amount;

            try {
                price = Integer.parseInt(sign.getLine(3).replace("$", "").replace(",", ""));
                amount = Integer.parseInt(sign.getLine(1));
            } catch (NumberFormatException e) {
                return;
            }

            if (Basic.get().getEconomyManager().getBalance(player.getName()) >= price) {
                if (player.getInventory().firstEmpty() != -1) {
                    Basic.get().getEconomyManager().withdrawPlayer(player.getName(), price);

                    itemStack.setAmount(amount);
                    player.getInventory().addItem(itemStack);
                    player.updateInventory();

                    showSignPacket(player, sign,
                            "§aBOUGHT§r " + amount,
                            "for §a$" + NumberFormat.getNumberInstance(Locale.US).format(price),
                            "New Balance:",
                            "§a$" + NumberFormat.getNumberInstance(Locale.US).format((int) Basic.get().getEconomyManager().getBalance(player.getName()))
                    );
                } else {
                    showSignPacket(player, sign,
                            "§c§lError!",
                            "",
                            "§cNo space",
                            "§cin inventory!"
                    );
                }
            } else {
                showSignPacket(player, sign,
                        "§cInsufficient",
                        "§cfunds for",
                        sign.getLine(2),
                        sign.getLine(3)
                );
            }
        } else if (sign.getLine(0).toLowerCase().contains("sell")) {
            double pricePerItem;
            int amount;

            try {
                int price = Integer.parseInt(sign.getLine(3).replace("$", "").replace(",", ""));
                amount = Integer.parseInt(sign.getLine(1));

                pricePerItem = (float) price / (float) amount;
            } catch (NumberFormatException e) {
                return;
            }

            int amountInInventory = Math.min(amount, countItems(player, itemStack.getType(), (int) itemStack.getDurability()));

            if (amountInInventory == 0) {
                showSignPacket(player, sign,
                        "§cYou do not",
                        "§chave any",
                        sign.getLine(2),
                        "§con you!"
                );
            } else {
                int totalPrice = (int) (amountInInventory * pricePerItem);

                removeItem(player, itemStack, amountInInventory);
                player.updateInventory();

                Basic.get().getEconomyManager().depositPlayer(player.getName(), totalPrice);

                showSignPacket(player, sign,
                        "§aSOLD§r " + amountInInventory,
                        "for §a$" + NumberFormat.getNumberInstance(Locale.US).format(totalPrice),
                        "New Balance:",
                        "§a$" + NumberFormat.getNumberInstance(Locale.US).format((int) Basic.get().getEconomyManager().getBalance(player.getName()))
                );
            }
        }
    }

    public void handleKitSign(Sign sign, Player player) {
        String kit = ChatColor.stripColor(sign.getLine(1));

        if (kit.equalsIgnoreCase("Fishing")){
            int uses = FoxtrotPlugin.getInstance().getFishingKitMap().getUses(player.getUniqueId());

            if (uses == 3){
                showSignPacket(player, sign, "§aFishing Kit:", "", "§cAlready used", "§c3/3 times!");
            } else {
                ItemStack rod = new ItemStack(Material.FISHING_ROD);

                rod.addEnchantment(Enchantment.LURE, 2);
                player.getInventory().addItem(rod);
                player.updateInventory();
                player.sendMessage(ChatColor.GOLD + "Equipped the " + ChatColor.WHITE + "Fishing" + ChatColor.GOLD + " kit!");
                FoxtrotPlugin.getInstance().getFishingKitMap().setUses(player.getUniqueId(), uses + 1);
                showSignPacket(player, sign, "§aFishing Kit:", "§bEquipped!", "", "§dUses: §e" + (uses) + "/3");
            }
        }
    }

    public void removeItem(Player p, ItemStack it, int amount) {
        boolean specialDamage = it.getType().getMaxDurability() == (short) 0;

        for (int a = 0; a < amount; a++) {
            for (ItemStack i : p.getInventory()) {
                if (i != null) {
                    if (i.getType() == it.getType() && (!specialDamage || it.getDurability() == i.getDurability())) {
                        if (i.getAmount() == 1) {
                            p.getInventory().clear(p.getInventory().first(i));
                            break;
                        } else {
                            i.setAmount(i.getAmount() - 1);
                            break;
                        }
                    }
                }
            }
        }

    }

    public ItemStack generateDeathSign(String killed, String killer) {
        ItemStack deathsign = new ItemStack(Material.SIGN);
        ItemMeta meta = deathsign.getItemMeta();

        ArrayList<String> lore = new ArrayList<>();

        lore.add("§4" + killed);
        lore.add("§eSlain By:");
        lore.add("§a" + killer);

        DateFormat sdf = new SimpleDateFormat("M/d HH:mm:ss");

        lore.add(sdf.format(new Date()).replace(" AM", "").replace(" PM", ""));

        meta.setLore(lore);
        meta.setDisplayName("§dDeath Sign");
        deathsign.setItemMeta(meta);

        return (deathsign);
    }

    public ItemStack generateKOTHSign(String koth, String capper) {
        ItemStack kothsign = new ItemStack(Material.SIGN);
        ItemMeta meta = kothsign.getItemMeta();

        ArrayList<String> lore = new ArrayList<>();

        lore.add("§9" + koth);
        lore.add("§eCaptured By:");
        lore.add("§a" + capper);

        DateFormat sdf = new SimpleDateFormat("M/d HH:mm:ss");

        lore.add(sdf.format(new Date()).replace(" AM", "").replace(" PM", ""));

        meta.setLore(lore);
        meta.setDisplayName("§dKOTH Capture Sign");
        kothsign.setItemMeta(meta);

        return (kothsign);
    }

    private HashMap<Sign, BukkitRunnable> showSignTasks = new HashMap<>();

    public void showSignPacket(Player player, final Sign sign, String... lines) {
        player.sendSignChange(sign.getLocation(), lines);

        if (showSignTasks.containsKey(sign)) {
            showSignTasks.remove(sign).cancel();
        }

        BukkitRunnable br = new BukkitRunnable() {

            @Override
            public void run(){
                sign.update();
                showSignTasks.remove(sign);
            }

        };

        showSignTasks.put(sign, br);
        br.runTaskLater(FoxtrotPlugin.getInstance(), 90L);
    }

    public int countItems(Player player, Material material, int damageValue) {
        PlayerInventory inventory = player.getInventory();
        ItemStack[] items = inventory.getContents();
        int amount = 0;

        for (ItemStack item : items) {
            if (item != null) {
                boolean specialDamage = material.getMaxDurability() == (short) 0;

                if (item.getType() != null && item.getType() == material && (!specialDamage || item.getDurability() == (short) damageValue)) {
                    amount += item.getAmount();
                }
            }
        }

        return (amount);
    }

}