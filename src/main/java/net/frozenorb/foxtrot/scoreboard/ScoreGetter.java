package net.frozenorb.foxtrot.scoreboard;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.koth.KOTH;
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
            return (ChatColor.RED + "Spawn Tag");
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
            return (ChatColor.YELLOW + "Enderpearl");
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
            return (ChatColor.GREEN + "PVP Timer");
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

        private String lastActiveKOTH;

        @Override
        public String getTitle(Player player) {
            return (lastActiveKOTH);
        }

        @Override
        public int getSeconds(Player player) {
            for (KOTH koth : FoxtrotPlugin.getInstance().getKOTHHandler().getKOTHs()) {
                if (koth.isHidden()) {
                    continue;
                }

                if (koth.isActive()) {
                    if (koth.getName().equals("Citadel")) {
                        lastActiveKOTH = ChatColor.DARK_PURPLE + "Citadel";
                    } else if (koth.getName().equals("EOTW")) {
                        lastActiveKOTH = ChatColor.DARK_RED + "EOTW";
                    } else {
                        lastActiveKOTH = ChatColor.BLUE + koth.getName();
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
            return (ChatColor.GREEN + "Bard Buff");
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
            return (ChatColor.GOLD + "Archer Mark");
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
            return (ChatColor.AQUA + "Energy");
        }

        @Override
        public int getSeconds(Player player) {
            if (BaseBardClass.getEnergy().containsKey(player.getName()) && BaseBardClass.getEnergy().get(player.getName()) > 0) {
                return (BaseBardClass.getEnergy().get(player.getName()).intValue());
            }

            return (NO_SCORE);
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

}