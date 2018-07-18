package net.frozenorb.foxtrot.map.kit.killstreaks;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.util.ClassUtils;

public class KillstreakHandler {

    @Getter private List<Killstreak> killstreaks = Lists.newArrayList();
    @Getter private List<PersistentKillstreak> persistentKillstreaks = Lists.newArrayList();

    public KillstreakHandler() {
        String packageName = Foxtrot.getInstance().getMapHandler().getScoreboardTitle().contains("Arcane") ? "arcanetypes" : "velttypes";
        ClassUtils.getClassesInPackage(Foxtrot.getInstance(), "net.frozenorb.foxtrot.map.kit.killstreaks." + packageName).forEach(clazz -> {
            if (Killstreak.class.isAssignableFrom(clazz)) {
                try {
                    Killstreak killstreak = (Killstreak) clazz.newInstance();

                    killstreaks.add(killstreak);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    PersistentKillstreak killstreak = (PersistentKillstreak) clazz.newInstance();

                    persistentKillstreaks.add(killstreak);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

        killstreaks.sort((first, second) -> {
            int firstNumber = first.getKills()[0];
            int secondNumber = second.getKills()[0];

            if (firstNumber < secondNumber) {
                return -1;
            }
            return 1;

        });
        
        persistentKillstreaks.sort((first, second) -> {
            int firstNumber = first.getKillsRequired();
            int secondNumber = second.getKillsRequired();

            if (firstNumber < secondNumber) {
                return -1;
            }
            return 1;

        });
    }

    public Killstreak check(int kills) {
        for (Killstreak killstreak : killstreaks) {
            for (int kill : killstreak.getKills()) {
                if (kills == kill) {
                    return killstreak;
                }
            }
        }

        return null;
    }
    
    public List<PersistentKillstreak> getPersistentKillstreaks(Player player, int count) {
        return persistentKillstreaks.stream().filter(s -> s.check(count)).collect(Collectors.toList());
    }

}
