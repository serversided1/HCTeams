package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.persist.StringPersistMap;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * ---------- hcteams ----------
 * Created by Fraser.Cumming on 29/03/2016.
 * © 2016 Fraser Cumming All Rights Reserved
 */
public class IPMap extends StringPersistMap<List<UUID>> {

    public IPMap() {
        super("ipaddresses", "ipaddresses");
    }

    @Override
    public String getRedisValue(List<UUID> uuids) {
        StringJoiner joiner = new StringJoiner("|");
        for( UUID id : uuids ) {
            joiner.add(id.toString());
        }
        return joiner.toString();
    }

    @Override
    public Object getMongoValue(List<UUID> uuids) {
        return getRedisValue(uuids);
    }

    @Override
    public List<UUID> getJavaObject(String str) {
        List<UUID> ret = new ArrayList<>();
        for( String s : str.split("\\|")) {
            ret.add(UUID.fromString(s));
        }
        return ret;
    }

    public boolean contains( String ip ) {
        return super.contains( ip );
    }

    public void add( String ip, UUID id ) {
        List<UUID> list = get(ip);
        if( list == null ) list = new ArrayList<>();
        list.add((id));
        this.updateValueAsync(ip, list);
    }

    public List<UUID> get( String ip ) {
        return super.getValue(ip);
    }


}
