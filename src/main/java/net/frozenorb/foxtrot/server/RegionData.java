package net.frozenorb.foxtrot.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Data
public class RegionData<T> {
	private Location location;
	private Region region;
	private T data;

	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof RegionData<?>)) {
			return false;
		}

		RegionData<?> other = (RegionData<?>) obj;

		return other.region == region && ((data == null) || other.data.equals(data));
	}

	public int hashCode() {
		return super.hashCode();
	}

	public String getName(Player p) {
		if (region.getDisplayName().isEmpty()) {
			if (data instanceof Team) {
				Team ownerTo = (Team) data;

				if (ownerTo.isMember(p)) {
					return "§a" + ownerTo.getFriendlyName();

				} else {

					return "§c" + ownerTo.getFriendlyName();
				}
			}

			if (region == Region.KOTH_ARENA) {
                if (((String) data).equalsIgnoreCase("Citadel")) {
                    return (ChatColor.DARK_PURPLE + "Citadel");
                } else {
                    return (ChatColor.AQUA + (String) data + ChatColor.GOLD + " KOTH");
                }
			}
		}

		return (region.getDisplayName());
	}

}