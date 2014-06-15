package net.frozenorb.foxtrot.team;

import lombok.Data;
import lombok.NonNull;

@Data
public class ClaimedChunk {
	@NonNull Integer x, z;

	@Override
	public boolean equals(Object o) {
		if (o instanceof ClaimedChunk) {

			return ((ClaimedChunk) o).x.equals(x) && ((ClaimedChunk) o).z.equals(z);
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
