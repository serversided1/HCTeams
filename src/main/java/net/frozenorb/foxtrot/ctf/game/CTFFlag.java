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
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.bitmask.DTRBitmaskType;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class CTFFlag {

    public static final int INVALID_LOCATION_MAX = (int) TimeUnit.MINUTES.toSeconds(5);

    @Getter @Setter private Location spawnLocation;
    @Getter @Setter private Location captureLocation;
    @Getter @Setter private CTFFlagColor color;
    @Getter @Setter private CTFFlagState state;
    @Getter @Setter private int invalidLocationTimer;
    @Getter private Player flagHolder; // Setter is manually implemented down below

    private static Map<String, Long> messageCooldown = new HashMap<>();

    public CTFFlag(Location spawnLocation, Location captureLocation, CTFFlagColor color) {
        this.spawnLocation = spawnLocation;
        this.captureLocation = captureLocation;
        this.color = color;
        this.state = CTFFlagState.CAP_POINT;
        this.flagHolder = null;
        this.invalidLocationTimer = INVALID_LOCATION_MAX;

        dropFlag(true);
    }

    public void tick(int tick) {
        // Update our visual every 5 seconds
        if (tick % 5 == 0) {
            updateVisual();
        }

        if (getFlagHolder() != null) {
            // Y-level requirements
            if (getFlagHolder().getLocation().getBlockY() > 120 || getFlagHolder().getLocation().getBlockY() < 20) {
                setInvalidLocationTimer(getInvalidLocationTimer() - 1);
            }

            Team claim = LandBoard.getInstance().getTeam(getFlagHolder().getLocation());

            if (claim != null) {
                if (claim.hasDTRBitmask(DTRBitmaskType.SAFE_ZONE)) { // If the player is in spawn
                    setInvalidLocationTimer(getInvalidLocationTimer() - 1);
                } else if (claim.getOwner() != null) { // Otherwise if they're not in a 'special' claim
                    setInvalidLocationTimer(getInvalidLocationTimer() - 1);
                }
            }

            // Auto drop the flag if our invalid location timer is 0.
            if (getInvalidLocationTimer() <= 0) {
                dropFlag(false);
                FoxtrotPlugin.getInstance().getServer().broadcastMessage(CTFHandler.PREFIX + " " + getFlagHolder().getDisplayName() + ChatColor.YELLOW + " has dropped the " + getColor().getChatColor() + getColor().getName() + " Flag" + ChatColor.YELLOW + "!");
                return;
            }
        }

        if (getFlagHolder() != null) {
            // Capture the flag
            if (getFlagHolder().getLocation().distanceSquared(getCaptureLocation()) > 4) { // 2 blocks
                return;
            }

            Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(getFlagHolder().getName());

            if (team == null) {
                getFlagHolder().sendMessage(CTFHandler.PREFIX + " " + ChatColor.RED + "You must be on a team in order to capture the flag.");
                return;
            }

            if (FoxtrotPlugin.getInstance().getCTFHandler().getGame().getCapturedFlags().containsKey(team.getUniqueId()) && FoxtrotPlugin.getInstance().getCTFHandler().getGame().getCapturedFlags().get(team.getUniqueId()).contains(getColor())) {
                getFlagHolder().sendMessage(CTFHandler.PREFIX + " " + ChatColor.RED + "Your team has already captured this flag! It has been returned to its spawn location.");
                dropFlag(true);
                return;
            }

            captureFlag(false);
        } else {
            List<Player> onCap = new ArrayList<Player>();

            for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                if (player.getWorld().equals(getSpawnLocation().getWorld()) && getSpawnLocation().distanceSquared(player.getLocation()) < 4 && !player.isDead() && player.getGameMode() == GameMode.SURVIVAL) {
                    onCap.add(player);
                }
            }

            // We shuffle as otherwise having 'relog' will give you priority in starting to cap.
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

                for (CTFFlag possibleFlag : FoxtrotPlugin.getInstance().getCTFHandler().getGame().getFlags().values()) {
                    if (possibleFlag.getFlagHolder() != null && possibleFlag.getFlagHolder() == capper) {
                        if (!(messageCooldown.containsKey(capper.getName())) || messageCooldown.get(capper.getName()) < System.currentTimeMillis()) {
                            capper.sendMessage(CTFHandler.PREFIX + " " + ChatColor.RED + "You cannot carry multiple flags at the same time!");
                            messageCooldown.put(capper.getName(), System.currentTimeMillis() + 3000L);
                        }

                        return;
                    }
                }

                pickupFlag(capper, false);
            }
        }
    }

    public void setFlagHolder(Player player) {
        if (flagHolder != null && player == null) {
            flagHolder.removePotionEffect(PotionEffectType.SLOW);
        } else if (flagHolder == null && player != null) {
            player.removePotionEffect(PotionEffectType.SPEED);
            player.removePotionEffect(PotionEffectType.SLOW);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1));
        }

        this.flagHolder = player;
        this.invalidLocationTimer = INVALID_LOCATION_MAX;
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

        player.sendMessage(ChatColor.LIGHT_PURPLE + "You've picked up a flag! Take the flag to " + getCaptureLocation().getBlockX() + ", " + getCaptureLocation().getBlockY() + ", " + getCaptureLocation().getBlockZ() + " to capture it!");

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

            ItemStack helmet = new ItemStack(Material.WOOL, 1, getColor().getDyeColor().getWoolData());
            ItemMeta itemMeta = helmet.getItemMeta();

            itemMeta.setDisplayName(getColor().getChatColor() + getColor().getName() + " Flag");
            itemMeta.setLore(Arrays.asList("", ChatColor.YELLOW + "To drop the flag, use /drop", ChatColor.YELLOW + "or drop this item."));

            helmet.setItemMeta(itemMeta);
            getFlagHolder().getInventory().setHelmet(helmet);
        }
    }

}