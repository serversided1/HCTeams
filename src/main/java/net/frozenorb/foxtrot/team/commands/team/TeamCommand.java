package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamCommand {

    @Command(names={ "team", "t", "f", "faction", "fac" }, permissionNode="")
    public static void team(Player sender) {

        String[] msg = {

                "§6§m-----------------------------------------------------",
                "§9§lTeam Help §7- §eTeam Help",
                "§7§m-----------------------------------------------------",


                "§9General Commands:",
                "§e/t create <teamName> §7- Create a new team",
                "§e/t accept <teamName> §7- Accept a pending invitation",
                "§e/t leave §7- Leave your current team",
                "§e/t home §7- Teleport to your team home",
                "§e/t stuck §7- Teleport out of enemy territory",
                "§e/t deposit <amount§7|§eall> §7- Deposit money into your team balance",


                "",
                "§9Information Commands:",
                "§e/t who [player§7|§eteamName] §7- Display team information",
                "§e/t map §7- Show nearby claims (identified by pillars)",
                "§e/t list §7- Show list of teams online (sorted by most online)",


                "",
                "§9Captain Commands:",
                "§e/t invite <player> §7- Invite a player to your team",
                "§e/t uninvite <player> §7- Revoke an invitation",
                "§e/t invites §7- List all open invitations",
                "§e/t kick <player> §7- Kick a player from your team.",
                "§e/t claim §7- Start a claim for your team.",
                "§e/t subclaim §7- Show the subclaim help page.",
                "§e/t sethome §7- Set your team's home at your current location",
                "§e/t withdraw <amount> §7- Withdraw money from your team's balance",
                "§e/t motd [message here] §7- Set your team's message of the day",

                "",
                "§9Leader Commands:",

                "§e/t promote <player> §7- Promote a member to Captain",
                "§e/t demote <player> §7- Demotes a Captain to member",
                "§e/t unclaim [all] §7- Unclaim land",
                "§e/t rename <newName> §7- Rename your team.",
                "§e/t disband §7- Disband your team.",


                "§6§m-----------------------------------------------------",



        };
        sender.sendMessage(msg);
    }

}