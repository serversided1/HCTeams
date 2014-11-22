package net.frozenorb.foxtrot.serialization;

import com.mongodb.BasicDBObject;

/**
 * Extension of the {@link Serializable} interface, dealing with JSON objects
 * only.
 * <p>
 * Objects are able to be serialized into JSON objects as well as read from
 * them.
 * 
 * @author Kerem Celik
 * 
 */
public interface JSONSerializable extends Serializable<BasicDBObject> {

    @Override
    public BasicDBObject serialize();

    @Override
    public void deserialize(BasicDBObject json);
}
