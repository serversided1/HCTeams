package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.util.InvUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.command.BaseCommand;
import net.frozenorb.foxtrot.nametag.NametagManager;

@SuppressWarnings("deprecation")
public class Test extends BaseCommand {

	public Test() {
		super("test");
	}

	@Override
	public void syncExecute() {
        if(!(sender.isOp())){
            sender.sendMessage("error 420");
            return;
        }

        ((Player) sender).getInventory().addItem(InvUtils.CROWBAR);

        //sender.sendMessage(((Player) sender).getItemInHand().getDurability() + "");

		for (Player p : Bukkit.getOnlinePlayers()) {
			//NametagManager.clear(p);
			// NametagManager.cleanupTeams(p);
		}
	}

}
