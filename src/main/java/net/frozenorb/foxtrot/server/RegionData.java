package net.frozenorb.foxtrot.server;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.team.Team;
import lombok.AllArgsConstructor;
import lombok.Data;

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

		return other.region == region && ((data == null && data == null) || other.data.equals(data));
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public String getName(Player p) {
		if (region.getDisplayName().isEmpty()) {

			if (data instanceof Team) {
				Team ownerTo = (Team) data;

				if (ownerTo.isOnTeam(p)) {
					return "§a" + ownerTo.getFriendlyName();

				} else {

					return "§c" + ownerTo.getFriendlyName();
				}
			}

			if (region == Region.KOTH_ARENA) {
				return "§b" + (String) data + "§6 KOTH";
			}
		}
		return region.getDisplayName();
	}
}
