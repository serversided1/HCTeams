package net.frozenorb.foxtrot.ctf.game;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.ctf.CTFHandler;
import net.frozenorb.foxtrot.ctf.enums.CTFFlagColor;
import net.frozenorb.foxtrot.team.Team;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CTFGame implements Listener {

    @Getter private Map<CTFFlagColor, CTFFlag> flags = new HashMap<CTFFlagColor, CTFFlag>();
    @Getter private Map<ObjectId, Set<CTFFlagColor>> capturedFlags = new HashMap<ObjectId, Set<CTFFlagColor>>();
    int tick = 0;

    public CTFGame(CTFFlag... flags) {
        FoxtrotPlugin.getInstance().getServer().getPluginManager().registerEvents(this, FoxtrotPlugin.getInstance());

        for (CTFFlag flag : flags) {
            getFlags().put(flag.getColor(), flag);
        }

        FoxtrotPlugin.getInstance().getCTFHandler().setGame(this);
    }

    public void endGame(Team winner) {
        if (winner == null) {
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(CTFHandler.PREFIX + " " + ChatColor.GOLD.toString() + "The game has ended!");
        } else {
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(CTFHandler.PREFIX + " " + ChatColor.GOLD.toString() + winner.getName() + " has won CTF!");
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
                    flag.getFlagHolder().sendMessage(CTFHandler.PREFIX + " " + ChatColor.RED + "Your team has already captured this flag!");
                    flag.dropFlag(true);

                    String teamString = ChatColor.GOLD + "[" + ChatColor.YELLOW + team.getName() + ChatColor.GOLD + "]";
                    FoxtrotPlugin.getInstance().getServer().broadcastMessage(CTFHandler.PREFIX + " " + ChatColor.YELLOW + "The " + flag.getColor().getChatColor() + flag.getColor().getName() + " Flag " + ChatColor.YELLOW + "has been captured by " + teamString + ChatColor.AQUA + flag.getFlagHolder().getName() + ChatColor.YELLOW + ".");
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

                Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(event.getPlayer().getName());

                if (team == null) {
                    event.getPlayer().sendMessage(CTFHandler.PREFIX + " " + ChatColor.RED + "You must be on a team in order to pickup the flag.");
                    return;
                }

                for (CTFFlag possibleFlag : getFlags().values()) {
                    if (possibleFlag.getFlagHolder() != null && possibleFlag.getFlagHolder() == event.getPlayer()) {
                        event.getPlayer().sendMessage(CTFHandler.PREFIX + " " + ChatColor.RED + "You cannot carry multiple flags at the same time!");
                        return;
                    }
                }

                flag.pickupFlag(event.getPlayer(), false);
            }
        }
    }

}