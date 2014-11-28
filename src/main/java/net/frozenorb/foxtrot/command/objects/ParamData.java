package net.frozenorb.foxtrot.command.objects;

import lombok.Getter;
import net.frozenorb.foxtrot.command.annotations.Param;

/**
 * Created by macguy8 on 11/2/2014.
 */
public class ParamData {

    @Getter private String name;
    @Getter private boolean wildcard;
    @Getter private String defaultValue;
    @Getter private Class<?> parameterClass;

    public ParamData(Class<?> parameterClass, Param paramAnnotation) {
        this.name =  paramAnnotation.name();
        this.wildcard = paramAnnotation.wildcard();
        this.defaultValue = paramAnnotation.defaultValue();
        this.parameterClass = parameterClass;
    }

}