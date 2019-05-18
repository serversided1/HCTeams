package net.frozenorb.foxtrot.events.nightmare.packet;

import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.events.nightmare.NightmareHandler;
import net.frozenorb.foxtrot.events.nightmare.progress.ProgressData;
import net.hylist.handler.PacketHandler;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_7_R4.PlayerConnection;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class EntitySpawnPacketHandler implements PacketHandler {

	private final NightmareHandler handler;

	@Override
	public void handleReceivedPacket(PlayerConnection playerConnection, Packet packet) {

	}

	@Override
	public void handleSentPacket(PlayerConnection playerConnection, Packet packet) {

	}

	@Override
	public boolean handleSentPacketCancellable(PlayerConnection playerConnection, Packet packet) {
		if (packet instanceof PacketPlayOutSpawnEntity) {
			PacketPlayOutSpawnEntity cast = (PacketPlayOutSpawnEntity) packet;

			if (cast.j != 2) {
				Player player = playerConnection.getPlayer();

				if (player.getWorld().equals(handler.getWorld()) && handler.hasProgression(player)) {
					ProgressData progressData = handler.getOrCreateProgression(player.getUniqueId());
					return progressData.isTrackedId(cast.a);
				}
			}
		} else if (packet instanceof PacketPlayOutSpawnEntityLiving) {
			PacketPlayOutSpawnEntityLiving cast = (PacketPlayOutSpawnEntityLiving) packet;
			Player player = playerConnection.getPlayer();

			if (player.getWorld().equals(handler.getWorld()) && handler.hasProgression(player)) {
				ProgressData progressData = handler.getOrCreateProgression(player.getUniqueId());
				return progressData.isTrackedId(cast.a);
			}
		}

		return true;
	}

}
