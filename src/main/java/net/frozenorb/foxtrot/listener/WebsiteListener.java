package net.frozenorb.foxtrot.listener;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.serialization.ItemStackSerializer;
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

import java.util.Date;

public class WebsiteListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        final BasicDBObject playerDeath = new BasicDBObject();

        if (event.getEntity().getKiller() != null) {
            playerDeath.append("soups", -1);
            playerDeath.append("healthLeft", (int) event.getEntity().getKiller().getHealth());
            playerDeath.append("killerUUID", event.getEntity().getKiller().getUniqueId().toString().replace("-", ""));
            playerDeath.append("killerHunger", event.getEntity().getKiller().getFoodLevel());

            if (event.getEntity().getKiller().getItemInHand() != null) {
                playerDeath.append("item", ItemStackSerializer.serialize(event.getEntity().getKiller().getItemInHand()));
            } else {
                playerDeath.append("item", "NONE");
            }
        } else {
            try{
                playerDeath.append("reason", event.getEntity().getLastDamageCause().getCause().toString());
            } catch (NullPointerException ignored) {

            }
        }

        playerDeath.append("playerHunger", event.getEntity().getFoodLevel());

        BasicDBObject playerInv = new BasicDBObject();
        BasicDBObject armor = new BasicDBObject();

        armor.put("helmet", ItemStackSerializer.serialize(event.getEntity().getInventory().getHelmet()));
        armor.put("chestplate", ItemStackSerializer.serialize(event.getEntity().getInventory().getChestplate()));
        armor.put("leggings", ItemStackSerializer.serialize(event.getEntity().getInventory().getLeggings()));
        armor.put("boots", ItemStackSerializer.serialize(event.getEntity().getInventory().getBoots()));

        BasicDBList contents = new BasicDBList();

        for (int i = 0; i < 9; i++) {
            if (event.getEntity().getInventory().getItem(i) != null) {
                contents.add(ItemStackSerializer.serialize(event.getEntity().getInventory().getItem(i)));
            } else {
                contents.add(ItemStackSerializer.serialize(new ItemStack(Material.AIR)));
            }
        }

        playerInv.append("armor", armor);
        playerInv.append("items", contents);

        playerDeath.append("playerInventory", playerInv);

        if (event.getEntity().getKiller() != null) {
            BasicDBObject killerInventory = new BasicDBObject();
            BasicDBObject killerArmor = new BasicDBObject();

            armor.put("helmet", ItemStackSerializer.serialize(event.getEntity().getKiller().getInventory().getHelmet()));
            armor.put("chestplate", ItemStackSerializer.serialize(event.getEntity().getKiller().getInventory().getChestplate()));
            armor.put("leggings", ItemStackSerializer.serialize(event.getEntity().getKiller().getInventory().getLeggings()));
            armor.put("boots", ItemStackSerializer.serialize(event.getEntity().getKiller().getInventory().getBoots()));

            BasicDBList killerContents = new BasicDBList();

            for (int i = 0; i < 9; i++) {
                if (event.getEntity().getKiller().getInventory().getItem(i) != null) {
                    killerContents.add(ItemStackSerializer.serialize(event.getEntity().getKiller().getInventory().getItem(i)));
                } else {
                    killerContents.add(ItemStackSerializer.serialize(new ItemStack(Material.AIR)));
                }
            }

            killerInventory.append("armor", killerArmor);
            killerInventory.append("items", killerContents);
            playerDeath.append("killerInventory", killerInventory);
        }

        playerDeath.append("ip", event.getEntity().getAddress().toString().split(":")[0].replace("/", ""));
        playerDeath.append("uuid", event.getEntity().getUniqueId().toString().replace("-", ""));
        playerDeath.append("type", "death");
        playerDeath.append("when", new Date());

        new BukkitRunnable() {

            public void run() {
                Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("Deaths").insert(playerDeath);
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
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
                event.getBlock().setMetadata("PlacedByPlayer", new FixedMetadataValue(Foxtrot.getInstance(), true));
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
                Foxtrot.getInstance().getDiamondMinedMap().setMined(event.getPlayer(), Foxtrot.getInstance().getDiamondMinedMap().getMined(event.getPlayer().getUniqueId()) + 1);
                break;
            case GOLD_ORE:
                Foxtrot.getInstance().getGoldMinedMap().setMined(event.getPlayer().getUniqueId(), Foxtrot.getInstance().getGoldMinedMap().getMined(event.getPlayer().getUniqueId()) + 1);
                break;
            case IRON_ORE:
                Foxtrot.getInstance().getIronMinedMap().setMined(event.getPlayer().getUniqueId(), Foxtrot.getInstance().getIronMinedMap().getMined(event.getPlayer().getUniqueId()) + 1);
                break;
            case COAL_ORE:
                Foxtrot.getInstance().getCoalMinedMap().setMined(event.getPlayer().getUniqueId(), Foxtrot.getInstance().getCoalMinedMap().getMined(event.getPlayer().getUniqueId()) + 1);
                break;
            case REDSTONE_ORE:
            case GLOWING_REDSTONE_ORE:
                Foxtrot.getInstance().getRedstoneMinedMap().setMined(event.getPlayer().getUniqueId(), Foxtrot.getInstance().getRedstoneMinedMap().getMined(event.getPlayer().getUniqueId()) + 1);
                break;
            case LAPIS_ORE:
                Foxtrot.getInstance().getLapisMinedMap().setMined(event.getPlayer().getUniqueId(), Foxtrot.getInstance().getLapisMinedMap().getMined(event.getPlayer().getUniqueId()) + 1);
                break;
            case EMERALD_ORE:
                Foxtrot.getInstance().getEmeraldMinedMap().setMined(event.getPlayer().getUniqueId(), Foxtrot.getInstance().getEmeraldMinedMap().getMined(event.getPlayer().getUniqueId()) + 1);
                break;
        }
    }

}