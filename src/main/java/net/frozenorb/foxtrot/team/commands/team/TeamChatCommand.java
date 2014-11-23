package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.chat.ChatMode;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamChatCommand {

    @Command(names={ "team chat", "t chat", "f chat", "faction chat", "fac chat", "team c", "t c", "f c", "faction c", "fac c", "mc" }, permissionNode="")
    public static void teamChat(Player sender, @Param(name="chat mode", defaultValue="toggle") String params) {
        if (FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName()) == null) {
            sender.sendMessage(ChatColor.GRAY + "You're not in a team!");
            return;
        }

        ChatMode chatMode = null;

        if (params.equalsIgnoreCase("t") || params.equalsIgnoreCase("team") || params.equalsIgnoreCase("f") || params.equalsIgnoreCase("fac") || params.equalsIgnoreCase("faction") || params.equalsIgnoreCase("fc")) {
            chatMode = ChatMode.TEAM;
        } else if (params.equalsIgnoreCase("g") || params.equalsIgnoreCase("p") || params.equalsIgnoreCase("global") || params.equalsIgnoreCase("public") || params.equalsIgnoreCase("gc")) {
            chatMode = ChatMode.PUBLIC;
        } else if (params.equalsIgnoreCase("a") || params.equalsIgnoreCase("allies") || params.equalsIgnoreCase("ally") || params.equalsIgnoreCase("alliance") || params.equalsIgnoreCase("ac")) {
            chatMode = ChatMode.ALLIANCE;
        }

        setChat(sender, chatMode);
    }

    private static void setChat(Player player, ChatMode chatMode) {
        if (chatMode != null) {
            switch (chatMode) {
                case PUBLIC:
                    player.sendMessage(ChatColor.DARK_AQUA + "You are now in public chat.");
                    break;
                case ALLIANCE:
                    player.sendMessage(ChatColor.DARK_AQUA + "You are now in alliance chat.");
                    break;
                case TEAM:
                    player.sendMessage(ChatColor.DARK_AQUA + "You are now in team chat.");
                    break;
            }

            FoxtrotPlugin.getInstance().getChatModeMap().setChatMode(player.getName(), chatMode);
        } else {
            chatMode = FoxtrotPlugin.getInstance().getChatModeMap().getChatMode(player.getName());

            switch (chatMode) {
                case PUBLIC:
                    //setChat(player, ChatMode.ALLIANCE);
                    setChat(player, ChatMode.TEAM);
                    break;
                case ALLIANCE:
                    setChat(player, ChatMode.TEAM);
                    break;
                case TEAM:
                    setChat(player, ChatMode.PUBLIC);
                    break;
            }
        }
    }

}