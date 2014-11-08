package net.frozenorb.foxtrot.nametag.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.InvUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by macguy8 on 11/5/2014.
 */
public class CrowbarListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getItem() == null || !InvUtils.isSimilar(event.getItem(), InvUtils.CROWBAR_NAME) || !(event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if (!FoxtrotPlugin.getInstance().getServerHandler().isClaimedAndRaidable(event.getClickedBlock().getLocation()) && !FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            Team team = FoxtrotPlugin.getInstance().getTeamHandler().getOwner(event.getClickedBlock().getLocation());

            if (team != null && !team.isMember(event.getPlayer())) {
                event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot crowbar in " + ChatColor.RED + team.getFriendlyName() + ChatColor.YELLOW + "'s territory!");
                return;
            }
        }

        if (FoxtrotPlugin.getInstance().getServerHandler().isGlobalSpawn(event.getClickedBlock().getLocation())) {
            event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot crowbar spawn!");
            return;
        }

        if (event.getClickedBlock().getType() == Material.ENDER_PORTAL_FRAME) {
            int portals = InvUtils.getCrowbarUsesPortal(event.getItem());

            if (portals == 0) {
                event.getPlayer().sendMessage(ChatColor.RED + "This crowbar has no more uses on end portals!");
                return;
            }

            event.getClickedBlock().getWorld().playEffect(event.getClickedBlock().getLocation(), Effect.STEP_SOUND, event.getClickedBlock().getTypeId());
            event.getClickedBlock().setType(Material.AIR);
            event.getClickedBlock().getState().update();

            event.getClickedBlock().getWorld().dropItemNaturally(event.getClickedBlock().getLocation(), new ItemStack(Material.ENDER_PORTAL_FRAME));
            event.getClickedBlock().getWorld().playSound(event.getClickedBlock().getLocation(), Sound.ANVIL_USE, 1.0F, 1.0F);

            portals -= 1;

            if (portals == 0) {
                event.getPlayer().setItemInHand(null);
                event.getClickedBlock().getLocation().getWorld().playSound(event.getClickedBlock().getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
                return;
            }

            ItemMeta meta = event.getItem().getItemMeta();

            meta.setLore(InvUtils.getCrowbarLore(portals, 0));

            event.getItem().setItemMeta(meta);

            double max = Material.DIAMOND_HOE.getMaxDurability();
            double dura = (max / (double) InvUtils.CROWBAR_PORTALS) * portals;

            event.getItem().setDurability((short) (max - dura));
            event.getPlayer().setItemInHand(event.getItem());
        } else if (event.getClickedBlock().getType() == Material.MOB_SPAWNER) {
            CreatureSpawner spawner = (CreatureSpawner) event.getClickedBlock().getState();
            int spawners = InvUtils.getCrowbarUsesSpawner(event.getItem());

            if (spawners == 0) {
                event.getPlayer().sendMessage(ChatColor.RED + "This crowbar has no more uses on mob spawners!");
                return;
            }

            if (event.getClickedBlock().getWorld().getEnvironment() == World.Environment.NETHER) {
                event.getPlayer().sendMessage(ChatColor.RED + "You cannot break spawners in the nether!");
                event.setCancelled(true);
                return;
            }

            if (event.getClickedBlock().getWorld().getEnvironment() == World.Environment.THE_END) {
                event.getPlayer().sendMessage(ChatColor.RED + "You cannot break spawners in the end!");
                event.setCancelled(true);
                return;
            }

            event.getClickedBlock().getLocation().getWorld().playEffect(event.getClickedBlock().getLocation(), Effect.STEP_SOUND, event.getClickedBlock().getTypeId());
            event.getClickedBlock().setType(Material.AIR);
            event.getClickedBlock().getState().update();

            ItemStack drop = new ItemStack(Material.MOB_SPAWNER);
            ItemMeta meta = drop.getItemMeta();

            meta.setDisplayName(ChatColor.RESET + StringUtils.capitaliseAllWords(spawner.getSpawnedType().toString().toLowerCase().replaceAll("_", " ")) + " Spawner");
            drop.setItemMeta(meta);

            event.getClickedBlock().getLocation().getWorld().dropItemNaturally(event.getClickedBlock().getLocation(), drop);
            event.getClickedBlock().getLocation().getWorld().playSound(event.getClickedBlock().getLocation(), Sound.ANVIL_USE, 1.0F, 1.0F);

            spawners -= 1;

            if (spawners == 0) {
                event.getPlayer().setItemInHand(null);
                event.getClickedBlock().getLocation().getWorld().playSound(event.getClickedBlock().getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
                return;
            }

            meta = event.getItem().getItemMeta();

            meta.setLore(InvUtils.getCrowbarLore(0, spawners));

            event.getItem().setItemMeta(meta);

            double max = Material.DIAMOND_HOE.getMaxDurability();
            double dura = (max / (double) InvUtils.CROWBAR_SPAWNERS) * spawners;

            event.getItem().setDurability((short) (max - dura));
            event.getPlayer().setItemInHand(event.getItem());
        } else {
            event.getPlayer().sendMessage(ChatColor.RED + "Crowbars can only break end portals and mob spawners!");
            event.setCancelled(true);
        }
    }

}