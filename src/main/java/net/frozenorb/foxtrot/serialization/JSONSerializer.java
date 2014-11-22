package net.frozenorb.foxtrot.serialization;

import com.mongodb.BasicDBObject;

/**
 * Represents a class that turns Java objects into JSON and vice-versa.
 * 
 * @author Kerem Celik
 * 
 * @param <T>
 *            The type of object being serialized.
 */
public interface JSONSerializer<T> {

    /**
     * Serializes the object and returns the JSON output.
     *
     * @param o
     *            the object to serialize
     * @return json
     */
    public BasicDBObject serialize(T o);

    /**
     * Deserializes and returns the Java object that can be deserialized from
     * the JSON object.
     *
     * @param dbobj
     *            json
     * @return object
     */
    public T deserialize(BasicDBObject dbobj);
}
