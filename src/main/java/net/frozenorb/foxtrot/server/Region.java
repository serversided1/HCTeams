package net.frozenorb.foxtrot.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;

@AllArgsConstructor
public enum Region {

    SPAWN(true, "§aSpawn", (e) -> {
        if (FoxtrotPlugin.getInstance().getServerHandler().isEOTW()) {
            return true;
        }

        if (SpawnTag.isTagged(e.getPlayer()) && e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            e.getPlayer().sendMessage(ChatColor.RED + "You cannot enter spawn while spawn-tagged.");
            e.setTo(e.getFrom());
            return false;
        }

        e.getPlayer().setHealth(e.getPlayer().getMaxHealth());
        e.getPlayer().setFoodLevel(20);

        return true;
    }),

    SPAWN_NETHER(true, "§aNether Spawn", (e) -> {
        if (FoxtrotPlugin.getInstance().getServerHandler().isEOTW()) {
            return true;
        }

        if (SpawnTag.isTagged(e.getPlayer()) && e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            e.getPlayer().sendMessage(ChatColor.RED + "You cannot enter spawn while spawn-tagged.");
            e.setTo(e.getFrom());
            return false;
        }

        e.getPlayer().setHealth(e.getPlayer().getMaxHealth());
        e.getPlayer().setFoodLevel(20);
        return true;
    }),

    SPAWN_END(true, "§aEnd Spawn", (e) -> {
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            e.getPlayer().sendMessage(ChatColor.RED + "You cannot re-enter the end spawn.");
            e.setTo(e.getFrom());
            return false;
        }

        return true;
    }),

    EXIT_END(true, "§aEnd Exit", (e) -> {
        if (FoxtrotPlugin.getInstance().getServerHandler().isEOTW()) {
            return true;
        }

        if (SpawnTag.isTagged(e.getPlayer())) {
            e.getPlayer().sendMessage(ChatColor.RED + "You cannot enter the end exit while spawn-tagged.");
            e.setTo(e.getFrom());
            return false;
        }

        return true;
    }),

	WARZONE(false, "§cWarzone", (e) -> true),

	DIAMOND_MOUNTAIN(true, "§bDiamond Mountain", (e) -> true),

	WILDNERNESS(false, "§7The Wilderness", (e) -> true),

	KOTH_ARENA(true, "", (e) -> {
        if (FoxtrotPlugin.getInstance().getServerHandler().isEOTW()) {
            return true;
        }

		if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(e.getPlayer().getName())) {
            e.getPlayer().sendMessage(ChatColor.RED + "You cannot do this while your PVP Timer is active!");
            e.getPlayer().sendMessage(ChatColor.RED + "Type '" + ChatColor.YELLOW + "/pvp enable" + ChatColor.RED + "' to remove your timer.");
            e.setTo(e.getFrom());
			return false;
		}

		return true;

	}),

    ROAD_NORTH(false, "§cNorth Road", (e) -> true),

    ROAD_EAST(false, "§cEast Road", (e) -> true),

    ROAD_SOUTH(false, "§cSouth Road", (e) -> true),

    ROAD_WEST(false, "§cWest Road", (e) -> true),

	CLAIMED_LAND(false, "", (e) -> {
		Team ownerTo = FoxtrotPlugin.getInstance().getTeamHandler().getOwner(e.getTo());

		if (ownerTo == null || !ownerTo.isMember(e.getPlayer())) {
			if (e.getPlayer().getInventory().contains(net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands.Subclaim.SELECTION_WAND)) {
				e.getPlayer().sendMessage(ChatColor.RED + "You cannot have the subclaim wand in other teams' claims!");
				e.getPlayer().getInventory().remove(net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands.Subclaim.SELECTION_WAND);
			}
		}

        if (FoxtrotPlugin.getInstance().getServerHandler().isEOTW()) {
            return true;
        }

		if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(e.getPlayer().getName())) {
			e.setTo(e.getFrom());
            e.getPlayer().sendMessage(ChatColor.RED + "You cannot do this while your PVP Timer is active!");
            e.getPlayer().sendMessage(ChatColor.RED + "Type '" + ChatColor.YELLOW + "/pvp enable" + ChatColor.RED + "' to remove your timer.");
			return false;
		}

		return true;
	});

	private boolean reducedDeathban;

    public boolean isReducedDeathban() {
        return (reducedDeathban && !FoxtrotPlugin.getInstance().getServerHandler().isEOTW());
    }

	@Getter private String displayName;
	@Getter private RegionMoveHandler moveHandler;

}