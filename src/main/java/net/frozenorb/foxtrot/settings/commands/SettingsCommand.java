package net.frozenorb.foxtrot.settings.commands;

import net.frozenorb.foxtrot.settings.menu.SettingsMenu;
import net.frozenorb.qlib.command.Command;
import org.bukkit.entity.Player;

public class SettingsCommand {

    @Command(names = {"settings", "options"}, permission = "")
    public static void settings(Player sender) {
        new SettingsMenu().openMenu(sender);
    }

}
