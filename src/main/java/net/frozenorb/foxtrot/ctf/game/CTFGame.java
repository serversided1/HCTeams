package net.frozenorb.foxtrot.ctf.game;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.ctf.CTFHandler;
import net.frozenorb.foxtrot.ctf.enums.CTFFlagColor;
import net.frozenorb.foxtrot.ctf.enums.CTFFlagState;
import net.frozenorb.foxtrot.team.Team;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CTFGame implements Listener {

    @Getter private Map<CTFFlagColor, CTFFlag> flags = new HashMap<CTFFlagColor, CTFFlag>();
    @Getter private Map<ObjectId, Set<CTFFlagColor>> capturedFlags = new HashMap<ObjectId, Set<CTFFlagColor>>();
    private int tick = 0;

    public CTFGame(CTFFlag... flags) {
        FoxtrotPlugin.getInstance().getServer().getPluginManager().registerEvents(this, FoxtrotPlugin.getInstance());

        for (CTFFlag flag : flags) {
            getFlags().put(flag.getColor(), flag);
        }

        FoxtrotPlugin.getInstance().getServer().broadcastMessage(CTFHandler.PREFIX + " " + ChatColor.GOLD.toString() + ChatColor.BOLD + "A new CTF game has initiated. Use /CTF for more info about the game.");
        FoxtrotPlugin.getInstance().getCTFHandler().setGame(this);
    }

    public void endGame(Team winner) {
        if (winner == null) {
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(CTFHandler.PREFIX + " " + ChatColor.GOLD.toString() + "The game has ended!");
        } else {
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(CTFHandler.PREFIX + " " + ChatColor.GOLD.toString() + winner.getName() + " has won CTF!");
        }

        for (CTFFlag flag : getFlags().values()) {
            flag.removeVisual();
        }

        HandlerList.unregisterAll(this);
        FoxtrotPlugin.getInstance().getCTFHandler().setGame(null);
    }

    public void tick() {
        tick++;

        // 2 minute update message
        if (tick % 120 == 0) {
            sendUpdateMessage();
        }

        // Tick all our individual flags
        for (CTFFlag flag : getFlags().values()) {
            flag.tick(tick);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() != Material.WOOL) {
            return;
        }

        ItemMeta itemMeta = event.getCurrentItem().getItemMeta();

        if (!itemMeta.hasDisplayName() || !itemMeta.getDisplayName().startsWith(String.valueOf(ChatColor.COLOR_CHAR))) {
            return;
        }

        for (CTFFlag flag : getFlags().values()) {
            if (flag.getFlagHolder() != null && flag.getFlagHolder() == event.getWhoClicked()) {
                flag.dropFlag(false);
                FoxtrotPlugin.getInstance().getServer().broadcastMessage(CTFHandler.PREFIX + " " + ((Player) event.getWhoClicked()).getDisplayName() + ChatColor.YELLOW + " has dropped the " + flag.getColor().getChatColor() + flag.getColor().getName() + " Flag" + ChatColor.YELLOW + "!");
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().getType() != Material.WOOL) {
            return;
        }

        ItemMeta itemMeta = event.getItemDrop().getItemStack().getItemMeta();

        if (!itemMeta.hasDisplayName() || !itemMeta.getDisplayName().startsWith(String.valueOf(ChatColor.COLOR_CHAR))) {
            return;
        }

        for (CTFFlag flag : getFlags().values()) {
            if (flag.getFlagHolder() != null && flag.getFlagHolder() == event.getPlayer()) {
                event.getItemDrop().remove();
                flag.dropFlag(false);
                FoxtrotPlugin.getInstance().getServer().broadcastMessage(CTFHandler.PREFIX + " " + event.getPlayer().getDisplayName() + ChatColor.YELLOW + " has dropped the " + flag.getColor().getChatColor() + flag.getColor().getName() + " Flag" + ChatColor.YELLOW + "!");
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        for (CTFFlag flag : getFlags().values()) {
            if (flag.getFlagHolder() != null && flag.getFlagHolder() == event.getEntity()) {
                // Don't allow the 'flag' to be dropped.
                for (ItemStack dropItem : new ArrayList<ItemStack>(event.getDrops())) {
                    if (dropItem.getType() == Material.WOOL) {
                        ItemMeta itemMeta = dropItem.getItemMeta();

                        if (itemMeta.hasDisplayName() && itemMeta.getDisplayName().startsWith(String.valueOf(ChatColor.COLOR_CHAR))) {
                            event.getDrops().remove(dropItem);
                        }
                    }
                }

                flag.dropFlag(false);
                FoxtrotPlugin.getInstance().getServer().broadcastMessage(CTFHandler.PREFIX + " " + event.getEntity().getDisplayName() + ChatColor.YELLOW + " has dropped the " + flag.getColor().getChatColor() + flag.getColor().getName() + " Flag" + ChatColor.YELLOW + "!");
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        for (CTFFlag flag : getFlags().values()) {
            if (flag.getFlagHolder() != null && flag.getFlagHolder() == event.getPlayer()) {
                // This will remove all wool, but .remove with an ItemStack is derpy (and they could've modified the itemstack), so we do this.
                event.getPlayer().getInventory().remove(Material.WOOL);

                flag.dropFlag(false);
                FoxtrotPlugin.getInstance().getServer().broadcastMessage(CTFHandler.PREFIX + " " + event.getPlayer().getDisplayName() + ChatColor.YELLOW + " has disconnected and dropped the " + flag.getColor().getChatColor() + flag.getColor().getName() + " Flag" + ChatColor.YELLOW + "!");
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() != Material.POTION) {
            return;
        }

        for (CTFFlag flag : getFlags().values()) {
            if (flag.getFlagHolder() != null && flag.getFlagHolder() == event.getPlayer()) {
                try {
                    Potion potion = Potion.fromItemStack(event.getItem());

                    for (PotionEffect effect : potion.getEffects()) {
                        if (effect.getType().equals(PotionEffectType.SPEED)) {
                            event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot drink speed potions while holding the flag! Use beacons or Bards to get speed!");
                            event.setCancelled(true);
                        }
                    }
                } catch (Exception e) {

                }

                return;
            }
        }
    }

    private void sendUpdateMessage() {
        FoxtrotPlugin.getInstance().getServer().broadcastMessage(CTFHandler.PREFIX + " " + ChatColor.GOLD + "CTF flag update:");

        for (CTFFlag flag : getFlags().values()) {
            Location flagLocation = flag.getLocation();
            String locationString;

            if (flag.getState() == CTFFlagState.CAP_POINT) {
                locationString = ChatColor.AQUA + "At cap point";
            } else {
                locationString = ChatColor.AQUA + "Held by " + flag.getFlagHolder().getName();
            }

            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.DARK_AQUA + "=> " + flag.getColor().getChatColor() + flag.getColor().getName() + " Flag: " + ChatColor.WHITE + locationString + ChatColor.DARK_AQUA + " (" + flagLocation.getBlockX() + ", " + flagLocation.getBlockY() + ", " + flagLocation.getBlockZ() + ")");
        }
    }

}