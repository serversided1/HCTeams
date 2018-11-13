package net.frozenorb.foxtrot.map.kit.kits;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import com.google.common.collect.Maps;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.profile.Profile;
import net.frozenorb.hydrogen.rank.Rank;

public class KitListener implements Listener {
    
    private Map<UUID, Long> lastClicked = Maps.newHashMap();
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        if (event.getClickedBlock() == null || !(event.getClickedBlock().getState() instanceof Sign)) {
            return;
        }
        
        Sign sign = (Sign) event.getClickedBlock().getState();
        
        if (!sign.getLine(0).startsWith(ChatColor.BLUE + "- Kit")) {
            return;
        }
        
        Kit kit = Foxtrot.getInstance().getMapHandler().getKitManager().get(player.getUniqueId(), sign.getLine(1));
        
        if (kit == null) {
            player.sendMessage(ChatColor.RED + "Unknown kit.");
            return;
        }
        
        if (player.hasMetadata("modmode")) {
            player.sendMessage(ChatColor.RED + "You cannot use this while in mod mode.");
            return;
        }
        
        if (lastClicked.containsKey(player.getUniqueId()) && (System.currentTimeMillis() - lastClicked.get(player.getUniqueId()) < TimeUnit.SECONDS.toMillis(15))) {
            player.sendMessage(ChatColor.RED + "Please wait before using this again.");
            return;
        }
        
        if (!canUse(player, kit.getName())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou do not own this kit. Purchase it at store.veltpvp.com."));
            return;
        }
        
        kit.apply(player);
        
        lastClicked.put(player.getUniqueId(), System.currentTimeMillis());
    }
    
    private boolean canUse(Player player, String kitName) {
        if (kitName.equals("PvP") || kitName.equals("Archer") || kitName.equals("Bard") || kitName.equals("Rogue")) {
            return true;
        }
        
        Optional<Profile> profileOptional = Hydrogen.getInstance().getProfileHandler().getProfile(player.getUniqueId());
        if (!profileOptional.isPresent())
            return false;
        
        Profile profile = profileOptional.get();
        Rank highestRank = profile.getBestDisplayRank();
        
        if (highestRank.isStaffRank() || highestRank.getDisplayName().equals("YouTuber") || highestRank.getDisplayName().equals("Famous")) {
            return true;
        }
        
        if (kitName.equals("Miner") || kitName.equals("Builder")) {
            return true;
        }
        
        return highestRank.getDisplayName().equals(kitName);
    }
    
}
