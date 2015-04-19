package net.frozenorb.foxtrot.koth;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.koth.events.*;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KOTH {

    @Getter private String name;
    @Getter private BlockVector capLocation;
    @Getter private String world;
    @Getter private int capDistance;
    @Getter private int capTime;
    @Getter private boolean hidden = false;

    @Getter private transient boolean active;
    @Getter private transient String currentCapper;
    @Getter private transient int remainingCapTime;
    @Getter @Setter private transient int level;
    @Getter @Setter private transient boolean terminate;

    public KOTH(String name, Location location) {
        this.name = name;
        this.capLocation = location.toVector().toBlockVector();
        this.world = location.getWorld().getName();
        this.capDistance = 3;
        this.capTime = 60 * 15;
        this.level = 2;
        this.terminate = false;

        Foxtrot.getInstance().getKOTHHandler().getKOTHs().add(this);
        Foxtrot.getInstance().getKOTHHandler().saveKOTHs();
    }

    public void setLocation(Location location) {
        this.capLocation = location.toVector().toBlockVector();
        this.world = location.getWorld().getName();
        Foxtrot.getInstance().getKOTHHandler().saveKOTHs();
    }

    public void setCapDistance(int capDistance) {
        this.capDistance = capDistance;
        Foxtrot.getInstance().getKOTHHandler().saveKOTHs();
    }

    public void setCapTime(int capTime) {
        this.capTime = capTime;

        if (this.remainingCapTime > this.capTime) {
            this.capTime = capTime;
        }

        Foxtrot.getInstance().getKOTHHandler().saveKOTHs();
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
        Foxtrot.getInstance().getKOTHHandler().saveKOTHs();
    }

    public boolean activate() {
        if (active) {
            return (false);
        }

        Foxtrot.getInstance().getServer().getPluginManager().callEvent(new KOTHActivatedEvent(this));

        this.active = true;
        this.currentCapper = null;
        this.remainingCapTime = this.capTime;
        this.level = 2;
        this.terminate = false;

        return (true);
    }

    public boolean deactivate() {
        if (!active) {
            return (false);
        }

        Foxtrot.getInstance().getServer().getPluginManager().callEvent(new KOTHDeactivatedEvent(this));

        this.active = false;
        this.currentCapper = null;
        this.remainingCapTime = this.capTime;
        this.level = 2;
        this.terminate = false;

        return (true);
    }

    public void startCapping(Player player) {
        if (currentCapper != null) {
            resetCapTime();
        }

        this.currentCapper = player.getName();
        this.remainingCapTime = capTime;
    }

    public boolean finishCapping() {
        Player capper = Foxtrot.getInstance().getServer().getPlayerExact(currentCapper);

        if (capper == null) {
            resetCapTime();
            return (false);
        }

        KOTHCapturedEvent event = new KOTHCapturedEvent(this, capper);
        Foxtrot.getInstance().getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            resetCapTime();
            return (false);
        }

        deactivate();
        return (true);
    }

    public void resetCapTime() {
        Foxtrot.getInstance().getServer().getPluginManager().callEvent(new KOTHControlLostEvent(this));

        this.currentCapper = null;
        this.remainingCapTime = capTime;

        if (terminate) {
            deactivate();
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.BLUE + getName() + ChatColor.YELLOW + " has been terminated.");
        }
    }

    protected void tick() {
        if (currentCapper != null) {
            Player capper = Foxtrot.getInstance().getServer().getPlayerExact(currentCapper);

            if (capper == null || !onCap(capper) || capper.isDead() || capper.getGameMode() != GameMode.SURVIVAL || capper.hasMetadata("invisible")) {
                resetCapTime();
            } else {
                if (remainingCapTime % 10 == 0 && remainingCapTime > 1 && !isHidden()) {
                    capper.sendMessage(ChatColor.GOLD + "[KingOfTheHill]" + ChatColor.YELLOW + " Attempting to control " + ChatColor.BLUE + getName() + ChatColor.YELLOW + ".");
                }

                if (remainingCapTime <= 0) {
                    finishCapping();
                } else {
                    Foxtrot.getInstance().getServer().getPluginManager().callEvent(new KOTHControlTickEvent(this));
                }

                this.remainingCapTime--;
            }
        } else {
            List<Player> onCap = new ArrayList<>();

            for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                if (onCap(player) && !player.isDead() && player.getGameMode() == GameMode.SURVIVAL && !player.hasMetadata("invisible")) {
                    onCap.add(player);
                }
            }

            Collections.shuffle(onCap);

            if (onCap.size() != 0) {
                startCapping(onCap.get(0));
            }
        }
    }

    public boolean onCap(Player player) {
        if (!player.getWorld().getName().equalsIgnoreCase(world)) {
            return (false);
        }

        int yDistance = player.getLocation().getBlockY() - capLocation.getBlockY();
        return (Math.abs(player.getLocation().getBlockX() - capLocation.getBlockX()) <= capDistance && yDistance >= 0 && yDistance <= 5  && Math.abs(player.getLocation().getBlockZ() - capLocation.getBlockZ()) <= capDistance);
    }

}