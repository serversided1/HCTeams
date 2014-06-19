package net.frozenorb.foxtrot.serialization.serializers;

import org.bukkit.entity.EntityType;

import net.frozenorb.foxtrot.serialization.JSONSerializer;

import com.mongodb.BasicDBObject;

public class EntityTypeSerializer implements JSONSerializer<EntityType> {

	@Override
	public BasicDBObject serialize(EntityType o) {
		return new BasicDBObject("name", o.name());
	}

	@Override
	public EntityType deserialize(BasicDBObject dbobj) {
		return EntityType.valueOf(dbobj.getString("name"));
	}

}
