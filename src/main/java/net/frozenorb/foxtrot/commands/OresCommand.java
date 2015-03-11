package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class OresCommand {

    @Command(names={ "Ores" }, permissionNode="")
    public static void ores(Player sender, @Parameter(name="target", defaultValue="self") UUID target) {
        sender.sendMessage(ChatColor.AQUA + "Diamond mined: " + ChatColor.WHITE + FoxtrotPlugin.getInstance().getDiamondMinedMap().getMined(target));
        sender.sendMessage(ChatColor.GREEN + "Emerald mined: " + ChatColor.WHITE + FoxtrotPlugin.getInstance().getEmeraldMinedMap().getMined(target));
        sender.sendMessage(ChatColor.RED + "Redstone mined: " + ChatColor.WHITE + FoxtrotPlugin.getInstance().getRedstoneMinedMap().getMined(target));
        sender.sendMessage(ChatColor.GOLD + "Gold mined: " + ChatColor.WHITE + FoxtrotPlugin.getInstance().getGoldMinedMap().getMined(target));
        sender.sendMessage(ChatColor.GRAY + "Iron mined: " + ChatColor.WHITE + FoxtrotPlugin.getInstance().getIronMinedMap().getMined(target));
        sender.sendMessage(ChatColor.BLUE + "Lapis mined: " + ChatColor.WHITE + FoxtrotPlugin.getInstance().getLapisMinedMap().getMined(target));
        sender.sendMessage(ChatColor.DARK_GRAY + "Coal mined: " + ChatColor.WHITE + FoxtrotPlugin.getInstance().getCoalMinedMap().getMined(target));
    }

}