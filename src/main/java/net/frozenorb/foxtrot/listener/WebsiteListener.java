package net.frozenorb.foxtrot.listener;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import net.frozenorb.Utilities.Serialization.Serializers.ItemStackSerializer;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.mShared.Shared;
import net.frozenorb.mShared.Utilities.Utilities;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by macguy8 on 11/12/2014.
 */
public class WebsiteListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        BasicDBObject playerDeath = new BasicDBObject();

        if (event.getEntity().getKiller() != null) {
            playerDeath.append("soups", -1);
            playerDeath.append("healthLeft", (int) event.getEntity().getKiller().getHealth());
            playerDeath.append("killer", event.getEntity().getKiller().getName());
            playerDeath.append("killerUUID", event.getEntity().getKiller().getUniqueId().toString().replace("-", ""));
            playerDeath.append("killerHunger", event.getEntity().getKiller().getFoodLevel());

            if (event.getEntity().getKiller().getItemInHand() != null) {
                playerDeath.append("item", Shared.get().getUtilities().getDatabaseRepresentation(event.getEntity().getKiller().getItemInHand()));
            } else {
                playerDeath.append("item", "NONE");
            }
        } else {
            try{
                playerDeath.append("reason", event.getEntity().getLastDamageCause().getCause().toString());
            } catch (NullPointerException localNullPointerException) {

            }
        }

        playerDeath.append("playerHunger", event.getEntity().getFoodLevel());

        BasicDBObject playerInv = new BasicDBObject();
        BasicDBObject armor = new BasicDBObject();

        armor.put("helmet", new ItemStackSerializer().serialize(event.getEntity().getInventory().getHelmet()));
        armor.put("chestplate", new ItemStackSerializer().serialize(event.getEntity().getInventory().getChestplate()));
        armor.put("leggings", new ItemStackSerializer().serialize(event.getEntity().getInventory().getLeggings()));
        armor.put("boots", new ItemStackSerializer().serialize(event.getEntity().getInventory().getBoots()));

        BasicDBList contents = new BasicDBList();

        for (int i = 0; i < 9; i++) {
            if (event.getEntity().getInventory().getItem(i) != null) {
                contents.add(new ItemStackSerializer().serialize(event.getEntity().getInventory().getItem(i)));
            } else {
                contents.add(new ItemStackSerializer().serialize(new ItemStack(Material.AIR)));
            }
        }

        playerInv.append("armor", armor);
        playerInv.append("items", contents);

        playerDeath.append("playerInventory", playerInv);

        if (event.getEntity().getKiller() != null) {
            BasicDBObject killerInventory = new BasicDBObject();
            BasicDBObject killerArmor = new BasicDBObject();

            armor.put("helmet", new ItemStackSerializer().serialize(event.getEntity().getKiller().getInventory().getHelmet()));
            armor.put("chestplate", new ItemStackSerializer().serialize(event.getEntity().getKiller().getInventory().getChestplate()));
            armor.put("leggings", new ItemStackSerializer().serialize(event.getEntity().getKiller().getInventory().getLeggings()));
            armor.put("boots", new ItemStackSerializer().serialize(event.getEntity().getKiller().getInventory().getBoots()));

            BasicDBList killerContents = new BasicDBList();

            for (int i = 0; i < 9; i++) {
                if (event.getEntity().getKiller().getInventory().getItem(i) != null) {
                    killerContents.add(new ItemStackSerializer().serialize(event.getEntity().getKiller().getInventory().getItem(i)));
                } else {
                    killerContents.add(new ItemStackSerializer().serialize(new ItemStack(Material.AIR)));
                }
            }

            killerInventory.append("armor", killerArmor);
            killerInventory.append("items", killerContents);
            playerDeath.append("killerInventory", killerInventory);
        }

        playerDeath.append("ip", event.getEntity().getAddress().toString().split(":")[0].replace("/", ""));
        playerDeath.append("uuid", event.getEntity().getUniqueId().toString().replace("-", ""));
        playerDeath.append("player", event.getEntity().getName());
        playerDeath.append("type", "death");
        playerDeath.append("when", Utilities.getInstance().getTime(System.currentTimeMillis()));

        new BukkitRunnable() {

            public void run() {
                FoxtrotPlugin.getInstance().getMongoPool().getDB("HCTeams").getCollection("Deaths").insert(playerDeath);
            }

        }.runTaskAsynchronously(FoxtrotPlugin.getInstance());
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onBlockPlace(BlockPlaceEvent event) {
        switch (event.getBlock().getType()) {
            case DIAMOND_ORE:
            case GOLD_ORE:
            case IRON_ORE:
            case COAL_ORE:
            case REDSTONE_ORE:
            case GLOWING_REDSTONE_ORE:
            case LAPIS_ORE:
            case EMERALD_ORE:
                event.getBlock().setMetadata("PlacedByPlayer", new FixedMetadataValue(FoxtrotPlugin.getInstance(), true));
                break;
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onBlockBreak(BlockBreakEvent event) {
        if ((event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH)) || event.getBlock().hasMetadata("PlacedByPlayer")) {
            return;
        }

        switch (event.getBlock().getType()) {
            case DIAMOND_ORE:
                FoxtrotPlugin.getInstance().getDiamondMinedMap().setMined(event.getPlayer().getName(), FoxtrotPlugin.getInstance().getDiamondMinedMap().getMined(event.getPlayer().getName()) + 1);
                break;
            case GOLD_ORE:
                FoxtrotPlugin.getInstance().getGoldMinedMap().setMined(event.getPlayer().getName(), FoxtrotPlugin.getInstance().getGoldMinedMap().getMined(event.getPlayer().getName()) + 1);
                break;
            case IRON_ORE:
                FoxtrotPlugin.getInstance().getIronMinedMap().setMined(event.getPlayer().getName(), FoxtrotPlugin.getInstance().getIronMinedMap().getMined(event.getPlayer().getName()) + 1);
                break;
            case COAL_ORE:
                FoxtrotPlugin.getInstance().getCoalMinedMap().setMined(event.getPlayer().getName(), FoxtrotPlugin.getInstance().getCoalMinedMap().getMined(event.getPlayer().getName()) + 1);
                break;
            case REDSTONE_ORE:
            case GLOWING_REDSTONE_ORE:
                FoxtrotPlugin.getInstance().getRedstoneMinedMap().setMined(event.getPlayer().getName(), FoxtrotPlugin.getInstance().getRedstoneMinedMap().getMined(event.getPlayer().getName()) + 1);
                break;
            case LAPIS_ORE:
                FoxtrotPlugin.getInstance().getLapisMinedMap().setMined(event.getPlayer().getName(), FoxtrotPlugin.getInstance().getLapisMinedMap().getMined(event.getPlayer().getName()) + 1);
                break;
            case EMERALD_ORE:
                FoxtrotPlugin.getInstance().getEmeraldMinedMap().setMined(event.getPlayer().getName(), FoxtrotPlugin.getInstance().getEmeraldMinedMap().getMined(event.getPlayer().getName()) + 1);
                break;
        }
    }

}