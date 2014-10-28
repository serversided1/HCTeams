package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;

public class Chat extends Subcommand {

	public Chat(String name, String errorMessage, String... aliases) {
		super(name, errorMessage, aliases);
	}

	@Override
	public void syncExecute() {
		final Player p = (Player) sender;

		if (FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName()) == null) {
			boolean first = true;
			StringBuilder sb = new StringBuilder();
			for (String a : args) {
				if (!first)
					sb.append(a + " ");
				first = false;
			}
			p.chat("/t create " + sb.toString());
			return;
		}

        String chat;

        if(args.length >= 2){
            if(args[1].equalsIgnoreCase("t") || args[1].equalsIgnoreCase("team") || args[1].equalsIgnoreCase("f") || args[1].equalsIgnoreCase("fac") || args[1].equalsIgnoreCase("faction")){
                chat = "team";
            } else if(args[1].equalsIgnoreCase("g") || args[1].equalsIgnoreCase("p") || args[1].equalsIgnoreCase("global") || args[1].equalsIgnoreCase("public")){
                chat = "public";
            } else {
                p.sendMessage(ChatColor.RED + "Invalid chat channel!");
                return;
            }
        } else {
            if(p.hasMetadata("teamChat")){
                chat = "public";
            } else {
                chat = "team";
            }
        }

        setChat(p, chat);
	}

	@Override
	public List<String> tabComplete() {
		return new ArrayList<String>();
	}

    private void setChat(Player player, String type){
        boolean curTeam = player.hasMetadata("teamChat");

        if(type != null){
            if(type.equals("team")){
                if(!(player.hasMetadata("teamChat"))){
                    player.setMetadata("teamChat", new FixedMetadataValue(FoxtrotPlugin.getInstance(), true));
                }

                player.sendMessage(ChatColor.DARK_AQUA + "You are now in faction chat only mode.");
            } else if(type.equals("public")){
                if(player.hasMetadata("teamChat")){
                    player.removeMetadata("teamChat", FoxtrotPlugin.getInstance());
                }

                player.sendMessage(ChatColor.DARK_AQUA + "You are now in public chat.");
            }
        } else {
            if(curTeam) {
                player.removeMetadata("teamChat", FoxtrotPlugin.getInstance());
                player.sendMessage(ChatColor.DARK_AQUA + "You are now in public chat.");
            } else {
                player.setMetadata("teamChat", new FixedMetadataValue(FoxtrotPlugin.getInstance(), true));
                player.sendMessage(ChatColor.DARK_AQUA + "You are now in faction chat only mode.");
            }
        }
    }
}
