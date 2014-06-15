package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.BaseCommand;
import net.frozenorb.foxtrot.jedis.RedisSaveTask;

public class Save extends BaseCommand {

	public Save() {
		super("save", "savemyshit");
		setPermissionLevel("foxtrot.save", "§cYou are not allowed to do this!");
	}

	@Override
	public void syncExecute() {
		if (sender.isOp()) {
			RedisSaveTask.getInstance().save();
			sender.sendMessage("§cSaved!");
		}
	}

}
