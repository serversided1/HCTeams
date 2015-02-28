package net.frozenorb.foxtrot.util;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;

public class ItemMessage {

    private String[] FORMATS = new String[] { "%s", " %s " };

    public void sendMessage(Player player, ItemMessageGetter message, int slot) {
        (new ItemMessageTask(player, message, slot)).runTaskTimer(FoxtrotPlugin.getInstance(), 0L, 20L);
    }

    public class ItemMessageTask extends BukkitRunnable implements Listener {

        private WeakReference<Player> playerRef;
        private ItemMessageGetter message;
        private int slot;
        private int iterations = 0;

        public ItemMessageTask(Player player, ItemMessageGetter message, int slot) {
            this.playerRef = new WeakReference<>(player);
            this.slot = slot;
            this.message = message;

            FoxtrotPlugin.getInstance().getServer().getPluginManager().registerEvents(this, FoxtrotPlugin.getInstance());
            run();
        }

        @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
        public void onItemHeldChange(PlayerItemHeldEvent event) {
            Player player = event.getPlayer();

            if (player.equals(playerRef.get())) {
                finish(player);
            }
        }

        @Override
        public void run() {
            Player player = playerRef.get();

            if (player != null) {
                refresh(player);
            } else {
                cleanup();
            }
        }

        private void refresh(Player player) {
            sendItemSlotChange(player, slot, makeStack(player));
        }

        private void finish(Player player) {
            sendItemSlotChange(player, slot, player.getInventory().getItem(slot));
            cleanup();
        }

        private void cleanup() {
            cancel();
            HandlerList.unregisterAll(this);
        }

        private ItemStack makeStack(Player player) {
            ItemStack stack = player.getInventory().getItem(slot);

            if (stack == null || stack.getType() == Material.AIR) {
                return (null);
            }

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(stack.getType());
            // fool the client into thinking the item name has changed, so it actually (re)displays it
            meta.setDisplayName(String.format(FORMATS[iterations % FORMATS.length], message.getMessage(player)));
            stack.setItemMeta(meta);

            return (stack);
        }

        private void sendItemSlotChange(Player player, int slot, ItemStack stack) {
            if (stack == null) {
                finish(player);
                return;
            }

            PacketContainer setSlot = new PacketContainer(103);
            // int field 0: window id (0 = player inventory)
            // int field 1: slot number (36 - 44 for player hotbar)
            setSlot.getIntegers().write(0, 0).write(1, slot + 36);
            setSlot.getItemModifier().write(0, stack);

            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, setSlot);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static interface ItemMessageGetter {

        public String getMessage(Player player);

    }

}