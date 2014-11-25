package net.frozenorb.foxtrot.command.objects;

import net.frozenorb.foxtrot.command.CommandHandler;
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

    //***********************//

    public TwixCommandMap(Server server) {
        super(server);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String cmdLine) {
        foxTabComplete.startTiming();
        try {
            if (!(sender instanceof Player)) {
                return (null);
            }

            Player player = (Player) sender;
            int spaceIndex = cmdLine.indexOf(' ');
            Set<String> completions = new HashSet<String>();

            CmdLoop:
            for (CommandData command : CommandHandler.getCommands()) {
                boolean permission = true;

                if (command.getPermissionNode().equals("op")) {
                    if (!player.isOp()) {
                        permission = false;
                    }
                } else if (!command.getPermissionNode().equals("")) {
                    if (!player.hasPermission(command.getPermissionNode())) {
                        permission = false;
                    }
                }

                if (permission) {
                    for (String alias : command.getNames()) {
                        String split = alias.split(" ")[0];

                        if (spaceIndex != -1) {
                            split = alias;
                        }

                        if (StringUtil.startsWithIgnoreCase(split.trim(), cmdLine.trim()) || StringUtil.startsWithIgnoreCase(cmdLine.trim(), split.trim())) {
                            if (spaceIndex == -1 && cmdLine.length() < alias.length()) {
                                // Complete the command
                                completions.add("/" + split.toLowerCase());
                            } else if (cmdLine.toLowerCase().startsWith(alias.toLowerCase() + " ") && command.getParameters().size() > 0) {
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

                                break CmdLoop;
                            } else {
                                String halfSplitString = split.toLowerCase().replaceFirst(alias.split(" ")[0].toLowerCase(), "").trim();
                                String[] splitString = halfSplitString.split(" ");
                                completions.add(splitString[splitString.length - 1].trim());
                            }
                        }
                    }
                }
            }

            List<String> completionList = new ArrayList<String>(completions);
            List<String> vanillaCompletionList = super.tabComplete(sender, cmdLine);

            if (vanillaCompletionList == null) {
                vanillaCompletionList = new ArrayList<String>();
            }

            for (String vanillaCompletion : vanillaCompletionList) {
                completionList.add(vanillaCompletion);
            }

            completionList.sort(new Comparator<String>() {

                @Override
                public int compare(String o1, String o2) {
                    return (o2.length() - o1.length());
                }

            });

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