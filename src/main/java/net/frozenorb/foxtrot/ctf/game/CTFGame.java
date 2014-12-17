package net.frozenorb.foxtrot.ctf.game;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.ctf.CTFHandler;
import net.frozenorb.foxtrot.ctf.enums.CTFFlagColor;
import net.frozenorb.foxtrot.team.Team;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CTFGame implements Listener {

    @Getter private Map<CTFFlagColor, CTFFlag> flags = new HashMap<CTFFlagColor, CTFFlag>();
    @Getter private Map<ObjectId, Set<CTFFlagColor>> capturedFlags = new HashMap<ObjectId, Set<CTFFlagColor>>();
    private int tick = 0;
    private Map<String, Long> messageCooldown = new HashMap<>();

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

        for (CTFFlag flag : getFlags().values()) {
            if (tick % 20 == 0) {
                flag.removeVisual();
                flag.updateVisual();
            }

            if (flag.getFlagHolder() != null && flag.getFlagHolder().getLocation().distance(flag.getCaptureLocation()) < 5) {
                // Capture the flag
                Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(flag.getFlagHolder().getName());

                if (team == null) {
                    flag.getFlagHolder().sendMessage(CTFHandler.PREFIX + " " + ChatColor.RED + "You must be on a team in order to capture the flag.");
                    continue;
                }

                if (capturedFlags.containsKey(team.getUniqueId()) && capturedFlags.get(team.getUniqueId()).contains(flag.getColor())) {
                    flag.getFlagHolder().sendMessage(CTFHandler.PREFIX + " " + ChatColor.RED + "Your team has already captured this flag! It has been returned to its spawn location.");
                    flag.dropFlag(true);
                    continue;
                }

                flag.captureFlag(false);
            }
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        for (CTFFlag flag : getFlags().values()) {
            if (flag.getAnchorItem() != null && flag.getAnchorItem().equals(event.getItem())) {
                // Pickup the flag
                event.setCancelled(true);

                if (event.getPlayer().hasMetadata("invisible")) {
                    return;
                }

                Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(event.getPlayer().getName());

                if (team == null) {
                    if (!(messageCooldown.containsKey(event.getPlayer().getName())) || messageCooldown.get(event.getPlayer().getName()) < System.currentTimeMillis()) {
                        event.getPlayer().sendMessage(CTFHandler.PREFIX + " " + ChatColor.RED + "You must be on a team in order to pickup the flag.");
                        messageCooldown.put(event.getPlayer().getName(), System.currentTimeMillis() + 3000L);
                    }

                    return;
                }

                for (CTFFlag possibleFlag : getFlags().values()) {
                    if (possibleFlag.getFlagHolder() != null && possibleFlag.getFlagHolder() == event.getPlayer()) {
                        if (!(messageCooldown.containsKey(event.getPlayer().getName())) || messageCooldown.get(event.getPlayer().getName()) < System.currentTimeMillis()) {
                            event.getPlayer().sendMessage(CTFHandler.PREFIX + " " + ChatColor.RED + "You cannot carry multiple flags at the same time!");
                            messageCooldown.put(event.getPlayer().getName(), System.currentTimeMillis() + 3000L);
                        }

                        return;
                    }
                }

                flag.pickupFlag(event.getPlayer(), false);
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
                FoxtrotPlugin.getInstance().getServer().broadcastMessage(CTFHandler.PREFIX + " " + ChatColor.AQUA + event.getPlayer().getName() + ChatColor.YELLOW + " has dropped the " + flag.getColor().getChatColor() + flag.getColor().getName() + " Flag" + ChatColor.YELLOW + "!");
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
                FoxtrotPlugin.getInstance().getServer().broadcastMessage(CTFHandler.PREFIX + " " + ChatColor.AQUA + event.getEntity().getName() + ChatColor.YELLOW + " has dropped the " + flag.getColor().getChatColor() + flag.getColor().getName() + " Flag" + ChatColor.YELLOW + "!");
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
                FoxtrotPlugin.getInstance().getServer().broadcastMessage(CTFHandler.PREFIX + " " + ChatColor.AQUA + event.getPlayer().getName() + ChatColor.YELLOW + " has disconnected and dropped the " + flag.getColor().getChatColor() + flag.getColor().getName() + " Flag" + ChatColor.YELLOW + "!");
            }
        }
    }

}