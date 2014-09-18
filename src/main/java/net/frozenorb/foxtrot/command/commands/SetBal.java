package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.BaseCommand;
import net.frozenorb.mBasic.Basic;

public class SetBal extends BaseCommand {

	public SetBal() {
		super("setbal");
		setPermissionLevel("foxtrot.setbal", "§cYou are not allowed to do this!");
	}

	@Override
	public void syncExecute() {
		if (args.length > 1) {
			String name = args[0];

			try {
				double amt = Double.parseDouble(args[1]);

				Basic.get().getEconomyManager().setBalance(name, amt);

			}
			catch (NumberFormatException e) {
				sender.sendMessage(e.getMessage());
				return;
			}

		} else {
			sender.sendMessage("/setbal <player> <amt>");
		}

	}
}
