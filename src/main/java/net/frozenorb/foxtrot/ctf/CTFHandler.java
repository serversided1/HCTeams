package net.frozenorb.foxtrot.ctf;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.ctf.game.CTFGame;
import net.frozenorb.foxtrot.ctf.task.CTFTickTask;
import org.bukkit.ChatColor;

public class CTFHandler {

    public static final String PREFIX = ChatColor.AQUA + "[CTF]";
    @Getter @Setter private CTFGame game = null;

    public CTFHandler() {
        (new CTFTickTask()).runTaskTimer(FoxtrotPlugin.getInstance(), 0L, 20L);
    }

}