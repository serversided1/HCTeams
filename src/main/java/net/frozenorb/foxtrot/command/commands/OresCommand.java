package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class OresCommand {

    @Command(names={ "Ores" }, permissionNode="")
    public static void ores(Player sender, @Param(name="target", defaultValue="self") OfflinePlayer target) {
        sender.sendMessage(ChatColor.AQUA + "Diamond mined: " + ChatColor.WHITE + FoxtrotPlugin.getInstance().getDiamondMinedMap().getMined(target.getName()));
        sender.sendMessage(ChatColor.GREEN + "Emerald mined: " + ChatColor.WHITE + FoxtrotPlugin.getInstance().getEmeraldMinedMap().getMined(target.getName()));
        sender.sendMessage(ChatColor.RED + "Redstone mined: " + ChatColor.WHITE + FoxtrotPlugin.getInstance().getRedstoneMinedMap().getMined(target.getName()));
        sender.sendMessage(ChatColor.GOLD + "Gold mined: " + ChatColor.WHITE + FoxtrotPlugin.getInstance().getGoldMinedMap().getMined(target.getName()));
        sender.sendMessage(ChatColor.GRAY + "Iron mined: " + ChatColor.WHITE + FoxtrotPlugin.getInstance().getIronMinedMap().getMined(target.getName()));
        sender.sendMessage(ChatColor.BLUE + "Lapis mined: " + ChatColor.WHITE + FoxtrotPlugin.getInstance().getLapisMinedMap().getMined(target.getName()));
        sender.sendMessage(ChatColor.DARK_GRAY + "Coal mined: " + ChatColor.WHITE + FoxtrotPlugin.getInstance().getCoalMinedMap().getMined(target.getName()));
    }

}