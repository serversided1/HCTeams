package net.frozenorb.foxtrot.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class ClientCommandPacketAdaper extends PacketAdapter {

    public ClientCommandPacketAdaper() {
        super(Foxtrot.getInstance(), PacketType.Play.Client.CLIENT_COMMAND);
    }

    @Override
    public void onPacketReceiving(final PacketEvent event) {
        if (event.getPacket().getClientCommands().read(0) == EnumWrappers.ClientCommand.PERFORM_RESPAWN) {
            if (!Foxtrot.getInstance().getDeathbanMap().isDeathbanned(event.getPlayer().getUniqueId())) {
                return;
            }

            long unbannedOn = Foxtrot.getInstance().getDeathbanMap().getDeathban(event.getPlayer().getUniqueId());
            long left = unbannedOn - System.currentTimeMillis();
            final String time = TimeUtils.formatIntoDetailedString((int) left / 1000);
            event.setCancelled(true);

            new BukkitRunnable() {

                public void run() {
                    event.getPlayer().setMetadata("loggedout", new FixedMetadataValue(Foxtrot.getInstance(), true));

                    if (Foxtrot.getInstance().getServerHandler().isPreEOTW()) {
                        event.getPlayer().kickPlayer(ChatColor.YELLOW + "Come back tomorrow for SOTW!");
                    } else {
                        event.getPlayer().kickPlayer(ChatColor.YELLOW + "Come back in " + time + "!");
                    }
                }

            }.runTask(Foxtrot.getInstance());
        }
    }

}