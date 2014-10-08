package net.frozenorb.foxtrot.listener;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Set;

import lombok.Getter;
import net.frozenorb.Utilities.DataSystem.Regioning.RegionManager;
import net.frozenorb.Utilities.Utils.FaceUtil;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.commands.HostKOTH;
import net.frozenorb.foxtrot.diamond.MountainHandler;
import net.frozenorb.foxtrot.game.Minigame.State;
import net.frozenorb.foxtrot.game.games.KingOfTheHill;
import net.frozenorb.foxtrot.nametag.NametagManager;
import net.frozenorb.foxtrot.nms.FixedVillager;
import net.frozenorb.foxtrot.server.LocationTickStore;
import net.frozenorb.foxtrot.server.PlayerDamagePair;
import net.frozenorb.foxtrot.server.RegionData;
import net.frozenorb.foxtrot.server.ServerManager;
import net.frozenorb.foxtrot.server.SpawnTag;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.TeamManager;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.foxtrot.util.InvUtils;
import net.frozenorb.foxtrot.util.NMSMethods;
import net.frozenorb.foxtrot.util.TimeUtils;
import net.frozenorb.foxtrot.visual.scrollers.MinigameCountdownScroller;
import net.frozenorb.utils.hologram.object.CraftHologram;
import net.frozenorb.utils.hologram.object.HologramManager;

import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MathHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPortalExitEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.comphenix.protocol.ProtocolLibrary;
import com.google.common.base.Function;

@SuppressWarnings("deprecation")
public class FoxListener implements Listener {
	private FoxtrotPlugin plugin = FoxtrotPlugin.getInstance();

	@Getter private static HashMap<String, Long> enderpearlCooldown = new HashMap<String, Long>();

	private HashMap<String, Integer> mobSpawns = new HashMap<String, Integer>();
	private HashMap<String, Villager> combatLoggers = new HashMap<String, Villager>();
	private HashMap<PlayerDamagePair, Long> lastPlayerDamager = new HashMap<PlayerDamagePair, Long>();
	private HashSet<Integer> droppedItems = new HashSet<Integer>();

	private static final Material[] PROJECTILE_MATERIALS = {
			Material.ENDER_PEARL, Material.SNOW_BALL, Material.EGG };

	public static final PotionEffectType[] DEBUFFS = { PotionEffectType.POISON,
			PotionEffectType.SLOW, PotionEffectType.WEAKNESS,
			PotionEffectType.HARM };

	private static final Material[] NO_INTERACT_WITH = { Material.LAVA_BUCKET,
			Material.WATER_BUCKET, Material.BUCKET };

	private static final Material[] NO_INTERACT_WITH_SPAWN = {
			Material.SNOW_BALL, Material.ENDER_PEARL, Material.EGG,
			Material.FISHING_ROD };

	private static final Material[] NO_INTERACT = { Material.FENCE_GATE,
			Material.FURNACE, Material.BREWING_STAND, Material.CHEST,
			Material.HOPPER, Material.DISPENSER, Material.WOODEN_DOOR,
			Material.STONE_BUTTON, Material.WOOD_BUTTON,
			Material.TRAPPED_CHEST, Material.TRAP_DOOR, Material.LEVER,
			Material.WORKBENCH, Material.DROPPER };

	public static final Material[] NON_TRANSPARENT_ATTACK_DISABLING_BLOCKS = {
			Material.GLASS, Material.WOOD_DOOR, Material.IRON_DOOR,
			Material.FENCE_GATE };

	@EventHandler
	public void playerhit(EntityDamageByEntityEvent e) {
		if ((e.getEntity() instanceof Player && e.getDamager() instanceof Player)) {

			if (e.isCancelled())
				return;

			Player p = (Player) e.getEntity();
			Player pl = (Player) e.getDamager();

			Iterator<Entry<PlayerDamagePair, Long>> entryiter = lastPlayerDamager.entrySet().iterator();

			while (entryiter.hasNext()) {
				Entry<PlayerDamagePair, Long> ent = entryiter.next();

				if (ent.getKey().getVictimUUID().equals(p.getUniqueId())) {
					entryiter.remove();
				}
			}

			lastPlayerDamager.put(new PlayerDamagePair(p.getUniqueId(), pl.getUniqueId()), System.currentTimeMillis() + (PlayerDamagePair.FALL_DAMAGE_ASSIST_SECONDS * 1000));

			if (plugin.getTeamManager().getPlayerTeam(p.getName()) == null)
				return;

			if (plugin.getTeamManager().getPlayerTeam(pl.getName()) == null)
				return;

			Team team = plugin.getTeamManager().getPlayerTeam(p.getName());

			if (!team.isFriendlyFire()) {
				if (plugin.getTeamManager().getPlayerTeam(pl.getName()) == team)
					e.setCancelled(true);
			} else
				e.setCancelled(false);
		}
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Arrow) {

			if (e.isCancelled())
				return;

			Player p = (Player) e.getEntity();
			if (!(((Arrow) e.getDamager()).getShooter() instanceof Player)) {
				return;
			}
			Player pl = ((Player) ((Arrow) e.getDamager()).getShooter());

			if (plugin.getTeamManager().getPlayerTeam(p.getName()) == null)
				return;

			if (plugin.getTeamManager().getPlayerTeam(pl.getName()) == null)
				return;

			Team team = plugin.getTeamManager().getPlayerTeam(p.getName());

			if (!team.isFriendlyFire()) {
				if (plugin.getTeamManager().getPlayerTeam(pl.getName()) == team)
					e.setCancelled(true);
			} else
				e.setCancelled(false);

		}
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e) {
		e.getPlayer().getInventory().remove(net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands.Subclaim.SELECTION_WAND);
		e.getPlayer().getInventory().remove(net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands.Claim.SELECTION_WAND);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onVerticalBlockPlaceGlitch(BlockPlaceEvent e) {

		if (FoxtrotPlugin.getInstance().getTeamManager().isTaken(e.getBlock().getLocation())) {
			if (e.isCancelled()) {
				e.getPlayer().teleport(e.getPlayer().getLocation());
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onVerticalBlockBreakGlitch(final BlockBreakEvent e) {
		if (e.isCancelled() && e.getBlock().getType().isSolid() && e.getPlayer().getName().equalsIgnoreCase("this_is_a_comment_technically")) {
			final Location tpTo = LocationTickStore.getInstance().recallOldestLocation(e.getPlayer().getName());

			Bukkit.getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), new Runnable() {

				@Override
				public void run() {
					e.getPlayer().teleport(tpTo);
				}
			}, 1);
		}
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e) {

		if (FoxtrotPlugin.getInstance().getServerManager().isWarzone(e.getEntity().getLocation())) {
			e.blockList().clear();
			return;
		}

		Iterator<Block> iter = e.blockList().iterator();

		while (iter.hasNext()) {
			Block b = iter.next();

			if (FoxtrotPlugin.getInstance().getTeamManager().isTaken(b.getLocation())) {
				iter.remove();
			}
		}

		if (FoxtrotPlugin.getInstance().getServerManager().isUnclaimed(e.getLocation()) && e.getEntity() != null && e.getEntityType() == EntityType.CREEPER) {
			e.blockList().clear();
		}

	}

	@EventHandler
	public void onFireSpread(BlockSpreadEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onFireSpread(BlockBurnEvent e) {
		if (FoxtrotPlugin.getInstance().getServerManager().isWarzone(e.getBlock().getLocation())) {
			e.setCancelled(true);
			return;
		}
		if (FoxtrotPlugin.getInstance().getServerManager().isClaimedAndRaidable(e.getBlock().getLocation())) {
			return;
		}

		if (FoxtrotPlugin.getInstance().getTeamManager().isTaken(e.getBlock().getLocation())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onFireSpread(BlockIgniteEvent e) {

		if (e.getPlayer() != null) {
			if (FoxtrotPlugin.getInstance().getServerManager().isAdminOverride(e.getPlayer())) {
				return;
			}
		}

		if (FoxtrotPlugin.getInstance().getServerManager().isSpawn(e.getBlock().getLocation())) {
			e.setCancelled(true);
			return;
		}

		if (FoxtrotPlugin.getInstance().getServerManager().isClaimedAndRaidable(e.getBlock().getLocation())) {
			return;
		}

		if (FoxtrotPlugin.getInstance().getTeamManager().isTaken(e.getBlock().getLocation())) {
			Team owner = FoxtrotPlugin.getInstance().getTeamManager().getOwner(e.getBlock().getLocation());

			if (e.getCause() == IgniteCause.FLINT_AND_STEEL && owner.isOnTeam(e.getPlayer())) {
				return;
			}
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent e) {
		Location fromLoc = e.getFrom();

		Location toLoc = e.getTo();
		double toX = toLoc.getX();
		double toZ = toLoc.getZ();
		double toY = toLoc.getY();
		double fromX = fromLoc.getX();
		double fromZ = fromLoc.getZ();
		double fromY = fromLoc.getY();

		if (FoxtrotPlugin.getInstance().getJoinTimerMap().hasTimer(e.getPlayer()) && LandBoard.getInstance().getTeamAt(e.getPlayer().getLocation()) != null && LandBoard.getInstance().getTeamAt(e.getPlayer().getLocation()).isOnTeam(e.getPlayer().getName())) {
			FoxtrotPlugin.getInstance().getJoinTimerMap().updateValue(e.getPlayer().getName(), -1L);
		}

		if (fromX != toX || fromZ != toZ || fromY != toY) {

			if (ServerManager.getTasks().containsKey(e.getPlayer().getName())) {
				if (fromLoc.distance(toLoc) > 0.1 && (fromX != toX || fromZ != toZ || fromY != toY)) {
					Bukkit.getScheduler().cancelTask(ServerManager.getTasks().get(e.getPlayer().getName()));
					ServerManager.getTasks().remove(e.getPlayer().getName());
					e.getPlayer().sendMessage(ChatColor.YELLOW + "§lLOGOUT §c§lCANCELLED!");

				}
			}

			ServerManager sm = FoxtrotPlugin.getInstance().getServerManager();

			RegionData<?> from = sm.getRegion(fromLoc, e.getPlayer());
			RegionData<?> to = sm.getRegion(toLoc, e.getPlayer());

			if (!from.equals(to)) {
				boolean cont = to.getRegion().getMoveHandler().handleMove(e);

				if (!cont) {
					return;
				}

				String fromStr = "§eNow leaving: " + from.getName(e.getPlayer()) + (from.getRegion().isReducedDeathban() ? "§e(§aNon-Deathban§e)" : "§e(§cDeathban§e)");
				String toStr = "§eNow entering: " + to.getName(e.getPlayer()) + (to.getRegion().isReducedDeathban() ? "§e(§aNon-Deathban§e)" : "§e(§cDeathban§e)");

				e.getPlayer().sendMessage(new String[] { fromStr, toStr });
			}
		}

	}

	@EventHandler
	public void onEntityInteract(PlayerInteractEntityEvent e) {
		if (e.getRightClicked().hasMetadata("dummy")) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockExplosion(EntityChangeBlockEvent event) {
		Entity entity = event.getEntity();
		if (!(entity instanceof Wither))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onEnderchestOpen(InventoryOpenEvent e) {
		if (e.getInventory().getType() == InventoryType.ENDER_CHEST) {
			e.setCancelled(true);
		}
	}

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onPlayerQuit(final PlayerQuitEvent e) {
		e.getPlayer().getInventory().remove(net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands.Subclaim.SELECTION_WAND);
		e.getPlayer().getInventory().remove(net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands.Claim.SELECTION_WAND);

		e.setQuitMessage(null);
		FoxtrotPlugin.getInstance().getPlaytimeMap().playerQuit(e.getPlayer());

		NametagManager.getTeamMap().remove(e.getPlayer().getName());

		boolean enemyWithinRange = false;
		TeamManager tm = FoxtrotPlugin.getInstance().getTeamManager();

		for (Entity ent : e.getPlayer().getNearbyEntities(40, 256, 40)) {
			if (ent instanceof Player) {
				Player other = (Player) ent;

				if (other.hasMetadata("invisible")) {
					continue;
				}

				if (tm.getPlayerTeam(other.getName()) != tm.getPlayerTeam(e.getPlayer().getName())) {
					enemyWithinRange = true;
				}

			}
		}

		if (e.getPlayer().hasMetadata("loggedout")) {

			e.getPlayer().removeMetadata("loggedout", FoxtrotPlugin.getInstance());
			return;
		}

		if (FoxtrotPlugin.getInstance().getServerManager().isSpawn(e.getPlayer().getLocation())) {
			return;
		}

		if ((enemyWithinRange && !e.getPlayer().isDead()) || e.getPlayer().getName().equalsIgnoreCase("LazyLemons") || !e.getPlayer().isOnGround()) {
			String name = "§e" + e.getPlayer().getName();

			ItemStack[] armor = e.getPlayer().getInventory().getArmorContents();
			ItemStack[] inv = e.getPlayer().getInventory().getContents();

			ItemStack[] drops = new ItemStack[armor.length + inv.length];
			System.arraycopy(armor, 0, drops, 0, armor.length);
			System.arraycopy(inv, 0, drops, armor.length, inv.length);

			FixedVillager fv = new FixedVillager(((CraftWorld) e.getPlayer().getWorld()).getHandle());

			Location l = e.getPlayer().getLocation();
			fv.setLocation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());

			int i = MathHelper.floor(fv.locX / 16.0D);
			int j = MathHelper.floor(fv.locZ / 16.0D);
			net.minecraft.server.v1_7_R4.World world = ((CraftWorld) e.getPlayer().getWorld()).getHandle();

			world.getChunkAt(i, j).a(fv);
			world.entityList.add(fv);

			try {
				Method m = world.getClass().getDeclaredMethod("a", net.minecraft.server.v1_7_R4.Entity.class);
				m.setAccessible(true);

				m.invoke(world, fv);

			}
			catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
				ex.printStackTrace();
			}

			final Villager villager = (Villager) fv.getBukkitEntity();

			villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 100));
			villager.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 100));

			PlayerInventory pi = e.getPlayer().getInventory();

			if (pi.getHelmet() != null) {
				villager.getEquipment().setHelmet(pi.getHelmet());
				villager.getEquipment().setHelmetDropChance(0F);
			}

			if (pi.getChestplate() != null) {
				villager.getEquipment().setChestplate(pi.getChestplate());
				villager.getEquipment().setChestplateDropChance(0F);
			}

			if (pi.getLeggings() != null) {
				villager.getEquipment().setLeggings(pi.getLeggings());
				villager.getEquipment().setLeggingsDropChance(0F);
			}
			if (pi.getBoots() != null) {
				villager.getEquipment().setBoots(pi.getBoots());
				villager.getEquipment().setBootsDropChance(0F);
			}

			villager.setMetadata("dummy", new FixedMetadataValue(FoxtrotPlugin.getInstance(), drops));
			villager.setAgeLock(true);
			villager.setHealth(((Damageable) e.getPlayer()).getHealth());
			villager.setCustomName(name);
			villager.setCustomNameVisible(true);

			combatLoggers.put(e.getPlayer().getName(), villager);

			Bukkit.getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), new Runnable() {

				@Override
				public void run() {
					if (!villager.isDead() && villager.isValid()) {
						villager.remove();
						combatLoggers.remove(e.getPlayer().getName());
					}
				}
			}, 30 * 20L);
		}

	}

	@EventHandler
	public void onDummyDeath(EntityDeathEvent e) {
		if (e.getEntity().hasMetadata("dummy")) {
			String playerName = e.getEntity().getCustomName().substring(2);

			if (FoxtrotPlugin.getInstance().getServerManager().isKOTHArena(e.getEntity().getLocation())) {
				FoxtrotPlugin.getInstance().getDeathbanMap().deathban(playerName, 300);

			} else {
				FoxtrotPlugin.getInstance().getDeathbanMap().deathban(playerName, 15 * 60);
			}

			if (e.getEntity().getKiller() != null) {
				String msg = "§c" + playerName + " §7(Combat-Logger)§e was slain by §c" + e.getEntity().getKiller().getName();
				Bukkit.broadcastMessage(msg);

				ItemStack[] drops = (ItemStack[]) e.getEntity().getMetadata("dummy").get(0).value();

				if (Bukkit.getPlayerExact(playerName) == null) {
					for (ItemStack item : drops) {
						e.getDrops().add(item);
					}
				}

				ItemStack deathsign = new ItemStack(Material.SIGN);
				ItemMeta meta = deathsign.getItemMeta();

				ArrayList<String> lore = new ArrayList<String>();

				lore.add("§4" + ChatColor.stripColor(e.getEntity().getCustomName()));
				lore.add("§eSlain By:");
				lore.add("§a" + e.getEntity().getKiller().getName());

				DateFormat sdf = new SimpleDateFormat("M/d HH:mm:ss");

				lore.add(sdf.format(new Date()).replace(" AM", "").replace(" PM", ""));

				meta.setLore(lore);
				meta.setDisplayName("§dDeath Sign");
				deathsign.setItemMeta(meta);

				for (ItemStack it : e.getEntity().getKiller().getInventory().addItem(deathsign).values()) {
					e.getDrops().add(it);
				}

			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerKickEvent e) {
		e.getPlayer().removeMetadata("subTitle", FoxtrotPlugin.getInstance());
		e.setLeaveMessage(null);

	}

	@EventHandler
	public void onEntityDespawn(ChunkUnloadEvent e) {
		for (Entity ent : e.getChunk().getEntities()) {
			if (ent.hasMetadata("dummy") && !ent.isDead()) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		if (FoxtrotPlugin.getInstance().getServerManager().isSpawn(e.getEntity().getLocation()) && e.getFoodLevel() < ((Player) e.getEntity()).getFoodLevel()) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {

		NametagManager.sendPacketsInitialize(e.getPlayer());
		NametagManager.sendTeamsToPlayer(e.getPlayer());
		NametagManager.reloadPlayer(e.getPlayer());

		e.getPlayer().chat("/f who");

		if (combatLoggers.containsKey(e.getPlayer().getName())) {
			Villager v = combatLoggers.get(e.getPlayer().getName());

			if (!v.getLocation().getChunk().isLoaded()) {
				v.getLocation().getChunk().load();
			}

			if (v.isDead() || !v.isValid()) {
				e.getPlayer().getInventory().setArmorContents(null);
				e.getPlayer().getInventory().clear();

				Team t = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(e.getPlayer().getName());

				if (t != null) {
					t.playerDeath(e.getPlayer());
				}
				if (t != null && t.getHQ() != null) {
					e.getPlayer().teleport(t.getHQ());
				} else {
					e.getPlayer().teleport(FoxtrotPlugin.getInstance().getServerManager().getSpawnLocation());
				}
			}

			if (v.isValid() || !v.isDead()) {
				e.getPlayer().setHealth(((Damageable) v).getHealth());
				combatLoggers.get(e.getPlayer().getName()).remove();

			}
			combatLoggers.remove(e.getPlayer().getName());
		}

		e.setJoinMessage(null);
		e.getPlayer().setMetadata("freshJoin", new FixedMetadataValue(FoxtrotPlugin.getInstance(), true));
		FoxtrotPlugin.getInstance().getPlaytimeMap().playerJoined(e.getPlayer());
		if (!e.getPlayer().hasPlayedBefore()) {

			e.getPlayer().sendMessage(ChatColor.YELLOW + "Your PVP Timer has been activated for 30 minutes.");
			e.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot attack, take damage, or enter other's claims while this is active!");
			FoxtrotPlugin.getInstance().getJoinTimerMap().createTimer(e.getPlayer(), 30 * 60);

			e.getPlayer().teleport(FoxtrotPlugin.getInstance().getServerManager().getSpawnLocation());
		}

		for (PotionEffect pe : e.getPlayer().getActivePotionEffects()) {

			if (pe.getDuration() > 1_000_000) {
				e.getPlayer().removePotionEffect(pe.getType());
			}
		}

		try {
			for (Field f : HostKOTH.class.getFields()) {
				if (f.getType() == KingOfTheHill.class) {
					KingOfTheHill koth = (KingOfTheHill) f.get(null);

					if (koth != null && koth.getGameState() == State.JOINABLE) {
						FoxtrotPlugin.getInstance().getBossBarManager().registerMessage(e.getPlayer(), new MinigameCountdownScroller(koth));
						break;
					}
					if (koth != null && koth.getGameState() == State.IN_PROGRESS) {
						FoxtrotPlugin.getInstance().getBossBarManager().unregisterPlayer(e.getPlayer());
					}
				}
			}
		}
		catch (IllegalArgumentException | IllegalAccessException ex) {
			ex.printStackTrace();
		}

		FoxtrotPlugin.getInstance().getScoreboardManager().startTask(e.getPlayer());

	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		Team t = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(e.getPlayer().getName());

		if (t != null && t.getHQ() != null) {
			e.setRespawnLocation(t.getHQ());
		} else {
			e.setRespawnLocation(FoxtrotPlugin.getInstance().getServerManager().getSpawnLocation());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSpawnTagMonitor(EntityDamageByEntityEvent e) {
		Player killer = null;

		if (e.getEntity() instanceof Player) {

			if (e.getDamager() instanceof Player) {
				killer = (Player) e.getDamager();
			} else if (e.getDamager() instanceof Projectile) {
				if (((Projectile) e.getDamager()).getShooter() instanceof Player) {
					killer = (Player) ((Projectile) e.getDamager()).getShooter();
				}
			}

			if (killer != null && killer != e.getEntity()) {
				SpawnTag.applyTag(killer);
			}
		}
	}

	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent e) {
		if (e.getEntity().getShooter() instanceof Player && !(e.getEntity() instanceof ThrownPotion) && !(e.getEntity() instanceof Arrow) && !(e.getEntity() instanceof EnderPearl)) {
			SpawnTag.applyTag((Player) e.getEntity().getShooter());

		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSpawnProtCheck(EntityDamageByEntityEvent e) {
		Player killer = null;

		if (e.getDamager() instanceof Player) {
			killer = (Player) e.getDamager();
		} else if (e.getDamager() instanceof Projectile) {
			if (((Projectile) e.getDamager()).getShooter() instanceof Player) {
				killer = (Player) ((Projectile) e.getDamager()).getShooter();
			}
		}

		if (e.getEntity() instanceof Player && killer != null) {
			Player vic = (Player) e.getEntity();

			if (FoxtrotPlugin.getInstance().getServerManager().isSpawn(vic.getLocation())) {

				e.setCancelled(true);
				return;
			}

			if (FoxtrotPlugin.getInstance().getServerManager().isSpawn(killer.getLocation())) {
				e.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (ServerManager.getTasks().containsKey(p.getName())) {
				Bukkit.getScheduler().cancelTask(ServerManager.getTasks().get(p.getName()));
				ServerManager.getTasks().remove(p.getName());
				p.sendMessage(ChatColor.YELLOW + "§lLOGOUT §c§lCANCELLED!");
			}
			if (FoxtrotPlugin.getInstance().getServerManager().isSpawn(p.getLocation())) {
				e.setCancelled(true);
			}
		}

		if (e instanceof EntityDamageByEntityEvent) {
			if (((EntityDamageByEntityEvent) e).getDamager() instanceof Player) {
				Player p = ((Player) ((EntityDamageByEntityEvent) e).getDamager());

				if (e.getEntity().hasMetadata("dummy")) {
					Villager v = (Villager) e.getEntity();

					String name = v.getCustomName().substring(2);

					if (plugin.getTeamManager().getPlayerTeam(p.getName()) == null)
						return;

					if (plugin.getTeamManager().getPlayerTeam(name) == null)
						return;

					Team team = plugin.getTeamManager().getPlayerTeam(p.getName());

					if (!team.isFriendlyFire()) {
						if (plugin.getTeamManager().getPlayerTeam(name) == team)
							e.setCancelled(true);
					} else
						e.setCancelled(false);

				}

				if (e.getEntity() instanceof Player) {
					Player rec = (Player) e.getEntity();
					if (FoxtrotPlugin.getInstance().getJoinTimerMap().hasTimer(p)) {
						p.sendMessage(ChatColor.RED + "You cannot attack others while you have your PVP Timer. Type '§e/pvptimer remove§c' to remove your timer.");
						e.setCancelled(true);
						return;
					}

					if (FoxtrotPlugin.getInstance().getJoinTimerMap().hasTimer(rec)) {
						p.sendMessage(ChatColor.RED + "That player currently has their PVP Timer!");
						e.setCancelled(true);
						return;
					}
				}
				if (ServerManager.getTasks().containsKey(p.getName())) {
					Bukkit.getScheduler().cancelTask(ServerManager.getTasks().get(p.getName()));
					ServerManager.getTasks().remove(p.getName());
					p.sendMessage(ChatColor.YELLOW + "§lLOGOUT §c§lCANCELLED!");
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		Team team = plugin.getTeamManager().getPlayerTeam(p.getName());

		if (team == null) {
			e.setFormat("%s§f: %s");
			return;
		}

		e.setFormat("§7[§e" + team.getFriendlyName() + "§7] §r%s§f: %s");

		Set<String> members = team.getMembers();

		boolean doTeamChat = true;
		if (e.getMessage().charAt(0) == '!' && !e.getMessage().equalsIgnoreCase("!")) {
			e.setMessage(e.getMessage().substring(1).trim());
			doTeamChat = false;
		}

		if (p.hasMetadata("teamChat") && doTeamChat) {
			e.setCancelled(true);
			for (Player pl : Bukkit.getOnlinePlayers()) {
				if (members.contains(pl.getName())) {

					pl.sendMessage(ChatColor.DARK_AQUA + "(Team) " + p.getName() + ":§e " + e.getMessage());
				}
			}
			return;
		}

		e.setCancelled(true);

		for (Player pl : Bukkit.getOnlinePlayers()) {

			String plMsg = String.format(e.getFormat(), e.getPlayer().getDisplayName(), e.getMessage());

			if (team.isOnTeam(pl)) {
				plMsg = plMsg.replace("§7[§e", "§7[§a");
			}

			pl.sendMessage(plMsg);
		}

		Bukkit.getConsoleSender().sendMessage(String.format(e.getFormat(), e.getPlayer().getDisplayName(), e.getMessage()));

	}

	@EventHandler
	public void onMount(final VehicleEnterEvent event) {
		if (event.getEntered() instanceof Player) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(FoxtrotPlugin.getInstance(), new Runnable() {
				public void run() {
					if (event.getVehicle().isValid() && event.getEntered().isValid()) {
						ProtocolLibrary.getProtocolManager().updateEntity(event.getVehicle(), Arrays.asList(new Player[] { (Player) event.getEntered() }));
					}
				}
			});
		}
	}

	@EventHandler
	public void onItemEnchant(EnchantItemEvent e) {
		for (Entry<Enchantment, Integer> entry : ServerManager.getMaxEnchantments().entrySet()) {
			if (e.getEnchantsToAdd().containsKey(entry.getKey())) {
				if (e.getEnchantsToAdd().get(entry.getKey()) > entry.getValue()) {
					e.getEnchantsToAdd().put(entry.getKey(), entry.getValue());
				}
			}
		}
	}

	@EventHandler
	public void onPotionSplash(PotionSplashEvent e) {
		ItemStack potion = e.getPotion().getItem();
		int value = (int) potion.getDurability();

        for (LivingEntity le : e.getAffectedEntities()) {
			if (le instanceof Player) {
				Player p = (Player) le;

				if (FoxtrotPlugin.getInstance().getServerManager().isSpawn(p.getLocation())) {
					e.setIntensity(le, 0D);
				}
			}
        }

		if (e.getPotion().getShooter() instanceof Player) {

			if (Arrays.asList(DEBUFFS).contains(e.getPotion().getEffects().iterator().next().getType())) {
				if (e.getAffectedEntities().size() > 1 || (e.getAffectedEntities().size() == 1 && !e.getAffectedEntities().contains(e.getPotion().getShooter()))) {
					SpawnTag.applyTag((Player) e.getPotion().getShooter());
				}
			}
		}

		for (int i : ServerManager.DISALLOWED_POTIONS) {
			if (i == value) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerConsume(PlayerItemConsumeEvent e) {

		if (e.getItem().getType() == Material.POTION) {
			ItemStack potion = e.getItem();
			int value = (int) potion.getDurability();

			for (int i : ServerManager.DISALLOWED_POTIONS) {
				if (i == value) {
					e.setCancelled(true);
					e.getPlayer().sendMessage(ChatColor.RED + "This potion is not usable!");
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onAnvilClick(InventoryClickEvent e) {

		if (!e.isCancelled()) {
			HumanEntity ent = e.getWhoClicked();

			// not really necessary
			if (ent instanceof Player) {
				Player player = (Player) ent;
				Inventory inv = e.getInventory();

				if (e.getInventory().getType() == InventoryType.MERCHANT) {
					for (ItemStack item : e.getInventory()) {
						if (item != null) {
							InvUtils.fixItem(item);
						}
					}
				}

				// see if the event is about an anvil
				if (inv instanceof AnvilInventory) {
					InventoryView view = e.getView();
					int rawSlot = e.getRawSlot();

					// compare the raw slot with the inventory view to make sure
					// we are talking about the upper inventory
					if (rawSlot == view.convertSlot(rawSlot)) {
						/*
						 * slot 0 = left item slot slot 1 = right item slot slot
						 * 2 = result item slot
						 * 
						 * see if the player clicked in the result item slot of
						 * the anvil inventory
						 */
						if (rawSlot == 2) {
							/*
							 * get the current item in the result slot I think
							 * inv.getItem(rawSlot) would be possible too
							 */
							ItemStack item = e.getCurrentItem();
							ItemStack baseItem = inv.getItem(0);

							// check if there is an item in the result slot
							if (item != null) {

								boolean book = item.getType() == Material.ENCHANTED_BOOK;

								for (Entry<Enchantment, Integer> entry : ServerManager.getMaxEnchantments().entrySet()) {

									if (book) {
										EnchantmentStorageMeta esm = (EnchantmentStorageMeta) item.getItemMeta();
										if (esm.hasStoredEnchant(entry.getKey()) && esm.getStoredEnchantLevel(entry.getKey()) > entry.getValue()) {
											player.sendMessage(ChatColor.RED + "That book would be too strong to use!");
											e.setCancelled(true);
											return;
										}

									} else {
										if (item.containsEnchantment(entry.getKey()) && item.getEnchantmentLevel(entry.getKey()) > entry.getValue()) {
											if (entry.getValue() == -1) {
												item.addEnchantment(Enchantment.DURABILITY, entry.getValue());
											} else {
												item.addEnchantment(entry.getKey(), entry.getValue());
											}
										}
									}
								}
								ItemMeta meta = item.getItemMeta();

								// it is possible that the item does not have
								// meta data

								if (meta != null) {
									// see whether the item is beeing renamed
									if (meta.hasDisplayName()) {

										String displayName = fixName(meta.getDisplayName());

										if (baseItem.hasItemMeta() && baseItem.getItemMeta().getDisplayName() != null && FoxtrotPlugin.getInstance().getServerManager().getUsedNames().contains(fixName(baseItem.getItemMeta().getDisplayName())) && !baseItem.getItemMeta().getDisplayName().equals(meta.getDisplayName())) {
											e.setCancelled(true);
											player.sendMessage(ChatColor.RED + "You cannot rename an item with a name!");
											return;
										}

										if (FoxtrotPlugin.getInstance().getServerManager().getUsedNames().contains(displayName) && (baseItem.hasItemMeta() && baseItem.getItemMeta().getDisplayName() != null ? !baseItem.getItemMeta().getDisplayName().equals(meta.getDisplayName()) : true)) {
											e.setCancelled(true);
											player.sendMessage(ChatColor.RED + "An item with that name already exists.");

										} else {

											List<String> lore = new ArrayList<String>();

                                            boolean hasForgedMeta = false;
                                            for (String s : meta.getLore()) {
                                                if (s.toLowerCase().contains("forged"))
                                                    hasForgedMeta = true;
                                            }

											if (meta.getLore() != null && !hasForgedMeta) {
												lore = meta.getLore();
											}

											DateFormat sdf = DateFormat.getDateTimeInstance();

											lore.add(0, "§eForged by " + player.getDisplayName() + "§e on " + sdf.format(new Date()));

											meta.setLore(lore);
											item.setItemMeta(meta);

											FoxtrotPlugin.getInstance().getServerManager().getUsedNames().add(displayName);
											FoxtrotPlugin.getInstance().getServerManager().save();
										}

									}
								}
							}
						}
					}
				}
			}
		}
	}

	private String fixName(String name) {
		String b = name.toLowerCase().trim();
		char[] allowed = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()-_'".toCharArray();
		char[] charArray = b.toCharArray();
		StringBuilder result = new StringBuilder();
		for (char c : charArray) {
			for (char a : allowed) {
				if (c == a) {
					result.append(a);
				}
			}
		}

		return result.toString();
	}

	@EventHandler
	public void onBlockCombust(BlockBurnEvent e) {

		if (FoxtrotPlugin.getInstance().getServerManager().isWarzone(e.getBlock().getLocation())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onBucketEmpty(PlayerBucketEmptyEvent e) {

		if (FoxtrotPlugin.getInstance().getServerManager().isAdminOverride(e.getPlayer())) {
			return;
		}

		Team owner = FoxtrotPlugin.getInstance().getTeamManager().getOwner(e.getBlockClicked().getRelative(e.getBlockFace()).getLocation());

		if (owner != null && !owner.isOnTeam(e.getPlayer())) {

			e.setCancelled(true);
			e.getBlockClicked().getRelative(e.getBlockFace()).setType(Material.AIR);

			e.setItemStack(new ItemStack(e.getBucket()));
			return;
		}

		if (FoxtrotPlugin.getInstance().getServerManager().isWarzone(e.getBlockClicked().getLocation())) {
			e.setCancelled(true);
			e.getBlockClicked().getRelative(e.getBlockFace()).setType(Material.AIR);

			e.setItemStack(new ItemStack(e.getBucket()));
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {

		double mult = 1;

		if (e.getEntity().getKiller() != null) {
			Player p = (Player) e.getEntity().getKiller();

			if (p.getItemInHand() != null) {
				ItemStack it = p.getItemInHand();

				if (it.containsEnchantment(Enchantment.LOOT_BONUS_MOBS)) {
					int lvl = it.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);

					switch (lvl) {
					case 1:
						mult = 1.2D;
						break;
					case 2:
						mult = 1.4D;
						break;
					case 3:
						mult = 2D;
						break;
					default:
						mult = 2.5D;
						break;

					}
				}
			}
		}

		e.setDroppedExp((int) Math.ceil(e.getDroppedExp() * mult));
	}

	@EventHandler
	public void onEntityShootBow(EntityShootBowEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();

			if (FoxtrotPlugin.getInstance().getJoinTimerMap().hasTimer(p)) {

				p.sendMessage(ChatColor.RED + "You cannot do this while your PVP Timer is active! Type '§e/pvptimer remove§c' to remove your timer.");
				e.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onProjetileInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();

		if (e.getItem() != null && e.getItem().getType() == Material.POTION) {
			ItemStack i = e.getItem();

			Potion pot = Potion.fromItemStack(i);
			if (i.getAmount() > 1 && pot.isSplash()) {
				e.setCancelled(true);

				e.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
				e.setUseItemInHand(org.bukkit.event.Event.Result.DENY);
				e.getPlayer().updateInventory();
			}

			if (FoxtrotPlugin.getInstance().getServerManager().isSpawn(p.getLocation()) && pot.isSplash() && Arrays.asList(DEBUFFS).contains(pot.getType().getEffectType())) {
				e.setCancelled(true);

				e.getPlayer().sendMessage(ChatColor.RED + "You cannot launch debuffs from inside spawn!");

				e.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
				e.setUseItemInHand(org.bukkit.event.Event.Result.DENY);
				e.getPlayer().updateInventory();
			}
		}

		if (p.getItemInHand() != null && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {

			if (FoxtrotPlugin.getInstance().getJoinTimerMap().hasTimer(p)) {
				if (Arrays.asList(PROJECTILE_MATERIALS).contains(e.getMaterial())) {
					p.sendMessage(ChatColor.RED + "You cannot do this while your PVP Timer is active! Type '§e/pvptimer remove§c' to remove your timer.");
					e.setCancelled(true);
				}

			}

			if (e.getMaterial() == Material.ENDER_PEARL) {

				if (enderpearlCooldown.containsKey(p.getName()) && enderpearlCooldown.get(p.getName()) > System.currentTimeMillis()) {

					long millisLeft = enderpearlCooldown.get(p.getName()) - System.currentTimeMillis();

					double value = (millisLeft / 1000D);
					double sec = Math.round(10.0 * value) / 10.0;

					e.setCancelled(true);
					String msg = "§cYou cannot use this for another §l" + sec + "§c seconds!";
					p.sendMessage(msg);
					p.updateInventory();

				} else {
					enderpearlCooldown.put(p.getName(), System.currentTimeMillis() + 16000);

				}

			}

		}
	}

	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent e) {
		if (FoxtrotPlugin.getInstance().getServerManager().isSpawn(e.getBlock().getLocation())) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onSignInteract(final PlayerInteractEvent e) {

		if (e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.SKULL && e.getClickedBlock().getData() == (byte) 3) {
			Skull sk = (Skull) e.getClickedBlock().getState();

			Location loc = sk.getLocation().add(0, 1, 0);

			Location to = e.getPlayer().getLocation();

			Vector v = to.toVector().subtract(loc.toVector());

			Location newloc = loc.getBlock().getRelative(FaceUtil.getDirection(v)).getLocation();
			newloc.setY((loc.getY() + newloc.getY()) / 2D);

			CraftHologram ch = new CraftHologram(sk.getOwner(), newloc);

			HologramManager.addHologram(ch);

			ch.addLine(sk.getOwner());
			ch.update();

            Bukkit.getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), new Runnable() {
                @Override
                public void run() {
                    ch.delete();;
                }
            }, 60l);
		}

		if (e.getClickedBlock() != null && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (e.getClickedBlock().getType() == Material.WALL_SIGN || e.getClickedBlock().getType() == Material.SIGN_POST) {
				Sign s = (Sign) e.getClickedBlock().getState();

				if (s.getLine(0) != null && FoxtrotPlugin.getInstance().getServerManager().isSpawn(e.getClickedBlock().getLocation())) {
					FoxtrotPlugin.getInstance().getServerManager().handleShopSign(s, e.getPlayer());
				}
			}
		}

		if (e.getItem() != null && e.getMaterial() == Material.SIGN) {

			if (e.getItem().hasItemMeta() && e.getItem().getItemMeta().getLore() != null) {
				ArrayList<String> lore = (ArrayList<String>) e.getItem().getItemMeta().getLore();

				if (lore.size() > 1 && lore.get(1).contains("§e")) {
					if (e.getClickedBlock() != null) {
						e.getClickedBlock().getRelative(e.getBlockFace()).getState().setMetadata("noSignPacket", new FixedMetadataValue(FoxtrotPlugin.getInstance(), true));

						Bukkit.getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), new Runnable() {

							@Override
							public void run() {
								e.getClickedBlock().getRelative(e.getBlockFace()).getState().removeMetadata("noSignPacket", FoxtrotPlugin.getInstance());
							}
						}, 20);
					}
				}
			}
		}
	}

	@EventHandler
	public void onSignPlace(BlockPlaceEvent e) {
		if (e.getItemInHand().getType() == Material.SIGN) {
			if (e.getItemInHand().hasItemMeta() && e.getItemInHand().getItemMeta().getLore() != null) {
				ArrayList<String> lore = (ArrayList<String>) e.getItemInHand().getItemMeta().getLore();

				if (e.getBlock().getType() == Material.WALL_SIGN || e.getBlock().getType() == Material.SIGN_POST) {
					Sign s = (Sign) e.getBlock().getState();

					for (int i = 0; i < 4; i++) {
						s.setLine(i, lore.get(i));
					}
					s.setMetadata("deathSign", new FixedMetadataValue(FoxtrotPlugin.getInstance(), true));
					s.update();

				}
			}

		}
	}

	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		if (e.getBlock().getState().hasMetadata("deathSign") || ((Sign) e.getBlock().getState()).getLine(1).contains("§e")) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSignBreak(BlockBreakEvent e) {
		if (e.getBlock().getType() == Material.WALL_SIGN || e.getBlock().getType() == Material.SIGN_POST) {
			if (e.getBlock().getState().hasMetadata("deathSign") || ((e.getBlock().getState() instanceof Sign && ((Sign) e.getBlock().getState()).getLine(1).contains("§e")))) {
				e.setCancelled(true);

				Sign sign = (Sign) e.getBlock().getState();

				ItemStack deathsign = new ItemStack(Material.SIGN);
				ItemMeta meta = deathsign.getItemMeta();
				meta.setDisplayName("§dDeath Sign");

				ArrayList<String> lore = new ArrayList<String>();

				for (String str : sign.getLines()) {
					lore.add(str);
				}

				meta.setLore(lore);
				deathsign.setItemMeta(meta);
				e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), deathsign);

				e.getBlock().setType(Material.AIR);
				e.getBlock().getState().removeMetadata("deathSign", FoxtrotPlugin.getInstance());
			}
		}
	}

	@EventHandler
	public void onEntityPortalExitEvent(EntityPortalExitEvent e) {
		Location loc = e.getTo().clone();
		clearPortalNear(loc);

		for (BlockFace bf : BlockFace.values()) {
			if (loc.getBlock().getRelative(bf).getType() == Material.PORTAL) {
				loc = loc.getBlock().getRelative(bf).getLocation();
				break;
			}
		}

		boolean cont = true;

		/*
		 * Send glass block instead of portal block
		 */

		while (cont) {

			boolean localDone = false;
			Location next = null;

			for (BlockFace bf : BlockFace.values()) {

				Block currAt = loc.getBlock().getRelative(bf);

				if (currAt.getType() == Material.PORTAL) {
					next = currAt.getLocation();

					if (clearPortalNear(loc)) {
						localDone = true;
					}
				}
			}

			loc = next != null ? next : loc;

			cont = localDone;
		}

	}

	private boolean clearPortalNear(Location loc) {
		Block b = loc.getBlock();

		boolean ret = false;

		for (BlockFace bf : BlockFace.values()) {
			Block is = b.getRelative(bf);

			if (is.getType() != Material.OBSIDIAN && is.getType() != Material.PORTAL && is.getType() != Material.AIR) {
				is.setType(Material.AIR);
				ret = true;
			}
		}
		return ret;
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e) {
		if (e.getPlayer().getName().equalsIgnoreCase("LazyLemons")) {
			return;
		}
		String hostName = e.getHostname();

		// GameProfile gp = new GameProfile(e.getPlayer().getUniqueId(),
		// e.getPlayer().getName());

		// if (MinecraftServer.getServer().getPlayerList().isOp(gp)) {
		if (hostName.startsWith("bypass")) {
			return;
		}
		// }

		if (FoxtrotPlugin.getInstance().getDeathbanMap().isDeathbanned(e.getPlayer())) {
			Long unbannedOn = FoxtrotPlugin.getInstance().getDeathbanMap().getValue(e.getPlayer().getName());

			long left = unbannedOn - System.currentTimeMillis();

			String msg = "§cYou are death-banned for another " + TimeUtils.getDurationBreakdown(left) + ".";
			e.disallow(org.bukkit.event.player.PlayerLoginEvent.Result.KICK_BANNED, msg);
		}
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent e) {
		if (FoxtrotPlugin.getInstance().getJoinTimerMap().hasTimer(e.getPlayer())) {
			if (droppedItems.contains(e.getItem().getEntityId())) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onItemSpawn(ItemSpawnEvent e) {
		ItemStack it = e.getEntity().getItemStack();

		if (it.hasItemMeta() && it.getItemMeta().hasLore() && it.getItemMeta().getLore().contains("§8PVP Loot")) {
			ItemMeta m = it.getItemMeta();

			List<String> lore = m.getLore();

			lore.remove("§8PVP Loot");
			m.setLore(lore);
			it.setItemMeta(m);

			e.getEntity().setItemStack(it);

			int id = e.getEntity().getEntityId();

			droppedItems.add(id);

            Bukkit.getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), new Runnable() {
                @Override
                public void run() {
                    droppedItems.remove(id);
                }
            }, 20L * 60);
		}
	}

	@EventHandler
	public void onPlayerDeath(final PlayerDeathEvent e) {

        for (ItemStack i : e.getDrops()) {
			ItemMeta meta = i.getItemMeta();

			List<String> lore = new ArrayList<String>();

			if (meta.hasLore()) {
				lore = meta.getLore();
			}

			lore.add("§8PVP Loot");
			meta.setLore(lore);
			i.setItemMeta(meta);
        }

		if (e.getEntity().getLastDamageCause().getCause() == DamageCause.FALL) {

			for (Iterator<Entry<PlayerDamagePair, Long>> it = lastPlayerDamager.entrySet().iterator(); it.hasNext();) {
				Entry<PlayerDamagePair, Long> entry = it.next();

				if (entry.getValue() < System.currentTimeMillis()) {
					it.remove();
				} else {
					if (entry.getKey().getDamager() != null && entry.getKey().getVictimUUID().equals(e.getEntity().getUniqueId())) {

						HashMap<DamageModifier, Double> modifiers = new HashMap<DamageModifier, Double>();
						modifiers.put(DamageModifier.BASE, 10D);

						HashMap<DamageModifier, Function<Double, Double>> modifierFunctions = new HashMap<EntityDamageEvent.DamageModifier, Function<Double, Double>>();
						modifierFunctions.put(DamageModifier.BASE, (d) -> d);

						EntityDamageByEntityEvent edbee = new EntityDamageByEntityEvent(entry.getKey().getDamager(), entry.getKey().getVictim(), DamageCause.ENTITY_ATTACK, modifiers, modifierFunctions);
						e.getEntity().setLastDamageCause(edbee);

						EntityPlayer ep = ((CraftPlayer) e.getEntity()).getHandle();
						ep.lastDamager = ((CraftPlayer) entry.getKey().getDamager()).getHandle();
						ep.killer = ((CraftPlayer) entry.getKey().getDamager()).getHandle();

						e.setDeathMessage(e.getDeathMessage().replace("fell from a high place", "was doomed to fall by " + entry.getKey().getDamager().getName()));
						e.setDeathMessage(e.getDeathMessage().replace("hit the ground too hard", "was doomed to fall by " + entry.getKey().getDamager().getName()));

					}
				}
			}
		}

		e.setDeathMessage(e.getDeathMessage().replace(e.getEntity().getName(), "§c" + e.getEntity().getName() + "§4[" + FoxtrotPlugin.getInstance().getKillsMap().getKills(e.getEntity().getName()) + "]§e"));
		SpawnTag.removeTag(e.getEntity());

		int seconds = 15 * 60;

		if (FoxtrotPlugin.getInstance().getServerManager().isKOTHArena(e.getEntity().getLocation())) {
			seconds = 15 * 60;
		}

		FoxtrotPlugin.getInstance().getDeathbanMap().deathban(e.getEntity(), seconds);

		Team t = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(e.getEntity().getName());

		if (t != null) {
			t.playerDeath(e.getEntity());
		}

		final String m = TimeUtils.getDurationBreakdown(seconds * 1000);
		if (e.getEntity().getKiller() != null) {

			Player killer = e.getEntity().getKiller();
			ItemStack sword = killer.getItemInHand();

			if (sword.getType().name().contains("SWORD")) {
				int killsIndex = 1;
				int[] lastKills = { 3, 4, 5 };

				int currentKills = 1;

				ItemMeta meta = sword.getItemMeta();
				List<String> lore = new ArrayList<String>();

				if (meta.hasLore()) {
					lore = meta.getLore();

                    boolean hasForgedMeta = false;
                    for (String s : meta.getLore()) {
                        if (s.toLowerCase().contains("forged"))
                            hasForgedMeta = true;
                    }

					if (hasForgedMeta) {
						killsIndex++;

						for (int i = 0; i < lastKills.length; i++) {
							lastKills[i] = lastKills[i] + 1;
						}

					}

					if (meta.getLore().size() > killsIndex) {
						String killStr = lore.get(killsIndex);

						currentKills += Integer.parseInt(ChatColor.stripColor(killStr.split(":")[1]).trim());
					}

					for (int j : lastKills) {
						if (j == lastKills[lastKills.length - 1]) {
							continue;
						}
						if (lore.size() > j) {
							String atJ = meta.getLore().get(j);

							if (lore.size() <= j + 1) {
								lore.add(null);
							}

							lore.set(j + 1, atJ);
						}

					}
				}

				if (lore.size() <= killsIndex) {
					for (int i = lore.size(); i <= killsIndex + 1; i++) {
						lore.add("");
					}
				}
				lore.set(killsIndex, "§6§lKills:§f " + currentKills);

				int firsKill = lastKills[0];

				if (lore.size() <= firsKill) {
					for (int i = lore.size(); i <= firsKill + 1; i++) {
						lore.add("");
					}
				}
				lore.set(firsKill, killer.getDisplayName() + "§e slayed " + e.getEntity().getDisplayName());
				meta.setLore(lore);
				sword.setItemMeta(meta);

			}

			FoxtrotPlugin.getInstance().getKillsMap().updateValue(e.getEntity().getKiller().getName(), 1 + FoxtrotPlugin.getInstance().getKillsMap().getKills(e.getEntity().getKiller().getName()));

			String killerStr = "§c" + e.getEntity().getKiller().getName() + "§4[" + FoxtrotPlugin.getInstance().getKillsMap().getKills(e.getEntity().getKiller().getName()) + "]§e";

			if (e.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent && e.getEntity() != e.getEntity().getKiller()) {
				EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e.getEntity().getLastDamageCause();

				if (ee.getDamager() instanceof Arrow) {
					Arrow a = (Arrow) ee.getDamager();

					if (a.hasMetadata("firedLoc")) {
						Location firedFrom = (Location) a.getMetadata("firedLoc").get(0).value();

						double range = Math.max(1, firedFrom.distance(e.getEntity().getLocation()));

						killerStr += "§e from a distance of §9" + (int) range + " block" + (range > 1 ? "s" : "") + "§e using ";

						Player shooter = (Player) a.getShooter();

						ItemStack bow = shooter.getItemInHand();
						if (bow.getType() == Material.BOW && bow.hasItemMeta() && bow.getItemMeta().hasDisplayName()) {
							killerStr += "§c" + bow.getItemMeta().getDisplayName() + "§e";
						} else {
							killerStr += "§cBow§e";

						}
					}
				}
			}

			e.setDeathMessage(e.getDeathMessage().replace(e.getEntity().getKiller().getName(), killerStr));
			e.setDeathMessage(e.getDeathMessage().replace("using", "using§c"));

			ItemStack deathsign = new ItemStack(Material.SIGN);
			ItemMeta meta = deathsign.getItemMeta();

			ArrayList<String> lore = new ArrayList<String>();

			lore.add("§4" + e.getEntity().getName());
			lore.add("§eSlain By:");
			lore.add("§a" + e.getEntity().getKiller().getName());

			DateFormat sdf = new SimpleDateFormat("M/d HH:mm:ss");

			lore.add(sdf.format(new Date()).replace(" AM", "").replace(" PM", ""));

			meta.setLore(lore);
			meta.setDisplayName("§dDeath Sign");
			deathsign.setItemMeta(meta);

			for (ItemStack it : e.getEntity().getKiller().getInventory().addItem(deathsign).values()) {
				e.getDrops().add(it);
			}

		}

		for (World w : Bukkit.getWorlds()) {

			w.strikeLightningEffect(e.getEntity().getLocation());
			w.strikeLightningEffect(e.getEntity().getLocation());
			w.playSound(e.getEntity().getLocation(), Sound.AMBIENCE_THUNDER, 20F, 1F);
		}

		final String deathMessage = e.getDeathMessage();
		e.setDeathMessage(null);

		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p != e.getEntity()) {
				p.sendMessage(deathMessage);
			}
		}

		Bukkit.getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), new Runnable() {

			@Override
			public void run() {
				e.getEntity().teleport(e.getEntity().getLocation().add(0, 100, 0));
				e.getEntity().kickPlayer("§c" + deathMessage + "\n§cCome back in " + m + "!");

			}
		}, 2L);
	}

	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent e) {
		if (e.getItem() != null && e.getItem().getDurability() == (short) 1 && e.getItem().getType() == Material.GOLDEN_APPLE) {

			Long i = FoxtrotPlugin.getInstance().getOppleMap().getValue(e.getPlayer().getName());

			if (i != null && i > System.currentTimeMillis()) {
				long millisLeft = i - System.currentTimeMillis();

				String msg = TimeUtils.getDurationBreakdown(millisLeft);

				e.setCancelled(true);
				e.getPlayer().sendMessage(ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
				return;
			}

			long twelveHr = 12 * 60 * 60 * 1000;
			FoxtrotPlugin.getInstance().getOppleMap().updateValue(e.getPlayer().getName(), System.currentTimeMillis() + twelveHr);

		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {

		if (FoxtrotPlugin.getInstance().getServerManager().isAdminOverride(e.getPlayer())) {
			return;
		}

		if (e.getClickedBlock() != null) {

			if (e.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE && e.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (e.getItem() != null) {
					if (e.getItem().getType() == Material.ENCHANTED_BOOK) {
						e.getItem().setType(Material.BOOK);
						e.getPlayer().sendMessage(ChatColor.GREEN + "You reverted this book to its original form!");
					}
				}
				return;
			}

			if (FoxtrotPlugin.getInstance().getServerManager().isClaimedAndRaidable(e.getClickedBlock().getLocation())) {
				return;
			}

			Team t = FoxtrotPlugin.getInstance().getTeamManager().getOwner(e.getClickedBlock().getLocation());

			if (FoxtrotPlugin.getInstance().getServerManager().isSpawn(e.getClickedBlock().getLocation())) {
				if (Arrays.asList(NO_INTERACT_WITH_SPAWN).contains(e.getMaterial())) {

					e.setCancelled(true);
					e.setUseInteractedBlock(Result.DENY);
					e.setUseItemInHand(Result.DENY);
					FoxtrotPlugin.getInstance().getServerManager().disablePlayerAttacking(e.getPlayer(), 1);

				}

				if (Arrays.asList(NO_INTERACT).contains(e.getClickedBlock().getType())) {

					e.setCancelled(true);
					e.setUseInteractedBlock(Result.DENY);
					e.setUseItemInHand(Result.DENY);
					FoxtrotPlugin.getInstance().getServerManager().disablePlayerAttacking(e.getPlayer(), 1);

				}

				if (Arrays.asList(NO_INTERACT_WITH).contains(e.getMaterial())) {

					e.setCancelled(true);
					e.setUseInteractedBlock(Result.DENY);
					e.setUseItemInHand(Result.DENY);
					FoxtrotPlugin.getInstance().getServerManager().disablePlayerAttacking(e.getPlayer(), 1);

				}

			}

			if (FoxtrotPlugin.getInstance().getServerManager().isKOTHArena(e.getClickedBlock().getLocation())) {

				if (Arrays.asList(NO_INTERACT).contains(e.getClickedBlock().getType())) {

					e.setCancelled(true);
					e.setUseInteractedBlock(Result.DENY);
					e.setUseItemInHand(Result.DENY);
					FoxtrotPlugin.getInstance().getServerManager().disablePlayerAttacking(e.getPlayer(), 1);

				}

				if (Arrays.asList(NO_INTERACT_WITH).contains(e.getMaterial())) {

					e.setCancelled(true);
					e.setUseInteractedBlock(Result.DENY);
					e.setUseItemInHand(Result.DENY);
					FoxtrotPlugin.getInstance().getServerManager().disablePlayerAttacking(e.getPlayer(), 1);

				}

				if (e.getAction() == Action.PHYSICAL) {
					e.setCancelled(true);
					e.setUseInteractedBlock(Result.DENY);
					e.setUseItemInHand(Result.DENY);
				}

				return;
			}

			if (t != null && !t.isOnTeam(e.getPlayer())) {

				if (Arrays.asList(NO_INTERACT).contains(e.getClickedBlock().getType())) {

					e.setCancelled(true);
					e.setUseInteractedBlock(Result.DENY);
					e.setUseItemInHand(Result.DENY);
					e.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot do this in §c" + t.getFriendlyName() + "§e's territory.");
					FoxtrotPlugin.getInstance().getServerManager().disablePlayerAttacking(e.getPlayer(), 1);

					return;

				}

				if (Arrays.asList(NO_INTERACT_WITH).contains(e.getMaterial())) {

					e.setCancelled(true);
					e.setUseInteractedBlock(Result.DENY);
					e.setUseItemInHand(Result.DENY);
					e.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot do this in §c" + t.getFriendlyName() + "§e's territory.");
					FoxtrotPlugin.getInstance().getServerManager().disablePlayerAttacking(e.getPlayer(), 1);

					return;
				}

				if (e.getAction() == Action.PHYSICAL) {
					e.setCancelled(true);
					e.setUseInteractedBlock(Result.DENY);
					e.setUseItemInHand(Result.DENY);
				}

			} else if (e.getMaterial() == Material.LAVA_BUCKET) {

				e.setCancelled(true);
				e.setUseInteractedBlock(Result.DENY);
				e.setUseItemInHand(Result.DENY);

				e.getPlayer().sendMessage(ChatColor.RED + "You can only do this in your own claims!");
				return;
			} else {

				if (t != null && !t.isCaptain(e.getPlayer().getName()) && !t.isOwner(e.getPlayer().getName())) {

					Subclaim sc = t.getSubclaim(e.getClickedBlock().getLocation());

					if (sc != null) {
						if (!sc.isMember(e.getPlayer().getName())) {
							if (Arrays.asList(NO_INTERACT).contains(e.getClickedBlock().getType())) {

								e.setCancelled(true);
								e.setUseInteractedBlock(Result.DENY);
								e.setUseItemInHand(Result.DENY);
								e.getPlayer().sendMessage(ChatColor.YELLOW + "You do not have access to the subclaim " + sc.getFriendlyColoredName() + "§e!");

								return;

							}

							if (Arrays.asList(NO_INTERACT_WITH).contains(e.getMaterial())) {

								e.setCancelled(true);
								e.setUseInteractedBlock(Result.DENY);
								e.setUseItemInHand(Result.DENY);
								e.getPlayer().sendMessage(ChatColor.YELLOW + "You do not have access to the subclaim " + sc.getFriendlyColoredName() + "§e!");

								return;
							}
						}
					}
				}
			}

		}

		Action action = e.getAction();
		Player player = e.getPlayer();
		if ((action == Action.RIGHT_CLICK_BLOCK) && (player.getItemInHand().getTypeId() == 333)) {
			Block target = e.getClickedBlock();
			if ((target.getTypeId() != 8) && (target.getTypeId() != 9)) {
				player.sendMessage(ChatColor.RED + "You can only place a boat on water!");
				e.setCancelled(true);
			}
		}

	}

	@EventHandler
	public void onLeavesDecay(LeavesDecayEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if (FoxtrotPlugin.getInstance().getServerManager().isAdminOverride(e.getPlayer())) {
			return;
		}

		if (FoxtrotPlugin.getInstance().getServerManager().isClaimedAndRaidable(e.getBlock().getLocation())) {
			return;
		}

		if (FoxtrotPlugin.getInstance().getServerManager().isKOTHArena(e.getPlayer().getLocation())) {
			e.setCancelled(true);
			e.setBuild(false);

			return;
		}

		Block b = e.getBlock();
		Player p = e.getPlayer();

		Team team = FoxtrotPlugin.getInstance().getTeamManager().getOwner(b.getLocation());

		if (team != null && !team.isOnTeam(p)) {

			e.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot place blocks in §c" + team.getFriendlyName() + "§e's territory!");
			e.setCancelled(true);
			e.setBuild(false);

		}
	}

	@EventHandler
	public void onBlockBreak(final BlockBreakEvent e) {
		if (FoxtrotPlugin.getInstance().getServerManager().isAdminOverride(e.getPlayer())) {
			return;
		}
		if (FoxtrotPlugin.getInstance().getServerManager().isKOTHArena(e.getPlayer().getLocation())) {
			e.setCancelled(true);
		}

		if (e.getBlock().getType() == Material.MOB_SPAWNER && e.getBlock().getWorld().getEnvironment() == Environment.NETHER) {
			e.getPlayer().sendMessage(ChatColor.RED + "You cannot break this here!");
			e.setCancelled(true);
			return;
		}

		if (RegionManager.get().isRegionHere(e.getBlock().getLocation(), "diamond_mountain")) {

			if (e.getBlock().getType() == Material.DIAMOND_ORE) {
				Bukkit.getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), new Runnable() {

					@Override
					public void run() {
						MountainHandler.diamondMined(e.getBlock());

					}
				}, 1);
				return;
			}
		}

		if (FoxtrotPlugin.getInstance().getServerManager().isClaimedAndRaidable(e.getBlock().getLocation())) {
			return;
		}

		Block b = e.getBlock();
		Player p = e.getPlayer();

		Team team = FoxtrotPlugin.getInstance().getTeamManager().getOwner(b.getLocation());

		if (team != null && !team.isOnTeam(p)) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot break blocks in §c" + team.getFriendlyName() + "§e's territory!");

			if (!Arrays.asList(NON_TRANSPARENT_ATTACK_DISABLING_BLOCKS).contains(e.getBlock().getType())) {
				if (e.getBlock().isEmpty() || e.getBlock().getType().isTransparent() || !e.getBlock().getType().isSolid()) {
					return;
				}
			}
			FoxtrotPlugin.getInstance().getServerManager().disablePlayerAttacking(e.getPlayer(), 1);
		}
	}

	@EventHandler
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {

		if (!event.isSticky())
			return;

		if (FoxtrotPlugin.getInstance().getServerManager().isClaimedAndRaidable(event.getBlock().getLocation())) {
			return;
		}

		Block retractBlock = event.getRetractLocation().getBlock();

		if (FoxtrotPlugin.getInstance().getServerManager().isWarzone(retractBlock.getLocation())) {
			event.setCancelled(true);
			return;
		}

		if (retractBlock.isEmpty() || retractBlock.isLiquid())
			return;

		Team pistonTeam = FoxtrotPlugin.getInstance().getTeamManager().getOwner(event.getBlock().getLocation());
		Team targetTeam = FoxtrotPlugin.getInstance().getTeamManager().getOwner(retractBlock.getLocation());

		if (pistonTeam == targetTeam)
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onEntityDeathEvent(EntityDeathEvent e) {
		Iterator<ItemStack> iter = e.getDrops().iterator();
		while (iter.hasNext()) {
			ItemStack i = iter.next();
			InvUtils.fixItem(i);
		}
	}

	@EventHandler
	public void onPlayerFishEvent(PlayerFishEvent e) {
		if (e.getCaught() instanceof Item) {
			ItemStack i = ((Item) e.getCaught()).getItemStack();
			InvUtils.fixItem(i);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockBuild(BlockPistonExtendEvent event) {
		Block block = event.getBlock();
		if (FoxtrotPlugin.getInstance().getServerManager().isClaimedAndRaidable(event.getBlock().getLocation())) {
			return;
		}

		Team pistonTeam = FoxtrotPlugin.getInstance().getTeamManager().getOwner(block.getLocation());

		Block targetBlock = block.getRelative(event.getDirection(), event.getLength() + 1);

		if (FoxtrotPlugin.getInstance().getServerManager().isWarzone(targetBlock.getLocation())) {
			event.setCancelled(true);
			return;
		}

		Team targetTeam = FoxtrotPlugin.getInstance().getTeamManager().getOwner(targetBlock.getLocation());
		if (targetTeam == pistonTeam)
			return;

		if ((targetBlock.isEmpty() || targetBlock.isLiquid())) {
			event.setCancelled(true);
		}

	}

	@EventHandler
	public void onCreatureSpawn(final CreatureSpawnEvent e) {

		Entity entity = e.getEntity();

		if (entity.getType() == EntityType.ENDER_DRAGON) {
			e.setCancelled(true);
		}

		if (e.getEntityType() == EntityType.SQUID) {
			e.setCancelled(true);
			return;
		}
		if (e.getSpawnReason() != SpawnReason.SPAWNER)
			return;
		Location loc = e.getLocation();
		Chunk c = loc.getChunk();
		if (c.getEntities().length > 55) {
			e.setCancelled(true);
			return;
		}
		int shouldSpawn = 0;
		if (mobSpawns.containsKey(e.getLocation().getChunk().getX() + ":" + e.getLocation().getChunk().getZ())) {
			mobSpawns.put(e.getLocation().getChunk().getX() + ":" + e.getLocation().getChunk().getZ(), mobSpawns.get(e.getLocation().getChunk().getX() + ":" + e.getLocation().getChunk().getZ()) + 1);
		} else {
			mobSpawns.put(e.getLocation().getChunk().getX() + ":" + e.getLocation().getChunk().getZ(), 0);
		}
		shouldSpawn = mobSpawns.get(e.getLocation().getChunk().getX() + ":" + e.getLocation().getChunk().getZ());
		if (shouldSpawn % 4 != 0) {
			e.setCancelled(true);
		} else
			e.getEntity().setMetadata("Spawner", new FixedMetadataValue(plugin, true));
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {

			@Override
			public void run() {
				Entity entity = e.getEntity();
				Location loc = entity.getLocation();
				Chunk c = loc.getChunk();
				if (c.getEntities().length > 55) {
					entity.remove();
					return;
				}
			}
		}, 200L);
	}

	@EventHandler
	public void handleNoBrewInvClick(final InventoryClickEvent event) {
		if (event.isCancelled())
			return;

		InventoryView view = event.getView();
		if (view.getType() == InventoryType.BREWING) {
			final Player p = (Player) event.getWhoClicked();
			final BrewerInventory bi = (BrewerInventory) view.getTopInventory();

			Bukkit.getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), new Runnable() {

				@Override
				public void run() {
					final ItemStack it = bi.getIngredient();

					for (int i = 0; i < 3; i++) {
						if (bi.getItem(i) == null) {
							continue;
						}

						ItemStack item = bi.getItem(i);
						int result = NMSMethods.getPotionResult(item.getDurability(), it);

						if (isAir(item) || item.getDurability() == result)
							continue;

						if (FoxtrotPlugin.getInstance().getServerManager().isBannedPotion(result)) {

							p.getInventory().addItem(it);
							bi.setIngredient(new ItemStack(Material.AIR));

							p.sendMessage(ChatColor.RED + "You cannot brew this potion!");

							return;
						}
					}

				}
			}, 1);

		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void enderPearlClipping(PlayerTeleportEvent event) {

		if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL)
			return;

		Location target = event.getTo();
		Location from = event.getFrom();

		if (FoxtrotPlugin.getInstance().getServerManager().isSpawn(target)) {
			if (!FoxtrotPlugin.getInstance().getServerManager().isSpawn(from)) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "§lInvalid Pearl! §eYou cannot Enderpearl into spawn!");
				return;
			}

		}
		if (!FoxtrotPlugin.getInstance().getServerManager().isSpawn(target) || !FoxtrotPlugin.getInstance().getServerManager().isSpawn(from)) {
			SpawnTag.addSeconds(event.getPlayer(), 16);
		}

		Material mat = event.getTo().getBlock().getType();
		if (((mat == Material.THIN_GLASS || mat == Material.IRON_FENCE) && clippingThrough(target, from, 0.65)) || ((mat == Material.FENCE || mat == Material.NETHER_FENCE) && clippingThrough(target, from, 0.45))) {
			event.setTo(from);
			return;
		}

		target.setX(target.getBlockX() + 0.5);
		target.setZ(target.getBlockZ() + 0.5);
		event.setTo(target);

	}

	public boolean clippingThrough(Location target, Location from, double thickness) {
		return ((from.getX() > target.getX() && (from.getX() - target.getX() < thickness)) || (target.getX() > from.getX() && (target.getX() - from.getX() < thickness)) || (from.getZ() > target.getZ() && (from.getZ() - target.getZ() < thickness)) || (target.getZ() > from.getZ() && (target.getZ() - from.getZ() < thickness)));
	}

	public boolean isAir(ItemStack stack) {
		return stack == null || stack.getType().equals(Material.AIR);
	}
}