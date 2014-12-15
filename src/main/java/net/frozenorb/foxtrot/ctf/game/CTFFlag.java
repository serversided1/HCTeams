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
import net.minecraft.server.v1_7_R3.EntityWitherSkull;
import net.minecraft.server.v1_7_R3.WorldServer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class CTFFlag {

    public static final int POLE_HEIGHT = 10;
    public static final int FLAG_WIDTH = 12;
    public static final int FLAG_HEIGHT = 5;
    public static final float FLAG_PROPORTION = 0.4F;

    public static final Material POLE_MATERIAL = Material.WOOL;
    public static final Material FLAG_MATERIAL = Material.WOOL;

    @Getter @Setter private Location spawnLocation;
    @Getter @Setter private Location captureLocation;
    @Getter @Setter private CTFFlagColor color;
    @Getter @Setter private CTFFlagState state;
    @Getter @Setter private Item anchorItem;
    @Getter @Setter private Player flagHolder;

    private Set<Entity> flagItems = new HashSet<Entity>();

    public CTFFlag(Location spawnLocation, Location captureLocation, CTFFlagColor color) {
        this.spawnLocation = spawnLocation;
        this.captureLocation = captureLocation;
        this.color = color;
        this.state = CTFFlagState.CAP_POINT;
        this.anchorItem = null;
        this.flagHolder = null;

        dropFlag(true);
    }

    public void pickupFlag(Player player, boolean silent) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());

        if (team == null) {
            return;
        }

        setState(CTFFlagState.HELD_BY_PLAYER);
        getAnchorItem().remove();
        setAnchorItem(null);
        setFlagHolder(player);

        if (!silent) {
            String teamString = ChatColor.GOLD + "[" + ChatColor.YELLOW + team.getName() + ChatColor.GOLD + "]";
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(CTFHandler.PREFIX + " " + ChatColor.YELLOW + "The " + getColor().getChatColor() + getColor().getName() + " Flag " + ChatColor.YELLOW + "has been picked up by " + teamString + ChatColor.AQUA + player.getName() + ChatColor.YELLOW + ". " + ChatColor.DARK_AQUA + "(" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ() + ")");
        }

        FoxtrotPlugin.getInstance().getServer().getPluginManager().callEvent(new PlayerPickupFlagEvent(player, this));
        updateVisual();
    }

    public void dropFlag(boolean silent) {
        setState(CTFFlagState.CAP_POINT);

        if (getFlagHolder() != null) {
            getFlagHolder().getInventory().setHelmet(null);
        }

        setAnchorItem(spawnAnchorItem());

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

        if (!silent) {
            int teamCaptures = game.getCapturedFlags().get(team.getUniqueId()).size();
            int neededCaptures = CTFFlagColor.values().length;
            String teamString = ChatColor.GOLD + "[" + ChatColor.YELLOW + team.getName() + ChatColor.GOLD + "]";

            if (teamCaptures != neededCaptures) {
                FoxtrotPlugin.getInstance().getServer().broadcastMessage(CTFHandler.PREFIX + " " + ChatColor.YELLOW + "The " + getColor().getChatColor() + getColor().getName() + " Flag " + ChatColor.YELLOW + "has been captured by " + teamString + ChatColor.AQUA + getFlagHolder().getName() + ChatColor.YELLOW + ". " + ChatColor.DARK_AQUA + "(" + teamCaptures + "/" + neededCaptures + ")");
            } else {
                game.endGame(team);
            }
        }

        FoxtrotPlugin.getInstance().getServer().getPluginManager().callEvent(new PlayerCaptureFlagEvent(getFlagHolder(), this));
        dropFlag(false);
    }

    public Item spawnAnchorItem() {
        Item anchorItem = getSpawnLocation().getWorld().dropItem(getSpawnLocation(), new ItemStack(POLE_MATERIAL));
        return (anchorItem);
    }

    public Location getLocation() {
        if (getState() == CTFFlagState.CAP_POINT) {
            if (getAnchorItem() == null || getAnchorItem().isDead()) {
                setAnchorItem(spawnAnchorItem());
            }

            return (getAnchorItem().getLocation());
        } else {
            if (getFlagHolder() == null) {
                dropFlag(true);
                return (getLocation());
            }

            return (getFlagHolder().getLocation());
        }
    }

    public void updateVisual() {
        if (getState() == CTFFlagState.CAP_POINT) {
            updatePoleVisual();
            updateFlagVisual();
        } else {
            removeVisual();
            getFlagHolder().getInventory().setHelmet(new ItemStack(FLAG_MATERIAL, 1, getColor().getDyeColor().getWoolData()));
        }
    }

    public void removeVisual() {
        for (Entity entity : flagItems) {
            entity.remove();
        }

        flagItems.clear();
    }

    public void updatePoleVisual() {
        Item[] pole = new Item[POLE_HEIGHT - 4];
        Location anchorLocation = getAnchorItem().getLocation();

        //Create the pole items
        for (int y = 0; y < POLE_HEIGHT - 4; y++) {
            Location itemLoc = anchorLocation.clone().add(new Vector(0, y * FLAG_PROPORTION, 0));
            ItemStack itemStack = new ItemStack(POLE_MATERIAL, 1);
            ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.setDisplayName(FoxtrotPlugin.RANDOM.nextInt(100000) + "");

            itemStack.setItemMeta(itemMeta);

            pole[y] = anchorLocation.getWorld().dropItem(itemLoc, itemStack);
            pole[y].setPickupDelay(Integer.MAX_VALUE);

            WorldServer nmsWorld = ((CraftWorld) itemLoc.getWorld()).getHandle();
            EntityWitherSkull witherSkull = new EntityWitherSkull(nmsWorld);

            witherSkull.setLocation(itemLoc.getX(), itemLoc.getY(), itemLoc.getZ(), 0, 0);

            nmsWorld.addEntity(witherSkull);

            witherSkull.getBukkitEntity().setPassenger(pole[y]);

            flagItems.add(pole[y]);
            flagItems.add(witherSkull.getBukkitEntity());
        }
    }

    public void updateFlagVisual() {
        Item[][] flag = new Item[FLAG_WIDTH][FLAG_HEIGHT];
        Location anchorLocation = getAnchorItem().getLocation();

        for (int x = 0; x < FLAG_WIDTH; x++) {
            for (int y = 0; y < FLAG_HEIGHT; y++) {
                Location itemLoc = anchorLocation.clone().add(new Vector(0, (POLE_HEIGHT * FLAG_PROPORTION) - y * FLAG_PROPORTION, x * FLAG_PROPORTION));
                ItemStack itemStack = new ItemStack(FLAG_MATERIAL, 1, getColor().getDyeColor().getWoolData());
                ItemMeta itemMeta = itemStack.getItemMeta();

                itemMeta.setDisplayName(FoxtrotPlugin.RANDOM.nextInt(100000) + "");

                itemStack.setItemMeta(itemMeta);

                flag[x][y] = anchorLocation.getWorld().dropItem(itemLoc, itemStack);
                flag[x][y].setPickupDelay(Integer.MAX_VALUE);

                WorldServer nmsWorld = ((CraftWorld) itemLoc.getWorld()).getHandle();
                EntityWitherSkull witherSkull = new EntityWitherSkull(nmsWorld);

                witherSkull.setLocation(itemLoc.getX(), itemLoc.getY(), itemLoc.getZ(), 0, 0);

                nmsWorld.addEntity(witherSkull);

                witherSkull.getBukkitEntity().setPassenger(flag[x][y]);

                flagItems.add(flag[x][y]);
                flagItems.add(witherSkull.getBukkitEntity());
            }
        }
    }

}