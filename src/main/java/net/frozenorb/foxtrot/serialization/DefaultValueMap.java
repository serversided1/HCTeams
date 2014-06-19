package net.frozenorb.foxtrot.serialization;

import java.util.HashMap;

/**
 * Map of primitive classes to their default counterparts.
 * 
 * @author Kerem Celik
 * 
 */
public class DefaultValueMap extends HashMap<Class<?>, Object> {
	private static final long serialVersionUID = 8693766426388395238L;

	private static volatile DefaultValueMap instance;

	DefaultValueMap() {

		put(String.class, "");

		put(Integer.class, 0);
		put(int.class, 0);

		put(Long.class, 0L);
		put(long.class, 0L);

		put(Character.class, '\0');
		put(char.class, '\0');

		put(Boolean.class, false);
		put(boolean.class, false);

	}

	/**
	 * Lazy-creation, thread-safe, singleton instance getter.
	 * 
	 * @return {@link PlayContextCache} instance
	 */
	public static DefaultValueMap getInstance() {
		if (instance == null) {

			synchronized (DefaultValueMap.class) {

				if (instance == null) {
					instance = new DefaultValueMap();
				}
			}
		}
		return instance;
	}

}
