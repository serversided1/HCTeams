package net.frozenorb.foxtrot.map.kit.killstreaks;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.util.ClassUtils;

import java.util.List;

public class KillstreakHandler {

    @Getter private List<Killstreak> killstreaks = Lists.newArrayList();

    public KillstreakHandler() {
        ClassUtils.getClassesInPackage(Foxtrot.getInstance(), "net.frozenorb.foxtrot.map.kit.killstreaks.types").stream().filter(Killstreak.class::isAssignableFrom).forEach(clazz -> {
            try {
                Killstreak killstreak = (Killstreak) clazz.newInstance();

                killstreaks.add(killstreak);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
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

}
