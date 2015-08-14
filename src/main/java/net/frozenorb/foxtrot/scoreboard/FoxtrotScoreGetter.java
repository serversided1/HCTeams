package net.frozenorb.foxtrot.scoreboard;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.conquest.game.ConquestGame;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.foxtrot.listener.EnderpearlListener;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.ArcherClass;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.BardClass;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.scoreboard.ScoreFunction;
import net.frozenorb.qlib.scoreboard.ScoreGetter;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FoxtrotScoreGetter implements ScoreGetter {

    @Override
    public String[] getScores(Player player) {
        List<String> scores = new ArrayList<>();
        String spawnTagScore = getSpawnTagScore(player);
        String enderpearlScore = getEnderpearlScore(player);
        String pvpTimerScore = getPvPTimerScore(player);
        String archerMarkScore = getArcherMarkScore(player);
        String bardEffectScore = getBardEffectScore(player);
        String bardEnergyScore = getBardEnergyScore(player);

        if (spawnTagScore != null) {
            scores.add("&c&lSpawn Tag&7: &c" + spawnTagScore);
        }

        if (enderpearlScore != null) {
            scores.add("&e&lEnderpearl&7: &c" + enderpearlScore);
        }

        if (pvpTimerScore != null) {
            scores.add("&a&lPvP Timer&7: &c" + pvpTimerScore);
        }

        for (KOTH koth : Foxtrot.getInstance().getKOTHHandler().getKOTHs()) {
            if (!koth.isActive() || koth.isHidden()) {
                continue;
            }

            String displayName;

            switch (koth.getName()) {
                case "EOTW":
                    displayName = ChatColor.DARK_RED.toString() + ChatColor.BOLD + "EOTW";
                    break;
                case "Citadel":
                    displayName = ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Citadel";
                    break;
                default:
                    displayName = ChatColor.BLUE.toString() + ChatColor.BOLD + koth.getName();
                    break;
            }

            scores.add(displayName + "&7: &c" + ScoreFunction.TIME_SIMPLE.apply((float) koth.getRemainingCapTime()));
        }

        if (archerMarkScore != null) {
            scores.add("&6&lArcher Mark&7: &c" + archerMarkScore);
        }

        if (bardEffectScore != null) {
            scores.add("&a&lBard Effect&7: &c" + bardEffectScore);
        }

        if (bardEnergyScore != null) {
            scores.add("&b&lBard Energy&7: &c" + bardEnergyScore);
        }

        ConquestGame conquest = Foxtrot.getInstance().getConquestHandler().getGame();

        if (conquest != null) {
            scores.add("&c&7&m--------------------");
            scores.add("&e&lConquest:");
            int displayed = 0;

            for (Map.Entry<ObjectId, Integer> entry : conquest.getTeamPoints().entrySet()) {
                Team resolved = Foxtrot.getInstance().getTeamHandler().getTeam(entry.getKey());

                if (resolved != null) {
                    scores.add("  " + resolved.getName(player) + "&7: &f" + entry.getValue());
                    displayed++;
                }

                if (displayed == 3) {
                    break;
                }
            }

            if (displayed == 0) {
                scores.add("  &7No scores");
                scores.add("  &7yet");
            }
        }

        if (!scores.isEmpty()) {
            // 'Top' and bottom.
            scores.add(0, "&a&7&m--------------------");
            scores.add("&b&7&m--------------------");
        }

        return (scores.toArray(new String[scores.size()]));
    }

    public String getSpawnTagScore(Player player) {
        if (SpawnTagHandler.isTagged(player)) {
            float diff = SpawnTagHandler.getTag(player);

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }

    public String getEnderpearlScore(Player player) {
        if (EnderpearlListener.getEnderpearlCooldown().containsKey(player.getName()) && EnderpearlListener.getEnderpearlCooldown().get(player.getName()) >= System.currentTimeMillis()) {
            float diff = EnderpearlListener.getEnderpearlCooldown().get(player.getName()) - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }

    public String getPvPTimerScore(Player player) {
        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
            int secondsRemaining = Foxtrot.getInstance().getPvPTimerMap().getSecondsRemaining(player.getUniqueId());

            if (secondsRemaining >= 0) {
                return (ScoreFunction.TIME_SIMPLE.apply((float) secondsRemaining));
            }
        }

        return (null);
    }

    public String getArcherMarkScore(Player player) {
        if (ArcherClass.isMarked(player)) {
            long diff = ArcherClass.getMarkedPlayers().get(player.getName()) - System.currentTimeMillis();

            if (diff > 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }

    public String getBardEffectScore(Player player) {
        if (BardClass.getLastEffectUsage().containsKey(player.getName()) && BardClass.getLastEffectUsage().get(player.getName()) >= System.currentTimeMillis()) {
            float diff = BardClass.getLastEffectUsage().get(player.getName()) - System.currentTimeMillis();

            if (diff > 0) {
                return (ScoreFunction.TIME_SIMPLE.apply(diff / 1000F));
            }
        }

        return (null);
    }

    public String getBardEnergyScore(Player player) {
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