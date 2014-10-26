package net.frozenorb.foxtrot.server;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;

import org.bukkit.ChatColor;
import org.bukkit.entity.Damageable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Region {

	SPAWN(true, "§aSpawn", (e) -> {
		if (SpawnTag.isTagged(e.getPlayer())) {
			e.getPlayer().sendMessage(ChatColor.RED + "You cannot enter spawn while spawn-tagged.");
			e.setTo(e.getFrom());
			return false;

		}

		e.getPlayer().setHealth(((Damageable) e.getPlayer()).getMaxHealth());
		e.getPlayer().setFoodLevel(20);

		return true;
	}),

	WARZONE(false, "§cWarzone", (e) -> true),

	DIAMOND_MOUNTAIN(true, "§bDiamond Mountain", (e) -> true),

	WILDNERNESS(false, "§7The Wilderness", (e) -> true),

	KOTH_ARENA(true, "", (e) -> {
		if (FoxtrotPlugin.getInstance().getJoinTimerMap().hasTimer(e.getPlayer())) {
			e.setTo(e.getFrom());
			e.getPlayer().sendMessage(ChatColor.RED + "You cannot enter the KOTH arena with a PVP Timer. Type '§e/pvptimer remove§c' to remove your timer.");
			return false;
		}

		return true;

	}),

    ROAD_NORTH(false, "§cNorth Road", (e) -> true),

    ROAD_EAST(false, "§cEast Road", (e) -> true),

    ROAD_SOUTH(false, "§cSouth Road", (e) -> true),

    ROAD_WEST(false, "§cWest Road", (e) -> true),

	CLAIMED_LAND(false, "", (e) -> {
		Team ownerTo = FoxtrotPlugin.getInstance().getTeamManager().getOwner(e.getTo());

		if (ownerTo == null || !ownerTo.isOnTeam(e.getPlayer())) {
			if (e.getPlayer().getInventory().contains(net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands.Subclaim.SELECTION_WAND)) {
				e.getPlayer().sendMessage(ChatColor.RED + "You cannot have the subclaim wand in other teams' claims!");

				e.getPlayer().getInventory().remove(net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands.Subclaim.SELECTION_WAND);
			}
		}

		if (FoxtrotPlugin.getInstance().getJoinTimerMap().hasTimer(e.getPlayer())) {
			e.setTo(e.getFrom());
			e.getPlayer().sendMessage(ChatColor.RED + "You cannot enter claims with a PVP Timer. Type '§e/pvptimer remove§c' to remove your timer.");
			return false;
		}
		return true;
	});

	@Getter private boolean reducedDeathban;
	@Getter private String displayName;
	@Getter private RegionMoveHandler moveHandler;

}
