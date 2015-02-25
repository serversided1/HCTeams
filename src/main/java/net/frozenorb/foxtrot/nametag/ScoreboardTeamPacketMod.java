package net.frozenorb.foxtrot.nametag;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardTeam;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

public class ScoreboardTeamPacketMod {

    private PacketPlayOutScoreboardTeam packet;

    @SuppressWarnings("rawtypes")
    public ScoreboardTeamPacketMod(String name, String prefix, String suffix, Collection players, int paramInt) {
        packet = new PacketPlayOutScoreboardTeam();

        setField("a", name);
        setField("f", paramInt);

        if (paramInt == 0 || paramInt == 2) {
            setField("b", name);
            setField("c", prefix);
            setField("d", suffix);
            setField("g", 3);
        }

        if (paramInt == 0) {
            addAll(players);
        }
    }

    @SuppressWarnings("rawtypes")
    public ScoreboardTeamPacketMod(String name, Collection players, int paramInt) {
        packet = new PacketPlayOutScoreboardTeam();

        if (players == null) {
            players = new ArrayList<String>();
        }

        setField("g", 3);
        setField("a", name);
        setField("f", paramInt);
        addAll(players);
    }

    public void sendToPlayer(Player bukkitPlayer) {
        ((CraftPlayer) bukkitPlayer).getHandle().playerConnection.sendPacket(packet);
    }

    public void setField(String field, Object value) {
        try {
            Field fieldObject = packet.getClass().getDeclaredField(field);

            fieldObject.setAccessible(true);
            fieldObject.set(packet, value);
        } catch (Exception e) {
            FoxtrotPlugin.getInstance().getBugSnag().notify(e);
            e.printStackTrace();
        }
    }

    private void addAll(Collection col) {
        try {
            Field fieldObject = packet.getClass().getDeclaredField("e");

            fieldObject.setAccessible(true);
            ((Collection) fieldObject.get(packet)).addAll(col);
        } catch (Exception e) {
            FoxtrotPlugin.getInstance().getBugSnag().notify(e);
            e.printStackTrace();
        }
    }

}