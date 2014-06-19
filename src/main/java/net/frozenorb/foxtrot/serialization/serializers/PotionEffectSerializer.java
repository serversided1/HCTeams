package net.frozenorb.foxtrot.serialization.serializers;

import net.frozenorb.foxtrot.serialization.JSONSerializer;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.mongodb.BasicDBObject;

public class PotionEffectSerializer implements JSONSerializer<PotionEffect> {

	@Override
	public PotionEffect deserialize(BasicDBObject dbobj) {
		return new PotionEffect(PotionEffectType.getByName(dbobj.getString("type")), dbobj.getInt("duration"), dbobj.getInt("amplifier"));
	}

	@Override
	public BasicDBObject serialize(PotionEffect o) {
		return new BasicDBObject("type", o.getType().getName()).append("duration", o.getDuration()).append("amplifier", o.getAmplifier());
	}

}
