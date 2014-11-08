package net.frozenorb.foxtrot.nametag;

import lombok.Data;
import lombok.NonNull;

@Data
public class TeamInfo {

	@NonNull private String name;
	@NonNull private String prefix;
	@NonNull private String suffix;

}