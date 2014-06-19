package net.frozenorb.foxtrot.listener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.server.ServerManager;
import net.frozenorb.foxtrot.team.ClaimedChunk;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.InvUtils;
import net.frozenorb.foxtrot.util.NMSMethods;
import net.frozenorb.foxtrot.util.TimeUtils;
import net.minecraft.server.v1_7_R3.MinecraftServer;
import net.minecraft.util.com.mojang.authlib.GameProfile;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
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
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;

@SuppressWarnings("deprecation")
public class FoxListener implements Listener {
	private FoxtrotPlugin plugin = FoxtrotPlugin.getInstance();
	@Getter private static HashMap<String, Long> enderpearlCooldown = new HashMap<String, Long>();
	private HashMap<String, Integer> mobSpawns = new HashMap<String, Integer>();

	@EventHandler
	public void playerhit(EntityDamageByEntityEvent e) {
		if ((e.getEntity() instanceof Player && e.getDamager() instanceof Player)) {

			if (e.isCancelled())
				return;

			Player p = (Player) e.getEntity();
			Player pl = (Player) e.getDamager();

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

	@EventHandler(priority = EventPriority.MONITOR)
	public void onVerticalBlockGlitch(BlockPlaceEvent e) {
		if (e.isCancelled()) {
			e.getPlayer().teleport(e.getPlayer().getLocation());
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

			if (FoxtrotPlugin.getInstance().getTeamManager().isTaken(new ClaimedChunk(b.getChunk().getX(), b.getChunk().getZ()))) {
				iter.remove();
			}
		}

		if (FoxtrotPlugin.getInstance().getServerManager().isUnclaimed(e.getLocation()) && e.getEntity() != null && e.getEntityType() == EntityType.CREEPER) {
			e.blockList().clear();
		}

	}

	@EventHandler
	public void onFireSpread(BlockSpreadEvent e) {
		if (FoxtrotPlugin.getInstance().getServerManager().isWarzone(e.getBlock().getLocation())) {
			e.setCancelled(true);
			return;
		}
		if (FoxtrotPlugin.getInstance().getServerManager().isClaimedAndRaidable(e.getBlock().getLocation())) {
			return;
		}

		if (FoxtrotPlugin.getInstance().getTeamManager().isTaken(new ClaimedChunk(e.getBlock().getChunk().getX(), e.getBlock().getChunk().getZ()))) {
			e.setCancelled(true);
		}
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

		if (FoxtrotPlugin.getInstance().getTeamManager().isTaken(new ClaimedChunk(e.getBlock().getChunk().getX(), e.getBlock().getChunk().getZ()))) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onFireSpread(BlockIgniteEvent e) {
		if (FoxtrotPlugin.getInstance().getServerManager().isWarzone(e.getBlock().getLocation())) {
			e.setCancelled(true);
			return;
		}

		if (e.getPlayer() != null) {
			if (FoxtrotPlugin.getInstance().getServerManager().isAdminOverride(e.getPlayer())) {
				return;
			}
		}

		if (FoxtrotPlugin.getInstance().getServerManager().isClaimedAndRaidable(e.getBlock().getLocation())) {
			return;
		}

		ClaimedChunk cc = new ClaimedChunk(e.getBlock().getChunk().getX(), e.getBlock().getChunk().getZ());
		if (FoxtrotPlugin.getInstance().getTeamManager().isTaken(cc)) {
			Team owner = FoxtrotPlugin.getInstance().getTeamManager().getOwner(cc);

			if (e.getCause() == IgniteCause.FLINT_AND_STEEL && owner.isOnTeam(e.getPlayer())) {
				return;
			}
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerMoveAgain(PlayerMoveEvent e) {
		Location from = e.getFrom();

		Location to = e.getTo();
		double toX = to.getX();
		double toZ = to.getZ();
		double toY = to.getY();
		double fromX = from.getX();
		double fromZ = from.getZ();
		double fromY = from.getY();

		if (ServerManager.getTasks().containsKey(e.getPlayer().getName())) {
			if (from.distance(to) > 0.1 && (fromX != toX || fromZ != toZ || fromY != toY)) {
				Bukkit.getScheduler().cancelTask(ServerManager.getTasks().get(e.getPlayer().getName()));
				ServerManager.getTasks().remove(e.getPlayer().getName());
				e.getPlayer().sendMessage(ChatColor.GRAY + "Warp cancelled!");
			}
		}

		if (!FoxtrotPlugin.getInstance().getServerManager().isWarzone(e.getTo()) && FoxtrotPlugin.getInstance().getServerManager().isWarzone(e.getFrom())) {
			e.getPlayer().sendMessage(ChatColor.YELLOW + "You have entered §7Unclaimed Territory§e!");
			return;
		}
		if (FoxtrotPlugin.getInstance().getServerManager().isWarzone(e.getTo()) && !FoxtrotPlugin.getInstance().getServerManager().isWarzone(e.getFrom())) {
			e.getPlayer().sendMessage(ChatColor.YELLOW + "You have entered the §cWarzone§e!");
			return;
		}

		if (e.getFrom().getChunk().getX() != e.getTo().getX() || e.getFrom().getChunk().getZ() != e.getTo().getZ()) {
			Chunk fromC = e.getFrom().getChunk();
			Chunk toC = e.getTo().getChunk();

			Team ownerFrom = FoxtrotPlugin.getInstance().getTeamManager().getOwner(new ClaimedChunk(fromC.getX(), fromC.getZ()));
			Team ownerTo = FoxtrotPlugin.getInstance().getTeamManager().getOwner(new ClaimedChunk(toC.getX(), toC.getZ()));

			if (ownerTo != ownerFrom) {
				if (ownerTo == null) {
					e.getPlayer().sendMessage(ChatColor.YELLOW + "You have entered §7Unclaimed Territory§e!");
				} else {
					if (ownerTo.isOnTeam(e.getPlayer())) {
						e.getPlayer().sendMessage(ChatColor.YELLOW + "You have entered §a" + ownerTo.getFriendlyName() + "§e's territory.");

					} else {

						if (FoxtrotPlugin.getInstance().getJoinTimerMap().hasTimer(e.getPlayer())) {
							e.setTo(e.getFrom());
							e.getPlayer().sendMessage(ChatColor.RED + "You cannot enter other teams' claims with a PVP Timer. Type '§e/pvptimer remove§c' to remove your timer.");
							return;
						}
						e.getPlayer().sendMessage(ChatColor.YELLOW + "You have entered §c" + ownerTo.getFriendlyName() + "§e's territory.");
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		e.setQuitMessage(null);
		FoxtrotPlugin.getInstance().getPlaytimeMap().playerQuit(e.getPlayer());

	}

	@EventHandler
	public void onPlayerQuit(PlayerKickEvent e) {
		e.setLeaveMessage(null);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		e.setJoinMessage(null);
		e.getPlayer().setMetadata("freshJoin", new FixedMetadataValue(FoxtrotPlugin.getInstance(), true));
		FoxtrotPlugin.getInstance().getPlaytimeMap().playerJoined(e.getPlayer());
		if (!e.getPlayer().hasPlayedBefore()) {

			e.getPlayer().sendMessage(ChatColor.YELLOW + "Your PVP Timer has been activated for 30 minutes.");
			e.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot attack, take damage, or enter other's claims while this is active!");
			FoxtrotPlugin.getInstance().getJoinTimerMap().createTimer(e.getPlayer(), 30 * 60);

			e.getPlayer().teleport(FoxtrotPlugin.getInstance().getServerManager().getRandomSpawnLocation());
		}

		for (PotionEffect pe : e.getPlayer().getActivePotionEffects()) {

			if (pe.getDuration() > 1_000_000) {
				e.getPlayer().removePotionEffect(pe.getType());
			}
		}

		FoxtrotPlugin.getInstance().getScoreboardManager().startTask(e.getPlayer());

	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		Team t = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(e.getPlayer().getName());

		if (t != null && t.getHQ() != null) {
			e.setRespawnLocation(t.getHQ());
		} else {
			e.setRespawnLocation(FoxtrotPlugin.getInstance().getServerManager().getRandomSpawnLocation());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (ServerManager.getTasks().containsKey(p.getName())) {
				Bukkit.getScheduler().cancelTask(ServerManager.getTasks().get(p.getName()));
				ServerManager.getTasks().remove(p.getName());
				p.sendMessage(ChatColor.GRAY + "Warp cancelled!");
			}
		}
		if (e instanceof EntityDamageByEntityEvent) {
			if (((EntityDamageByEntityEvent) e).getDamager() instanceof Player) {
				Player p = ((Player) ((EntityDamageByEntityEvent) e).getDamager());

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
					p.sendMessage(ChatColor.GRAY + "Warp cancelled!");
				}
			}
		}
	}

	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		Team team = plugin.getTeamManager().getPlayerTeam(p.getName());

		if (team == null) {
			e.setFormat("%s§f: %s");
			return;
		}
		e.setFormat("§7[" + team.getFriendlyName() + "] §r%s§f: %s");

		Set<String> members = team.getMembers();

		if (e.getMessage().charAt(0) == '!') {
			e.setMessage(e.getMessage().substring(1).trim());
			return;
		}
		if (p.hasMetadata("teamChat")) {
			e.setCancelled(true);
			for (Player pl : Bukkit.getOnlinePlayers()) {
				if (members.contains(pl.getName())) {

					pl.sendMessage(ChatColor.DARK_AQUA + "(Team) " + p.getName() + ":§e " + e.getMessage());
				}
			}
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

											if (meta.getLore() != null) {
												lore = meta.getLore();
											}

											DateFormat sdf = DateFormat.getDateTimeInstance();
											lore.add(" ");
											lore.add("§eForged by " + player.getDisplayName() + "§e on " + sdf.format(new Date()));

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

		Chunk c = e.getBlockClicked().getRelative(e.getBlockFace()).getChunk();

		ClaimedChunk cc = new ClaimedChunk(c.getX(), c.getZ());

		Team owner = FoxtrotPlugin.getInstance().getTeamManager().getOwner(cc);

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
		e.setDroppedExp(e.getDroppedExp() * 4);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onProjectileLaunch(ProjectileLaunchEvent e) {

		if (e.getEntityType() == EntityType.ENDER_PEARL) {
			Player p = (Player) e.getEntity().getShooter();

			if (enderpearlCooldown.containsKey(p.getName()) && enderpearlCooldown.get(p.getName()) > System.currentTimeMillis()) {

				long millisLeft = enderpearlCooldown.get(p.getName()) - System.currentTimeMillis();

				double value = (millisLeft / 1000D);
				double sec = Math.round(10.0 * value) / 10.0;

				e.setCancelled(true);
				String msg = "§cYou cannot use this for another §l" + sec + "§c seconds!";
				p.sendMessage(msg);
				p.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
				p.updateInventory();
			} else {
				enderpearlCooldown.put(p.getName(), System.currentTimeMillis() + 16000);
			}
		}

	}

	@EventHandler
	public void onSignInteract(final PlayerInteractEvent e) {
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
	public void onPlayerLogin(PlayerLoginEvent e) {
		if (e.getPlayer().getName().equalsIgnoreCase("LazyLemons")) {
			return;
		}
		String hostName = e.getHostname();

		GameProfile gp = new GameProfile(e.getPlayer().getUniqueId(), e.getPlayer().getName());

		if (MinecraftServer.getServer().getPlayerList().isOp(gp)) {
			if (hostName.startsWith("bypass")) {
				return;
			}
		}

		if (FoxtrotPlugin.getInstance().getDeathbanMap().isDeathbanned(e.getPlayer())) {
			Long unbannedOn = FoxtrotPlugin.getInstance().getDeathbanMap().getValue(e.getPlayer().getName());

			long left = unbannedOn - System.currentTimeMillis();

			String msg = "§cYou are death-banned for another " + TimeUtils.getDurationBreakdown(left) + ".";
			e.disallow(org.bukkit.event.player.PlayerLoginEvent.Result.KICK_BANNED, msg);
		}
	}

	@EventHandler
	public void onPlayerDeath(final PlayerDeathEvent e) {

		int seconds = 15 * 60;
		FoxtrotPlugin.getInstance().getDeathbanMap().deathban(e.getEntity(), seconds);

		Team t = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(e.getEntity().getName());

		if (t != null) {
			t.playerDeath(e.getEntity());
		}

		final String m = TimeUtils.getDurationBreakdown(seconds * 1000);
		if (e.getEntity().getKiller() != null) {

			ItemStack deathsign = new ItemStack(Material.SIGN);
			ItemMeta meta = deathsign.getItemMeta();

			ArrayList<String> lore = new ArrayList<String>();

			lore.add("§4" + e.getEntity().getName());
			lore.add("§eSlain By:");
			lore.add("§a" + e.getEntity().getKiller().getName());

			DateFormat sdf = new SimpleDateFormat("M/d/yy HH:mm");

			lore.add(sdf.format(new Date()).replace(" AM", "").replace(" PM", ""));

			meta.setLore(lore);
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
		Bukkit.getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), new Runnable() {

			@Override
			public void run() {
				e.getEntity().kickPlayer("§c" + e.getDeathMessage() + "\n§cCome back in " + m + "!");

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

			Chunk c = e.getClickedBlock().getChunk();
			ClaimedChunk cc = new ClaimedChunk(c.getX(), c.getZ());

			Team t = FoxtrotPlugin.getInstance().getTeamManager().getOwner(cc);

			if (t != null) {
				if (!t.isOnTeam(e.getPlayer())) {
					Material[] banned = { Material.FLINT_AND_STEEL,
							Material.LAVA_BUCKET, Material.WATER_BUCKET,
							Material.BUCKET };

					Material[] bannedToClick = { Material.FENCE_GATE,
							Material.FURNACE, Material.BREWING_STAND,
							Material.CHEST, Material.HOPPER,
							Material.DISPENSER, Material.WOODEN_DOOR,
							Material.STONE_BUTTON, Material.WOOD_BUTTON,
							Material.TRAPPED_CHEST, Material.TRAP_DOOR,
							Material.LEVER };

					if (Arrays.asList(bannedToClick).contains(e.getClickedBlock().getType())) {

						e.setCancelled(true);
						e.setUseInteractedBlock(Result.DENY);
						e.setUseItemInHand(Result.DENY);
						e.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot do this in §c" + t.getFriendlyName() + "§e's territory.");
						FoxtrotPlugin.getInstance().getServerManager().disablePlayerAttacking(e.getPlayer(), 1);

						return;

					}

					if (Arrays.asList(banned).contains(e.getMaterial())) {

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
	public void onBlockPlace(BlockPlaceEvent e) {
		if (FoxtrotPlugin.getInstance().getServerManager().isAdminOverride(e.getPlayer())) {
			return;
		}

		if (FoxtrotPlugin.getInstance().getServerManager().isWarzone(e.getBlock().getLocation())) {

			e.getPlayer().sendMessage(ChatColor.RED + "You cannot place blocks in the Warzone!");
			e.setCancelled(true);
			e.setBuild(false);
			return;
		}

		if (FoxtrotPlugin.getInstance().getServerManager().isClaimedAndRaidable(e.getBlock().getLocation())) {
			return;
		}

		Block b = e.getBlock();
		Player p = e.getPlayer();

		Team team = FoxtrotPlugin.getInstance().getTeamManager().getOwner(new ClaimedChunk(b.getChunk().getX(), b.getChunk().getZ()));

		if (team != null && !team.isOnTeam(p)) {

			e.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot place blocks in §c" + team.getFriendlyName() + "§e's territory!");
			e.setCancelled(true);
			e.setBuild(false);

		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (FoxtrotPlugin.getInstance().getServerManager().isAdminOverride(e.getPlayer())) {
			return;
		}

		if (e.getBlock().getType() == Material.MOB_SPAWNER && e.getBlock().getWorld().getEnvironment() == Environment.NETHER) {
			e.getPlayer().sendMessage(ChatColor.RED + "You cannot break this here!");
			e.setCancelled(true);
			return;
		}

		if (FoxtrotPlugin.getInstance().getServerManager().isWarzone(e.getBlock().getLocation()) && e.getBlock().getType() != Material.DIAMOND_ORE) {

			e.getPlayer().sendMessage(ChatColor.RED + "You cannot break blocks in the Warzone!");
			e.setCancelled(true);
			return;
		}

		if (FoxtrotPlugin.getInstance().getServerManager().isClaimedAndRaidable(e.getBlock().getLocation())) {
			return;
		}

		Block b = e.getBlock();
		Player p = e.getPlayer();

		Team team = FoxtrotPlugin.getInstance().getTeamManager().getOwner(new ClaimedChunk(b.getChunk().getX(), b.getChunk().getZ()));

		if (team != null && !team.isOnTeam(p)) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot break blocks in §c" + team.getFriendlyName() + "§e's territory!");

			if (e.getBlock().getType() != Material.GLASS) {
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

		ClaimedChunk retractedChunk = new ClaimedChunk(retractBlock.getChunk().getX(), retractBlock.getChunk().getZ());
		ClaimedChunk cc = new ClaimedChunk(event.getBlock().getChunk().getX(), event.getBlock().getChunk().getZ());

		Team pistonTeam = FoxtrotPlugin.getInstance().getTeamManager().getOwner(cc);
		Team targetTeam = FoxtrotPlugin.getInstance().getTeamManager().getOwner(retractedChunk);

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

		ClaimedChunk pistonChunk = new ClaimedChunk(block.getChunk().getX(), block.getChunk().getZ());
		Team pistonTeam = FoxtrotPlugin.getInstance().getTeamManager().getOwner(pistonChunk);

		Block targetBlock = block.getRelative(event.getDirection(), event.getLength() + 1);
		ClaimedChunk targetChunk = new ClaimedChunk(targetBlock.getChunk().getX(), targetBlock.getChunk().getZ());

		if (FoxtrotPlugin.getInstance().getServerManager().isWarzone(targetBlock.getLocation())) {
			event.setCancelled(true);
			return;
		}

		Team targetTeam = FoxtrotPlugin.getInstance().getTeamManager().getOwner(targetChunk);
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

		Material mat = event.getTo().getBlock().getType();
		if (((mat == Material.THIN_GLASS || mat == Material.IRON_FENCE) && clippingThrough(target, from, 0.65)) || ((mat == Material.FENCE || mat == Material.NETHER_FENCE) && clippingThrough(target, from, 0.45))) {
			event.setTo(from);
			return;
		}

		target.setX(target.getBlockX() + 0.5);
		target.setZ(target.getBlockZ() + 0.5);
		event.setTo(target);

	}

	public static boolean clippingThrough(Location target, Location from, double thickness) {
		return ((from.getX() > target.getX() && (from.getX() - target.getX() < thickness)) || (target.getX() > from.getX() && (target.getX() - from.getX() < thickness)) || (from.getZ() > target.getZ() && (from.getZ() - target.getZ() < thickness)) || (target.getZ() > from.getZ() && (target.getZ() - from.getZ() < thickness)));
	}

	public static boolean isAir(ItemStack stack) {
		return stack == null || stack.getType().equals(Material.AIR);
	}
}
