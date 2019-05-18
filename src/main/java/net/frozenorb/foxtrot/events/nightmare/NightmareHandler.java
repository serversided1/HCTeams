package net.frozenorb.foxtrot.events.nightmare;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.events.nightmare.generator.EmptyWorldGenerator;
import net.frozenorb.foxtrot.events.nightmare.mob.MobLogicTask;
import net.frozenorb.foxtrot.events.nightmare.mob.MobReferenceTask;
import net.frozenorb.foxtrot.events.nightmare.packet.BlockChangePacketHandler;
import net.frozenorb.foxtrot.events.nightmare.packet.EntitySpawnPacketHandler;
import net.frozenorb.foxtrot.events.nightmare.progress.ProgressData;
import net.frozenorb.foxtrot.events.nightmare.progress.ProgressLogicTask;
import net.frozenorb.foxtrot.events.nightmare.thread.FakeBlockThread;
import net.frozenorb.foxtrot.events.nightmare.thread.FakeWallThread;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.JsonBuilder;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.frozenorb.qlib.util.ItemBuilder;
import net.hylist.HylistSpigot;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy;
import net.minecraft.util.com.google.gson.JsonArray;
import net.minecraft.util.com.google.gson.JsonElement;
import net.minecraft.util.com.google.gson.JsonObject;
import net.minecraft.util.com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Getter
public class NightmareHandler implements Listener {

	private static final List<String> DISABLED_COMMANDS = Arrays.asList(
			"f leave",
			"t leave",
			"team leave",
			"faction leave",
			"f disband",
			"t disband",
			"team disband",
			"faction disband"
	);

	private Map<Integer, List<Location>> wallLocations;
	private Map<Integer, Location> coreLocations;
	private List<Location> spawnerLocations;
	private Map<UUID, Location> previousLocations;
	private Map<UUID, ProgressData> playerProgress;
	private World world;
	private Team systemTeam;
	@Setter private boolean adminDisabled;

	public NightmareHandler() {
		this.wallLocations = new HashMap<>();
		this.coreLocations = new HashMap<>();
		this.spawnerLocations = new ArrayList<>();
		this.previousLocations = new HashMap<>();
		this.playerProgress = new HashMap<>();

		setupWorld();

		Foxtrot.getInstance().getServer().getPluginManager().registerEvents(this, Foxtrot.getInstance());
		FrozenCommandHandler.registerPackage(Foxtrot.getInstance(), "net.frozenorb.foxtrot.events.nightmare.command");

		loadConfig();
	}

	public void lateInitialization() {
		// Get the Nightmare event team
		systemTeam = Foxtrot.getInstance().getTeamHandler().getTeam("Nightmare");

		// Find and store all spawner locations within system teams' claims
		Foxtrot.getInstance().getLogger().info("Searching for spawner locations in claims...");

		for (Claim claim : systemTeam.getClaims()) {
			Location min = claim.getMinimumPoint();
			Location max = claim.getMaximumPoint();

			for (int x = min.getBlockX(); x < max.getBlockX() + 1; x++) {
				for (int y = 0; y < 255; y++) {
					for (int z = min.getBlockZ(); z < max.getBlockZ() + 1; z++) {
						if (world.getBlockAt(x, y, z).getType() == Material.MOB_SPAWNER) {
							spawnerLocations.add(new Location(world, x, y, z));
						}
					}
				}
			}
		}

		Foxtrot.getInstance().getLogger().info("Found " + spawnerLocations.size() + " spawners in claims.");

		// Start thread that handles stage walls
		new FakeWallThread(this).start();

		// Start thread that handles block visibility
		new FakeBlockThread(this).start();

		// Start task that handles progress logic
		new ProgressLogicTask(this).runTaskTimerAsynchronously(Foxtrot.getInstance(), 0L, 10L);

		// Start task that handles mob AI
		new MobLogicTask(this).runTaskTimerAsynchronously(Foxtrot.getInstance(), 0L, 20L);

		// Entity reference cleanup
		new MobReferenceTask(this).runTaskTimerAsynchronously(Foxtrot.getInstance(), 0L, 20L * 5);

		// Intercept block change packets at fake air block locations sent
		// by a relative block update to cancel the appearance of a laggy block
		HylistSpigot.INSTANCE.addPacketHandler(new BlockChangePacketHandler(this));

		// Intercept entity spawn packets sent that try to show entities which
		// shouldn't be shown to this player
		HylistSpigot.INSTANCE.addPacketHandler(new EntitySpawnPacketHandler(this));
	}

	private void setupWorld() {
		// Create the world (use EmptyWorldGenerator)
		this.world = new WorldCreator(Foxtrot.getInstance().getConfig().getString("nightmare.worldName", "NightmareEvent"))
				.environment(World.Environment.NETHER)
				.generator(new EmptyWorldGenerator())
				.createWorld();

		// Clear the world of entities
		for (Entity entity : world.getEntities()) {
			entity.remove();
		}
	}

	private void loadConfig() {
		try {
			Path path = Paths.get(Foxtrot.getInstance().getDataFolder().getAbsolutePath() + File.separator + "nightmareConfig.json");

			if (!path.toFile().exists()) {
				saveConfig();
				return;
			}

			JsonObject json = new JsonParser().parse(new String(Files.readAllBytes(path))).getAsJsonObject();

			for (JsonElement wallElement : json.getAsJsonArray("walls")) {
				JsonObject wallObject = wallElement.getAsJsonObject();
				JsonArray locationsArray = wallObject.getAsJsonArray("locations");

				List<Location> locations = new ArrayList<>();

				for (JsonElement locationElement : locationsArray) {
					JsonObject locationObject = locationElement.getAsJsonObject();
					locations.add(new Location(world, locationObject.get("x").getAsInt(), locationObject.get("y").getAsInt(), locationObject.get("z").getAsInt()));
				}

				wallLocations.put(wallObject.get("stage").getAsInt(), locations);
			}

			for (JsonElement coreElement : json.getAsJsonArray("cores")) {
				JsonObject coreObject = coreElement.getAsJsonObject();
				JsonObject locationObject = coreObject.get("location").getAsJsonObject();

				coreLocations.put(coreObject.get("stage").getAsInt(), new Location(world, locationObject.get("x").getAsInt(), locationObject.get("y").getAsInt(), locationObject.get("z").getAsInt()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveConfig() {
		try {
			JsonArray wallsArray = new JsonArray();

			for (Map.Entry<Integer, List<Location>> entry : wallLocations.entrySet()) {
				JsonArray locationsArray = new JsonArray();

				for (Location location : entry.getValue()) {
					locationsArray.add(new JsonBuilder().addProperty("x", location.getBlockX())
					                                    .addProperty("y", location.getBlockY())
					                                    .addProperty("z", location.getBlockZ())
					                                    .get());
				}

				wallsArray.add(new JsonBuilder().addProperty("stage", entry.getKey())
				                                .add("locations", locationsArray)
				                                .get());
			}

			JsonArray coresArray = new JsonArray();

			for (Map.Entry<Integer, Location> entry : coreLocations.entrySet()) {
				JsonObject locationObject = new JsonBuilder().addProperty("x", entry.getValue().getBlockX())
				                                             .addProperty("y", entry.getValue().getBlockY())
				                                             .addProperty("z", entry.getValue().getBlockZ())
				                                             .get();

				coresArray.add(new JsonBuilder().addProperty("stage", entry.getKey())
				                                .add("location", locationObject)
				                                .get());
			}

			JsonObject json = new JsonBuilder().add("walls", wallsArray)
			                                   .add("cores", coresArray)
			                                   .get();

			Path path = Paths.get(Foxtrot.getInstance().getDataFolder().getAbsolutePath() + File.separator + "nightmareConfig.json");

			Files.write(path, json.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void safeShutdown() {
		for (Player player : world.getPlayers()) {
			if (hasProgression(player)) {
				player.teleport(previousLocations.get(player.getUniqueId()));
			}
		}
	}

	public ItemStack getPotionItemStack() {
		return ItemBuilder.of(Material.POTION)
		                  .name(ChatColor.DARK_RED + "Potion of Dreams")
		                  .data((short) 8262)
		                  .addToLore(ChatColor.DARK_PURPLE + ChatColor.ITALIC.toString() + "Directions:")
		                  .addToLore(ChatColor.DARK_PURPLE + ChatColor.ITALIC.toString() + "Make sure someone brings a pick")
		                  .addToLore(ChatColor.DARK_PURPLE + ChatColor.ITALIC.toString() + "Only drink this if it's nighttime in the world")
		                  .addToLore(ChatColor.DARK_PURPLE + ChatColor.ITALIC.toString() + "Otherwise you will not be able to sleep in a bed")
		                  .build();
	}

	public boolean hasProgression(UUID uuid) {
		return playerProgress.containsKey(uuid);
	}

	public boolean hasProgression(Player player) {
		return hasProgression(player.getUniqueId());
	}

	/**
	 * Finds an existing {@link ProgressData} object linked
	 * to the given player UUID or any member of a team
	 * that the player UUID belongs to. If an existing object
	 * cannot be found, one is made.
	 *
	 * @param playerUuid the player UUID
	 * @return a {@link ProgressData} object linked to the given player UUID
	 */
	public ProgressData getOrCreateProgression(UUID playerUuid) {
		if (playerProgress.containsKey(playerUuid)) {
			return playerProgress.get(playerUuid);
		}

		Team team = Foxtrot.getInstance().getTeamHandler().getTeam(playerUuid);

		if (team != null) {
			for (UUID memberUuid : team.getMembers()) {
				if (playerProgress.containsKey(memberUuid)) {
					return playerProgress.get(memberUuid);
				}
			}
		}

		return new ProgressData(playerUuid);
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
		if (hasProgression(event.getPlayer()) && event.getMessage().length() > 1) {
			String command = event.getMessage().toLowerCase().substring(1, event.getMessage().length());

			if (DISABLED_COMMANDS.contains(command)) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "You can't use that command in the Nightmare event.");
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
		if (event.getItem().isSimilar(Foxtrot.getInstance().getNightmareHandler().getPotionItemStack())) {
			Player player = event.getPlayer();

			if (adminDisabled) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "An admin has disabled the Nightmare event, try again later.");
				return;
			}

			// Don't allow the player to drink the potion if their progress
			// is already being tracked
			if (hasProgression(player)) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You already drank one of these...");
				return;
			}

			// Don't allow the player to start the event if they don't have a team
			if (Foxtrot.getInstance().getTeamHandler().getTeam(player) == null) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You can't enter the dream world if you don't have a team.");
				return;
			}

			// Don't allow the player to start the event if they are on PvP timer
			if (Foxtrot.getInstance().getStartingPvPTimerMap().get(player.getUniqueId()) || Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You can't enter the dream world whilst you have PvP protection.");
				return;
			}

			player.sendMessage(ChatColor.RED + "You drink the the Potion of Dreams.");
			player.sendMessage(ChatColor.RED + "You're starting to feel awfully tired...");

			// Give the player effects
			//
			// Blindness and confusion gets removed when
			// the player enters the world
			//
			// If the player never enters the world, the
			// effects act as a punishment for wasting
			// the potion
			player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 60 * 10, 1));
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 60 * 15, 0));
			player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 60 * 15, 0));

			// Put player in map
			playerProgress.put(player.getUniqueId(), getOrCreateProgression(player.getUniqueId()));
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerBedEnterEvent(PlayerBedEnterEvent event) {
		if (hasProgression(event.getPlayer())) {
			Player player = event.getPlayer();

			if (player.getInventory().first(Material.ENDER_PEARL) != -1) {
				player.sendMessage(ChatColor.RED + "You can't take Ender Pearls into the Nightmare event!");
				event.setCancelled(true);
				return;
			}

			// Set 'AwaitingTP' metadata to prevent players from
			// spamming beds and being teleported multiple times
			if (player.hasMetadata("AwaitingTP")) {
				event.setCancelled(true);
				return;
			}

			player.setMetadata("AwaitingTP", new FixedMetadataValue(Foxtrot.getInstance(), System.currentTimeMillis()));
			player.sendMessage(ChatColor.RED + "You start to doze off...");

			// Store player's location
			previousLocations.put(event.getPlayer().getUniqueId(), event.getPlayer().getLocation());

			// Schedule task that teleports the player, removes
			// their potion effects, and removes their metadata
			Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
				((CraftPlayer) player).getHandle().allowServerSidePhase = true;
				player.teleport(systemTeam.getHQ());
				player.removePotionEffect(PotionEffectType.BLINDNESS);
				player.removePotionEffect(PotionEffectType.CONFUSION);
				player.removeMetadata("AwaitingTP", Foxtrot.getInstance());
			}, 20L * 3);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreakEvent(BlockBreakEvent event) {
		if (event.getBlock().getWorld().equals(world)) {
			Player player = event.getPlayer();

			// Check if DTR bitmask applies
			if (!DTRBitmask.NIGHTMARE.appliesAt(event.getBlock().getLocation())) {
				if (player.getGameMode() != GameMode.CREATIVE) {
					event.setCancelled(true);
				}

				return;
			}

			// Check if player has progression
			if (!hasProgression(player)) {
				if (player.getGameMode() == GameMode.CREATIVE) {
					event.setCancelled(false);
				} else {
					event.setCancelled(true);
				}

				return;
			}

			// Will always be cancelled from here on
			event.setCancelled(true);

			ProgressData progressData = getOrCreateProgression(player.getUniqueId());

			// If this block is already mined, don't let them keep mining it
			if (progressData.getAirBlocks().contains(event.getBlock().getLocation())) {
				return;
			}

			if (event.getBlock().getType() == Material.OBSIDIAN) {
				// Drop an obsidian block to simulate that the block was actually broken
				Item item = event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.OBSIDIAN));
				progressData.getDroppedItems().add(item);

				// Hide the dropped item manually for every player in the world that
				// doesn't belong to the same team as the player that broke the block does
				for (Player inWorld : world.getPlayers()) {
					if (!progressData.getParticipants().contains(inWorld.getUniqueId())) {
						destroyEntity(inWorld, item);
					}
				}
			} else if (event.getBlock().getType() == Material.WOOL && event.getBlock().getData() == 14) {
				if (!event.getBlock().getLocation().equals(coreLocations.get(progressData.getStagesComplete() + 1))) {
					return;
				}

				progressData.setStagesComplete(progressData.getStagesComplete() + 1);

				for (UUID participantUuid : progressData.getParticipants()) {
					Player participant = Bukkit.getPlayer(participantUuid);
					participant.sendMessage(ChatColor.GREEN + "A core has been destroyed! Stage: " + ChatColor.GRAY + "(" + ChatColor.GREEN + progressData.getStagesComplete() + ChatColor.GRAY + "/" + ChatColor.GREEN + 5 + ChatColor.GRAY + ")");
				}
			} else {
				return;
			}

			// Add block location to air blocks list
			progressData.getAirBlocks().add(event.getBlock().getLocation());
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		if (event.getBlockPlaced().getWorld().equals(world)) {
			Player player = event.getPlayer();

			if (hasProgression(player) || player.getGameMode() != GameMode.CREATIVE) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.getPlayer().getWorld().equals(world)) {
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
				if (event.getItem() != null && event.getItem().getType() == Material.ENDER_PEARL) {
					event.setCancelled(true);
					event.getPlayer().sendMessage(ChatColor.RED + "You can't use Ender Pearls during the Nightmare event!");
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onProjectileLaunchEvent(ProjectileLaunchEvent event) {
		if (event.getEntity().getWorld().equals(world)) {
			if (event.getEntity().getShooter() instanceof Player) {
				Player shooter = (Player) event.getEntity().getShooter();

				if (hasProgression(shooter)) {
					ProgressData progressData = getOrCreateProgression(shooter.getUniqueId());

					for (Player player : world.getPlayers()) {
						if (!progressData.getParticipants().contains(player.getUniqueId())) {
							destroyEntity(player, event.getEntity());
						}
					}
				} else {
					for (Player player : world.getPlayers()) {
						destroyEntity(player, event.getEntity());
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player victim = (Player) event.getEntity();

			if (hasProgression(victim)) {
				if (event.getDamager() instanceof Player) {
					Player killer = (Player) event.getDamager();

					// Prevent players from attacking each other
					if (hasProgression(killer)) {
						event.setCancelled(true);
					}
				} else if (event.getDamager() instanceof Projectile) {
					if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
						Player killer = (Player) ((Projectile) event.getDamager()).getShooter();

						// Prevent players from attacking each other
						if (hasProgression(killer)) {
							event.setCancelled(true);
						}
					}
				} else {
					ProgressData progressData = getOrCreateProgression(victim.getUniqueId());

					// Prevent mobs not assigned to a team's progress from attacking the team's players
					if (!progressData.isTrackedId(event.getDamager().getEntityId())) {
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent event) {
		// Remove the player from the progress map
		ProgressData progressData = playerProgress.remove(event.getEntity().getUniqueId());

		if (progressData != null) {
			Player player = event.getEntity();

			if (player.getWorld().equals(world)) {
				((CraftPlayer) player).getHandle().allowServerSidePhase = false;

				// Store items dropped by the event and drop them naturally
				List<Item> itemEntities = new ArrayList<>();

				for (ItemStack itemStack : event.getDrops()) {
					itemEntities.add(world.dropItemNaturally(event.getEntity().getLocation(), itemStack));
				}

				// Clear items dropped by the event
				event.getDrops().clear();

				// Hide items for all players that are not in the same team
				for (Player inWorld : world.getPlayers()) {
					if (!progressData.getParticipants().contains(inWorld.getUniqueId())) {
						for (Item item : itemEntities) {
							destroyEntity(inWorld, item);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onEntityDeathEvent(EntityDeathEvent event) {
		if (event.getEntity().getLocation().getWorld().equals(world)) {
			// Don't drop any mob items
			if (!(event.getEntity() instanceof Player)) {
				event.getDrops().clear();
			}

			// Give XP to killer if not null
			if (event.getEntity().getKiller() != null) {
				event.getEntity().getKiller().giveExp(event.getDroppedExp());
			}

			// Don't drop XP orbs
			event.setDroppedExp(0);
		}
	}

	@EventHandler
	public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
		if (event.getFrom().getWorld().equals(world) && event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
			ProgressData progressData = playerProgress.remove(event.getPlayer().getUniqueId());

			if (progressData != null) {
				((CraftPlayer) event.getPlayer()).getHandle().allowServerSidePhase = false;
				event.setTo(previousLocations.get(event.getPlayer().getUniqueId()));
				event.getPlayer().sendMessage(ChatColor.RED + "Congratulations! You've finished the Nightmare event!");
			}
		}
	}

	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		if (event.getTo().getWorld().equals(world) && hasProgression(event.getPlayer())) {
			Location toLoc = event.getTo();
			ProgressData progressData = getOrCreateProgression(event.getPlayer().getUniqueId());

			for (Map.Entry<Integer, List<Location>> wallEntry : wallLocations.entrySet()) {
				if (progressData.getStagesComplete() < wallEntry.getKey()) {
					if (wallEntry.getValue().contains(new Location(world, toLoc.getBlockX(), toLoc.getBlockY(), toLoc.getBlockZ()))) {
						event.setTo(event.getFrom());
						event.getPlayer().sendMessage(ChatColor.RED + "You haven't completed this stage yet!");
						return;
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		// Remove the player from the progress map
		ProgressData progressData = playerProgress.remove(event.getPlayer().getUniqueId());

		if (progressData != null) {
			Player player = event.getPlayer();

			// Remove the player's UUID from the participants list
			progressData.getParticipants().remove(player.getUniqueId());

			// If the player is in the Nightmare event world
			// teleport the player back to their previous location
			if (player.getWorld().equals(world)) {
				player.teleport(previousLocations.remove(event.getPlayer().getUniqueId()));
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		if (event.getPlayer().getWorld().equals(world) && hasProgression(event.getPlayer())) {
			ProgressData progressData = getOrCreateProgression(event.getPlayer().getUniqueId());
			progressData.getDroppedItems().add(event.getItemDrop());

			// Hide the dropped item manually for every player in the world that
			// doesn't belong to the same team as the player that dropped the item does
			for (Player inWorld : world.getPlayers()) {
				if (!progressData.getParticipants().contains(inWorld.getUniqueId())) {
					destroyEntity(inWorld, event.getItemDrop());
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
		if (event.getPlayer().getWorld().equals(world) && hasProgression(event.getPlayer())) {
			ProgressData progressData = getOrCreateProgression(event.getPlayer().getUniqueId());

			// Don't allow the player to pickup the item if it doesn't belong
			// to the player's team
			if (!progressData.getDroppedItems().remove(event.getItem())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
		if (event.getLocation().getWorld().equals(world)) {
			if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER ||
			    event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CHUNK_GEN ||
			    event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
				event.setCancelled(true);
			}
		}
	}

	public void progressCleanup(UUID uuid, ProgressData progressData) {
		progressData.getParticipants().remove(uuid);

		if (progressData.getParticipants().isEmpty()) {
			for (Entity entity : progressData.getTrackedEntities()) {
				entity.remove();
			}
		}
	}

	private static void destroyEntity(Player player, Entity entity) {
		PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entity.getEntityId());
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

}
