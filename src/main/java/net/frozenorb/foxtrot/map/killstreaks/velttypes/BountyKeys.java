package net.frozenorb.foxtrot.map.killstreaks.velttypes;

import net.frozenorb.foxtrot.map.killstreaks.Killstreak;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BountyKeys extends Killstreak {

    @Override
    public String getName() {
        return "3 Bounty Keys";
    }

    @Override
    public int[] getKills() {
        return new int[] {
                40
        };
    }

    @Override
    public void apply(Player player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " bounty 3");
    }

}
