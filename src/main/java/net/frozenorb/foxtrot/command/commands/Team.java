package net.frozenorb.foxtrot.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.command.BaseCommand;
import net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands.*;
import net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands.Help;

public class Team extends BaseCommand {

	public Team() {
		super("team", "t", "f");
		registerSubcommand(new Chat("chat", "§c/t chat", "c"));
		registerSubcommand(new Create("create", "§c/t create <name>"));
		// registerSubcommand(new FF("ff", "§c/t ff <on|off>", "friendlyfire"));
		registerSubcommand(new HQ("hq", "§c/t hq", "home"));
		registerSubcommand(new Info("info", "§c/t i", "i", "show", "who", "list"));
		registerSubcommand(new Kick("kick", "§c/t kick <player>", "k"));
		registerSubcommand(new Leave("leave", "§c/t leave", "l"));
		registerSubcommand(new SetHQ("sethq", "§c/t sethq", "sethome"));
		registerSubcommand(new Msg("msg", "§c/t msg <message>", new String[] {
				"m", "message" }));
		registerSubcommand(new NewLeader("newleader", "§c/t newleader <player>", "leader"));
		registerSubcommand(new Roster("roster", "§c/t roster <message>", "r"));
		registerSubcommand(new Claim("claim", "§c/t claim"));
		registerSubcommand(new Invite("invite", "§c/t invite <player>", "inv"));
		registerSubcommand(new Accept("accept", "§c/t accept <teamName>", "join", "j"));
		registerSubcommand(new Revoke("revoke", "§c/t revoke"));
		registerSubcommand(new Unclaim("unclaim", "§c/t unclaim"));
		registerSubcommand(new Claims("claims", "§c/t claims [team]"));
		registerSubcommand(new Rally("rally", "§c/t rally"));
		registerSubcommand(new SetRally("setrally", "§c/t setrally"));
		registerSubcommand(new Rank("rank", "§c/t rank <player> <member|captain>", "setrank"));
		registerSubcommand(new Help("help", ""));
		registerSubcommand(new Demote("demote", "§c/t demote <player>", "uncaptain"));
		registerSubcommand(new Promote("promote", "§c/t promote <player>", "p", "captain"));
		registerSubcommand(new Subclaim("subclaim", "", "sub", "s"));
		registerSubcommand(new NameClaim("nameclaim", "", "nc", "claimname"));
		registerSubcommand(new Withdraw("withdraw", "§c/t withdraw <amount>"));
		registerSubcommand(new Deposit("deposit", "§c/t deposit <amount>", "d"));
		registerSubcommand(new Rename("rename", ""));
		registerSubcommand(new Map("map", ""));

		registerSubcommandsToTabCompletions();
	}

	@Override
	public void syncExecute() {
		if (args.length == 0) {
			sendHelp((Player) sender);
		} else
			sender.sendMessage(new String[] { "§cUnrecognized team command.",
					"§7Type §3'/t'§7 for details." });
	}

	public static void sendHelp(Player p) {

		p.sendMessage(ChatColor.DARK_AQUA + "***Anyone***");
		p.sendMessage(ChatColor.GRAY + "/team accept <teamName> - Accept a pending invitation.");
		p.sendMessage(ChatColor.GRAY + "/team create [teamName] - Create a team.");
		p.sendMessage(ChatColor.GRAY + "/team leave - Leave your current team.");
		p.sendMessage(ChatColor.GRAY + "/team roster <team> - Get details about the team.");
		p.sendMessage(ChatColor.GRAY + "/team info [playerName] - Get details about a team.");
		p.sendMessage(ChatColor.GRAY + "/team chat - Toggle team chat only mode on or off.");
		p.sendMessage(ChatColor.GRAY + "/team claims [team] - View all claims for a team.");
		p.sendMessage(ChatColor.GRAY + "/team msg <message> - Sends a message to your team.");
		p.sendMessage(ChatColor.GRAY + "/team hq - Teleport to the team headquarters.");
		p.sendMessage(ChatColor.GRAY + "/team rally - Teleport to the team rally.");
		p.sendMessage(ChatColor.GRAY + "/team deposit <amount> - Deposit money to team balance.");
		p.sendMessage(ChatColor.GRAY + "/team map - View the boundaries of teams near you.");

		p.sendMessage(ChatColor.DARK_AQUA + "***Team Captains Only***");
		p.sendMessage(ChatColor.GRAY + "/team kick [player] - Kick a player from the team.");
		p.sendMessage(ChatColor.GRAY + "/team claim - Receive the claiming wand.");
		p.sendMessage(ChatColor.GRAY + "/team revoke - Revoke all pending invitations.");
		p.sendMessage(ChatColor.GRAY + "/team invite <player> - Invite a player to the team.");
		p.sendMessage(ChatColor.GRAY + "/team setrally - Set the team rally warp location.");
		p.sendMessage(ChatColor.GRAY + "/team sethq - Set the team headquarters warp location.");
		p.sendMessage(ChatColor.GRAY + "/team withdraw <amount> - Withdraw money from team balance.");

		p.sendMessage(ChatColor.DARK_AQUA + "***Team Leader Only***");
		p.sendMessage(ChatColor.GRAY + "/team promote -Promotes the targeted player to a Captain.");
		p.sendMessage(ChatColor.GRAY + "/team demote - Demotes the targeted player to a member.");
		p.sendMessage(ChatColor.GRAY + "/team unclaim - Unclaim a claim.");
		p.sendMessage(ChatColor.GRAY + "/team newleader [playerName] - Make a player an owner on your team.");

	}
}
