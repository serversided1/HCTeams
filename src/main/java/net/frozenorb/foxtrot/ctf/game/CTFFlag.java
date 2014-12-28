package net.frozenorb.foxtrot.ctf.game;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.ctf.CTFHandler;
import net.frozenorb.foxtrot.ctf.enums.CTFFlagColor;
import net.frozenorb.foxtrot.ctf.enums.CTFFlagState;
import net.frozenorb.foxtrot.ctf.events.PlayerCaptureFlagEvent;
import net.frozenorb.foxtrot.ctf.events.PlayerDropFlagEvent;
import net.frozenorb.foxtrot.ctf.events.PlayerPickupFlagEvent;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashSet;

public class CTFFlag {

    public static final Material POLE_MATERIAL = Material.FENCE;
    public static final Material FLAG_MATERIAL = Material.WOOL;

    @Getter @Setter private Location spawnLocation;
    @Getter @Setter private Location captureLocation;
    @Getter @Setter private CTFFlagColor color;
    @Getter @Setter private CTFFlagState state;
    @Getter private Player flagHolder; // Setter is manually implemented down below

    public CTFFlag(Location spawnLocation, Location captureLocation, CTFFlagColor color) {
        this.spawnLocation = spawnLocation;
        this.captureLocation = captureLocation;
        this.color = color;
        this.state = CTFFlagState.CAP_POINT;
        this.flagHolder = null;

        dropFlag(true);
    }

    public void setFlagHolder(Player player) {
        if (flagHolder != null && player == null) {
            flagHolder.removePotionEffect(PotionEffectType.SLOW);
        } else if (flagHolder == null && player != null) {
            player.removePotionEffect(PotionEffectType.SLOW);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1));
        }

        this.flagHolder = player;
    }

    public void pickupFlag(Player player, boolean silent) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());

        if (team == null) {
            return;
        }

        setState(CTFFlagState.HELD_BY_PLAYER);
        setFlagHolder(player);

        if (!silent) {
            String teamString = ChatColor.GOLD + "[" + ChatColor.YELLOW + team.getName() + ChatColor.GOLD + "]";
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(CTFHandler.PREFIX + " " + ChatColor.YELLOW + "The " + getColor().getChatColor() + getColor().getName() + " Flag " + ChatColor.YELLOW + "has been picked up by " + teamString + player.getDisplayName() + ChatColor.YELLOW + ". " + ChatColor.DARK_AQUA + "(" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ() + ")");
        }

        player.sendMessage(ChatColor.LIGHT_PURPLE + "You've picked up a flag! Take the flag to " + getCaptureLocation().getBlockX() + ", " + getCaptureLocation().getBlockY() + ", " + getCaptureLocation().getBlockZ() + " within 30 minutes to capture it!");

        FoxtrotPlugin.getInstance().getServer().getPluginManager().callEvent(new PlayerPickupFlagEvent(player, this));
        updateVisual();
    }

    public void dropFlag(boolean silent) {
        setState(CTFFlagState.CAP_POINT);

        if (getFlagHolder() != null) {
            getFlagHolder().getInventory().setHelmet(null);
        }

        FoxtrotPlugin.getInstance().getServer().getPluginManager().callEvent(new PlayerDropFlagEvent(getFlagHolder(), this));
        setFlagHolder(null);

        if (!silent) {
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(CTFHandler.PREFIX + " " + ChatColor.YELLOW + "The " + getColor().getChatColor() + getColor().getName() + " Flag " + ChatColor.YELLOW + "has been returned to its spawn location." + ChatColor.DARK_AQUA + " (" + getSpawnLocation().getBlockX() + ", " + getSpawnLocation().getBlockY() + ", " + getSpawnLocation().getBlockZ() + ")");
        }

        updateVisual();
    }

    public void captureFlag(boolean silent) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(getFlagHolder().getName());
        CTFGame game = FoxtrotPlugin.getInstance().getCTFHandler().getGame();

        if (team == null) {
            return;
        }

        if (!game.getCapturedFlags().containsKey(team.getUniqueId())) {
            game.getCapturedFlags().put(team.getUniqueId(), new HashSet<CTFFlagColor>());
        }

        game.getCapturedFlags().get(team.getUniqueId()).add(getColor());

        int teamCaptures = game.getCapturedFlags().get(team.getUniqueId()).size();
        int neededCaptures = CTFFlagColor.values().length;
        String teamString = ChatColor.GOLD + "[" + ChatColor.YELLOW + team.getName() + ChatColor.GOLD + "]";

        if (teamCaptures != neededCaptures) {
            if (!silent) {
                FoxtrotPlugin.getInstance().getServer().broadcastMessage(CTFHandler.PREFIX + " " + ChatColor.YELLOW + "The " + getColor().getChatColor() + getColor().getName() + " Flag " + ChatColor.YELLOW + "has been captured by " + teamString + getFlagHolder().getDisplayName() + ChatColor.YELLOW + ". " + ChatColor.DARK_AQUA + "(" + teamCaptures + "/" + neededCaptures + ")");
            }

            dropFlag(false);
        } else {
            game.endGame(team);
        }

        FoxtrotPlugin.getInstance().getServer().getPluginManager().callEvent(new PlayerCaptureFlagEvent(getFlagHolder(), this));
    }

    public Location getLocation() {
        if (getState() == CTFFlagState.CAP_POINT) {
            return (getSpawnLocation());
        } else {
            if (getFlagHolder() == null) {
                dropFlag(true);
                return (getLocation());
            }

            return (getFlagHolder().getLocation());
        }
    }

    public void removeVisual() {
        // Remove the beacon (if it's still there)
        Block relativeNorth = getSpawnLocation().getBlock().getRelative(BlockFace.NORTH);
        getSpawnLocation().getBlock().setTypeIdAndData(relativeNorth.getTypeId(), relativeNorth.getData(), true);
    }

    public void updateVisual() {
        if (getState() == CTFFlagState.CAP_POINT) {
            getSpawnLocation().getBlock().setType(Material.BEACON);
        } else {
            removeVisual();

            ItemStack helmet = new ItemStack(FLAG_MATERIAL, 1, getColor().getDyeColor().getWoolData());
            ItemMeta itemMeta = helmet.getItemMeta();

            itemMeta.setDisplayName(getColor().getChatColor() + getColor().getName() + " Flag");
            itemMeta.setLore(Arrays.asList("", ChatColor.YELLOW + "To drop the flag, use /drop", ChatColor.YELLOW + "or drop this item."));

            helmet.setItemMeta(itemMeta);
            getFlagHolder().getInventory().setHelmet(helmet);
        }
    }

}