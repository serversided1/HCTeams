package net.frozenorb.foxtrot.team.commands.team;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.frozenorb.Utilities.DataSystem.Regioning.CuboidRegion;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.foxtrot.util.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamSubclaimCommand implements Listener {

    private static Map<String, Selection> selections = new HashMap<String, TeamSubclaimCommand.Selection>();
    public static final ItemStack SELECTION_WAND = new ItemStack(Material.WOOD_SPADE);

    static {
        ItemMeta meta = SELECTION_WAND.getItemMeta();

        meta.setDisplayName("§a§oSubclaim Wand");
        meta.setLore(ListUtils.wrap(" | §eRight/Left Click§6 Block   §b- §fSelect subclaim's corners", ""));
        SELECTION_WAND.setItemMeta(meta);
    }

    @Command(names={ "team subclaim", "t subclaim", "f subclaim", "faction subclaim", "fac subclaim" }, permissionNode="")
    public static void teamSubclaim(Player sender) {
        sender.sendMessage(ChatColor.RED + "/f subclaim wand - obtains a subclaiming wand");
        sender.sendMessage(ChatColor.RED + "/f subclaim claim <subclaim> - creates a subclaim");
        sender.sendMessage(ChatColor.RED + "/f subclaim add <subclaim> <player> - adds a player to a subclaim");
        sender.sendMessage(ChatColor.RED + "/f subclaim remove <subclaim> <player> - removes a player from a subclaim");
        sender.sendMessage(ChatColor.RED + "/f subclaim list - views all subclaims");
        sender.sendMessage(ChatColor.RED + "/f subclaim info <subclaim> - views info about a subclaim");
        sender.sendMessage(ChatColor.RED + "/f subclaim unclaim <subclaim> <player> - unclaims a subclaim");
    }

    @Command(names={ "team subclaim grant", "t subclaim grant", "f subclaim grant", "faction subclaim grant", "fac subclaim grant", "team subclaim add", "t subclaim add", "f subclaim add", "faction subclaim add", "fac subclaim add" }, permissionNode="")
    public static void teamSubclaimGrant(Player sender, @Param(name="subclaim") Subclaim subclaim, @Param(name="player") OfflinePlayer player) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

        if (!team.getOwner().equals(sender.getName()) && !team.isCaptain(sender.getName())) {
            sender.sendMessage(ChatColor.RED + "Only the team captains can do this.");
            return;
        }

        if (!team.isMember(player.getName())) {
            sender.sendMessage(ChatColor.RED + player.getName() + " is not on your team!");
            return;
        }

        if (subclaim.isMember(player.getName())) {
            sender.sendMessage(ChatColor.RED + "The player already has access to that subclaim!");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + team.getActualPlayerName(player.getName()) + ChatColor.YELLOW + " has been added to the subclaim " + ChatColor.GREEN + subclaim.getName() + ChatColor.YELLOW + ".");
        subclaim.addMember(player.getName());
        team.flagForSave();
    }

    @Command(names={ "team subclaim revoke", "t subclaim revoke", "f subclaim revoke", "faction subclaim revoke", "fac subclaim revoke", "team subclaim remove", "t subclaim remove", "f subclaim remove", "faction subclaim remove", "fac subclaim remove" }, permissionNode="")
    public static void teamSubclaimRevoke(Player sender, @Param(name="subclaim") Subclaim subclaim, @Param(name="player") OfflinePlayer player) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

        if (!team.getOwner().equals(sender.getName()) && !team.isCaptain(sender.getName())) {
            sender.sendMessage(ChatColor.RED + "Only the team captains can do this.");
            return;
        }

        if (!team.isMember(player.getName())) {
            sender.sendMessage(ChatColor.RED + player.getName() + " is not on your team!");
            return;
        }

        if (!subclaim.isMember(player.getName())) {
            sender.sendMessage(ChatColor.RED + "The player already does not have access to that subclaim!");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + team.getActualPlayerName(player.getName()) + ChatColor.YELLOW + " has been removed from the subclaim " + ChatColor.GREEN + subclaim.getName() + ChatColor.YELLOW + ".");
        subclaim.removeMember(player.getName());
        team.flagForSave();
    }

    @Command(names={ "team subclaim wand", "t subclaim wand", "f subclaim wand", "faction subclaim wand", "fac subclaim wand", "team subclaim tool", "t subclaim tool", "f subclaim tool", "faction subclaim tool", "fac subclaim tool" }, permissionNode="")
    public static void teamSubclaimWand(Player sender) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "The subclaiming system is temporarily disabled.");
            return;
        }

        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

        if (team == null) {
            sender.sendMessage(ChatColor.RED + "You must be on a team to execute this command!");
            return;
        }

        int slot = -1;

        for (int i = 0; i < 9; i++) {
            if (sender.getInventory().getItem(i) == null) {
                slot = i;
                break;
            }
        }

        if (slot == -1) {
            sender.sendMessage(ChatColor.RED + "You don't have space in your hotbar for the Subclaim Wand!");
            return;
        }

        sender.getInventory().setItem(slot, SELECTION_WAND.clone());
    }

    @Command(names={ "team subclaim list", "t subclaim list", "f subclaim list", "faction subclaim list", "fac subclaim list" }, permissionNode="")
    public static void teamSubclaimList(Player sender) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

        if (team == null) {
            sender.sendMessage(ChatColor.RED + "You must be on a team to execute this command!");
            return;
        }

        List<Subclaim> access = new ArrayList<Subclaim>();
        List<Subclaim> other = new ArrayList<Subclaim>();

        for (Subclaim scs : team.getSubclaims()) {
            if (scs.isMember(sender.getName()) || team.getOwner().equalsIgnoreCase(sender.getName()) || team.isCaptain(sender.getName())) {
                access.add(scs);
                continue;
            }

            other.add(scs);
        }

        sender.sendMessage(ChatColor.BLUE + team.getName() + ChatColor.YELLOW + " Subclaim List");
        sender.sendMessage(ChatColor.YELLOW + "Subclaims you can access: " + ChatColor.WHITE + access.toString().replace("[", "").replace("]", ""));
        sender.sendMessage(ChatColor.YELLOW + "Other Subclaims: " + ChatColor.WHITE + other.toString().replace("[", "").replace("]", ""));
    }

    @Command(names={ "team subclaim info", "t subclaim info", "f subclaim info", "faction subclaim info", "fac subclaim info" }, permissionNode="")
    public static void teamSubclaimInfo(Player sender, @Param(name="subclaim") Subclaim subclaim) {
        sender.sendMessage(ChatColor.BLUE + subclaim.getName() + ChatColor.YELLOW + " Subclaim Info");
        sender.sendMessage(ChatColor.YELLOW + "Location: " + ChatColor.GRAY + "Pos1. " + ChatColor.WHITE + subclaim.getLoc1().getBlockX() + "," + subclaim.getLoc1().getBlockY() + "," + subclaim.getLoc1().getBlockZ() + ChatColor.GRAY + " Pos2. " + ChatColor.WHITE + subclaim.getLoc2().getBlockX() + "," + subclaim.getLoc2().getBlockY() + "," + subclaim.getLoc2().getBlockZ());
        sender.sendMessage(ChatColor.YELLOW + "Members: " + ChatColor.WHITE + subclaim.getMembers().toString().replace("[", "").replace("]", ""));
    }

    @Command(names={ "team subclaim unclaim", "t subclaim unclaim", "f subclaim unclaim", "faction subclaim unclaim", "fac subclaim unclaim" }, permissionNode="")
    public static void teamSubclaimUnclaim(Player sender, @Param(name="subclaim") Subclaim subclaim) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

        if (team.isOwner(sender.getName()) || team.isCaptain(sender.getName())) {
            team.getSubclaims().remove(subclaim);
            team.flagForSave();
            sender.sendMessage(ChatColor.RED + "You have unclaimed the subclaim " + ChatColor.YELLOW + subclaim.getName() + ChatColor.RED + ".");
        } else {
            sender.sendMessage(ChatColor.RED + "Only team captains can unclaim subclaims!");
        }
    }

    @Command(names={ "team subclaim claim", "t subclaim claim", "f subclaim claim", "faction subclaim claim", "fac subclaim claim" }, permissionNode="")
    public static void teamSubclaimClaim(Player sender, @Param(name="subclaim") String subclaim) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

        if (team == null) {
            sender.sendMessage(ChatColor.RED + "You must be on a team to execute this command!");
            return;
        }

        if (!StringUtils.isAlphanumeric(subclaim)) {
            sender.sendMessage(ChatColor.RED + "Subclaim names must be alphanumeric!");
            return;
        }

        if (!team.isOwner(sender.getName()) && !team.isCaptain(sender.getName())) {
            sender.sendMessage(ChatColor.RED + "Only team captains can create subclaims!");
            return;
        }

        if (team.getSubclaim(subclaim) != null) {
            sender.sendMessage(ChatColor.RED + "Your team already has a subclaim with that name!");
            return;
        }

        if (!selections.containsKey(sender.getName()) || !selections.get(sender.getName()).isComplete()) {
            sender.sendMessage(ChatColor.RED + "You do not have a region fully selected!");
            return;
        }

        Selection selection = selections.get(sender.getName());
        int x = Math.abs(selection.getLoc1().getBlockX() - selection.getLoc2().getBlockX());
        int z = Math.abs(selection.getLoc1().getBlockZ() - selection.getLoc2().getBlockZ());

        if (x < 3 || z < 3) {
            sender.sendMessage(ChatColor.RED + "Subclaims must be at least 3x3.");
            return;
        }

        for (Location loc : new CuboidRegion("test123", selection.getLoc1(), selection.getLoc2())) {
            if (LandBoard.getInstance().getTeam(loc) != team) {
                sender.sendMessage(ChatColor.RED + "This subclaim would conflict with the claims of team §e" + LandBoard.getInstance().getTeam(loc).getName() + "§c!");
                return;
            }

            Subclaim subclaimAtLoc = team.getSubclaim(loc);

            if (subclaimAtLoc != null) {
                sender.sendMessage(ChatColor.RED + "This subclaim would conflict with " + ChatColor.YELLOW + subclaimAtLoc.getName() + ChatColor.RED + "!");
                return;
            }
        }

        Subclaim sc = new Subclaim(selection.getLoc1(), selection.getLoc2(), subclaim);

        team.getSubclaims().add(sc);
        team.flagForSave();

        sender.sendMessage(ChatColor.GREEN + "You have created the subclaim " + ChatColor.YELLOW + sc.getName() + ChatColor.GREEN + "!");
        sender.getInventory().remove(SELECTION_WAND);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(event.getPlayer().getName());

        if (event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) && event.getItem().getType() == SELECTION_WAND.getType()) {
            if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().contains("Subclaim")) {
                event.setCancelled(true);

                if (team != null) {
                    Subclaim subclaim = team.getSubclaim(event.getClickedBlock().getLocation());

                    if (subclaim != null) {
                        event.getPlayer().sendMessage(ChatColor.RED + "(" + event.getClickedBlock().getX() + ", " + event.getClickedBlock().getY() + ", " + event.getClickedBlock().getZ() + ") is a part of " + subclaim.getName() + "!");
                        return;
                    }

                    if (LandBoard.getInstance().getTeam(event.getClickedBlock().getLocation()) != team) {
                        event.getPlayer().sendMessage(ChatColor.RED + "This block is not a part of your teams' territory!");
                        return;
                    }
                }

                Selection selection = new Selection(null, null);

                if (selections.containsKey(event.getPlayer().getName())) {
                    selection = selections.get(event.getPlayer().getName());
                }

                int set = 0;

                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    set = 2;
                    selection.setLoc1(event.getClickedBlock().getLocation());
                } else {
                    set = 1;
                    selection.setLoc2(event.getClickedBlock().getLocation());
                }

                event.getPlayer().sendMessage(ChatColor.YELLOW + "Set subclaim's location " + ChatColor.LIGHT_PURPLE + set + ChatColor.YELLOW + " to " + ChatColor.GREEN + "(" + ChatColor.WHITE + event.getClickedBlock().getX() + ", " + event.getClickedBlock().getY() + ", " + event.getClickedBlock().getZ() + ChatColor.GREEN + ")" + ChatColor.YELLOW + ".");
                selections.put(event.getPlayer().getName(), selection);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().equals(SELECTION_WAND)) {
            event.getItemDrop().remove();
        }
    }

    @Data
    @AllArgsConstructor
    private static class Selection {

        private Location loc1;
        private Location loc2;

        public boolean isComplete() {
            return (loc1 != null && loc2 != null);
        }

    }

}
