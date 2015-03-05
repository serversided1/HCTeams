package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.qlib.command.annotations.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHTPCommand {

    @Command(names={ "KOTH TP" }, permissionNode="foxtrot.koth")
    public static void kothTP(Player sender, @Parameter(name="KOTH") KOTH target) {
        sender.teleport(target.getCapLocation().toLocation(FoxtrotPlugin.getInstance().getServer().getWorld(target.getWorld())));
        sender.sendMessage(ChatColor.GRAY + "Teleported to the " + target.getName() + " KOTH.");
    }

}