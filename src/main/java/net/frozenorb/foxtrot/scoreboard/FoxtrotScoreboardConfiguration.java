package net.frozenorb.foxtrot.scoreboard;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.foxtrot.listener.EnderpearlListener;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.ArcherClass;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.BardClass;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.qlib.scoreboard.ScoreFunction;
import net.frozenorb.qlib.scoreboard.ScoreGetter;
import net.frozenorb.qlib.scoreboard.ScoreboardConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FoxtrotScoreboardConfiguration {

    public static ScoreboardConfiguration create() {
        ScoreboardConfiguration configuration = new ScoreboardConfiguration();

        configuration.setTitle(Foxtrot.getInstance().getMapHandler().getScoreboardTitle());
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
                        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
                            int secondsRemaining = Foxtrot.getInstance().getPvPTimerMap().getSecondsRemaining(player.getUniqueId());

                            if (secondsRemaining >= 0) {
                                return (ScoreFunction.TIME_SIMPLE.apply((float) secondsRemaining));
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
                        for (KOTH koth : Foxtrot.getInstance().getKOTHHandler().getKOTHs()) {
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
                        if (BardClass.getLastEffectUsage().containsKey(player.getName()) && BardClass.getLastEffectUsage().get(player.getName()) >= System.currentTimeMillis()) {
                            float diff = BardClass.getLastEffectUsage().get(player.getName()) - System.currentTimeMillis();

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
                        if (BardClass.getEnergy().containsKey(player.getName())) {
                            float energy = BardClass.getEnergy().get(player.getName());

                            if (energy > 0) {
                                // No function here, as it's a "raw" value.
                                return (String.valueOf(BardClass.getEnergy().get(player.getName())));
                            }
                        }

                        return (null);
                    }

                }

        });

        return (configuration);
    }

}