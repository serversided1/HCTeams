package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.VisualClaim;
import net.frozenorb.foxtrot.team.claims.VisualClaim.VisualType;
import net.frozenorb.foxtrot.util.ListUtils;

public class Claim extends Subcommand implements Listener {
	public static final ItemStack SELECTION_WAND = new ItemStack(Material.WOOD_HOE);

	static {
		ItemMeta meta = SELECTION_WAND.getItemMeta();

		meta.setDisplayName("§a§oClaiming Wand");
		meta.setLore(ListUtils.wrap(" | §eRight/Left Click§6 Block   §b- §fSelect claim's corners" + " | §eRight Click §6Air  |  §b- §fCancel current claim" + " | §9Shift §eLeft Click §6Block/Air   §b- §fPurchase current claim", ""));
		SELECTION_WAND.setItemMeta(meta);
	}

	public Claim(String name, String errorMessage, String... aliases) {
		super(name, errorMessage, aliases);
	}

	@Override
	public void syncExecute() {
		final Player p = (Player) sender;

		Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName());
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

			new VisualClaim(p, VisualType.CREATE).draw(false);

			if (!VisualClaim.getCurrentMaps().containsKey(p.getName())) {
				new VisualClaim(p, VisualType.MAP).draw(false);
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

	@Override
	public List<String> tabComplete() {
		return new ArrayList<String>();
	}

}
