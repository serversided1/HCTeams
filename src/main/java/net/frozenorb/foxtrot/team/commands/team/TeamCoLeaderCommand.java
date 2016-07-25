package net.frozenorb.foxtrot.team.commands.team;


import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.UUIDUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamCoLeaderCommand {

    @Command(names={ "team coleader add", "t coleader add", "t co-leader add", "team co-leader add", "f co-leader add", "fac co-leader add", "faction co-leader add", "f coleader add", "fac coleader add", "faction coleader add"}, permission="")
    public static void coleaderAdd(Player sender, @Param(name = "player") UUID promote) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender.getUniqueId());
        if( team == null ) {
            sender.sendMessage(ChatColor.RED + "You must be in a team to execute this command.");
            return;
        }

        if(!team.isOwner(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only the team owner can execute this command.");
            return;
        }

        if(!team.isMember(promote)) {
            sender.sendMessage(ChatColor.RED + "This player must be a member of your team.");
            return;
        }

        if(team.isCoLeader(promote)) {
           sender.sendMessage(ChatColor.RED + "This player is already a co-leader of your team.");
            return;
        }

        team.addCoLeader(promote);
        sender.sendMessage(ChatColor.GREEN + "You have successfully added " + ChatColor.RED + UUIDUtils.name(promote) + ChatColor.GREEN + " as a co-leader.");
    }

    @Command(names={ "team coleader remove", "t coleader remove", "t co-leader remove", "team co-leader remove", "f co-leader remove", "fac co-leader remove", "faction co-leader remove", "f coleader remove", "fac coleader remove", "faction coleader remove" }, permission="")
    public static void coleaderRemove(Player sender, @Param(name = "player") UUID demote) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender.getUniqueId());
        if( team == null ) {
            sender.sendMessage(ChatColor.RED + "You must be in a team to execute this command.");
            return;
        }

        if(!team.isOwner(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only the team owner can execute this command.");
            return;
        }

        if(!team.isMember(demote)) {
            sender.sendMessage(ChatColor.RED + "This player must be a member of your team.");
            return;
        }

        if(!team.isCoLeader(demote)) {
            sender.sendMessage(ChatColor.RED + "This player is not a co-leader of your team.");
            return;
        }

        team.removeCoLeader(demote);
        sender.sendMessage(ChatColor.GREEN + "You have successfully removed " + ChatColor.RED + UUIDUtils.name(demote) + ChatColor.GREEN + " from co-leader.");
    }
}
