package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.util.InvUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class KOTHRewardKeyCommand {

    @Command(names={ "kothrewardkey" }, permissionNode="op")
    public static void kothRewardKey(Player sender, @Param(name="KOTH") String koth, @Param(name="Tier") int tier) {
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
            return;
        }

        sender.setItemInHand(InvUtils.generateKOTHRewardKey(koth, tier));
        sender.sendMessage(ChatColor.YELLOW + "Gave you a KOTH reward key.");
    }

}