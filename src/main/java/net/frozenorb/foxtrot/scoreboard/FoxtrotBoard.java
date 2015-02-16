package net.frozenorb.foxtrot.scoreboard;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.nametag.NametagManager;
import net.frozenorb.foxtrot.nametag.ScoreboardTeamPacketMod;
import net.frozenorb.foxtrot.nametag.TeamInfo;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by chasechocolate.
 */
public class FoxtrotBoard {

    public static final ScoreGetter[] SCORES = {

            ScoreGetter.KOTH_TIMER,
            ScoreGetter.SPAWN_TAG,
            ScoreGetter.ENDERPEARL,
            ScoreGetter.PVP_TIMER,
            ScoreGetter.ENERGY,
            ScoreGetter.ARCHER_MARK,
            ScoreGetter.BARD_BUFF

    };

    @Getter private Player player;
    @Getter private Objective objective;
    @Getter private Map<String, Integer> displayedScores = new HashMap<String, Integer>();
    @Getter private Set<String> sentTeamCreates = new HashSet<String>();

    public FoxtrotBoard(Player player) {
        this.player = player;

        Scoreboard board = FoxtrotPlugin.getInstance().getServer().getScoreboardManager().getNewScoreboard();

        objective = board.registerNewObjective("HCTeams", "dummy");
        objective.setDisplayName(FoxtrotPlugin.getInstance().getMapHandler().getScoreboardTitle());
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        player.setScoreboard(board);
    }

    public void update() {
        int nextValue = 14;

        for (ScoreGetter score : SCORES) {
            String value = score.getValue(player);
            String title = score.getTitle(player);

            // Null is returned if we shouldn't display anything.
            if (value == null) {
                if (displayedScores.containsKey(title)) {
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutScoreboardScore(title));
                    displayedScores.remove(title);
                }
            } else {
                if (!sentTeamCreates.contains(title)) {
                    ScoreboardTeamPacketMod scoreboardTeamAdd = new ScoreboardTeamPacketMod(title, "_", "_", new ArrayList<String>(), 0);
                    ScoreboardTeamPacketMod scoreboardTeamAddMember = new ScoreboardTeamPacketMod(title, Arrays.asList(title), 3);

                    scoreboardTeamAdd.sendToPlayer(player);
                    scoreboardTeamAddMember.sendToPlayer(player);

                    sentTeamCreates.add(title);
                }

                if (!displayedScores.containsKey(title) || displayedScores.get(title) != nextValue) {
                    PacketPlayOutScoreboardScore scoreboardScorePacket = new PacketPlayOutScoreboardScore();

                    setField(scoreboardScorePacket, "a", title);
                    setField(scoreboardScorePacket, "b", objective.getName());
                    setField(scoreboardScorePacket, "c", nextValue);
                    setField(scoreboardScorePacket, "d", 0);

                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(scoreboardScorePacket);
                    displayedScores.put(title, nextValue);
                }

                ScoreboardTeamPacketMod scoreboardTeamModify = new ScoreboardTeamPacketMod(title, "", ChatColor.GRAY + ": " + ChatColor.RED + value, null, 2);
                scoreboardTeamModify.sendToPlayer(player);
                nextValue--;
            }
        }

        if (nextValue == 14) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutScoreboardScore(" "));
        } else {
            PacketPlayOutScoreboardScore scoreboardScorePacket = new PacketPlayOutScoreboardScore();

            setField(scoreboardScorePacket, "a", " ");
            setField(scoreboardScorePacket, "b", objective.getName());
            setField(scoreboardScorePacket, "c", 15);
            setField(scoreboardScorePacket, "d", 0);

            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(scoreboardScorePacket);
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
