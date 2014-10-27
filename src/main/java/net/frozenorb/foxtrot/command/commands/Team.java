package net.frozenorb.foxtrot.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.command.BaseCommand;
import net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands.*;
import net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands.Help;

public class Team extends BaseCommand {

	public Team() {
		super("team", "t", "f", "faction", "fac");
		registerSubcommand(new Chat("chat", "§c/f chat", "c"));
		registerSubcommand(new Create("create", "§c/f create <name>"));
		// registerSubcommand(new FF("ff", "§c/f ff <on|off>", "friendlyfire"));
		registerSubcommand(new HQ("hq", "§c/f hq", "home"));
		registerSubcommand(new Info("info", "§c/f i", "i", "show", "who", "list"));
		registerSubcommand(new Kick("kick", "§c/f kick <player>", "k"));
		registerSubcommand(new Leave("leave", "§c/f leave", "l"));
		registerSubcommand(new SetHQ("sethq", "§c/f sethq", "sethome"));
		registerSubcommand(new Msg("msg", "§c/f msg <message>", new String[] {
				"m", "message" }));
		registerSubcommand(new NewLeader("newleader", "§c/f newleader <player>", "leader"));
		registerSubcommand(new Roster("roster", "§c/f roster <message>", "r"));
		registerSubcommand(new Claim("claim", "§c/f claim"));
		registerSubcommand(new Invite("invite", "§c/f invite <player>", "inv"));
		registerSubcommand(new Accept("accept", "§c/f accept <teamName>", "join", "j"));
		registerSubcommand(new Uninvite("uninvite", "§c/f uninvite", "uninv", "deinv", "deinvite"));
		registerSubcommand(new Unclaim("unclaim", "§c/f unclaim"));
		registerSubcommand(new Claims("claims", "§c/f claims [team]"));
		//registerSubcommand(new Rally("rally", "§c/f rally"));
		//registerSubcommand(new SetRally("setrally", "§c/f setrally"));
		registerSubcommand(new Rank("rank", "§c/f rank <player> <member|captain>", "setrank"));
		registerSubcommand(new Help("help", ""));
		registerSubcommand(new Demote("demote", "§c/f demote <player>", "uncaptain"));
		registerSubcommand(new Promote("promote", "§c/f promote <player>", "p", "captain", "officer"));
		registerSubcommand(new Subclaim("subclaim", "", "sub", "s"));
		registerSubcommand(new NameClaim("nameclaim", "", "nc", "claimname"));
		registerSubcommand(new Withdraw("withdraw", "§c/f withdraw <amount>"));
		registerSubcommand(new Deposit("deposit", "§c/f deposit <amount>", "d"));
		registerSubcommand(new Rename("rename", ""));
		registerSubcommand(new Map("map", ""));
        registerSubcommand(new Stuck("stuck", "§c/f stuck", "out"));
        registerSubcommand(new Disband("disband", "§c/f disband"));
        registerSubcommand(new ForceDisband("forcedisband", "§c/f forcedisband"));

		registerSubcommandsToTabCompletions();
	}

	@Override
	public void syncExecute() {
		if (args.length == 0) {
			sendHelp((Player) sender);
		} else
			sender.sendMessage(new String[] { "§cUnrecognized faction command.",
					"§7Type §3'/fac'§7 for details." });
	}

	public static void sendHelp(Player p) {

		p.sendMessage(ChatColor.DARK_AQUA + "***Anyone***");
		p.sendMessage(ChatColor.GRAY + "/f accept <teamName> - Accept a pending invitation.");
		p.sendMessage(ChatColor.GRAY + "/f create [teamName] - Create a faction.");
		p.sendMessage(ChatColor.GRAY + "/f leave - Leave your current faction.");
		p.sendMessage(ChatColor.GRAY + "/f roster <team> - Get details about the faction.");
		p.sendMessage(ChatColor.GRAY + "/f info [playerName] - Get details about a faction.");
		p.sendMessage(ChatColor.GRAY + "/f chat - Toggle faction chat only mode on or off.");
		p.sendMessage(ChatColor.GRAY + "/f claims [team] - View all claims for a faction.");
		p.sendMessage(ChatColor.GRAY + "/f msg <message> - Sends a message to your faction.");
		p.sendMessage(ChatColor.GRAY + "/f hq - Teleport to the team headquarters.");
		//p.sendMessage(ChatColor.GRAY + "/team rally - Teleport to the team rally.");
		p.sendMessage(ChatColor.GRAY + "/f deposit <amount> - Deposit money to team balance.");
		p.sendMessage(ChatColor.GRAY + "/f map - View the boundaries of factions near you.");
        p.sendMessage("");
		p.sendMessage(ChatColor.DARK_AQUA + "***Faction Captains Only***");
		p.sendMessage(ChatColor.GRAY + "/f kick [player] - Kick a player from the faction.");
		p.sendMessage(ChatColor.GRAY + "/f claim - Receive the claiming wand.");
		p.sendMessage(ChatColor.GRAY + "/f uninvite - Manage pending invitations.");
		p.sendMessage(ChatColor.GRAY + "/f invite <player> - Invite a player to the team.");
		//p.sendMessage(ChatColor.GRAY + "/team setrally - Set the team rally warp location.");
		p.sendMessage(ChatColor.GRAY + "/f sethq - Set the faction headquarters warp location.");
		p.sendMessage(ChatColor.GRAY + "/f withdraw <amount> - Withdraw money from team balance.");
        p.sendMessage("");
		p.sendMessage(ChatColor.DARK_AQUA + "***Faction Leader Only***");
		p.sendMessage(ChatColor.GRAY + "/f promote -Promotes the targeted player to a captain.");
		p.sendMessage(ChatColor.GRAY + "/f demote - Demotes the targeted player to a member.");
		p.sendMessage(ChatColor.GRAY + "/f unclaim - Unclaim land.");
		p.sendMessage(ChatColor.GRAY + "/f newleader [playerName] - Make a player an owner on your faction.");
        p.sendMessage(ChatColor.GRAY + "/f disband - Disband the faction. [Warning]");
	}
}
