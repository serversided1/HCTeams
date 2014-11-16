package net.frozenorb.foxtrot.team.claims;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.frozenorb.foxtrot.serialization.ReflectionSerializer;
import net.frozenorb.foxtrot.serialization.SerializableClass;

@EqualsAndHashCode(callSuper = false)
@SerializableClass
@Data
public class Subclaim extends ReflectionSerializer {

	@NonNull private Location loc1, loc2;
	@NonNull private String manager;
	@NonNull private String name;
	private List<String> members = new ArrayList<String>();

	public void addMember(String name) {
		members.add(name);
	}

	public boolean isMember(String name) {
		for (String str : members) {
			if (str.equalsIgnoreCase(name)) {
				return (true);
			}
		}

		return (name.equalsIgnoreCase(manager));
	}

	public void removeMember(String name) {
		Iterator<String> miter = members.iterator();

		while (miter.hasNext()) {
			if (miter.next().equalsIgnoreCase(name)) {
				miter.remove();
			}
		}
	}


	@Override
	public String toString() {
		return (getFriendlyColoredName());
	}

	public String saveString() {
		String msg = "";
		msg += "world " + loc1.getBlockX() + " " + loc1.getBlockY() + " " + loc1.getBlockZ() + "|";
		msg += "world " + loc2.getBlockX() + " " + loc2.getBlockY() + " " + loc2.getBlockZ() + "|";

		msg += manager + "|" + name;

		boolean first = true;
		for (String str : members) {
			if (first) {
				msg += "|";
			} else {
				msg += ",";
			}

			msg += str;
			first = false;
		}

		return (msg);
	}

	public String getFriendlyColoredName() {
		return (ChatColor.WHITE + manager + ChatColor.GRAY + "/" + name);
	}

	public String getFriendlyName() {
		return (manager + "/" + name);
	}

}