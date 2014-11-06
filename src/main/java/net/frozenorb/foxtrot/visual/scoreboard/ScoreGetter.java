package net.frozenorb.foxtrot.visual.scoreboard;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.armor.Kit;
import net.frozenorb.foxtrot.armor.kits.Bard;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.foxtrot.koth.KOTHHandler;
import net.frozenorb.foxtrot.listener.EnderpearlListener;
import net.frozenorb.foxtrot.server.SpawnTag;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by chasechocolate.
 */
public interface ScoreGetter {

    public static final long NO_SCORE = -1;

    public static final ScoreGetter SPAWN_TAG = new ScoreGetter() {

        @Override
        public String getTitle(Player player) {
            return (ChatColor.RED.toString() + ChatColor.BOLD + "Spawn Tag");
        }

        @Override
        public long getMillis(Player player) {
            if (SpawnTag.isTagged(player)) {
                long diff = SpawnTag.getSpawnTags().get(player.getName()).getExpires() - System.currentTimeMillis();

                if (diff >= 0) {
                    return (diff);
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
        public long getMillis(Player player) {
            if (EnderpearlListener.getEnderpearlCooldown().containsKey(player.getName()) && EnderpearlListener.getEnderpearlCooldown().get(player.getName()) >= System.currentTimeMillis()) {
                long diff = EnderpearlListener.getEnderpearlCooldown().get(player.getName()) - System.currentTimeMillis();

                if (diff >= 0) {
                    return (diff);
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
        public long getMillis(Player player) {
            if (FoxtrotPlugin.getInstance().getJoinTimerMap().hasTimer(player)) {
                long diff = (FoxtrotPlugin.getInstance().getJoinTimerMap().getValue(player.getName()) - System.currentTimeMillis());

                if (diff >= 0) {
                    return (diff);
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
        public long getMillis(Player player){
            if (Kit.getWarmupTasks().containsKey(player.getName())) {
                long diff = Kit.getWarmupTasks().get(player.getName()).getEnds() - System.currentTimeMillis();

                if (diff >= 0) {
                    return (diff);
                }
            }

            return (NO_SCORE);
        }

    };

    public static final ScoreGetter KOTH_TIMER = new ScoreGetter() {

        @Override
        public String getTitle(Player player) {
            for (KOTH koth : KOTHHandler.getKOTHs()) {
                if (koth.isActive()) {
                    if (koth.getName().equals("Citadel")) {
                        KOTH.LAST_ACTIVE_KOTH = ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Citadel";
                    } else if (koth.getName().equals("EOTW")) {
                        KOTH.LAST_ACTIVE_KOTH = ChatColor.DARK_RED.toString() + ChatColor.BOLD + "EOTW";
                    } else {
                        KOTH.LAST_ACTIVE_KOTH = ChatColor.BLUE.toString() + ChatColor.BOLD + koth.getName() + " KOTH";
                    }

                    break;
                }
            }

            return (KOTH.LAST_ACTIVE_KOTH);
        }

        @Override
        public long getMillis(Player player){
            for (KOTH koth : KOTHHandler.getKOTHs()) {
                if (koth.isActive()) {
                    return ((long) (koth.getRemainingCapTime() * 1000L));
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
        public long getMillis(Player player) {
            if (Bard.getPositiveEffectCooldown().containsKey(player.getName()) && Bard.getPositiveEffectCooldown().get(player.getName()) >= System.currentTimeMillis()) {
                long diff = Bard.getPositiveEffectCooldown().get(player.getName()) - System.currentTimeMillis();

                if (diff >= 0) {
                    return (diff);
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
        public long getMillis(Player player) {
            if (Bard.getNegativeEffectCooldown().containsKey(player.getName()) && Bard.getNegativeEffectCooldown().get(player.getName()) >= System.currentTimeMillis()) {
                long diff = Bard.getNegativeEffectCooldown().get(player.getName()) - System.currentTimeMillis();

                if (diff >= 0) {
                    return (diff);
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

    public long getMillis(Player player);

}