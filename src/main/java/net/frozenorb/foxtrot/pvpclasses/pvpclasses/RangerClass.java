package net.frozenorb.foxtrot.pvpclasses.pvpclasses;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.EnderpearlCooldownHandler;
import net.frozenorb.foxtrot.pvpclasses.PvPClass;
import net.frozenorb.foxtrot.pvpclasses.PvPClassHandler;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.qlib.nametag.FrozenNametagHandler;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class RangerClass extends PvPClass {

	private static final int TAG_SECONDS = 15;
	private static final int HIT_COOLDOWN_SECONDS = 20;
	private static final int MISS_COOLDOWN_SECONDS = 10;

	private static Map<String, Long> lastSpeedUsage = new HashMap<>();
	private static Map<String, Long> lastJumpUsage = new HashMap<>();
	private static Map<UUID, Long> throwCooldown = new HashMap<>();
	@Getter private static Map<UUID, Long> markedPlayers = new ConcurrentHashMap<>();

	public RangerClass() {
		super("Ranger", 5, Arrays.asList(Material.SUGAR, Material.FEATHER));
	}

	@Override
	public boolean qualifies(PlayerInventory armor) {
		return wearingAllArmor(armor) &&
		       armor.getHelmet().getType() == Material.CHAINMAIL_HELMET &&
		       armor.getChestplate().getType() == Material.LEATHER_CHESTPLATE &&
		       armor.getLeggings().getType() == Material.LEATHER_LEGGINGS &&
		       armor.getBoots().getType() == Material.CHAINMAIL_BOOTS;
	}

	@Override
	public void apply(Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1), true);
		player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0), true);
	}

	@Override
	public void tick(Player player) {
		if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
		}

		if (!player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
		}
	}

	@Override
	public boolean itemConsumed(Player player, Material material) {
		if (material == Material.SUGAR) { // SPEED
			if (lastSpeedUsage.containsKey(player.getName()) && lastSpeedUsage.get(player.getName()) > System.currentTimeMillis()) {
				long millisLeft = lastSpeedUsage.get(player.getName()) - System.currentTimeMillis();
				String msg = TimeUtils.formatIntoDetailedString((int) millisLeft / 1000);
				player.sendMessage(ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
				return (false);
			}

			lastSpeedUsage.put(player.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30));
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 10, 3), true);
			return (true);
		} else { // JUMP BOOST
			if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
				player.sendMessage(ChatColor.RED + "You can't use this in spawn!");
				return (false);
			}

			if (lastJumpUsage.containsKey(player.getName()) && lastJumpUsage.get(player.getName()) > System.currentTimeMillis()) {
				long millisLeft = lastJumpUsage.get(player.getName()) - System.currentTimeMillis();
				String msg = TimeUtils.formatIntoDetailedString((int) millisLeft / 1000);
				player.sendMessage(ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
				return (false);
			}

			lastJumpUsage.put(player.getName(), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1));
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 10, 3));
			return (true);
		}
	}

	@EventHandler
	public void onProjectileLaunchEvent(ProjectileLaunchEvent event) {
		if (event.getEntity() instanceof Snowball && event.getEntity().getShooter() instanceof Player) {
			final Snowball snowball = (Snowball) event.getEntity();
			final Player shooter = (Player) event.getEntity().getShooter();

			// Don't process if the player isn't in the Ranger class
			if (!PvPClassHandler.hasKitOn(shooter, this)) {
				return;
			}

			// Don't process if the player is in a safe-zone
			if (DTRBitmask.SAFE_ZONE.appliesAt(shooter.getLocation())) {
				shooter.sendMessage(ChatColor.RED + "You can't use this in spawn!");
				return;
			}

			long cooldown = throwCooldown.getOrDefault(shooter.getUniqueId(), 0L);

			if (cooldown > System.currentTimeMillis()) {
				event.setCancelled(true);
				shooter.sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD + TimeUtils.formatIntoDetailedString((int) (cooldown - System.currentTimeMillis()) / 1000) + ChatColor.RED + ".");
				return;
			}

			// Set snowball distance meta
			snowball.setMetadata("ShotFromDistance", new FixedMetadataValue(Foxtrot.getInstance(), snowball.getLocation()));

			// Add miss cooldown by default (gets set to hit cooldown if they hit a player)
			throwCooldown.put(shooter.getUniqueId(), System.currentTimeMillis() + (MISS_COOLDOWN_SECONDS * 1000L));
		}
	}

	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Snowball)) {
			return;
		}

		final Snowball snowball = (Snowball) event.getDamager();

		if (event.getEntity() instanceof Player && snowball.getShooter() instanceof Player) {
			final Player shooter = (Player) ((Snowball) event.getDamager()).getShooter();
			final Player victim = (Player) event.getEntity();

			// Don't process if the player isn't in the Ranger class
			if (!PvPClassHandler.hasKitOn(shooter, this)) {
				return;
			}

			if (!canUseMark(shooter)) {
				return;
			}

			Team shooterTeam = Foxtrot.getInstance().getTeamHandler().getTeam(shooter);

			if (shooterTeam != null) {
				if (shooterTeam.isOwner(victim.getUniqueId()) || shooterTeam.isCoLeader(victim.getUniqueId()) || shooterTeam.isCaptain(victim.getUniqueId()) || shooterTeam.isMember(victim.getUniqueId())) {
					shooter.sendMessage(ChatColor.RED + "You cannot stun a player on your team!");
					event.setCancelled(true);
					return;
				}
			}

			// Don't process if the victim is in a safe-zone
			if (DTRBitmask.SAFE_ZONE.appliesAt(victim.getLocation())) {
				shooter.sendMessage(ChatColor.RED + "You can't stun a player who is in spawn!");
				return;
			}

			// Don't process if the shooter is in a safe-zone
			if (DTRBitmask.SAFE_ZONE.appliesAt(shooter.getLocation())) {
				shooter.sendMessage(ChatColor.RED + "You can't use this in spawn!");
				return;
			}

			// Add spawn-tag to both players
			SpawnTagHandler.addOffensiveSeconds(victim, SpawnTagHandler.getMaxTagTime());
			SpawnTagHandler.addOffensiveSeconds(shooter, SpawnTagHandler.getMaxTagTime());

			int distance = (int) ((Location) snowball.getMetadata("ShotFromDistance").get(0).value()).distance(victim.getLocation());

			// Send shooter feedback
			if (PvPClassHandler.hasKitOn(victim, this)) {
				shooter.sendMessage(ChatColor.YELLOW + "[" + ChatColor.BLUE + "Stun Range" + ChatColor.YELLOW + " (" + ChatColor.RED + distance + ChatColor.YELLOW + ")] " + ChatColor.GOLD + "Cannot stun another Ranger!");
				return;
			} else {
				shooter.sendMessage(ChatColor.YELLOW + "[" + ChatColor.BLUE + "Stun Range" + ChatColor.YELLOW + " (" + ChatColor.RED + distance + ChatColor.YELLOW + ")] " + ChatColor.GOLD + "Slowed and weakened player!");
			}

			// Send damaged message
			victim.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "Stunned! " + ChatColor.YELLOW + "A ranger has shot and stunned you for " + TAG_SECONDS + " seconds.");

			// Add effects to damaged player
			victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * TAG_SECONDS, 0));
			victim.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * TAG_SECONDS, 3));

			// Add 45 second throw cooldown (because the shooter hit another player)
			throwCooldown.put(shooter.getUniqueId(), System.currentTimeMillis() + (HIT_COOLDOWN_SECONDS * 1000L));

			// Add enderpearl timer or cancel flying pearl
			if (victim.hasMetadata("LastEnderPearl")) {
				((EnderPearl) victim.getMetadata("LastEnderPearl").get(0).value()).remove();
			} else {
				EnderpearlCooldownHandler.resetEnderpearlTimer(victim);
			}

			// Mark the victim with a blue name
			// (handled in FoxtrotNametagProvider)
			markedPlayers.put(victim.getUniqueId(), System.currentTimeMillis() + (TAG_SECONDS * 1000L));

			// Trigger a nametag update and schedule one for when the tag is finished
			FrozenNametagHandler.reloadPlayer(victim);

			new BukkitRunnable() {
				@Override
				public void run() {
					if (victim.isOnline()) {
						FrozenNametagHandler.reloadPlayer(victim);
					}
				}
			}.runTaskLaterAsynchronously(Foxtrot.getInstance(), 20L * TAG_SECONDS);
		}
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		lastJumpUsage.remove(event.getPlayer().getName());
		lastSpeedUsage.remove(event.getPlayer().getName());
		throwCooldown.remove(event.getPlayer().getUniqueId());
		markedPlayers.remove(event.getPlayer().getUniqueId());
	}

	private boolean canUseMark(Player player) {
		if (Foxtrot.getInstance().getTeamHandler().getTeam(player) != null) {
			Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

			int amount = 0;

			for (Player member : team.getOnlineMembers()) {
				if (PvPClassHandler.hasKitOn(member, this)) {
					amount++;

					if (amount > 3) {
						break;
					}
				}
			}

			if (amount > 3) {
				player.sendMessage(ChatColor.RED + "Your team has too many rangers. Stun was not applied.");
				return false;
			}
		}

		return true;
	}

}
