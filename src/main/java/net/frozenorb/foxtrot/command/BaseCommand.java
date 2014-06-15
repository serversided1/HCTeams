package net.frozenorb.foxtrot.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

public abstract class BaseCommand implements CommandExecutor, TabExecutor {
	protected CommandSender sender;
	protected Command cmd;
	protected String label;
	protected String[] args;
	private String name;
	private String[] tabCompletions = null;
	protected String[] aliases = new String[] {};
	private HashSet<Subcommand> subcommands = new HashSet<Subcommand>();
	private String permission, permMessage;

	protected BaseCommand(String name, String... aliases) {
		this.name = name;
		if (aliases != null)
			this.aliases = aliases;
	}

	@Override
	public final boolean onCommand(final CommandSender arg0, final Command arg1, final String arg2, final String[] arg3) {
		this.sender = arg0;
		this.cmd = arg1;
		this.args = arg3;
		this.label = arg2;
		if (!verify()) {
			return false;
		}
		if (permission != null) {
			if (!arg0.hasPermission(permission)) {
				arg0.sendMessage(permMessage);
				return false;
			}
		}
		if (runSubcommands())
			return false;
		syncExecute();
		Bukkit.getScheduler().runTaskAsynchronously(FoxtrotPlugin.getInstance(), new Runnable() {

			@Override
			public void run() {
				asyncExecute(arg0, arg1, arg2, arg3);

			}
		});
		return false;
	}

	@Override
	public final List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		this.sender = arg0;
		this.cmd = arg1;
		this.args = arg3;
		this.label = arg2;
		return tabComplete();
	}

	/**
	 * Sets the tab completions to the given string array
	 * 
	 * @param tabCompletions
	 *            tab completion array
	 */
	public final void setTabCompletions(String[] tabCompletions) {
		this.tabCompletions = tabCompletions;
	}

	/**
	 * Gets the most applicable Subcommand based on a String
	 * 
	 * @param match
	 *            the name to find
	 * @return subcommand, null if none found
	 */
	public final Subcommand getSubcommand(String match) {
		for (Subcommand cmd : getSubcommands()) {
			if (cmd.getName().equalsIgnoreCase(match))
				return cmd;
			for (String str : cmd.getAliases())
				if (str.equalsIgnoreCase(match))
					return cmd;
		}
		return null;
	}

	/**
	 * Sets the tab completions to the given string collection
	 * 
	 * @param tabCompletions
	 *            tab completion collection
	 */
	public final void setTabCompletions(Collection<String> strs) {
		this.tabCompletions = strs.toArray(new String[] {});
	}

	/**
	 * Gets the command's tag
	 * <p>
	 * Example: /<commandName>
	 * 
	 * @return command name
	 */
	public final String getCommandName() {
		return name;
	}

	/**
	 * Gets a list of subcommands registered to the command
	 * 
	 * @return subcommands
	 */
	public HashSet<Subcommand> getSubcommands() {
		return subcommands;
	}

	/**
	 * Runs the subcommands that are registered.
	 * 
	 * @return true if any subcommands were run successfully
	 */
	public final boolean runSubcommands() {
		for (Subcommand sbcmd : subcommands)
			if (args.length > sbcmd.getArgumentLevel()) {
				ArrayList<String> als = new ArrayList<String>();
				for (String str : sbcmd.getAliases())
					als.add(str.toLowerCase());
				if (sbcmd.getName().equalsIgnoreCase(args[sbcmd.getArgumentLevel()]) || als.contains(args[sbcmd.getArgumentLevel()].toLowerCase())) {
					sbcmd.execute(sender, cmd, label, args);
					return true;
				}
			}
		return false;
	}

	/**
	 * Registers a new subcommand to be run
	 * <p>
	 * Subcommands are single instance per name, and are registered on a first
	 * come-first server basis.
	 * 
	 * @param subcmd
	 *            the subcommand object to run
	 */
	public final void registerSubcommand(Subcommand subcmd) {
		subcommands.add(subcmd);
	}

	/**
	 * Sets the permission and perm message
	 * 
	 * @param permission
	 *            permission to check for
	 * @param permMessage
	 *            message to send on permission check failure
	 */
	public final void setPermissionLevel(String permission, String permMessage) {
		this.permission = permission;
		this.permMessage = permMessage;
	}

	/**
	 * Sets the subcommands as tab completions
	 */
	public final void registerSubcommandsToTabCompletions() {
		setTabCompletions(new HashSet<String>() {
			private static final long serialVersionUID = 1L;

			{
				for (Subcommand sbcmd : getSubcommands()) {
					add(sbcmd.getName());
					addAll(Arrays.asList(sbcmd.getAliases()));
				}
			}
		});
	}

	/**
	 * Called when the command is executed
	 * <p>
	 * Do NOT run any async tasks, the player pointer will change
	 * 
	 */
	public abstract void syncExecute();

	/**
	 * Called when the command is executed
	 * <p>
	 * The command runs in another thread
	 * 
	 * @param sender
	 *            sender of the command
	 * @param cmd
	 *            the command that is run
	 * @param label
	 *            the command
	 * @param args
	 *            arguments run
	 */
	public void asyncExecute(CommandSender sender, Command cmd, String label, String[] args) {

	}

	/**
	 * Gets a list of tab completions as the first argument
	 * 
	 * @return the list of tab completions
	 */
	public List<String> getTabCompletions() {
		return null;
	}

	/**
	 * Gets a list of tab completions for the command
	 * <p>
	 * Defaults to the String[] given in the constructor
	 * 
	 * @return tab completions
	 */
	public List<String> tabComplete() {
		if (tabCompletions == null && getTabCompletions() == null)
			return null;
		LinkedList<String> args = new LinkedList<String>(Arrays.asList(this.args));
		LinkedList<String> results = new LinkedList<String>();
		String action = null;
		if (args.size() >= 1)
			action = args.pop().toLowerCase();
		else
			return results;
		if (this.args.length > 1) {
			for (int i = 0; i < this.args.length; i += 1) {
				Subcommand cmd = getSubcommand(this.args[i]);
				action = args.pop().toLowerCase();
				if (cmd == null)
					continue;
				if ((cmd.isCompleteSecondHalf() && this.args[i + 1].length() < 2))
					return new ArrayList<String>();

				if (cmd.tabComplete(sender, this.cmd, action, this.args) == null)
					return null;

				for (String p : cmd.tabComplete(sender, this.cmd, action, this.args)) {

					if (p.equalsIgnoreCase("~SECOND_STRING")) {
						action = args.pop().toLowerCase();

						continue;
					}

					if (p.toLowerCase().startsWith(action.toLowerCase())) {
						results.add(p);
					}
				}
				return results;
			}
			return null;
		}
		if (getTabCompletions() != null) {
			for (String p : getTabCompletions()) {
				if (p.toLowerCase().startsWith(action.toLowerCase())) {
					results.add(p);
				}
			}
		}
		if (tabCompletions != null) {

			for (String p : tabCompletions) {
				if (p.toLowerCase().startsWith(action.toLowerCase())) {
					results.add(p);
				}
			}
		}
		return results;
	}

	/**
	 * Gets whether the command is able to run subcommands or not
	 * 
	 * @return default to true
	 */
	public boolean verify() {
		return true;
	}
}
