package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.persist.maps.GlintMap;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GlintCommand {

    @Command(names={ "glint" }, permissionNode="")
    public static void glint(Player sender) {
        boolean val = !Foxtrot.getInstance().getGlintMap().getGlintToggled(sender.getUniqueId());

        Foxtrot.getInstance().getGlintMap().setGlintToggled(sender.getUniqueId(), val);
        GlintMap.setGlintEnabled(sender, val);

        sender.sendMessage(ChatColor.YELLOW + "You are now " + (!val ? ChatColor.RED + "unable" : ChatColor.GREEN + "able") + ChatColor.YELLOW + " to see enchantment glint!");
        sender.sendMessage(ChatColor.GOLD + "Nearby players will not update until you relog.");
    }

}