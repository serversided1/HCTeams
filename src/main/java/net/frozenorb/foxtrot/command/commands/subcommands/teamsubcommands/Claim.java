package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.VisualClaim;
import net.frozenorb.foxtrot.team.claims.VisualClaim.VisualType;
import net.frozenorb.foxtrot.util.ListUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Claim implements Listener {
	public static final ItemStack SELECTION_WAND = new ItemStack(Material.WOOD_HOE);

	static {
		ItemMeta meta = SELECTION_WAND.getItemMeta();

		meta.setDisplayName("§a§oClaiming Wand");
		meta.setLore(ListUtils.wrap(" | §eRight/Left Click§6 Block   §b- §fSelect claim's corners" + " | §eRight Click §6Air  |  §b- §fCancel current claim" + " | §9Shift §eLeft Click §6Block/Air   §b- §fPurchase current claim", ""));
		SELECTION_WAND.setItemMeta(meta);
	}

    @Command(names={ "team claim", "t claim", "f claim", "faction claim", "fac claim" }, permissionNode="")
    public static void teamClaim(Player sender) {
		final Player p = (Player) sender;

		Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(p.getName());
		if (team == null) {
			sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
			return;
		}
		if (team.isOwner(p.getName()) || team.isCaptain(p.getName())) {
			p.getInventory().remove(SELECTION_WAND);

            if(team.isRaidable()){
                p.sendMessage(ChatColor.RED + "You may not claim land while your faction is raidable!");
                return;
            }

			Bukkit.getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), () -> p.getInventory().addItem(SELECTION_WAND.clone()), 1L);

			new VisualClaim(p, VisualType.CREATE, false).draw(false);

			if (!VisualClaim.getCurrentMaps().containsKey(p.getName())) {
				new VisualClaim(p, VisualType.MAP, false).draw(false);
			}
		} else
			p.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
	}

    @Command(names={ "team opclaim", "t opclaim", "f opclaim", "faction opclaim", "fac opclaim" }, permissionNode="op")
    public static void teamOpClaim(Player sender) {
        final Player p = (Player) sender;

        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(p.getName());
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isOwner(p.getName()) || team.isCaptain(p.getName())) {
            p.getInventory().remove(SELECTION_WAND);


            Bukkit.getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), () -> p.getInventory().addItem(SELECTION_WAND.clone()), 1L);

            new VisualClaim(p, VisualType.CREATE, true).draw(false);

            if (!VisualClaim.getCurrentMaps().containsKey(p.getName())) {
                new VisualClaim(p, VisualType.MAP, true).draw(false);
            }
        } else
            p.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
    }

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		if (e.getItemDrop().getItemStack().equals(SELECTION_WAND)) {

			VisualClaim vc = VisualClaim.getVisualClaim(e.getPlayer().getName());

			if (vc != null) {
				e.setCancelled(true);
				vc.cancel(false);

			}
			Bukkit.getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), () -> e.getItemDrop().remove(), 1L);
		}
	}

}