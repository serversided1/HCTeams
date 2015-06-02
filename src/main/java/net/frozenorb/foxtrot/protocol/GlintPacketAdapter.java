package net.frozenorb.foxtrot.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class GlintPacketAdapter extends PacketAdapter {

    public GlintPacketAdapter() {
        super(Foxtrot.getInstance(), PacketType.Play.Server.ENTITY_EQUIPMENT);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        /*PacketContainer packet = event.getPacket();

        int entityID = packet.getIntegers().read(0);
        ItemStack item = packet.getItemModifier().read(0);

        if (!Foxtrot.getInstance().getGlintMap().getGlintToggled(event.getPlayer().getUniqueId())) {
            Player player = getPlayerByEntityID(entityID);

            if (player != null && item != null && item.getType() != Material.AIR) {
                for (Enchantment enchantment : item.getEnchantments().keySet()) {
                    item.removeEnchantment(enchantment);
                }
            }
        }*/
    }

    public static Player getPlayerByEntityID(int entityID) {
        for (World world : Foxtrot.getInstance().getServer().getWorlds()) {
            for (Player player : world.getEntitiesByClass(Player.class)) {
                if (player.getEntityId() == entityID) {
                    return (player);
                }
            }
        }

        return (null);
    }

}