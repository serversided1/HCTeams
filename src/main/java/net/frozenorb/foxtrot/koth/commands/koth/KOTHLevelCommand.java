package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.qlib.command.annotations.Parameter;
import net.frozenorb.foxtrot.koth.KOTH;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHLevelCommand {

    @Command(names={ "KOTH Level" }, permissionNode="foxtrot.koth")
    public static void kothLevel(Player sender, @Parameter(name="KOTH") KOTH target, @Parameter(name="Tier") int level) {
        target.setLevel(level);
        sender.sendMessage(ChatColor.GRAY + "Set level for the " + target.getName() + " KOTH.");
    }

}