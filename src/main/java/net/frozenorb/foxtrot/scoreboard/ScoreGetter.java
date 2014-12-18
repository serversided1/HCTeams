package net.frozenorb.foxtrot.scoreboard;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.foxtrot.koth.KOTHHandler;
import net.frozenorb.foxtrot.listener.EnderpearlListener;
import net.frozenorb.foxtrot.pvpclasses.PvPClassHandler;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by chasechocolate.
 */
public interface ScoreGetter {

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

    public static final ScoreGetter CLASS_WARMUP = new ScoreGetter() {

        @Override
        public String getTitle(Player player){
            return (ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Class Warmup");
        }

        @Override
        public int getSeconds(Player player) {
            if (PvPClassHandler.getWarmupTasks().containsKey(player.getName())) {
                long diff = PvPClassHandler.getWarmupTasks().get(player.getName()).getTime() - System.currentTimeMillis();

                if (diff > 0) {
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
            if (PvPClassHandler.getLastBardPositiveEffectUsage().containsKey(player.getName()) && PvPClassHandler.getLastBardPositiveEffectUsage().get(player.getName()) >= System.currentTimeMillis()) {
                long diff = PvPClassHandler.getLastBardPositiveEffectUsage().get(player.getName()) - System.currentTimeMillis();

                if (diff > 0) {
                    return ((int) diff / 1000);
                }
            }

            return (NO_SCORE);
        }

    };

    public static final ScoreGetter BARD_DEBUFF = new ScoreGetter() {

        @Override
        public String getTitle(Player player) {
            return (ChatColor.RED.toString() + ChatColor.BOLD + "Bard Debuff");
        }

        @Override
        public int getSeconds(Player player) {
            if (PvPClassHandler.getLastBardNegativeEffectUsage().containsKey(player.getName()) && PvPClassHandler.getLastBardNegativeEffectUsage().get(player.getName()) >= System.currentTimeMillis()) {
                long diff = PvPClassHandler.getLastBardNegativeEffectUsage().get(player.getName()) - System.currentTimeMillis();

                if (diff > 0) {
                    return ((int) diff / 1000);
                }
            }

            return (NO_SCORE);
        }

    };

    public static final ScoreGetter[] SCORES = {

            KOTH_TIMER,
            SPAWN_TAG,
            ENDERPEARL,
            PVP_TIMER,
            CLASS_WARMUP,
            BARD_BUFF,
            BARD_DEBUFF,

    };

    public String getTitle(Player player);

    public int getSeconds(Player player);

}