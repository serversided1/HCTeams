package net.frozenorb.foxtrot.serialization;

/**
 * Represents any object that is able to be serialized into any form, and
 * subsequently read from it. Differs from {@code java.io.Serializable} in the
 * sense that this class's serialization and deserialization methods are not
 * written.
 * 
 * @see ReflectionSerializer
 * 
 * @author Kerem Celik
 * 
 */
public interface Serializable<T> {

	/**
	 * Serializes the data into the appropriate data type.
	 * 
	 * @return serialized data
	 */
	public T serialize();

	/**
	 * Deserializes the class and loads the class data from the given object.
	 * 
	 * @param data
	 *            the data to use to load the class
	 */
	public void deserialize(T data);
}
