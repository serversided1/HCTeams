package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.mBasic.Basic;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetBalCommand {

    @Command(names={ "SetBal" }, permissionNode="foxtrot.setbal")
    public static void setBal(CommandSender sender, @Parameter(name="Target") String target, @Parameter(name="Amount") float value) {
        if (value > 10000 && sender instanceof Player && !sender.isOp()) {
            sender.sendMessage("§cYou cannot set a balance this high. This action has been logged.");
            return;
        }

        if (value > 250000 && sender instanceof Player) {
            sender.sendMessage("§cWhat the fuck are you trying to do?");
            return;
        }

        Player targetPlayer = Foxtrot.getInstance().getServer().getPlayer(target);
        Basic.get().getEconomyManager().setBalance(target, value);

        if (sender != targetPlayer) {
            sender.sendMessage("§6Balance for §e" + target + "§6 set to §e$" + value);
        }

        if (sender instanceof Player && (targetPlayer != null)) {
            String targetDisplayName = ((Player) sender).getDisplayName();
            targetPlayer.sendMessage("§aYour balance has been set to: §6$" + value + "§a by §6" + targetDisplayName);
        } else if (targetPlayer != null) {
            targetPlayer.sendMessage("§aYour balance has been set to: §6$" + value + "§a by §4CONSOLE§a.");
        }
    }

}