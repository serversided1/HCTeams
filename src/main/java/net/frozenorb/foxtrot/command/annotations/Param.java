package net.frozenorb.foxtrot.command.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by macguy8 on 11/2/2014.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {

    public String name();
    public boolean wildcard() default (false);
    public String defaultValue() default ("");

}