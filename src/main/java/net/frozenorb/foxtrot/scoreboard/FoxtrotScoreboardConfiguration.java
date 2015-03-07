package net.frozenorb.foxtrot.scoreboard;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.foxtrot.listener.EnderpearlListener;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.ArcherClass;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.BaseBardClass;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.qlib.scoreboard.ScoreFunction;
import net.frozenorb.qlib.scoreboard.ScoreGetter;
import net.frozenorb.qlib.scoreboard.ScoreboardConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FoxtrotScoreboardConfiguration {

    public static ScoreboardConfiguration create() {
        ScoreboardConfiguration configuration = new ScoreboardConfiguration();

        configuration.setTitle(FoxtrotPlugin.getInstance().getMapHandler().getScoreboardTitle());
        configuration.setScores(new ScoreGetter[] {

                new ScoreGetter(ChatColor.RED.toString() + ChatColor.BOLD + "Spawn Tag") {

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

                },

                new ScoreGetter(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Enderpearl") {

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

                },

                new ScoreGetter(ChatColor.GREEN.toString() + ChatColor.BOLD + "PVP Timer") {

                    @Override
                    public String getValue(Player player) {
                        if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
                            float diff = FoxtrotPlugin.getInstance().getPvPTimerMap().getTimer(player.getUniqueId()) - System.currentTimeMillis();

                            if (diff >= 0) {
                                return (ScoreFunction.TIME_SIMPLE.apply(diff / 1000F));
                            }
                        }

                        return (null);
                    }

                },

                new ScoreGetter() {

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

                            switch (koth.getName()) {
                                case "EOTW":
                                    lastActiveKOTH = ChatColor.DARK_RED.toString() + ChatColor.BOLD + "EOTW";
                                    break;
                                case "Citadel":
                                    lastActiveKOTH = ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Citadel";
                                    break;
                                default:
                                    lastActiveKOTH = ChatColor.BLUE.toString() + ChatColor.BOLD + koth.getName();
                                    break;
                            }

                            return (ScoreFunction.TIME_SIMPLE.apply((float) koth.getRemainingCapTime()));
                        }

                        return (null);
                    }

                },

                new ScoreGetter(ChatColor.GREEN.toString() + ChatColor.BOLD + "Bard Buff") {

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

                },

                new ScoreGetter(ChatColor.GOLD.toString() + ChatColor.BOLD + "Archer Mark") {

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

                },

                new ScoreGetter(ChatColor.AQUA.toString() + ChatColor.BOLD + "Energy") {

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

                }

        });

        return (configuration);
    }

}