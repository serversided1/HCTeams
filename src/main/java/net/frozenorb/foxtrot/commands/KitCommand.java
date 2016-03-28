package net.frozenorb.foxtrot.commands;

import net.frozenorb.qlib.command.Command;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

public class KitCommand {

    @Command(names = "kit", permissionNode = "")
    public static void kit(Player sender) {
        String sharp = "Sharpness " + Enchantment.DAMAGE_ALL.getMaxLevel();
        String prot = "Protection " + Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel();
        String bow = "Power " + Enchantment.ARROW_DAMAGE.getMaxLevel();
        sender.sendMessage("§eEnchant Limits: §7" + prot + ", " + sharp + ", " + bow);
    }
}
