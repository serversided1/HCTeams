package net.frozenorb.foxtrot.scoreboard;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.foxtrot.listener.EnderpearlListener;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.ArcherClass;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.BaseBardClass;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public abstract class ScoreGetter {

    public static final ScoreGetter SPAWN_TAG = new ScoreGetter(ChatColor.RED.toString() + ChatColor.BOLD + "Spawn Tag") {

        @Override
        public String getValue(Player player) {
            if (SpawnTagHandler.isTagged(player)) {
                float diff = SpawnTagHandler.getTag(player);

                if (diff >= 0) {
                    return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
                }
            }

            return (null);
        }

    };

    public static final ScoreGetter ENDERPEARL = new ScoreGetter(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Enderpearl") {

        @Override
        public String getValue(Player player) {
            if (EnderpearlListener.getEnderpearlCooldown().containsKey(player.getName()) && EnderpearlListener.getEnderpearlCooldown().get(player.getName()) >= System.currentTimeMillis()) {
                float diff = EnderpearlListener.getEnderpearlCooldown().get(player.getName()) - System.currentTimeMillis();

                if (diff >= 0) {
                    return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
                }
            }

            return (null);
        }

    };

    public static final ScoreGetter PVP_TIMER = new ScoreGetter(ChatColor.GREEN.toString() + ChatColor.BOLD + "PVP Timer") {

        @Override
        public String getValue(Player player) {
            if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(player.getName())) {
                float diff = FoxtrotPlugin.getInstance().getPvPTimerMap().getTimer(player.getName()) - System.currentTimeMillis();

                if (diff >= 0) {
                    return (ScoreFunction.TIME_SIMPLE.apply(diff / 1000F));
                }
            }

            return (null);
        }

    };

    public static final ScoreGetter KOTH_TIMER = new ScoreGetter() {

        private String lastActiveKOTH;

        @Override
        public String getTitle(Player player) {
            return (lastActiveKOTH);
        }

        @Override
        public String getValue(Player player) {
            for (KOTH koth : FoxtrotPlugin.getInstance().getKOTHHandler().getKOTHs()) {
                if (koth.isHidden() || !koth.isActive()) {
                    continue;
                }

                if (koth.getName().equals("Citadel")) {
                    lastActiveKOTH = ChatColor.DARK_PURPLE + "Citadel";
                } else if (koth.getName().equals("EOTW")) {
                    lastActiveKOTH = ChatColor.DARK_RED + "EOTW";
                } else {
                    lastActiveKOTH = ChatColor.BLUE + koth.getName();
                }

                return (ScoreFunction.TIME_SIMPLE.apply((float) koth.getRemainingCapTime()));
            }

            return (null);
        }

    };

    public static final ScoreGetter BARD_BUFF = new ScoreGetter(ChatColor.GREEN.toString() + ChatColor.BOLD + "Bard Buff") {

        @Override
        public String getValue(Player player) {
            if (BaseBardClass.getLastEffectUsage().containsKey(player.getName()) && BaseBardClass.getLastEffectUsage().get(player.getName()) >= System.currentTimeMillis()) {
                float diff = BaseBardClass.getLastEffectUsage().get(player.getName()) - System.currentTimeMillis();

                if (diff > 0) {
                    return (ScoreFunction.TIME_SIMPLE.apply(diff / 1000F));
                }
            }

            return (null);
        }

    };

    public static final ScoreGetter ARCHER_MARK = new ScoreGetter(ChatColor.GOLD.toString() + ChatColor.BOLD + "Archer Mark") {

        @Override
        public String getValue(Player player) {
            if (ArcherClass.isMarked(player)) {
                long diff = ArcherClass.getMarkedPlayers().get(player.getName()) - System.currentTimeMillis();

                if (diff > 0) {
                    return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
                }
            }

            return (null);
        }

    };

    public static final ScoreGetter ENERGY = new ScoreGetter(ChatColor.AQUA.toString() + ChatColor.BOLD + "Energy") {

        @Override
        public String getValue(Player player) {
            if (BaseBardClass.getEnergy().containsKey(player.getName())) {
                float energy = BaseBardClass.getEnergy().get(player.getName());

                if (energy > 0) {
                    // No function here, as it's a "raw" value.
                    return (String.valueOf(BaseBardClass.getEnergy().get(player.getName())));
                }
            }

            return (null);
        }

    };

    private String defaultTitle;

    // If we're not going to have a constant title,
    // we need to use this.
    public ScoreGetter() {

    }

    public ScoreGetter(String defaultTitle) {
        this.defaultTitle = defaultTitle;
    }

    public String getTitle(Player player) {
        return (defaultTitle);
    }

    public abstract String getValue(Player player);

}