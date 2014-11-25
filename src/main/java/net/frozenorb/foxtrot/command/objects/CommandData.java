package net.frozenorb.foxtrot.command.objects;

import lombok.Getter;
import net.frozenorb.foxtrot.command.annotations.Command;
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
        this.timingsHandler = new CustomTimingsHandler("Foxtrot - CH " + getName() + " Process");
    }

    public String getName() {
        return (names[0]);
    }

}