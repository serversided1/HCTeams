package net.frozenorb.foxtrot.serialization.serializers;

import org.bukkit.util.Vector;

import com.mongodb.BasicDBObject;

import net.frozenorb.foxtrot.serialization.JSONSerializer;

public class VectorSerializer implements JSONSerializer<Vector> {

	@Override
	public Vector deserialize(BasicDBObject dbobj) {
		return new Vector(dbobj.getDouble("x"), dbobj.getDouble("y"), dbobj.getDouble("z"));
	}

	@Override
	public BasicDBObject serialize(Vector vec) {
		if (vec == null)
			return new BasicDBObject("empty", true);
		return new BasicDBObject("x", vec.getX()).append("y", vec.getY()).append("z", vec.getZ());
	}

}
