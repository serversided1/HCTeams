package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.frozenorb.Utilities.DataSystem.Regioning.CuboidRegion;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class TeamSubclaimCommand implements Listener {

	private static HashMap<String, Selection> selections = new HashMap<String, TeamSubclaimCommand.Selection>();
	public static final ItemStack SELECTION_WAND = new ItemStack(Material.WOOD_SPADE);

	static {
		ItemMeta meta = SELECTION_WAND.getItemMeta();

		meta.setDisplayName("§a§oSubclaim Wand");
		meta.setLore(Arrays.asList("This wand is used to create", "subclaims in your team's claims"));
		SELECTION_WAND.setItemMeta(meta);
	}

    @Command(names={ "team subclaim", "t subclaim", "f subclaim", "faction subclaim", "fac subclaim" }, permissionNode="")
    public static void teamInvite(Player sender, @Param(name="Parameter") String params) {
        String[] args = ("arg1 " + params).split(" ");
		Team t = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

		if (t == null) {
			sender.sendMessage(ChatColor.RED + "You must be on a team to execute this command!");
			return;
		}

		Player p = (Player) sender;

		if (args.length > 1) {
			String cmd = args[1];

			if (cmd.equalsIgnoreCase("grant") || cmd.equalsIgnoreCase("add")) {
				if (args.length == 4) {
					String player = args[2];
					String subclaim = args[3];

					net.frozenorb.foxtrot.team.claims.Subclaim toChange = t.getSubclaim(subclaim, true);

					if (toChange == null) {
						sender.sendMessage(ChatColor.RED + "Subclaim could not be found! Please use the full subclaim name!");
						return;
					}

					if (t.isMember(player)) {
						if (toChange.isMember(player)) {
							sender.sendMessage(ChatColor.RED + "The player already has access to that subclaim!");
							return;
						}

						sender.sendMessage(ChatColor.GREEN + t.getActualPlayerName(player) + "§e has been added to the subclaim §a" + toChange.getFriendlyName() + "§e.");
						toChange.addMember(player);
						t.setChanged(true);

					} else {
						sender.sendMessage(ChatColor.RED + "Player '" + player + "' is not on your team!");

					}

				} else {
					sender.sendMessage(ChatColor.RED + "/t subclaim grant <player> <subclaim_Name>");
				}
			}

			if (cmd.equalsIgnoreCase("revoke") || cmd.equalsIgnoreCase("remove") || cmd.equalsIgnoreCase("del")) {
				if (args.length == 4) {
					String player = args[2];
					String subclaim = args[3];

					net.frozenorb.foxtrot.team.claims.Subclaim toChange = t.getSubclaim(subclaim, true);

					if (toChange == null) {
						sender.sendMessage(ChatColor.RED + "Subclaim could not be found! Please use the full subclaim name!");
						return;
					}

					if (t.isMember(player)) {
						if (toChange.getManager().equalsIgnoreCase(player)) {
							sender.sendMessage(ChatColor.RED + "You cannot revoke the manager's access!");
							return;
						}

						if (!toChange.isMember(player)) {
							sender.sendMessage(ChatColor.RED + "The player already does not have access to that subclaim!");
							return;
						}

						sender.sendMessage(ChatColor.GREEN + t.getActualPlayerName(player) + "§e has been removed from the subclaim §a" + toChange.getFriendlyName() + "§e.");
						toChange.removeMember(player);
						t.setChanged(true);

					} else {
						sender.sendMessage(ChatColor.RED + "Player '" + player + "' is not on your team!");
					}

				} else {
					sender.sendMessage(ChatColor.RED + "/t subclaim revoke <player> <subclaim_Name>");
				}
			}

			if (cmd.equalsIgnoreCase("reassign") || cmd.equalsIgnoreCase("transfer")) {

				if (args.length == 4) {
					String subclaim = args[2];
					String newOwner = args[3];

					net.frozenorb.foxtrot.team.claims.Subclaim toChange = t.getSubclaim(subclaim, true);

					if (toChange == null) {
						sender.sendMessage(ChatColor.RED + "Subclaim could not be found! Please use the full subclaim name!");
						return;
					}
					String oldName = toChange.getFriendlyColoredName();

					if (t.isMember(newOwner)) {

						if (t.getSubclaim(t.getActualPlayerName(newOwner) + "/" + toChange.getName(), true) != null) {
							sender.sendMessage(ChatColor.RED + "A subclaim with the same name already belongs to " + t.getActualPlayerName(newOwner) + "!");
							return;
						}

						toChange.setManager(t.getActualPlayerName(newOwner));
						t.setChanged(true);
						sender.sendMessage(ChatColor.RED + oldName + "§e has been reassigned to player " + t.getActualPlayerName(newOwner) + "!");

					} else {
						sender.sendMessage(ChatColor.RED + "Player '" + newOwner + "' is not on your team!");
					}

				} else {
					sender.sendMessage(ChatColor.RED + "/t rename <Subclaim> <NewName>");
				}

			}

			if (cmd.equalsIgnoreCase("rename")) {
				if (args.length == 4) {
					String oldName = args[2];
					String newName = args[3];

					if (t.getSubclaim(newName, true) != null) {
						sender.sendMessage(ChatColor.RED + "Your team already has a subclaim with that name!");
						return;
					}

					net.frozenorb.foxtrot.team.claims.Subclaim old = t.getSubclaim(oldName, true);

					if (old != null) {
						if (!newName.startsWith(old.getManager() + "/")) {
							p.sendMessage(ChatColor.RED + "This subclaim's new name must start with '" + old.getManager() + "/'.");
							return;
						}

						if (!StringUtils.isAlphanumeric(newName.substring(newName.indexOf('/') + 1))) {
							sender.sendMessage(ChatColor.RED + "You can only have letters and numbers in your subclaim name!");
							return;
						}

						old.setName(newName.substring(newName.indexOf('/') + 1));
						t.setChanged(true);
						sender.sendMessage(ChatColor.GREEN + "Subclaim has been renamed to§e " + old.getFriendlyName() + "§a!");

					} else {
						sender.sendMessage(ChatColor.RED + "Subclaim could not be found! Please use the full subclaim name!");
						return;
					}

				} else {
					sender.sendMessage(ChatColor.RED + "/t rename <Subclaim> <NewName>");
				}

			}

			if (cmd.equalsIgnoreCase("unclaim")) {
				if (args.length == 3) {
					String subclaimName = args[2];

					net.frozenorb.foxtrot.team.claims.Subclaim sc = t.getSubclaim(subclaimName, true);
					if (sc == null) {
						sender.sendMessage(ChatColor.RED + "Subclaim could not be found! Please use the full subclaim name!");
						return;
					}

					if (sc.getManager().equalsIgnoreCase(p.getName()) || t.isOwner(p.getName()) || t.isCaptain(p.getName())) {
						t.getSubclaims().remove(sc);
						t.setChanged(true);
						p.sendMessage(ChatColor.RED + "You have unclaimed the subclaim §e" + sc.getFriendlyName() + "§c.");
					} else {
						sender.sendMessage(ChatColor.RED + "You can only unclaim your own subclaims!");
					}

				} else {
					sender.sendMessage(ChatColor.RED + "/t subclaim unclaim <subclaim<");
				}
			}

			if (cmd.equalsIgnoreCase("claim")) {
				if (args.length == 4) {
					String managerName = args[2];
					String subclaimName = args[3];

					if (!StringUtils.isAlphanumeric(subclaimName)) {
						sender.sendMessage(ChatColor.RED + "You can only have letters and numbers in your subclaim name!");
						return;
					}

					if (t.isMember(managerName)) {
						if (t.getSubclaim(managerName + "/" + subclaimName, true) != null) {
							sender.sendMessage(ChatColor.RED + "Your team already has a subclaim with that name!");
							return;
						}

						if (selections.containsKey(p.getName()) && selections.get(p.getName()).isComplete()) {

							Selection sel = selections.get(p.getName());

							for (Location loc : new CuboidRegion("test123", sel.getLoc1(), sel.getLoc2())) {
								if (FoxtrotPlugin.getInstance().getTeamHandler().getOwner(loc) != t) {
									p.sendMessage(ChatColor.RED + "This subclaim would conflict with the claims of team §e" + FoxtrotPlugin.getInstance().getTeamHandler().getOwner(loc).getFriendlyName() + "§c!");
									return;
								}

								net.frozenorb.foxtrot.team.claims.Subclaim wbe = t.getSubclaim(loc);

								if (wbe != null) {
									p.sendMessage(ChatColor.RED + "This subclaim would conflict with §e" + wbe.getFriendlyName() + "§c!");
									return;
								}
							}

							net.frozenorb.foxtrot.team.claims.Subclaim sc = new net.frozenorb.foxtrot.team.claims.Subclaim(sel.getLoc1(), sel.getLoc2(), t.getActualPlayerName(managerName), subclaimName);

							t.getSubclaims().add(sc);
							t.setChanged(true);

							p.sendMessage(ChatColor.GREEN + "You have created the subclaim §e" + sc.getFriendlyName() + "§a!");
							p.getInventory().remove(SELECTION_WAND);

						} else {
							p.sendMessage(ChatColor.RED + "You do not have a region fully selected!");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Player '" + managerName + "' is not on your team!");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "/t claim <ManagerName> <SubclaimName>");
				}
			}

			if (cmd.equalsIgnoreCase("wand")) {
				int slot = -1;

				for (int i = 0; i < 9; i++) {
					if (p.getInventory().getItem(i) == null) {
						slot = i;
                        break;
					}
				}

				if (slot == -1) {
					sender.sendMessage(ChatColor.RED + "You don't have space in your hotbar for the Subclaim Wand!");
					return;
				}

				p.getInventory().setItem(slot, SELECTION_WAND.clone());
			}

			if (cmd.equalsIgnoreCase("list")) {
				ArrayList<net.frozenorb.foxtrot.team.claims.Subclaim> your = new ArrayList<net.frozenorb.foxtrot.team.claims.Subclaim>();
				ArrayList<net.frozenorb.foxtrot.team.claims.Subclaim> access = new ArrayList<net.frozenorb.foxtrot.team.claims.Subclaim>();
				ArrayList<net.frozenorb.foxtrot.team.claims.Subclaim> other = new ArrayList<net.frozenorb.foxtrot.team.claims.Subclaim>();

				for (net.frozenorb.foxtrot.team.claims.Subclaim scs : t.getSubclaims()) {
					if (scs.getManager().equals(sender.getName())) {
						your.add(scs);
						continue;
					}
					if (scs.isMember(sender.getName())) {
						your.add(scs);
						continue;
					}

					your.add(scs);

				}

				sender.sendMessage("§9" + t.getFriendlyName() + "§e Subclaim List");
				sender.sendMessage("§eYour Subclaims:§f " + your.toString().replace("[", "").replace("]", ""));
				sender.sendMessage("§eAccessible Subclaims:§f " + access.toString().replace("[", "").replace("]", ""));
				sender.sendMessage("§eOther Subclaims:§f " + other.toString().replace("[", "").replace("]", ""));
			}

			if (cmd.equalsIgnoreCase("i") || cmd.equalsIgnoreCase("info")) {
				if (args.length > 2) {
					String name = args[2];
					net.frozenorb.foxtrot.team.claims.Subclaim sc = t.getSubclaim(name, true);

					if (sc == null) {
						sender.sendMessage(ChatColor.RED + "Subclaim could not be found! Please use the full subclaim name!");
						return;
					}

					sender.sendMessage(ChatColor.BLUE + sc.getFriendlyName() + "§e Subclaim Info");
					sender.sendMessage("§eLocation:§7 Pos1. §f" + sc.getLoc1().getBlockX() + "," + sc.getLoc1().getBlockY() + "," + sc.getLoc1().getBlockZ() + " §7Pos2. §f" + sc.getLoc2().getBlockX() + "," + sc.getLoc2().getBlockY() + "," + sc.getLoc2().getBlockZ());
					sender.sendMessage(ChatColor.YELLOW + "Shared with:§f " + sc.getMembers().toString().replace("[", "").replace("]", ""));

				} else {
					sender.sendMessage(ChatColor.RED + "/t subclaim info <subclaim>");
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(event.getPlayer().getName());

		if (event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) && event.getItem().getType() == SELECTION_WAND.getType()) {
			if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().contains("Subclaim")) {
				Selection sel = new Selection(null, null);

				String coordinate = String.format("(%s, %s, %s)", event.getClickedBlock().getX(), event.getClickedBlock().getY(), event.getClickedBlock().getZ());

				if (team != null) {
					net.frozenorb.foxtrot.team.claims.Subclaim sc = team.getSubclaim(event.getClickedBlock().getLocation());
					if (sc != null) {
						event.getPlayer().sendMessage("§c" + coordinate + " is a part of " + sc.getFriendlyName() + "!");
						return;
					}

					if (FoxtrotPlugin.getInstance().getTeamHandler().getOwner(event.getClickedBlock().getLocation()) != team) {
						event.getPlayer().sendMessage("§cThis block is not a part of your teams' territory!");
						return;
					}

				}

				if (selections.containsKey(event.getPlayer().getName())) {
					sel = selections.get(event.getPlayer().getName());
				}

				int set = 0;

				if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					set = 2;
					sel.setLoc1(event.getClickedBlock().getLocation());

				} else {
					set = 1;
					sel.setLoc2(event.getClickedBlock().getLocation());
				}

				event.getPlayer().sendMessage("§eSubclaim postion " + set + " has been set to " + coordinate + "!");

				selections.put(event.getPlayer().getName(), sel);
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
