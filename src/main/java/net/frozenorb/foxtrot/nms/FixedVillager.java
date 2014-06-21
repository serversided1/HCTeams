package net.frozenorb.foxtrot.nms;

import java.lang.reflect.Field;

import org.bukkit.craftbukkit.v1_7_R3.util.UnsafeList;

import net.minecraft.server.v1_7_R3.Entity;
import net.minecraft.server.v1_7_R3.EntityVillager;
import net.minecraft.server.v1_7_R3.PathfinderGoalSelector;
import net.minecraft.server.v1_7_R3.World;

@SuppressWarnings("rawtypes")
public class FixedVillager extends EntityVillager {

	public FixedVillager(World w) {
		super(w);

		try {
			Field gsa = PathfinderGoalSelector.class.getDeclaredField("b");
			gsa.setAccessible(true);

			gsa.set(goalSelector, new UnsafeList());
			gsa.set(targetSelector, new UnsafeList());

		}
		catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void h() {
		motX = 0D;
		motY = 0D;
		motZ = 0D;

		super.h();
	}

	@Override
	public void collide(Entity arg0) {}
}
