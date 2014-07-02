package net.frozenorb.foxtrot.team.claims;

import org.bukkit.Location;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PhysicalChunk {
	@NonNull Integer x, z;

	public PhysicalChunk(Location loc) {
		this.x = loc.getChunk().getX();
		this.z = loc.getChunk().getZ();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof PhysicalChunk) {

			return ((PhysicalChunk) o).x.equals(x) && ((PhysicalChunk) o).z.equals(z);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return ((Integer) x).hashCode() + ((Integer) z).hashCode();
	}

	@Override
	public String toString() {
		return x + ":" + z;
	}
}
