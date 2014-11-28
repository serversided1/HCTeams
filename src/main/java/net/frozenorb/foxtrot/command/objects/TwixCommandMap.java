package net.frozenorb.foxtrot.command.objects;

import net.frozenorb.foxtrot.command.CommandHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.spigotmc.CustomTimingsHandler;

import java.util.*;

/**
 * Created by macguy8 on 11/2/2014.
 */
public class TwixCommandMap extends SimpleCommandMap {

    private CustomTimingsHandler foxTabComplete = new CustomTimingsHandler("Foxtrot - CH Command Tab Complete");

    public TwixCommandMap(Server server) {
        super(server);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String cmdLine) {
        foxTabComplete.startTiming();

        try {
            if (cmdLine.equals("/") || cmdLine.equals("/ ")) {
                return (null);
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Tab completion only works from in-game. Sorry!");
                return (null);
            }

            Player player = (Player) sender;
            int spaceIndex = cmdLine.indexOf(' ');
            Set<String> completions = new HashSet<String>();

            CommandLoop:
            for (CommandData command : CommandHandler.getCommands()) {
                if (!command.canAccess(player)) {
                    continue;
                }

                for (String alias : command.getNames()) {
                    String split = alias.split(" ")[0];

                    if (spaceIndex != -1) {
                        split = alias;
                    }

                    if (split.toLowerCase().startsWith(cmdLine) || (split + " ").toLowerCase().startsWith(cmdLine)) {
                        if (spaceIndex == -1 && cmdLine.length() < alias.length()) {
                            // Complete the command
                            completions.add("/" + split.toLowerCase());
                        } else if (cmdLine.toLowerCase().startsWith(split.toLowerCase()) && cmdLine.endsWith(" ") && command.getParameters().size() != 0) {
                            // Complete the params
                            int paramIndex = (cmdLine.split(" ").length - alias.split(" ").length);

                            // If they didn't hit space, complete the param before it.
                            if (paramIndex == command.getParameters().size() || !cmdLine.endsWith(" ")) {
                                paramIndex = paramIndex - 1;
                            }

                            if (paramIndex < 0) {
                                paramIndex = 0;
                            }

                            ParamData paramData = command.getParameters().get(paramIndex);
                            String[] params = cmdLine.split(" ");

                            for (String completion : CommandHandler.tabCompleteParameter(player, cmdLine.endsWith(" ") ? "" : params[params.length - 1], paramData.getParameterClass())) {
                                completions.add(completion);
                            }

                            break CommandLoop;
                        } else {
                            String[] splitString = split.toLowerCase().split(" ");
                            completions.add(splitString[splitString.length - 1].trim());
                        }
                    }
                }
            }

            List<String> completionList = new ArrayList<String>(completions);
            List<String> vanillaCompletionList = super.tabComplete(sender, cmdLine);

            if (vanillaCompletionList != null) {
                for (String vanillaCompletion : vanillaCompletionList) {
                    completionList.add(vanillaCompletion);
                }
            }

            return (completionList);
        } catch (Exception e) {
            e.printStackTrace();
            return (new ArrayList<String>());
        } finally {
            foxTabComplete.stopTiming();
        }
    }

    //***********************//

}