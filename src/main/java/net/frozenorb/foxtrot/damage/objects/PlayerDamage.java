package net.frozenorb.foxtrot.damage.objects;

public abstract class PlayerDamage extends Damage {

    //***************************//

    private String damager;

    //***************************//

    public PlayerDamage(String damaged, double damage, String damager) {
        super(damaged, damage);

        this.damager = damager;
    }

    //***************************//

    public String getDamager() {
        return (damager);
    }

    //***************************//

}