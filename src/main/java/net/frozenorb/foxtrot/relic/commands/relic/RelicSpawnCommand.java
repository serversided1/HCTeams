package net.frozenorb.foxtrot.relic.commands.relic;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.relic.enums.Relic;
import net.frozenorb.foxtrot.util.InvUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class RelicSpawnCommand {

    @Command(names={ "relic spawn" }, permissionNode="op")
    public static void relicSpawn(Player sender, @Param(name="relic") String relic, @Param(name="Tier") int tier) {
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
            return;
        }

        sender.setItemInHand(InvUtils.generateRelic(Relic.valueOf(relic.toUpperCase()), tier, "Admin Command"));
        sender.sendMessage(ChatColor.YELLOW + "Gave you a Relic.");
    }

}