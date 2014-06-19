package net.frozenorb.foxtrot.visual.scrollers;

import net.frozenorb.Utilities.Types.Scrollable;

/**
 * Represents a Scrollable that can be toggled on and off.
 * 
 * @author Kerem Celik
 * 
 */
public interface ToggleableScrollable extends Scrollable {

	/**
	 * Gets if the scrollable should be displayed on the scrollable.
	 * 
	 * @return display
	 */
	public boolean shouldDisplay();

	/**
	 * Gets if the scrollable was previously disabled and not displaying
	 * anything.
	 * 
	 * @return enabled
	 */
	public boolean wasEnabled();

	/**
	 * Sets wasEnabled.
	 * <p>
	 * In any implementation, should be used as a setter ONLY.
	 * 
	 */
	public void setEnabled(boolean enabled);

}
