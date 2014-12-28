package net.frozenorb.foxtrot.ctf.game;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.ctf.CTFHandler;
import net.frozenorb.foxtrot.ctf.enums.CTFFlagColor;
import net.frozenorb.foxtrot.ctf.enums.CTFFlagState;
import net.frozenorb.foxtrot.team.Team;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

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

        if (tick % 120 == 0) {
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

        for (CTFFlag flag : getFlags().values()) {
            if (tick % 5 == 0) {
                flag.updateVisual();
            }

            if (flag.getFlagHolder() != null) {
                // Capture the flag
                if (flag.getFlagHolder().getLocation().distanceSquared(flag.getCaptureLocation()) > 4) { // 2 blocks
                    continue;
                }

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
            } else {
                List<Player> onCap = new ArrayList<Player>();

                for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                    if (flag.getSpawnLocation().distanceSquared(player.getLocation()) < 4 && !player.isDead() && player.getGameMode() == GameMode.SURVIVAL) {
                        onCap.add(player);
                    }
                }

                // We shuffle as other having 'relog' will give you priority in starting to cap.
                Collections.shuffle(onCap);

                if (onCap.size() != 0) {
                    Player capper = onCap.get(0);
                    Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(capper.getName());

                    if (team == null) {
                        if (!(messageCooldown.containsKey(capper.getName())) || messageCooldown.get(capper.getName()) < System.currentTimeMillis()) {
                            capper.sendMessage(CTFHandler.PREFIX + " " + ChatColor.RED + "You must be on a team in order to pickup the flag.");
                            messageCooldown.put(capper.getName(), System.currentTimeMillis() + 3000L);
                        }

                        return;
                    }

                    for (CTFFlag possibleFlag : getFlags().values()) {
                        if (possibleFlag.getFlagHolder() != null && possibleFlag.getFlagHolder() == capper) {
                            if (!(messageCooldown.containsKey(capper.getName())) || messageCooldown.get(capper.getName()) < System.currentTimeMillis()) {
                                capper.sendMessage(CTFHandler.PREFIX + " " + ChatColor.RED + "You cannot carry multiple flags at the same time!");
                                messageCooldown.put(capper.getName(), System.currentTimeMillis() + 3000L);
                            }

                            return;
                        }
                    }

                    flag.pickupFlag(capper, false);
                }
            }
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

}