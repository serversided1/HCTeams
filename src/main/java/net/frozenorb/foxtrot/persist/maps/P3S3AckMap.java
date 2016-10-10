package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.persist.PersistMap;

import java.util.UUID;

public class P3S3AckMap extends PersistMap<Boolean> {

    public P3S3AckMap() {
        super("P3S3Acks", "P3S3Ack");
    }

    @Override
    public String getRedisValue(Boolean toggled) {
        return String.valueOf(toggled);
    }

    @Override
    public Object getMongoValue(Boolean toggled) {
        return String.valueOf(toggled);
    }

    @Override
    public Boolean getJavaObject(String str) {
        return Boolean.valueOf(str);
    }

    public void markAcknowledgedP3S3(UUID update) {
        updateValueAsync(update, true);
    }

    public boolean acknowledgedP3S3(UUID check) {
        return (contains(check) ? getValue(check) : false);
    }

}