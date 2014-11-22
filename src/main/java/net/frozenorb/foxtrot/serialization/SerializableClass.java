package net.frozenorb.foxtrot.serialization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a class that will have all of its fields serialized.
 * <p>
 * Transient fields will be ignored, as well as not modified on deserialization,
 * unless otherwise specified with
 * {@link SerializableClass#deserializeTransientFields()}.
 * 
 * @author Kerem Celik
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE })
@Inherited
public @interface SerializableClass {

    /**
     * Whether the fields of any superclasses should be serialized or not
     *
     * @return serialize super classes
     */
    boolean serializeSuperclasses() default false;

    /**
     * Gets the max amount of superclasses to serialize, if
     * serializeSuperclasses() is true
     * <p>
     * The starting class is ignored
     *
     * @return the amount of superclasses to serialize
     */
    int maxSuperclassDepth() default 0;

    /**
     * Whether the output of the serialization should have a '#className' field
     *
     * @return signatures
     */
    boolean appendClassSignature() default true;
}
