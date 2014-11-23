package net.frozenorb.foxtrot.serialization;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import com.mongodb.util.JSONParseException;

/**
 * A class that uses annotations and reflection to serialize and deserialize
 * Java objects to JSON objects.
 * 
 * 
 * @author Kerem Celik
 * 
 */
public abstract class ReflectionSerializer implements Serializable<BasicDBObject> {

    @Override
    public BasicDBObject serialize() {
        BasicDBObject db = new BasicDBObject();
        try {
            serializeClass(getClass(), db);
            Class<?> c = getClass();
            final String name = c.getName();

            boolean serializableClass = c.isAnnotationPresent(SerializableClass.class);
            SerializableClass cs = c.getAnnotation(SerializableClass.class);
            if ((serializableClass && cs.serializeSuperclasses()) || !serializableClass) {
                int classesDone = 0;
                while (c.getSuperclass() != null) {
                    if (serializableClass && ++classesDone > cs.maxSuperclassDepth()) {
                        break;
                    }
                    c = c.getSuperclass();
                    serializeClass(c, db);
                }
            }

            if (serializableClass && cs.appendClassSignature()) {
                db.append("#className", name);
            }
        }
        catch (IllegalArgumentException | IllegalAccessException | NoSuchMethodException | SecurityException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        return db;
    }

    @Override
    public void deserialize(BasicDBObject dbobj) {
        try {
            SerializableClass cs = getClass().getAnnotation(SerializableClass.class);
            deserializeClass(getClass(), dbobj);
            Class<?> c = getClass();
            boolean serializableClass = c.isAnnotationPresent(SerializableClass.class);
            if ((serializableClass && cs.serializeSuperclasses()) || !serializableClass) {
                int classesDone = 0;
                while (c.getSuperclass() != null) {
                    if (serializableClass && ++classesDone > cs.maxSuperclassDepth()) {
                        break;
                    }
                    c = c.getSuperclass();
                    deserializeClass(c, dbobj);

                }
            }
        }
        catch (IllegalArgumentException | IllegalAccessException | ClassNotFoundException | InstantiationException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("unchecked")
    private void serializeClass(Class<?> c, BasicDBObject db)
            throws NoSuchMethodException, SecurityException,
            IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        boolean serializableClass = c.isAnnotationPresent(SerializableClass.class);
        for (Field f : c.getDeclaredFields()) {
            f.setAccessible(true);
            if (Modifier.isTransient(f.getModifiers())) {

                continue;
            }
            if (f.isAnnotationPresent(SerializableField.class) || serializableClass) {
                Object o = f.get(this);
                if (o != null) {
                    Class<?> type = f.getType();
                    if (JSONSerializable.class.isAssignableFrom(type)) {
                        Method ser = type.getDeclaredMethod("serialize");
                        BasicDBObject ses = (BasicDBObject) ser.invoke(o);
                        SerializableField sf = f.getAnnotation(SerializableField.class);

                        db.append(sf != null ? sf.name().replace("${ACTUAL_FIELD_NAME}", f.getName()) : f.getName(), ses.append("#className", type.getName()));
                    } else {
                        SerializableField sf = f.getAnnotation(SerializableField.class);
                        Object js = new BasicDBObject();
                        if (sf != null && sf.serializer() != Object.class) {
                            js = ((JSONSerializer<Object>) sf.serializer().newInstance()).serialize(o);
                            ((BasicDBObject) js).append("#serializingClass", sf.serializer().getName());
                            ((BasicDBObject) js).append("#className", type.getName());
                        } else {
                            js = o;
                        }

                        db.append(sf != null ? sf.name().replace("${ACTUAL_FIELD_NAME}", f.getName()) : f.getName(), js);
                    }
                }
            }
        }
    }

    private void deserializeClass(Class<?> c, BasicDBObject dbobj)
            throws ClassNotFoundException, IllegalArgumentException,
            IllegalAccessException, NoSuchFieldException, SecurityException,
            InstantiationException {
        boolean serializableClass = c.isAnnotationPresent(SerializableClass.class);
        for (Field f : c.getDeclaredFields()) {
            f.setAccessible(true);
            if (Modifier.isTransient(f.getModifiers())) {
                continue;
            }
            if (f.isAnnotationPresent(SerializableField.class) || serializableClass) {
                SerializableField sf = f.getAnnotation(SerializableField.class);
                if (dbobj.containsField(sf != null ? sf.name().replace("${ACTUAL_FIELD_NAME}", f.getName()) : f.getName())) {
                    Object json = dbobj.get(sf != null ? sf.name().replace("${ACTUAL_FIELD_NAME}", f.getName()) : f.getName());
                    if (json instanceof BasicDBObject && ((BasicDBObject) json).containsField("#className")) {
                        String className = ((BasicDBObject) json).getString("#className");

                        Class<?> deser = null;
                        if (((BasicDBObject) json).containsField("#serializingClass")) {
                            String serializingClass = ((BasicDBObject) json).getString("#serializingClass");
                            deser = Class.forName(serializingClass);
                        }

                        Class<?> cls = Class.forName(className);
                        for (Constructor<?> css : cls.getConstructors()) {
                            css.setAccessible(true);
                        }
                        Object set = null;
                        if (JSONSerializable.class.isAssignableFrom(cls)) {
                            set = ((JSONSerializable) cls.newInstance());
                        }
                        if (deser == null) {
                            ((JSONSerializable) set).deserialize((BasicDBObject) json);
                        } else {
                            set = deser.newInstance();
                            set = ((JSONSerializer<?>) set).deserialize((BasicDBObject) json);
                        }
                        Field modifiersField = Field.class.getDeclaredField("modifiers");
                        modifiersField.setAccessible(true);
                        modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
                        f.set(this, set);
                    } else {
                        Field modifiersField = Field.class.getDeclaredField("modifiers");
                        modifiersField.setAccessible(true);
                        modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
                        f.set(this, json);
                    }
                }
            }
        }
    }

    /**
     * Gets if the given string is valid json or not
     *
     * @param json
     *            the string to check
     * @return validity
     */
    public boolean isValidJSON(String json) {
        try {
            JSON.parse(json);
            return true;
        }
        catch (JSONParseException e) {
            return false;
        }
    }
}
