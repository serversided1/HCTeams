package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.qlib.command.annotations.Parameter;
import net.frozenorb.foxtrot.koth.KOTH;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHDistCommand {

    @Command(names={ "KOTH Dist" }, permissionNode="foxtrot.koth.admin")
    public static void kothDist(Player sender, @Parameter(name="KOTH") KOTH target, @Parameter(name="Max Distance") int maxDistance) {
        target.setCapDistance(maxDistance);
        sender.sendMessage(ChatColor.GRAY + "Set max distance for the " + target.getName() + " KOTH.");
    }

}