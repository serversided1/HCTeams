package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHTPCommand {

    @Command(names={ "KOTH TP" }, permissionNode="foxtrot.koth")
    public static void kothTP(Player sender, @Parameter(name="KOTH") KOTH target) {
        sender.teleport(target.getCapLocation().toLocation(Foxtrot.getInstance().getServer().getWorld(target.getWorld())));
        sender.sendMessage(ChatColor.GRAY + "Teleported to the " + target.getName() + " KOTH.");
    }

}