package net.frozenorb.foxtrot.command.commands.koth;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.koth.KOTH;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/20/2014.
 */
public class KOTHTPCommand {

    @Command(names={ "KOTH TP" }, permissionNode="foxtrot.koth.admin")
    public static void kothTP(Player sender, @Param(name="KOTH") KOTH target) {
        sender.teleport(target.getCapLocation().toLocation(FoxtrotPlugin.getInstance().getServer().getWorld(target.getWorld())));
        sender.sendMessage(ChatColor.GRAY + "Teleported to the " + target.getName() + " KOTH.");
    }

}