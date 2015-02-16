package net.frozenorb.foxtrot.scoreboard;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardScore;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by chasechocolate.
 */
public class FoxtrotBoard {

    @Getter private Player player;
    @Getter private Objective objective;
    @Getter private Set<String> displayedScores = new HashSet<String>();

    public FoxtrotBoard(Player player) {
        this.player = player;

        Scoreboard board = FoxtrotPlugin.getInstance().getServer().getScoreboardManager().getNewScoreboard();

        objective = board.registerNewObjective("HCTeams", "dummy");
        objective.setDisplayName(FoxtrotPlugin.getInstance().getMapHandler().getScoreboardTitle());
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        update();
        player.setScoreboard(board);
    }

    public void update() {
        for (ScoreGetter getter : ScoreGetter.SCORES) {
            int seconds = getter.getSeconds(player);
            String title = getter.getTitle(player);

            if (seconds == ScoreGetter.NO_SCORE) {
                if (displayedScores.contains(title)) {
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutScoreboardScore(title));
                    displayedScores.remove(title);
                }
            } else {
                displayedScores.add(title);
                PacketPlayOutScoreboardScore scoreboardScorePacket = new PacketPlayOutScoreboardScore();

                setField(scoreboardScorePacket, "a", title);
                setField(scoreboardScorePacket, "b", objective.getName());
                setField(scoreboardScorePacket, "c", seconds);
                setField(scoreboardScorePacket, "d", 0);

                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(scoreboardScorePacket);
            }
        }
    }

    public void setField(Packet packet, String field, Object value) {
        try {
            Field fieldObject = packet.getClass().getDeclaredField(field);

            fieldObject.setAccessible(true);
            fieldObject.set(packet, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}