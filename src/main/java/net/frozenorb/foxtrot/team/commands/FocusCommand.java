package net.frozenorb.foxtrot.team.commands;


import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.nametag.FrozenNametagHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FocusCommand {

    @Command(names={ "focus" }, permission="")
    public static void focus(Player sender, @Param(name="player") Player target) {
        Team senderTeam = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
        Team targetTeam = Foxtrot.getInstance().getTeamHandler().getTeam(target);

        if (senderTeam == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        // There's a few ways this can go:
        // a. Target's team is null, in which case they can be targeted.
        // b. Target's team is not null, in which case...
        //      1. The teams are equal, where they can't be targeted.
        //      2. They aren't equal, in which case they can be targeted.
        // This if statement really isn't as complex as the above
        // comment made it sound, but it took me a few minutes of
        // thinking through, so this is just to save time.
        if (senderTeam == targetTeam) {
            sender.sendMessage(ChatColor.RED + "You cannot target a player on your team.");
            return;
        }

        senderTeam.setFocused(target.getUniqueId());
        senderTeam.sendMessage(ChatColor.LIGHT_PURPLE + target.getName() + ChatColor.YELLOW + " has been focused by " + ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.YELLOW + ".");

        for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            if (senderTeam.isMember(onlinePlayer.getUniqueId())) {
                FrozenNametagHandler.reloadOthersFor(onlinePlayer);
            }
        }
    }

}