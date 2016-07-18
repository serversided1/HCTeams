package net.frozenorb.foxtrot.util;

import com.google.common.collect.ImmutableSet;
import lombok.experimental.UtilityClass;
import net.frozenorb.hydrogen.Hydrogen;

import java.util.Set;
import java.util.UUID;

@UtilityClass
public class HydrogenUtil {

	public static Set<UUID> getHighRollers() {
		return ImmutableSet.copyOf(Hydrogen.getInstance().getRankHandler().getUsersWithRank("highroller"));
	}

}