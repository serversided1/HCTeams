package net.frozenorb.foxtrot.nametag;

import net.frozenorb.foxtrot.util.ReflectionUtils;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

public class ScoreboardTeamPacketMod {

    private static Method getHandle;
    private static Method sendPacket;
    private static Field playerConnection;

    private static Class<?> packetType;

    private Object packet;

    static {
        try {
            packetType = Class.forName(ReflectionUtils.getPacketTeamClasspath());

            Class<?> typeCraftPlayer = Class.forName(ReflectionUtils.getCraftPlayerClasspath());
            Class<?> typeNMSPlayer = Class.forName(ReflectionUtils.getNMSPlayerClasspath());
            Class<?> typePlayerConnection = Class.forName(ReflectionUtils.getPlayerConnectionClasspath());

            getHandle = typeCraftPlayer.getMethod("getHandle");
            playerConnection = typeNMSPlayer.getField("playerConnection");
            sendPacket = typePlayerConnection.getMethod("sendPacket", Class.forName(ReflectionUtils.getPacketClasspath()));
        } catch (Exception e) {
            System.out.println("Failed to setup reflection for Packet209Mod!");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("rawtypes")
    public ScoreboardTeamPacketMod(String name, String prefix, String suffix, Collection players, int paramInt)
            throws ClassNotFoundException, IllegalAccessException,
            InstantiationException, NoSuchMethodException,
            NoSuchFieldException, InvocationTargetException {

        packet = packetType.newInstance();

        setField("a", name);
        setField("f", paramInt);

        if ((paramInt == 0) || (paramInt == 2)) {
            setField("b", name);
            setField("c", prefix);
            setField("d", suffix);
            setField("g", 3);
        }
        if (paramInt == 0)
            addAll(players);
    }

    @SuppressWarnings("rawtypes")
    public ScoreboardTeamPacketMod(String name, Collection players, int paramInt)
            throws ClassNotFoundException, IllegalAccessException,
            InstantiationException, NoSuchMethodException,
            NoSuchFieldException, InvocationTargetException {

        packet = packetType.newInstance();

        if ((paramInt != 3) && (paramInt != 4))
            throw new IllegalArgumentException("Method must be join or leave for player constructor");
        if ((players == null) || (players.isEmpty()))
            players = new ArrayList<String>();

        setField("g", 3);
        setField("a", name);
        setField("f", paramInt);
        addAll(players);
    }

    public void sendToPlayer(Player bukkitPlayer) {
        try {
            Object player = getHandle.invoke(bukkitPlayer);
            Object connection = playerConnection.get(player);

            sendPacket.invoke(connection, packet);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setField(String field, Object value) {
        try {
            Field f = packet.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(packet, value);
            f.setAccessible(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void addAll(Collection<?> col) throws NoSuchFieldException, IllegalAccessException {
        Field f = packet.getClass().getDeclaredField("e");
        f.setAccessible(true);
        ((Collection) f.get(packet)).addAll(col);
    }

}