package net.frozenorb.foxtrot.server;

import com.google.common.collect.Sets;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.Utilities.DataSystem.Regioning.CuboidRegion;
import net.frozenorb.Utilities.DataSystem.Regioning.RegionManager;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.commands.FreezeCommand;
import net.frozenorb.foxtrot.factionactiontracker.FactionActionTracker;
import net.frozenorb.foxtrot.jedis.persist.PvPTimerMap;
import net.frozenorb.foxtrot.listener.EnderpearlListener;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.TeamHandler;
import net.frozenorb.foxtrot.util.InvUtils;
import net.frozenorb.mBasic.Basic;
import net.minecraft.server.v1_7_R3.PacketPlayOutUpdateSign;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
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
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("deprecation")
public class ServerHandler {

	public static int WARZONE_RADIUS = 1000;

    // NEXT MAP //
    // http://minecraft.gamepedia.com/Potion#Data_value_table
    public static final Set<Integer> DISALLOWED_POTIONS = Sets.newHashSet(
        8193, 8225, 8257, 16385, 16417, 16449, // Regeneration Potions
        8200, 8232, 8264, 16392, 16424, 16456, // Weakness Potions
        8201, 8233, 8265, 16393, 16425, 16457, // Strength Potions
        8204, 8236, 8268, 16396, 16428, 16460, // Harming Potions
        8238, 8270, 16430, 16462, 16398, // Invisibility Potions
        8228, 8260, 16420, 16452, // Poison Potions
        8234, 8266, 16426, 16458 // Slowness Potions
    );

	@Getter private static HashMap<String, Integer> tasks = new HashMap<String, Integer>();

	@Getter private HashSet<String> usedNames = new HashSet<String>();
    @Getter private HashSet<String> highRollers = new HashSet<String>();

    @Getter @Setter private boolean EOTW = false;
    @Getter @Setter private boolean PreEOTW = false;

	public ServerHandler() {
		try {
			File f = new File("usedNames.json");

			if (!f.exists()) {
				f.createNewFile();
			}

			BasicDBObject dbo = (BasicDBObject) JSON.parse(FileUtils.readFileToString(f));

			if (dbo != null) {
				for (Object o : (BasicDBList) dbo.get("names")) {
					usedNames.add((String) o);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

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
                StringBuilder highRollers = new StringBuilder();

                for (String highRoller : FoxtrotPlugin.getInstance().getServerHandler().getHighRollers()) {
                    highRollers.append(ChatColor.DARK_PURPLE).append(highRoller).append(ChatColor.GOLD).append(", ");
                }

                if (highRollers.length() > 2) {
                    highRollers.setLength(highRollers.length() - 2);
                }

                FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "HCTeams HighRollers: " + highRollers.toString());
            }

        }.runTaskTimer(FoxtrotPlugin.getInstance(), 20L, 20L * 60 * 5);
	}

	public void save() {
		try {
			File f = new File("usedNames.json");

			if (!f.exists()) {
				f.createNewFile();
			}

			BasicDBObject dbo = new BasicDBObject();
			BasicDBList list = new BasicDBList();

			for (String n : usedNames) {
				list.add(n);
			}

			dbo.put("names", list);
			FileUtils.write(f, new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(dbo.toString())));
		} catch (IOException e) {
			e.printStackTrace();
		}

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
	}

	public boolean isBannedPotion(int value) {
		for (int i : DISALLOWED_POTIONS) {
			if (i == value) {
				return (true);
			}
		}

		return (false);
	}

	public boolean isWarzone(Location loc) {
		if (loc.getWorld().getEnvironment() != Environment.NORMAL) {
			return (false);
		}

		int x = loc.getBlockX();
		int z = loc.getBlockZ();

		return ((x < WARZONE_RADIUS && x > -WARZONE_RADIUS) && (z < WARZONE_RADIUS && z > -WARZONE_RADIUS));
	}

	public void startLogoutSequence(final Player player) {
		player.sendMessage(ChatColor.YELLOW + "§lLogging out... §ePlease wait§c 30§e seconds.");
		final AtomicInteger seconds = new AtomicInteger(30);

		BukkitTask taskid = new BukkitRunnable() {

			@Override
			public void run() {

				seconds.set(seconds.get() - 1);
				player.sendMessage(ChatColor.RED + "" + seconds.get() + "§e seconds...");

				if (seconds.get() == 0) {
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
			Bukkit.getScheduler().cancelTask(tasks.remove(player.getName()));
		}

		tasks.put(player.getName(), taskid.getTaskId());
	}

	public RegionData<?> getRegion(Location loc, Player p) {
		if (isOverworldSpawn(loc)) {
			return new RegionData<Object>(loc, Region.SPAWN, null);
		} else if (isNetherSpawn(loc)){
            return new RegionData<Object>(loc, Region.SPAWN_NETHER, null);
        } else if (isEndSpawn(loc)){
            return new RegionData<Object>(loc, Region.SPAWN_END, null);
        } else if (isEndExit(loc)){
            return new RegionData<Object>(loc, Region.EXIT_END, null);
        } else if (isDiamondMountain(loc)) {
			return new RegionData<Object>(loc, Region.DIAMOND_MOUNTAIN, null);
		}

        //Road
        String road = getRoad(loc);

        if (!(road.equals(""))) {
            Region reg = null;

            if(road.contains("north")){
                reg = Region.ROAD_NORTH;
            } else if(road.contains("east")){
                reg = Region.ROAD_EAST;
            } else if(road.contains("south")){
                reg = Region.ROAD_SOUTH;
            } else if(road.contains("west")){
                reg = Region.ROAD_WEST;
            }

            if (reg != null){
                return (new RegionData<Object>(loc, reg, null));
            }
        }

		if (isUnclaimed(loc)) {
			return (new RegionData<Object>(loc, Region.WILDNERNESS, null));
		}

		Team ownerTo = FoxtrotPlugin.getInstance().getTeamHandler().getOwner(loc);

        if (ownerTo != null) {
            // If we're a 50DTR faction... (KOTH)
            if (ownerTo.getDtr() == 50D || ownerTo.getDtr() == 100D) {
                return (new RegionData<String>(loc, Region.KOTH_ARENA, ownerTo.getFriendlyName()));
            }

            return (new RegionData<Team>(loc, Region.CLAIMED_LAND, ownerTo));
        } else if (isWarzone(loc)) {
            return (new RegionData<Object>(loc, Region.WARZONE, null));
        }

        // This will never happen.
        return (new RegionData<Object>(loc, Region.WILDNERNESS, null));
	}

	public void beginWarp(final Player player, final Team team, int price) {
		if (player.getGameMode() == GameMode.CREATIVE || player.hasMetadata("invisible") || (!FoxtrotPlugin.getInstance().getServerHandler().isEOTW() && isGlobalSpawn(player.getLocation()))) {
            if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(player.getName()) || FoxtrotPlugin.getInstance().getPvPTimerMap().getTimer(player.getName()) == PvPTimerMap.PENDING_USE) {
                FoxtrotPlugin.getInstance().getPvPTimerMap().removeTimer(player.getName());
            }

            FactionActionTracker.logAction(team, "actions", "HQ Teleport: " + player.getName());
			player.teleport(team.getHq());
			return;
		}

        if(FreezeCommand.isFrozen(player)){
            player.sendMessage(ChatColor.RED + "You cannot teleport while frozen!");
            return;
        }

		TeamHandler tm = FoxtrotPlugin.getInstance().getTeamHandler();
        double bal = tm.getPlayerTeam(player.getName()).getBalance();

        if (bal < price) {
            player.sendMessage(ChatColor.RED + "This costs §e$" + price + "§c while your team has only §e$" + bal + "§c!");
            return;
        }

        // Disallow warping while on enderpearl cooldown.
		if (EnderpearlListener.getEnderpearlCooldown().containsKey(player.getName()) && EnderpearlListener.getEnderpearlCooldown().get(player.getName()) > System.currentTimeMillis()) {
			player.sendMessage(ChatColor.RED + "You cannot warp while your enderpearl cooldown is active!");
			return;
		}

		boolean enemyWithinRange = false;

		for (Entity e : player.getNearbyEntities(30, 256, 30)) {
			if (e instanceof Player) {
				Player other = (Player) e;

				if (other.hasMetadata("invisible") || FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(other.getName())) {
					continue;
				}

				if (tm.getPlayerTeam(other.getName()) != tm.getPlayerTeam(player.getName())) {
					enemyWithinRange = true;
                    break;
				}
			}
		}

		if (enemyWithinRange) {
			player.sendMessage(ChatColor.RED + "You cannot warp because an enemy is nearby!");
			return;
		}

		if (player.getHealth() <= player.getMaxHealth() - 1D) {
			player.sendMessage(ChatColor.RED + "You cannot warp because you do not have full health!");
			return;
		}

		if (player.getFoodLevel() != 20) {
			player.sendMessage(ChatColor.RED + "You cannot warp because you do not have full hunger!");
			return;
		}

        // Remove their PvP timer.
        if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(player.getName()) || FoxtrotPlugin.getInstance().getPvPTimerMap().getTimer(player.getName()) == PvPTimerMap.PENDING_USE) {
            FoxtrotPlugin.getInstance().getPvPTimerMap().removeTimer(player.getName());
        }

        player.sendMessage(ChatColor.YELLOW + "§d$" + price + " §ehas been deducted from your team balance.");
        tm.getPlayerTeam(player.getName()).setBalance(tm.getPlayerTeam(player.getName()).getBalance() - price);

        FactionActionTracker.logAction(team, "actions", "HQ Teleport: " + player.getName());
		player.teleport(team.getHq());
	}

	public boolean isUnclaimed(Location loc) {
		return (!FoxtrotPlugin.getInstance().getTeamHandler().isTaken(loc) && !isWarzone(loc) && getRoad(loc).equals(""));
	}

	public boolean isAdminOverride(Player p) {
		return p.getGameMode() == GameMode.CREATIVE;
	}

	public Location getSpawnLocation() {
		return (Bukkit.getWorld("world").getSpawnLocation().add(new Vector(0.5, 1, 0.5)));
	}

    public boolean isGlobalSpawn(Location loc) {
        return (isOverworldSpawn(loc) || isNetherSpawn(loc) || isEndSpawn(loc) || isEndExit(loc));
    }

    public boolean isOverworldSpawn(Location loc) {
        return (RegionManager.get().hasTag(loc, "overworldspawn"));
    }

    public boolean isNetherSpawn(Location loc) {
        return (RegionManager.get().hasTag(loc, "netherspawn"));
    }

    public boolean isEndSpawn(Location loc) {
        return (RegionManager.get().hasTag(loc, "endspawn"));
    }

    public boolean isEndExit(Location loc) {
        return (RegionManager.get().hasTag(loc, "endexit"));
    }

	public boolean isClaimedAndRaidable(Location loc) {
		Team owner = FoxtrotPlugin.getInstance().getTeamHandler().getOwner(loc);
		return owner != null && owner.isRaidable();
	}

    public float getDTRLossAt(Location loc) {
        // MAP 0.9
        return (0.1F);

        /*Team ownerTo = FoxtrotPlugin.getInstance().getTeamHandler().getOwner(loc);

        if (ownerTo != null) {
            if (ownerTo.getDtr() == 100D) {
                return (0.5F);
            }
        }

        return (1F);*/
    }

    public int getDeathBanAt(String playerName, Location loc) {
        // MAP 0.9
        return ((int) TimeUnit.SECONDS.toSeconds(5));

        /*if (isPreEOTW()) {
            return ((int) TimeUnit.DAYS.toSeconds(1000));
        }

        Team ownerTo = FoxtrotPlugin.getInstance().getTeamHandler().getOwner(loc);

        if (ownerTo != null) {
            if (ownerTo.getDtr() == 50D) {
                return ((int) TimeUnit.MINUTES.toSeconds(5));
            } else if (ownerTo.getDtr() == 100D) {
                return ((int) TimeUnit.MINUTES.toSeconds(15));
            }
        }

        PlaytimeMap playtime = FoxtrotPlugin.getInstance().getPlaytimeMap();
        // MAP 0.9
        long max = TimeUnit.MINUTES.toSeconds(60);
        long ban;

        if (FoxtrotPlugin.getInstance().getServer().getPlayerExact(playerName) != null && playtime.contains(playerName)){
            ban = playtime.getPlaytime(playerName) + (playtime.getCurrentSession(playerName) / 1000L);
        } else {
            ban = playtime.getCurrentSession(playerName) / 1000L;
        }

        return ((int) Math.min(max, ban));*/
    }

	public void disablePlayerAttacking(final Player p, int seconds) {
		if (seconds == 10) {
			p.sendMessage(ChatColor.GRAY + "You cannot attack for " + seconds + " seconds.");
		}

		final Listener l = new Listener() {
			@EventHandler
			public void onPlayerDamage(EntityDamageByEntityEvent e) {
				if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
					if (((Player) e.getDamager()).getName().equals(p.getName())) {
						e.setCancelled(true);
					}
				}

			}
		};

		Bukkit.getPluginManager().registerEvents(l, FoxtrotPlugin.getInstance());
		Bukkit.getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), new Runnable() {
			public void run() {
				HandlerList.unregisterAll(l);
			}
		}, seconds * 20);
	}

	public boolean isKOTHArena(Location loc) {
        Team ownerTo = FoxtrotPlugin.getInstance().getTeamHandler().getOwner(loc);

        if (ownerTo == null) {
            return (false);
        }

        // If we're a 50DTR or 100DTR faction.
        return (ownerTo.getDtr() == 50D || ownerTo.getDtr() == 100D);
	}

	public boolean isDiamondMountain(Location loc) {
		for (CuboidRegion cr : RegionManager.get().getApplicableRegions(loc)) {
			if (cr.getName().toLowerCase().startsWith("diamond")) {
				return (true);
			}
		}

		return (false);
	}

    public boolean isSpawnBufferZone(Location loc) {
        if (loc.getWorld().getEnvironment() != Environment.NORMAL){
            return (false);
        }

        int radius = 175;
        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        return ((x < radius && x > -radius) && (z < radius && z > -radius));
    }

    public boolean isNetherBufferZone(Location loc) {
        if (loc.getWorld().getEnvironment() != Environment.NETHER){
            return (false);
        }

        int radius = 150;
        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        return ((x < radius && x > -radius) && (z < radius && z > -radius));
    }

    public String getRoad(Location loc){
        for(CuboidRegion cr : RegionManager.get().getApplicableRegions(loc)){
            if(cr.getName().toLowerCase().startsWith("road_")){
                return cr.getName();
            }
        }

        return "";
    }

	public void handleShopSign(Sign sign, Player p) {
		ItemStack it = (sign.getLine(2).contains("Crowbar") ? InvUtils.CROWBAR : Basic.get().getItemDb().get(sign.getLine(2).toLowerCase().replace(" ", "")));

		if (it == null) {
			System.err.println(sign.getLine(2).toLowerCase().replace(" ", ""));
			return;
		}

		if (sign.getLine(0).toLowerCase().contains("buy")) {
			int price = 0;
			int amount = 0;

			try {
				price = Integer.parseInt(sign.getLine(3).replace("$", "").replace(",", ""));
				amount = Integer.parseInt(sign.getLine(1));

			}
			catch (NumberFormatException e) {
				e.printStackTrace();
				System.out.println(sign.getLine(3).replace("$", "").replace(",", ""));
				return;
			}

			if (Basic.get().getEconomyManager().getBalance(p.getName()) >= price) {
				if (p.getInventory().firstEmpty() != -1) {
					Basic.get().getEconomyManager().withdrawPlayer(p.getName(), price);

					it.setAmount(amount);
					p.getInventory().addItem(it);
                    p.updateInventory();

					String[] msgs = {
							"§cBOUGHT§r " + amount,
							"for §c$" + NumberFormat.getNumberInstance(Locale.US).format(price),
							"New Balance:",
							"§c$" + NumberFormat.getNumberInstance(Locale.US).format((int) Basic.get().getEconomyManager().getBalance(p.getName())) };

					showSignPacket(p, sign, msgs);
				} else {
					showSignPacket(p, sign, new String[] { "§c§lError!", "",
							"§cNo space", "§cin inventory!" });
				}
			} else {
				showSignPacket(p, sign, new String[] { "§cInsufficient",
						"§cfunds for", sign.getLine(2), sign.getLine(3) });
			}
		} else if (sign.getLine(0).toLowerCase().contains("sell")) {
			int price = 0;

			try {
				int totalStackPrice = Integer.parseInt(sign.getLine(3).replace("$", "").replace(",", ""));
				int amount = Integer.parseInt(sign.getLine(1));

				price = (int) ((double) totalStackPrice / (double) amount);
			}
			catch (NumberFormatException e) {
				e.printStackTrace();
				System.out.println(sign.getLine(3).replace("$", "").replace(",", ""));
				return;
			}

			int amountInInventory = Math.min(64, countItems(p, it.getType(), (int) it.getDurability()));

			if (amountInInventory == 0) {
				showSignPacket(p, sign, new String[] { "§cYou do not",
						"§chave any", sign.getLine(2), "§con you!" });
			} else {
				int totalPrice = amountInInventory * price;
				removeItem(p, it, amountInInventory);
				p.updateInventory();

				Basic.get().getEconomyManager().depositPlayer(p.getName(), totalPrice);

				String[] msgs = {
						"§aSOLD§r " + amountInInventory,
						"for §a$" + NumberFormat.getNumberInstance(Locale.US).format(totalPrice),
						"New Balance:",
						"§a$" + NumberFormat.getNumberInstance(Locale.US).format((int) Basic.get().getEconomyManager().getBalance(p.getName())) };

				showSignPacket(p, sign, msgs);
			}
		}
	}

    public void handleKitSign(Sign sign, Player player){
        String kit = ChatColor.stripColor(sign.getLine(1));

        if (kit.equalsIgnoreCase("Fishing")){
            int uses = FoxtrotPlugin.getInstance().getFishingKitMap().getUses(player.getName());

            if (uses == 3){
                showSignPacket(player, sign, new String[]{ "§aFishing Kit:", "", "§cAlready used", "§c3/3 times!"});
            } else {
                ItemStack rod = new ItemStack(Material.FISHING_ROD);

                rod.addEnchantment(Enchantment.LURE, 2);
                player.getInventory().addItem(rod);
                player.updateInventory();
                player.sendMessage(ChatColor.GOLD + "Equipped the " + ChatColor.WHITE + "Fishing" + ChatColor.GOLD + " kit!");
                FoxtrotPlugin.getInstance().getFishingKitMap().setUses(player.getName(), uses + 1);
                showSignPacket(player, sign, new String[]{"§aFishing Kit:", "§bEquipped!", "", "§dUses: §e" + (uses) + "/3"});
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

        ArrayList<String> lore = new ArrayList<String>();

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

        ArrayList<String> lore = new ArrayList<String>();

        lore.add("§4" + koth);
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

	public void showSignPacket(Player p, final Sign sign, String[] lines) {
		PacketPlayOutUpdateSign sgn = new PacketPlayOutUpdateSign(sign.getX(), sign.getY(), sign.getZ(), lines);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(sgn);

        if(showSignTasks.containsKey(sign)){
            showSignTasks.remove(sign).cancel();
        }

		BukkitRunnable br = new BukkitRunnable(){
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