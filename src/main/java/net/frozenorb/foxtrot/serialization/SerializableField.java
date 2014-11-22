package net.frozenorb.foxtrot.serialization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.frozenorb.Utilities.Serialization.ReflectionSerializer;

/**
 * Represents a serializable field that will be serialized with the
 * {@link ReflectionSerializer} class
 * 
 * @author Kerem Celik
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE })
public @interface SerializableField {

    /**
     * Gets the class to serialize and deserialize the field's value.
     *
     * @return serializing class
     */
    Class<?> serializer() default Object.class;

    /**
     * The field to serialize the value as
     *
     * @return the name to put in the json objet
     */
    String name() default "${ACTUAL_FIELD_NAME}";

}
