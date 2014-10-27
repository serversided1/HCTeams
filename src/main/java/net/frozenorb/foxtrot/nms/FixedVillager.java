package net.frozenorb.foxtrot.nms;

import net.minecraft.server.v1_7_R3.*;
import org.bukkit.craftbukkit.v1_7_R3.util.UnsafeList;

import java.lang.reflect.Field;

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
    public EntityAgeable createChild(EntityAgeable entityAgeable) {
        return null;
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
