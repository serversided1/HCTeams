package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.chat.enums.ChatMode;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamChatCommand {

    @Command(names={ "team chat", "t chat", "f chat", "faction chat", "fac chat", "team c", "t c", "f c", "faction c", "fac c", "mc" }, permissionNode="")
    public static void teamChat(Player sender, @Parameter(name="chat mode", defaultValue="toggle") String params) {
        if (FoxtrotPlugin.getInstance().getTeamHandler().getTeam(sender) == null) {
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

    @Command(names={ "fc", "tc" }, permissionNode="")
    public static void fc(Player sender) {
        setChat(sender, ChatMode.TEAM);
    }

    @Command(names={ "gc", "pc" }, permissionNode="")
    public static void gc(Player sender) {
        setChat(sender, ChatMode.PUBLIC);
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

            FoxtrotPlugin.getInstance().getChatModeMap().setChatMode(player.getUniqueId(), chatMode);
        } else {
            switch (FoxtrotPlugin.getInstance().getChatModeMap().getChatMode(player.getUniqueId())) {
                case PUBLIC:
                    Team team = FoxtrotPlugin.getInstance().getTeamHandler().getTeam(player);
                    boolean teamHasAllies = team != null && team.getAllies().size() > 0;

                    setChat(player, teamHasAllies ? ChatMode.ALLIANCE : ChatMode.TEAM);
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