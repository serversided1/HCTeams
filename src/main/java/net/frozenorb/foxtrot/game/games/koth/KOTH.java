package net.frozenorb.foxtrot.game.games.koth;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.game.games.koth.events.*;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.libs.com.google.gson.annotations.SerializedName;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by macguy8 on 10/31/2014.
 */
public class KOTH {

    //***************************//

    public static String LAST_ACTIVE_KOTH = "";

    //***************************//

    @SerializedName("Name")
    @Getter
    private String name;
    @SerializedName("Location")
    @Getter
    private BlockVector capLocation;
    @SerializedName("MaxDistance")
    @Getter
    private int capDistance;
    @SerializedName("CapTime")
    @Getter
    private int capTime;

    private transient boolean active;
    @Getter
    private transient String currentCapper;
    @Getter
    private transient float remainingCapTime;

    //***************************//

    public KOTH(String name, BlockVector location) {
        this.name = name;
        this.capLocation = location;
        this.capDistance = 3;
        this.capTime = 60 * 15;

        KOTHs.getKOTHs().add(this);
        KOTHs.saveKOTHs();
    }

    //***************************//

    public void setLocation(BlockVector location) {
        this.capLocation = location;
        KOTHs.saveKOTHs();
    }

    public void setCapDistance(int capDistance) {
        this.capDistance = capDistance;
        KOTHs.saveKOTHs();
    }

    public void setCapTime(int capTime) {
        this.capTime = capTime;
        KOTHs.saveKOTHs();
    }

    public boolean isActive() {
        return (active);
    }

    public boolean activate(boolean silent) {
        if (active) {
            return (false);
        }

        FoxtrotPlugin.getInstance().getServer().getPluginManager().callEvent(new KOTHActivatedEvent(this));

        this.active = true;
        this.currentCapper = null;
        this.remainingCapTime = this.capTime;

        return (true);
    }

    public boolean deactivate(boolean silent) {
        if (!active) {
            return (false);
        }

        FoxtrotPlugin.getInstance().getServer().getPluginManager().callEvent(new KOTHDeactivatedEvent(this));

        this.active = false;
        this.currentCapper = null;
        this.remainingCapTime = this.capTime;

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
        Player capper = FoxtrotPlugin.getInstance().getServer().getPlayerExact(currentCapper);

        if (capper == null) {
            resetCapTime();
            return (false);
        }

        KOTHCapturedEvent event = new KOTHCapturedEvent(this, capper);
        FoxtrotPlugin.getInstance().getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return (false);
        }

        deactivate(true);
        return (true);
    }

    public void resetCapTime() {
        FoxtrotPlugin.getInstance().getServer().getPluginManager().callEvent(new KOTHControlLostEvent(this));

        this.currentCapper = null;
        this.remainingCapTime = capTime;
    }

    protected void tick() {
        if (currentCapper != null) {
            Player capper = FoxtrotPlugin.getInstance().getServer().getPlayerExact(currentCapper);

            if (capper == null || !onCap(capper) || capper.isDead() || capper.getGameMode() != GameMode.SURVIVAL) {
                resetCapTime();
            } else {
                if (remainingCapTime % 10 == 0 && remainingCapTime > 1) {
                    capper.sendMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + "Attempting to control " + ChatColor.BLUE + getName() + ChatColor.YELLOW + ".");
                }

                if (remainingCapTime <= 0) {
                    finishCapping();
                } else if (remainingCapTime % (60 * 3) == 0) {
                    FoxtrotPlugin.getInstance().getServer().getPluginManager().callEvent(new KOTHControlTickEvent(this));
                }

                this.remainingCapTime -= 0.1;
            }
        } else {
            List<Player> allOnline = Arrays.asList(FoxtrotPlugin.getInstance().getServer().getOnlinePlayers());
            Collections.shuffle(allOnline); // Fix players who joined first having cap priority.

            for (Player player : allOnline) {
                if (onCap(player) && !player.isDead() && player.getGameMode() != GameMode.CREATIVE) {
                    startCapping(player);
                    break;
                }
            }
        }
    }

    public boolean onCap(Player player) {
        int yDistance = player.getLocation().getBlockY() - capLocation.getBlockY();
        return (Math.abs(player.getLocation().getBlockX() - capLocation.getBlockX()) <= capDistance && yDistance >= 0 && yDistance <= 5  && Math.abs(player.getLocation().getBlockZ() - capLocation.getBlockZ()) <= capDistance);
    }

}