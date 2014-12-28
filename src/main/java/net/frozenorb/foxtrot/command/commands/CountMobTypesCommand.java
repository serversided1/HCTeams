package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;

public class CountMobTypesCommand {

    @Command(names={ "countmobtypes" }, permissionNode="op")
    public static void countMobTypes(Player sender) {
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
            return;
        }

        Map<EntityType, Integer> counts = new HashMap<EntityType, Integer>();

        for (Entity entity : sender.getWorld().getEntities()) {
            if (counts.containsKey(entity.getType())) {
                counts.put(entity.getType(), counts.get(entity.getType()) + 1);
            } else {
                counts.put(entity.getType(), 1);
            }
        }

        LinkedHashMap<EntityType, Integer> sortedMap = sortByValues(counts);

        for (Map.Entry<EntityType, Integer> countEntry : sortedMap.entrySet()) {
            sender.sendMessage(ChatColor.DARK_AQUA + countEntry.getKey().name() + ": " + ChatColor.WHITE + countEntry.getValue());
        }
    }

    private static LinkedHashMap<EntityType, Integer> sortByValues(Map<EntityType, Integer> map) {
        LinkedList<Map.Entry<EntityType, Integer>> list = new LinkedList<>(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<EntityType, Integer>>() {

            public int compare(java.util.Map.Entry<EntityType, Integer> o1, java.util.Map.Entry<EntityType, Integer> o2) {
                return (o2.getValue().compareTo(o1.getValue()));
            }

        });

        LinkedHashMap<EntityType, Integer> sortedHashMap = new LinkedHashMap<EntityType, Integer>();
        Iterator<Map.Entry<EntityType, Integer>> iterator = list.iterator();

        while (iterator.hasNext()) {
            java.util.Map.Entry<EntityType, Integer> entry = iterator.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }

        return (sortedHashMap);
    }

}