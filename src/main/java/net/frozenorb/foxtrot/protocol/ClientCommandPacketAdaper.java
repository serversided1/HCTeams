package net.frozenorb.foxtrot.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class ClientCommandPacketAdaper extends PacketAdapter {

    public ClientCommandPacketAdaper() {
        super(FoxtrotPlugin.getInstance(), PacketType.Play.Client.CLIENT_COMMAND);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event.getPacket().getClientCommands().read(0) == EnumWrappers.ClientCommand.PERFORM_RESPAWN) {
            if (!FoxtrotPlugin.getInstance().getDeathbanMap().isDeathbanned(event.getPlayer().getUniqueId())) {
                return;
            }

            long unbannedOn = FoxtrotPlugin.getInstance().getDeathbanMap().getDeathban(event.getPlayer().getUniqueId());
            long left = unbannedOn - System.currentTimeMillis();
            final String time = TimeUtils.formatIntoDetailedString((int) left / 1000);

            new BukkitRunnable() {

                public void run() {
                    if (FoxtrotPlugin.getInstance().getServerHandler().isPreEOTW()) {
                        event.getPlayer().kickPlayer(ChatColor.YELLOW + "Come back tomorrow for SOTW!");
                    } else {
                        event.getPlayer().kickPlayer(ChatColor.YELLOW + "Come back in " + time + "!");
                    }
                }

            }.runTask(FoxtrotPlugin.getInstance());
        }
    }

}