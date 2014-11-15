package net.frozenorb.foxtrot.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Data
public class RegionData {

	private Location location;
	private RegionType regionType;
	private Team data;

	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof RegionData)) {
			return (false);
		}

		RegionData other = (RegionData) obj;

		return (other.regionType == regionType && (data == null || other.data.equals(data)));
	}

	public int hashCode() {
		return (super.hashCode());
	}

	public String getName(Player player) {
        switch (regionType) {
            case SPAWN:
                switch (player.getWorld().getEnvironment()) {
                    case NETHER:
                        return (ChatColor.GREEN + "Nether Spawn");
                    case THE_END:
                        return (ChatColor.GREEN + "The End Spawn");
                }

                return (ChatColor.GREEN + "Spawn");
            case WARZONE:
                return (ChatColor.RED + "Warzone");
            case WILDNERNESS:
                return (ChatColor.GRAY + "The Wilderness");
            case KOTH:
                return (ChatColor.AQUA + data.getFriendlyName() + ChatColor.GOLD + " KOTH");
            case ROAD:
                return (ChatColor.RED + "Road");
            case CLAIMED_LAND:
                if (data.isMember(player.getName())) {
                    return (ChatColor.GREEN + data.getFriendlyName());
                } else if (data.isAlly(player.getName())) {
                    return (ChatColor.LIGHT_PURPLE + data.getFriendlyName());
                } else {
                    return (ChatColor.RED + data.getFriendlyName());
                }
            case CITADEL_COURTYARD:
                return (ChatColor.DARK_PURPLE + "Citadel Courtyard");
            case CITADEL_KEEP:
                return (ChatColor.DARK_PURPLE + "Citadel Keep");
            case CITADEL_TOWN:
                return (ChatColor.DARK_PURPLE + "Citadel Town");
            default:
                return (ChatColor.DARK_RED + "N/A");
        }
	}

}