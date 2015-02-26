package net.frozenorb.foxtrot.conquest;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.conquest.game.ConquestGame;
import org.bukkit.ChatColor;

public class ConquestHandler {

    public static final String PREFIX = ChatColor.YELLOW + "[Conquest]";

    public static final int POINTS_TO_WIN = 250;
    public static final int POINTS_DEATH_PENALTY = 20;
    public static final String KOTH_NAME_PREFIX = "Conquest-";
    public static final int TIME_TO_CAP = 30;

    @Getter @Setter private ConquestGame game;

}