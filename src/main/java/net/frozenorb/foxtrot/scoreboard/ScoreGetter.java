package net.frozenorb.foxtrot.scoreboard;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.foxtrot.koth.KOTHHandler;
import net.frozenorb.foxtrot.listener.EnderpearlListener;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.ArcherClass;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.BaseBardClass;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by chasechocolate.
 */
public abstract class ScoreGetter {

    public static final int NO_SCORE = -1;

    public static final ScoreGetter SPAWN_TAG = new ScoreGetter() {

        @Override
        public String getTitle(Player player) {
            return (ChatColor.RED.toString() + ChatColor.BOLD + "Spawn Tag");
        }

        @Override
        public int getSeconds(Player player) {
            if (SpawnTagHandler.isTagged(player)) {
                long diff = SpawnTagHandler.getTag(player);

                if (diff >= 0) {
                    return ((int) diff / 1000);
                }
            }

            return (NO_SCORE);
        }

    };

    public static final ScoreGetter ENDERPEARL = new ScoreGetter() {

        @Override
        public String getTitle(Player player) {
            return (ChatColor.YELLOW.toString() + ChatColor.BOLD + "Enderpearl");
        }

        @Override
        public int getSeconds(Player player) {
            if (EnderpearlListener.getEnderpearlCooldown().containsKey(player.getName()) && EnderpearlListener.getEnderpearlCooldown().get(player.getName()) >= System.currentTimeMillis()) {
                long diff = EnderpearlListener.getEnderpearlCooldown().get(player.getName()) - System.currentTimeMillis();

                if (diff >= 0) {
                    return ((int) diff / 1000);
                }
            }

            return (NO_SCORE);
        }

    };

    public static final ScoreGetter PVP_TIMER = new ScoreGetter() {

        @Override
        public String getTitle(Player player) {
            return (ChatColor.GREEN.toString() + ChatColor.BOLD + "PVP Timer");
        }

        @Override
        public int getSeconds(Player player) {
            if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(player.getName())) {
                long diff = FoxtrotPlugin.getInstance().getPvPTimerMap().getTimer(player.getName()) - System.currentTimeMillis();

                if (diff >= 0) {
                    return ((int) diff / 1000);
                }
            }

            return (NO_SCORE);
        }

    };

    public static final ScoreGetter KOTH_TIMER = new ScoreGetter() {

        @Override
        public String getTitle(Player player) {
            return (KOTH.LAST_ACTIVE_KOTH);
        }

        @Override
        public int getSeconds(Player player) {
            for (KOTH koth : KOTHHandler.getKOTHs()) {
                if (koth.isActive()) {
                    if (koth.getName().equals("Citadel")) {
                        KOTH.LAST_ACTIVE_KOTH = ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Citadel";
                    } else if (koth.getName().equals("EOTW")) {
                        KOTH.LAST_ACTIVE_KOTH = ChatColor.DARK_RED.toString() + ChatColor.BOLD + "EOTW";
                    } else {
                        KOTH.LAST_ACTIVE_KOTH = ChatColor.BLUE.toString() + ChatColor.BOLD + koth.getName();
                    }

                    return (koth.getRemainingCapTime());
                }
            }

            return (NO_SCORE);
        }

    };

    public static final ScoreGetter BARD_BUFF = new ScoreGetter() {

        @Override
        public String getTitle(Player player) {
            return (ChatColor.GREEN.toString() + ChatColor.BOLD + "Bard Buff");
        }

        @Override
        public int getSeconds(Player player) {
            if (BaseBardClass.getLastEffectUsage().containsKey(player.getName()) && BaseBardClass.getLastEffectUsage().get(player.getName()) >= System.currentTimeMillis()) {
                long diff = BaseBardClass.getLastEffectUsage().get(player.getName()) - System.currentTimeMillis();

                if (diff > 0) {
                    return ((int) diff / 1000);
                }
            }

            return (NO_SCORE);
        }

    };

    public static final ScoreGetter ARCHER_MARK = new ScoreGetter() {

        @Override
        public String getTitle(Player player) {
            return (ChatColor.GOLD.toString() + ChatColor.BOLD + "Archer Mark");
        }

        @Override
        public int getSeconds(Player player) {
            if (ArcherClass.isMarked(player)) {
                long diff = ArcherClass.getMarkedPlayers().get(player.getName()) - System.currentTimeMillis();

                if (diff > 0) {
                    return ((int) diff / 1000);
                }
            }

            return (NO_SCORE);
        }

    };

    public static final ScoreGetter ENERGY = new ScoreGetter() {

        @Override
        public String getTitle(Player player) {
            return (ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Energy");
        }

        @Override
        public int getSeconds(Player player) {
            if (BaseBardClass.getEnergy().containsKey(player.getName()) && BaseBardClass.getEnergy().get(player.getName()) > 0) {
                return (BaseBardClass.getEnergy().get(player.getName()).intValue());
            }

            return (NO_SCORE);
        }

        @Override
        public boolean isRaw() {
            return (true);
        }

    };

    public static final ScoreGetter[] SCORES = {

            KOTH_TIMER,
            SPAWN_TAG,
            ENDERPEARL,
            PVP_TIMER,
            ENERGY,
            ARCHER_MARK,
            BARD_BUFF

    };

    public abstract String getTitle(Player player);

    public abstract int getSeconds(Player player);

    public boolean isRaw() {
        return (false);
    }

}