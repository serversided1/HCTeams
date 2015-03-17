package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class OresCommand {

    @Command(names={ "Ores" }, permissionNode="")
    public static void ores(Player sender, @Parameter(name="target", defaultValue="self") UUID target) {
        sender.sendMessage(ChatColor.AQUA + "Diamond mined: " + ChatColor.WHITE + Foxtrot.getInstance().getDiamondMinedMap().getMined(target));
        sender.sendMessage(ChatColor.GREEN + "Emerald mined: " + ChatColor.WHITE + Foxtrot.getInstance().getEmeraldMinedMap().getMined(target));
        sender.sendMessage(ChatColor.RED + "Redstone mined: " + ChatColor.WHITE + Foxtrot.getInstance().getRedstoneMinedMap().getMined(target));
        sender.sendMessage(ChatColor.GOLD + "Gold mined: " + ChatColor.WHITE + Foxtrot.getInstance().getGoldMinedMap().getMined(target));
        sender.sendMessage(ChatColor.GRAY + "Iron mined: " + ChatColor.WHITE + Foxtrot.getInstance().getIronMinedMap().getMined(target));
        sender.sendMessage(ChatColor.BLUE + "Lapis mined: " + ChatColor.WHITE + Foxtrot.getInstance().getLapisMinedMap().getMined(target));
        sender.sendMessage(ChatColor.DARK_GRAY + "Coal mined: " + ChatColor.WHITE + Foxtrot.getInstance().getCoalMinedMap().getMined(target));
    }

}