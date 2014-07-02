package net.frozenorb.foxtrot.command;

import java.util.List;

import org.bukkit.command.CommandSender;

public interface CompletionHandler {

	public List<String> complete(CommandSender sender, String[] args);
}
