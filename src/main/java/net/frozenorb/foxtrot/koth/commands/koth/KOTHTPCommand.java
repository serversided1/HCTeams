package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHTPCommand {

    @Command(names={ "KOTH TP" }, permissionNode="foxtrot.koth")
    public static void kothTP(Player sender, @Parameter(name="koth") KOTH koth) {
        sender.teleport(koth.getCapLocation().toLocation(Foxtrot.getInstance().getServer().getWorld(koth.getWorld())));
        sender.sendMessage(ChatColor.GRAY + "Teleported to the " + koth.getName() + " KOTH.");
    }

}