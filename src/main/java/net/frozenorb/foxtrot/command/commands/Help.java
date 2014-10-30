package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.BaseCommand;

public class Help extends BaseCommand {

    public Help() {
        super("help");
    }

    @Override
    public void syncExecute() {
        sender.sendMessage("§eWelcome to Operation Foxtrot Alpha Testing! Please contact an admin for help.");

    }

}
