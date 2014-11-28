package net.frozenorb.foxtrot.command.objects;

import lombok.Getter;
import net.frozenorb.foxtrot.command.CommandHandler;
import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.spigotmc.CustomTimingsHandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by macguy8 on 11/2/2014.
 */
public class CommandData {

    @Getter private boolean console;
    @Getter private String[] names;
    @Getter private String[] flags;
    @Getter private String permissionNode;
    @Getter private List<ParamData> parameters = new ArrayList<ParamData>();
    @Getter private Method method;
    @Getter private CustomTimingsHandler timingsHandler;

    public CommandData(Method method, Command commandAnnotation, List<ParamData> parameters, boolean console) {
        this.names = commandAnnotation.names();
        this.flags = commandAnnotation.flags();
        this.permissionNode = commandAnnotation.permissionNode();
        this.parameters = parameters;
        this.method = method;
        this.console = console;
        this.timingsHandler = new CustomTimingsHandler("Foxtrot - CH '/" + getName() + "' Process");
    }

    public String getName() {
        return (names[0]);
    }

    public boolean canAccess(CommandSender sender) {
        boolean permission = true;

        if (permissionNode.equals("op")) {
            if (!sender.isOp()) {
                permission = false;
            }
        } else if (!permissionNode.equals("")) {
            if (!sender.hasPermission(permissionNode)) {
                permission = false;
            }
        }

        return (permission);
    }

    public String getUsageString() {
        return (getUsageString(getName()));
    }

    public String getUsageString(String aliasUsed) {
        StringBuilder stringBuilder = new StringBuilder();

        for (ParamData paramHelp : getParameters()) {
            boolean needed = paramHelp.getDefaultValue().equals("");
            stringBuilder.append(needed ? "<" : "[").append(paramHelp.getName());
            stringBuilder.append(needed ? ">" : "]").append(" ");
        }

        return ("/" + aliasUsed.toLowerCase() + " " + stringBuilder.toString().trim().toLowerCase());
    }

    public void execute(CommandSender sender, String[] params) {
        ArrayList<Object> transformedParams = new ArrayList<Object>();

        transformedParams.add(sender); // Add the sender

        for (int paramIndex = 0; paramIndex < getParameters().size(); paramIndex++) {
            ParamData param = getParameters().get(paramIndex);
            String passedParam = (paramIndex < params.length ? params[paramIndex] : param.getDefaultValue()).trim();

            if (paramIndex >= params.length && (param.getDefaultValue() == null || param.getDefaultValue().equals(""))) {
                sender.sendMessage(ChatColor.RED + "Usage: " + getUsageString());
                return;
            }

            if (param.isWildcard() && !passedParam.trim().equals(param.getDefaultValue().trim())) {
                passedParam = toString(params, paramIndex);
            }

            Object result = CommandHandler.transformParameter(sender, passedParam, param.getParameterClass());

            if (result == null) {
                return;
            }

            transformedParams.add(result);

            if (param.isWildcard()) {
                break;
            }
        }

        timingsHandler.startTiming();

        try {
            method.invoke(null, transformedParams.toArray(new Object[transformedParams.size()]));
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "It appears there was some issues processing your command...");
            e.printStackTrace();
        }

        timingsHandler.stopTiming();
    }

    public static String toString(String[] args, int start) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int arg = start; arg < args.length; arg++) {
            stringBuilder.append(args[arg]).append(" ");
        }

        return (stringBuilder.toString().trim());
    }

}