package net.frozenorb.foxtrot.visual;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents an object on the player's tab screen.
 * 
 * @author Kerem Celik
 * 
 */
@AllArgsConstructor
@Data
public class TabPlayer {

	private String name;
	private TabOperation operation;

	@Override
	public boolean equals(Object o) {
		if (o instanceof TabPlayer) {
			return ((TabPlayer) o).getName().equals(getName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	/**
	 * All operations that can be done to the tab screen.
	 * 
	 * @author Kerem Celik
	 * 
	 */
	public static enum TabOperation {

		/**
		 * Addition to the tab screen.
		 */
		ADD,

		/**
		 * Removal from tab screen.
		 */
		REMOVE,

		/**
		 * Updates a player's ping in the tab screen.
		 */
		UPDATE_PING

	}
}
