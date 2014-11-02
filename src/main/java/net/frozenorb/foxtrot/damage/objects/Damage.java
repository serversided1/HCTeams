package net.frozenorb.foxtrot.damage.objects;

public abstract class Damage {

    //***************************//

    private String damaged;
    private double damage;
    private long time;

    //***************************//

    public Damage(String damaged, double damage) {
        this.damaged = damaged;
        this.damage = damage;
        this.time = System.currentTimeMillis();
    }

    //***************************//

    public abstract String getDescription();
    public abstract String getDeathMessage();

    public String getDamaged() {
        return (damaged);
    }

    public double getDamage() {
        return (damage);
    }

    public long getTime() {
        return (time);
    }

    //***************************//

}
