package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class Chat {

    @Command(names={ "team chat", "t chat", "f chat", "faction chat", "fac chat", "team c", "t c", "f c", "faction c", "fac c" }, permissionNode="")
    public static void teamChat(Player sender, @Param(name="chat mode", defaultValue="toggle") String params) {
		if (FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName()) == null) {
            sender.sendMessage(ChatColor.GRAY + "You're not in a team!");
		}

        String chat = "";

        if (params.equalsIgnoreCase("t") || params.equalsIgnoreCase("team") || params.equalsIgnoreCase("f") || params.equalsIgnoreCase("fac") || params.equalsIgnoreCase("faction")) {
            chat = "team";
        } else if (params.equalsIgnoreCase("g") || params.equalsIgnoreCase("p") || params.equalsIgnoreCase("global") || params.equalsIgnoreCase("public")){
            chat = "public";
        }

        setChat(sender, chat);
	}

    private static void setChat(Player player, String type){
        boolean curTeam = player.hasMetadata("teamChat");

        if (type != null) {
            if (type.equals("team")) {
                if (!(player.hasMetadata("teamChat"))) {
                    player.setMetadata("teamChat", new FixedMetadataValue(FoxtrotPlugin.getInstance(), true));
                }

                player.sendMessage(ChatColor.DARK_AQUA + "You are now in faction chat only mode.");
            } else if(type.equals("public")) {
                if (player.hasMetadata("teamChat")) {
                    player.removeMetadata("teamChat", FoxtrotPlugin.getInstance());
                }

                player.sendMessage(ChatColor.DARK_AQUA + "You are now in public chat.");
            }
        } else {
            if (curTeam) {
                player.removeMetadata("teamChat", FoxtrotPlugin.getInstance());
                player.sendMessage(ChatColor.DARK_AQUA + "You are now in public chat.");
            } else {
                player.setMetadata("teamChat", new FixedMetadataValue(FoxtrotPlugin.getInstance(), true));
                player.sendMessage(ChatColor.DARK_AQUA + "You are now in faction chat only mode.");
            }
        }
    }

}