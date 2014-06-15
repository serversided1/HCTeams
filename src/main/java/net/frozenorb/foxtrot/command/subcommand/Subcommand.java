package net.frozenorb.foxtrot.command.subcommand;

import java.util.List;

import net.frozenorb.foxtrot.FoxtrotPlugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class Subcommand {
	protected CommandSender sender;
	protected Command cmd;
	protected String label;
	protected String[] args;
	protected String[] removedArgs;
	protected String name;
	protected String errorMessage;
	protected String[] aliases = new String[] {};
	protected int argumentLevel = 0;
	private boolean completeSecondHalf;

	public Subcommand(String name, String errorMessage, String... aliases) {
		this.name = name;
		if (aliases != null)
			this.aliases = aliases;
		this.errorMessage = errorMessage;
	}

	public Subcommand(String name, String... aliases) {
		this.name = name;
		if (aliases != null)
			this.aliases = aliases;
	}

	/**
	 * Sets the index that the subcommand is meant to be
	 * <p>
	 * {@code /<base> <arg0> <arg1> ...}
	 * 
	 * @param i
	 *            the argument level
	 */
	public void setArgumentLevel(int argumentLevel) {
		this.argumentLevel = argumentLevel;
	}

	public void execute(final CommandSender sender, final Command cmd, final String label, final String[] args) {
		this.sender = sender;
		this.cmd = cmd;
		this.label = label;
		this.args = args;
		syncExecute();
		Bukkit.getScheduler().runTaskAsynchronously(FoxtrotPlugin.getInstance(), new Runnable() {

			@Override
			public void run() {
				asyncExecute(sender, cmd, label, args);
			}
		});
	}

	/**
	 * Called when the command is executed
	 * <p>
	 * Do NOT run any async tasks, the player pointer will change
	 * 
	 */
	protected abstract void syncExecute();

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
	protected void asyncExecute(CommandSender sender, Command cmd, String label, String[] args) {

	}

	/**
	 * Sends the error message to set player
	 * <p>
	 * Doesn't work for instantiated subcommands
	 */
	protected void sendErrorMessage() {
		if (errorMessage != null)
			sender.sendMessage(errorMessage);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Subcommand)
			return ((Subcommand) obj).name.equals(name);
		return super.equals(obj);
	}

	/*
	 * -----------GETTERS-----------
	 */
	/**
	 * Gets the aliases of the subcommand
	 * 
	 * @return aliases
	 */
	public String[] getAliases() {
		return aliases;
	}

	/**
	 * Gets the index that the subcommand is meant to be
	 * <p>
	 * {@code /<base> <arg0> <arg1> ...}
	 * 
	 * @return argumentLevel the argument level
	 */
	public int getArgumentLevel() {
		return argumentLevel;
	}

	/**
	 * Gets the error message of the subcommand
	 * 
	 * @return message
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Gets the name of the subcommand
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set whether tab completions should only tab complete the second half or
	 * not
	 * 
	 * @param completeSecondHalf
	 *            secondHalf
	 */
	public void setCompleteSecondHalf(boolean completeSecondHalf) {
		this.completeSecondHalf = completeSecondHalf;
	}

	public final List<String> tabComplete(final CommandSender sender, final Command cmd, final String label, final String[] args) {
		this.sender = sender;
		this.cmd = cmd;
		this.label = label;
		this.args = args;
		return tabComplete();
	}

	/**
	 * Gets whether tab completions should only tab complete after 2 letters
	 * 
	 * @return tab completion
	 */
	public boolean isCompleteSecondHalf() {
		return completeSecondHalf;
	}

	/**
	 * Gets the tabcompletions for a subcommand
	 * 
	 * @return tab completions
	 */
	public List<String> tabComplete() {
		return null;

	}

}
