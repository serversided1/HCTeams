package net.frozenorb.foxtrot.command.commands;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.command.BaseCommand;

public class Test extends BaseCommand {

	public Test() {
		super("test");
	}

	@Override
	public void syncExecute() {
		Bukkit.broadcastMessage(Arrays.toString(((Player) sender).getInventory().getArmorContents()));

	}

}
