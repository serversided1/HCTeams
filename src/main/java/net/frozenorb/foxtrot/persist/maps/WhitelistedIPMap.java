package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.persist.PersistMap;

import java.util.Map;
import java.util.UUID;

/**
 * ---------- hcteams ----------
 * Created by Fraser.Cumming on 29/03/2016.
 * © 2016 Fraser Cumming All Rights Reserved
 */
public class WhitelistedIPMap extends PersistMap<UUID> {

    public WhitelistedIPMap() {
        super("whitelistedipaddresses", "whitelistedipaddresses");
    }

    @Override
    public String getRedisValue(UUID uuid) {
        return uuid.toString();
    }

    @Override
    public Object getMongoValue(UUID uuid) {
        return uuid.toString();
    }

    @Override
    public UUID getJavaObject(String str) {
        return UUID.fromString(str);
    }

    public boolean contains( UUID id ) {
        return super.contains( id );
    }

    public void add( UUID id, UUID ass ) {
        this.updateValueAsync(id, ass);
    }

    public UUID get( UUID id ) {
        return super.getValue(id);
    }

    public boolean containsValue( UUID id ) {
        return wrappedMap.values().contains(id);
    }

    public Map<UUID, UUID> getMap() {
        return wrappedMap;
    }
}
