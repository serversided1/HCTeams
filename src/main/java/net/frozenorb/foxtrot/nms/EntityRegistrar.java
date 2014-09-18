package net.frozenorb.foxtrot.nms;

import net.frozenorb.foxtrot.util.ReflectionUtils;
import net.minecraft.server.v1_7_R4.EntityTypes;

/**
 * Class used to inject custom entities into NMS.
 * 
 * @author Kerem Celik
 * 
 */
public class EntityRegistrar {

	public static void registerCustomEntities() throws Exception {

		registerCustomEntity(FixedVillager.class, "Villager", 120);
	}

	@SuppressWarnings("rawtypes")
	public static void registerCustomEntity(Class entityClass, String name, int id)
			throws Exception {

		ReflectionUtils.putInPrivateStaticMap(EntityTypes.class, "d", entityClass, name);
		ReflectionUtils.putInPrivateStaticMap(EntityTypes.class, "f", entityClass, Integer.valueOf(id));
	}
}
