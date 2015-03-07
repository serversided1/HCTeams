package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class OresCommand {

    @Command(names={ "Ores" }, permissionNode="")
    public static void ores(Player sender, @Parameter(name="target", defaultValue="self") OfflinePlayer target) {
        sender.sendMessage(ChatColor.AQUA + "Diamond mined: " + ChatColor.WHITE + FoxtrotPlugin.getInstance().getDiamondMinedMap().getMined(target.getUniqueId()));
        sender.sendMessage(ChatColor.GREEN + "Emerald mined: " + ChatColor.WHITE + FoxtrotPlugin.getInstance().getEmeraldMinedMap().getMined(target.getUniqueId()));
        sender.sendMessage(ChatColor.RED + "Redstone mined: " + ChatColor.WHITE + FoxtrotPlugin.getInstance().getRedstoneMinedMap().getMined(target.getUniqueId()));
        sender.sendMessage(ChatColor.GOLD + "Gold mined: " + ChatColor.WHITE + FoxtrotPlugin.getInstance().getGoldMinedMap().getMined(target.getUniqueId()));
        sender.sendMessage(ChatColor.GRAY + "Iron mined: " + ChatColor.WHITE + FoxtrotPlugin.getInstance().getIronMinedMap().getMined(target.getUniqueId()));
        sender.sendMessage(ChatColor.BLUE + "Lapis mined: " + ChatColor.WHITE + FoxtrotPlugin.getInstance().getLapisMinedMap().getMined(target.getUniqueId()));
        sender.sendMessage(ChatColor.DARK_GRAY + "Coal mined: " + ChatColor.WHITE + FoxtrotPlugin.getInstance().getCoalMinedMap().getMined(target.getUniqueId()));
    }

}