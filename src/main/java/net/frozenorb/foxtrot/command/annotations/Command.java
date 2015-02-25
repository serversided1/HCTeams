package net.frozenorb.foxtrot.command.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    public String[] names();
    public String[] flags() default ("");
    public String permissionNode();

}