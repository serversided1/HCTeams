package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PlaySoundCommand {

    @Command(names={ "PlaySound" }, permissionNode="op")
    public static void playSound(Player sender, @Param(name="Sound") String sound, @Param(name="Pitch") float pitch) {
            try {
                Sound soundObj = Sound.valueOf(sound);
                sender.playSound(sender.getLocation(), sound, 20F, pitch);
            } catch (Exception ex) {
                sender.sendMessage(ex.getMessage());
            }
    }

}