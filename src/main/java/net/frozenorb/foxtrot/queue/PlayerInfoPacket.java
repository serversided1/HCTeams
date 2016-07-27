package net.frozenorb.foxtrot.queue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.xpacket.FrozenXPacketHandler;
import net.frozenorb.qlib.xpacket.XPacket;
import net.frozenorb.qqueue.qQueue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

@AllArgsConstructor
public class PlayerInfoPacket implements XPacket {

    @Getter private String queue;
    @Getter private UUID player;
    @Getter private int totalLives;
    @Getter private long deathbanRemaining;
    @Getter private String livesLocked;
    @Getter private boolean eotwJoinAllowed;
    @Getter private boolean memberOfPowerFaction;

    // We have to have this for XPacket to do its thing.
    public PlayerInfoPacket() {}

    public static void sendResponse(final UUID player) {
        if (Foxtrot.getInstance().getServer().getPluginManager().getPlugin("qQueue") == null) {
            return;
        }

        new BukkitRunnable() {

            public void run() {
                int totalLives = Foxtrot.getInstance().getSoulboundLivesMap().getLives(player) + Foxtrot.getInstance().getFriendLivesMap().getLives(player);
                long deathbannedUntil = Foxtrot.getInstance().getDeathbanMap().getDeathban(player);
                String livesLocked = null;
                boolean eotwJoinAllowed = true;
                boolean memberOfPowerFaction = false;

                if (Foxtrot.getInstance().getServerHandler().getBetrayer(player) != null) {
                    livesLocked = "Betrayer";
                }

                if (Foxtrot.getInstance().getServerHandler().isPreEOTW()) {
                    livesLocked = "EOTW";
                }

                if (Foxtrot.getInstance().getServerHandler().isPreEOTW() && !Foxtrot.getInstance().getPlaytimeMap().hasPlayed(player)) {
                    eotwJoinAllowed = false;
                }

                Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

                if (team != null && team.isPowerFaction()) {
                    memberOfPowerFaction = true;
                }

                String queueId = qQueue.getInstance().getQueueHandler().getQueueId();
                long deathbanRemaining = deathbannedUntil - System.currentTimeMillis();
                PlayerInfoPacket packet = new PlayerInfoPacket(queueId, player, totalLives, deathbanRemaining, livesLocked, eotwJoinAllowed, memberOfPowerFaction);
                FrozenXPacketHandler.sendToAll(packet);
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

    public void onReceive() {}

}