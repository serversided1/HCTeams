package net.frozenorb.foxtrot.deathmessage.objects;

public abstract class Damage {

    //***************************//

    private String damaged;
    private double damage;
    private long time;
    private double healthAfter;

    //***************************//

    public Damage(String damaged, double damage) {
        this.damaged = damaged;
        this.damage = damage;
        this.time = System.currentTimeMillis();
        this.healthAfter = -1D;
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

    public double getHealthAfter() {
        return (healthAfter);
    }

    public void setHealthAfter(double healthAfter) {
        this.healthAfter = healthAfter;
    }

    //***************************//

}
