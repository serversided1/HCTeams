package net.frozenorb.foxtrot.visual.scrollers;

import lombok.AllArgsConstructor;
import net.frozenorb.Utilities.Types.Scrollable;

/**
 * Scrollable that repeats the same thing over and over again.
 * 
 * @author Kerem Celik
 * 
 */
@AllArgsConstructor
public class ConstantScroller implements Scrollable {

	private String name;

	@Override
	public String next() {
		return name;
	}

}
