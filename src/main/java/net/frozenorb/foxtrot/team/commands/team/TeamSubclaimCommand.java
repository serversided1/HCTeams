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
import net.frozenorb.foxtrot.team.claims.VisualClaim;
import net.frozenorb.foxtrot.team.claims.VisualClaimType;
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

    @Command(names={ "team subclaim", "t subclaim", "f subclaim", "faction subclaim", "fac subclaim", "team sub", "t sub", "f sub", "faction sub", "fac sub" }, permissionNode="")
    public static void teamSubclaim(Player sender) {
        sender.sendMessage(ChatColor.RED + "/f subclaim start - starts the subclaiming process");
        sender.sendMessage(ChatColor.RED + "/f subclaim map - toggles a visual subclaim map");
        sender.sendMessage(ChatColor.RED + "/f subclaim create <subclaim> - creates a subclaim");
        sender.sendMessage(ChatColor.RED + "/f subclaim addplayer <player> <subclaim> - adds a player to a subclaim");
        sender.sendMessage(ChatColor.RED + "/f subclaim removeplayer <player> <subclaim> - removes a player from a subclaim");
        sender.sendMessage(ChatColor.RED + "/f subclaim list - views all subclaims");
        sender.sendMessage(ChatColor.RED + "/f subclaim info <subclaim> - views info about a subclaim");
        sender.sendMessage(ChatColor.RED + "/f subclaim unclaim <subclaim> <player> - unclaims a subclaim");
    }

    @Command(names={ "team subclaim addplayer", "t subclaim addplayer", "f subclaim addplayer", "faction subclaim addplayer", "fac subclaim addplayer", "team sub addplayer", "t sub addplayer", "f sub addplayer", "faction sub addplayer", "fac sub addplayer", "team subclaim grant", "t subclaim grant", "f subclaim grant", "faction subclaim grant", "fac subclaim grant", "team sub grant", "t sub grant", "f sub grant", "faction sub grant", "fac sub grant" }, permissionNode="")
    public static void teamSubclaimAddPlayer(Player sender, @Param(name="subclaim") Subclaim subclaim, @Param(name="player") OfflinePlayer player) {
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

    @Command(names={ "team subclaim removeplayer", "t subclaim removeplayer", "f subclaim removeplayer", "faction subclaim removeplayer", "fac subclaim removeplayer", "team sub removeplayer", "t sub removeplayer", "f sub removeplayer", "faction sub removeplayer", "fac sub removeplayer", "team subclaim revoke", "t subclaim revoke", "f subclaim revoke", "faction subclaim revoke", "fac subclaim revoke", "team sub revoke", "t sub revoke", "f sub revoke", "faction sub revoke", "fac sub revoke" }, permissionNode="")
    public static void teamSubclaimRemovePlayer(Player sender, @Param(name="subclaim") Subclaim subclaim, @Param(name="player") OfflinePlayer player) {
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

    @Command(names={ "team subclaim start", "t subclaim start", "f subclaim wand", "faction subclaim start", "fac subclaim start", "team sub start", "t sub start", "f sub wand", "faction sub start", "fac sub start" }, permissionNode="")
    public static void teamSubclaimStart(Player sender) {
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

        if (!VisualClaim.getCurrentSubclaimMaps().containsKey(sender.getName())) {
            new VisualClaim(sender, VisualClaimType.SUBCLAIM_MAP, true).draw(true);
        }

        sender.getInventory().setItem(slot, SELECTION_WAND.clone());
    }

    @Command(names={ "team subclaim list", "t subclaim list", "f subclaim list", "faction subclaim list", "fac subclaim list", "team sub list", "t sub list", "f sub list", "faction sub list", "fac sub list" }, permissionNode="")
    public static void teamSubclaimList(Player sender) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

        if (team == null) {
            sender.sendMessage(ChatColor.RED + "You must be on a team to execute this command!");
            return;
        }

        StringBuilder access = new StringBuilder();
        StringBuilder other = new StringBuilder();

        for (Subclaim scs : team.getSubclaims()) {
            if (scs.isMember(sender.getName()) || team.getOwner().equalsIgnoreCase(sender.getName()) || team.isCaptain(sender.getName())) {
                access.append(scs).append(", ");
                continue;
            }

            other.append(scs).append(", ");
        }

        if (access.length() > 2) {
            access.setLength(access.length() - 2);
        }

        if (other.length() > 2) {
            other.setLength(other.length() - 2);
        }

        sender.sendMessage(ChatColor.BLUE + team.getName() + ChatColor.YELLOW + " Subclaim List");
        sender.sendMessage(ChatColor.YELLOW + "Subclaims you can access: " + ChatColor.WHITE + access.toString());
        sender.sendMessage(ChatColor.YELLOW + "Other Subclaims: " + ChatColor.WHITE + other.toString());
    }

    @Command(names={ "team subclaim info", "t subclaim info", "f subclaim info", "faction subclaim info", "fac subclaim info", "team sub info", "t sub info", "f sub info", "faction sub info", "fac sub info" }, permissionNode="")
    public static void teamSubclaimInfo(Player sender, @Param(name="subclaim", defaultValue="location") Subclaim subclaim) {
        sender.sendMessage(ChatColor.BLUE + subclaim.getName() + ChatColor.YELLOW + " Subclaim Info");
        sender.sendMessage(ChatColor.YELLOW + "Location: " + ChatColor.GRAY + "Pos1. " + ChatColor.WHITE + subclaim.getLoc1().getBlockX() + "," + subclaim.getLoc1().getBlockY() + "," + subclaim.getLoc1().getBlockZ() + ChatColor.GRAY + " Pos2. " + ChatColor.WHITE + subclaim.getLoc2().getBlockX() + "," + subclaim.getLoc2().getBlockY() + "," + subclaim.getLoc2().getBlockZ());
        sender.sendMessage(ChatColor.YELLOW + "Members: " + ChatColor.WHITE + subclaim.getMembers().toString().replace("[", "").replace("]", ""));
    }

    @Command(names={ "team subclaim unclaim", "t subclaim unclaim", "f subclaim unclaim", "faction subclaim unclaim", "fac subclaim unclaim", "team subclaim unsubclaim", "t subclaim unsubclaim", "f subclaim unsubclaim", "faction subclaim unsubclaim", "fac subclaim unsubclaim", "team unsubclaim", "t unsubclaim", "f unsubclaim", "faction unsubclaim", "fac unsubclaim"}, permissionNode="")
    public static void teamSubclaimUnclaim(Player sender, @Param(name="subclaim", defaultValue="location") Subclaim subclaim) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

        if (team.isOwner(sender.getName()) || team.isCaptain(sender.getName())) {
            team.getSubclaims().remove(subclaim);
            LandBoard.getInstance().updateSubclaim(subclaim);
            team.flagForSave();
            sender.sendMessage(ChatColor.RED + "You have unclaimed the subclaim " + ChatColor.YELLOW + subclaim.getName() + ChatColor.RED + ".");
        } else {
            sender.sendMessage(ChatColor.RED + "Only team captains can unclaim subclaims!");
        }
    }

    @Command(names={ "team subclaim create", "t subclaim create", "f subclaim create", "faction subclaim create", "fac subclaim create", "team sub create", "t sub create", "f sub create", "faction sub create", "fac sub create" }, permissionNode="")
    public static void teamSubclaimCreate(Player sender, @Param(name="subclaim") String subclaim) {
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

        for (Location loc : new CuboidRegion("Subclaim", selection.getLoc1(), selection.getLoc2())) {
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
        LandBoard.getInstance().updateSubclaim(sc);
        team.flagForSave();

        sender.sendMessage(ChatColor.GREEN + "You have created the subclaim " + ChatColor.YELLOW + sc.getName() + ChatColor.GREEN + "!");
        sender.getInventory().remove(SELECTION_WAND);
    }

    @Command(names={ "team subclaim map", "t subclaim map", "f subclaim map", "faction subclaim map", "fac subclaim map", "team sub map", "t sub map", "f sub map", "faction sub map", "fac sub map" }, permissionNode="")
    public static void teamSubclaimMap(Player sender) {
        (new VisualClaim(sender, VisualClaimType.SUBCLAIM_MAP, false)).draw(false);
    }

    @Command(names={ "team subclaim opmap", "t subclaim opmap", "f subclaim opmap", "faction subclaim opmap", "fac subclaim opmap", "team sub opmap", "t sub opmap", "f sub opmap", "faction sub opmap", "fac sub opmap" }, permissionNode="op")
    public static void teamSubclaimOpMap(Player sender) {
        (new VisualClaim(sender, VisualClaimType.SUBCLAIM_MAP, true)).draw(false);
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
