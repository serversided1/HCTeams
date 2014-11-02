package net.frozenorb.foxtrot.deathmessage.objects;

import org.bukkit.entity.EntityType;

public abstract class MobDamage extends Damage {

    //***************************//

    private EntityType mobType;

    //***************************//

    public MobDamage(String damaged, double damage, EntityType mobType) {
        super(damaged, damage);

        this.mobType = mobType;
    }

    //***************************//

    public EntityType getMobType() {
        return (mobType);
    }

    //***************************//

}