package net.frozenorb.foxtrot.ctf;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.ctf.game.CTFGame;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class CTFHandler {

    public static final String PREFIX = ChatColor.AQUA + "[CTF]";
    @Getter @Setter private CTFGame game = null;

    public CTFHandler() {
        new BukkitRunnable() {

            public void run() {
                if (getGame() != null) {
                    getGame().tick();
                }
            }

        }.runTaskTimer(FoxtrotPlugin.getInstance(), 0L, 20L);
    }

}