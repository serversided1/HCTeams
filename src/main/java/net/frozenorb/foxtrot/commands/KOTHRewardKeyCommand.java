package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.util.InvUtils;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class KOTHRewardKeyCommand {

    @Command(names={ "kothrewardkey" }, permissionNode="op")
    public static void kothRewardKey(Player sender, @Parameter(name="KOTH") String koth, @Parameter(name="Tier") int tier) {
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
            return;
        }

        sender.setItemInHand(InvUtils.generateKOTHRewardKey(koth, tier));
        sender.sendMessage(ChatColor.YELLOW + "Gave you a KOTH reward key.");
    }

}