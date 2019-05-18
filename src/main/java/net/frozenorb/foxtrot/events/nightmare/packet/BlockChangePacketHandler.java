package net.frozenorb.foxtrot.events.nightmare.packet;

import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.events.nightmare.NightmareHandler;
import net.frozenorb.foxtrot.events.nightmare.progress.ProgressData;
import net.hylist.handler.PacketHandler;
import net.minecraft.server.v1_7_R4.BlockObsidian;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutBlockChange;
import net.minecraft.server.v1_7_R4.PacketPlayOutMultiBlockChange;
import net.minecraft.server.v1_7_R4.PlayerConnection;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class BlockChangePacketHandler implements PacketHandler {

	private final NightmareHandler handler;

	@Override
	public void handleReceivedPacket(PlayerConnection playerConnection, Packet packet) {

	}

	@Override
	public void handleSentPacket(PlayerConnection playerConnection, Packet packet) {

	}

	@Override
	public boolean handleSentPacketCancellable(PlayerConnection playerConnection, Packet packet) {
		if (packet instanceof PacketPlayOutBlockChange) {
			PacketPlayOutBlockChange cast = (PacketPlayOutBlockChange) packet;

			if (cast.block instanceof BlockObsidian) {
				Player player = playerConnection.getPlayer();

				if (player.getWorld().equals(handler.getWorld()) && handler.hasProgression(player)) {
					ProgressData progressData = handler.getOrCreateProgression(player.getUniqueId());
					return !progressData.getAirBlocks().contains(new Location(handler.getWorld(), cast.a, cast.b, cast.c));
				}
			}
		} else if (packet instanceof PacketPlayOutMultiBlockChange) {
			PacketPlayOutMultiBlockChange cast = (PacketPlayOutMultiBlockChange) packet;
			Player player = playerConnection.getPlayer();

			if (player.getWorld().equals(handler.getWorld()) && handler.hasProgression(player)) {
				ProgressData progressData = handler.getOrCreateProgression(player.getUniqueId());

				for (int i = 0; i < cast.d; i++) {
					int x = (cast.ashort[i] >> 12 & 15) + (cast.b.x * 16);
					int y = cast.ashort[i] & 255;
					int z = (cast.ashort[i] >> 8 & 15) + (cast.b.z * 16);

					if (progressData.getAirBlocks().contains(new Location(handler.getWorld(), x, y, z))) {
						return false;
					}
				}
			}
		}

		return true;
	}

}
