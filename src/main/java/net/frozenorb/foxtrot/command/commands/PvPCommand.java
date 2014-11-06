package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.entity.Player;

public class PvPCommand {

    @Command(names={ "pvptimer", "timer", "pvp" }, permissionNode="")
    public static void pvpTimer(Player sender) {
        String[] msges = {
                "§c/pvp lives [target] - Shows amount of lives that a player has",
                "§c/pvp revive <player> - Revives targeted player",
                "§c/pvp time - Shows time left on PVP Timer",
                "§c/pvp enable - Remove PVP Timer"};

        sender.sendMessage(msges);
    }

    /*

            if (args.length > 0) {
            if (args[0].equalsIgnoreCase("create") && sender.isOp()) {

                return;
            }

            if (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("remove")) {
                if (FoxtrotPlugin.getInstance().getJoinTimerMap().hasTimer(sender)) {
                    FoxtrotPlugin.getInstance().getJoinTimerMap().updateValue(sender.getName(), -1L);
                    sender.sendMessage(ChatColor.RED + "Your PVP Timer has been removed!");
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not have a PVP Timer on!");
                }
            } else if (args[0].equalsIgnoreCase("revive")) {
                if (!(sender.isOp())) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                    return;
                }


            } else if (args[0].equalsIgnoreCase("time")) {
                if (FoxtrotPlugin.getInstance().getJoinTimerMap().hasTimer(sender)) {
                    sender.sendMessage(ChatColor.RED + "You have " + TimeUtils.getDurationBreakdown(FoxtrotPlugin.getInstance().getJoinTimerMap().getValue(sender.getName()) - System.currentTimeMillis()) + " left on your PVP Timer.");
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not have a PVP Timer on!");
                }
                return;
            } else if (args[0].equalsIgnoreCase("lives")) {
                String name = sender.getName();

                if (args.length > 1) {
                    name = args[1];
                }
                int lives = FoxtrotPlugin.getInstance().getServerHandler().getLives(name);

                sender.sendMessage("§6" + name + "'s lives§f: " + lives);
            }
        }

     */

}