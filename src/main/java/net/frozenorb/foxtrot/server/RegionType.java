package net.frozenorb.foxtrot.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.commands.team.TeamSubclaimCommand;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;

@AllArgsConstructor
public enum RegionType {

    SPAWN((event) -> {
        if (SpawnTagHandler.isTagged(event.getPlayer()) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot enter spawn while spawn-tagged.");
            event.setTo(event.getFrom());
            return (false);
        }

        event.getPlayer().setHealth(event.getPlayer().getMaxHealth());
        event.getPlayer().setFoodLevel(20);
        return (true);
    }),

	WARZONE((event) -> true),

	WILDNERNESS((event) -> true),

	KOTH((event) -> {
		if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getName()) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot do this while your PVP Timer is active!");
            event.getPlayer().sendMessage(ChatColor.RED + "Type '" + ChatColor.YELLOW + "/pvp enable" + ChatColor.RED + "' to remove your timer.");
            event.setTo(event.getFrom());
			return (false);
		}

		return (true);
	}),

    ROAD((event) -> true),

    CITADEL_TOWN((event) -> {
        if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getName()) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot do this while your PVP Timer is active!");
            event.getPlayer().sendMessage(ChatColor.RED + "Type '" + ChatColor.YELLOW + "/pvp enable" + ChatColor.RED + "' to remove your timer.");
            event.setTo(event.getFrom());
            return (false);
        }

        return (true);
    }),

    CITADEL_KEEP((event) -> {
        if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getName()) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot do this while your PVP Timer is active!");
            event.getPlayer().sendMessage(ChatColor.RED + "Type '" + ChatColor.YELLOW + "/pvp enable" + ChatColor.RED + "' to remove your timer.");
            event.setTo(event.getFrom());
            return (false);
        }

        return (true);
    }),

    CITADEL_COURTYARD((event) -> {
        if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getName()) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot do this while your PVP Timer is active!");
            event.getPlayer().sendMessage(ChatColor.RED + "Type '" + ChatColor.YELLOW + "/pvp enable" + ChatColor.RED + "' to remove your timer.");
            event.setTo(event.getFrom());
            return (false);
        }

        return (true);
    }),

	CLAIMED_LAND((event) -> {
		Team ownerTo = FoxtrotPlugin.getInstance().getTeamHandler().getOwner(event.getTo());

		if (ownerTo == null || !ownerTo.isMember(event.getPlayer())) {
			if (event.getPlayer().getInventory().contains(TeamSubclaimCommand.SELECTION_WAND)) {
				event.getPlayer().sendMessage(ChatColor.RED + "You cannot have the subclaim wand in other teams' claims!");
				event.getPlayer().getInventory().remove(TeamSubclaimCommand.SELECTION_WAND);
			}
		}

		if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getName())) {
			event.setTo(event.getFrom());
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot do this while your PVP Timer is active!");
            event.getPlayer().sendMessage(ChatColor.RED + "Type '" + ChatColor.YELLOW + "/pvp enable" + ChatColor.RED + "' to remove your timer.");
			return (false);
		}

		return (true);
	});

	@Getter private RegionMoveHandler moveHandler;

}